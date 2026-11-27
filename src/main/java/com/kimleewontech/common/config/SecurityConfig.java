package com.kimleewontech.common.config;

import java.io.IOException;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.kimleewontech.common.exception.ErrorCode;
import com.kimleewontech.common.filter.JwtAuthFilter;
import com.kimleewontech.common.response.ApiResponse;
import com.kimleewontech.common.security.jwt.JwtProvider;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import tools.jackson.databind.ObjectMapper;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity
public class SecurityConfig {
	private final ObjectMapper objectMapper;
	private final JwtProvider jwtProvider;

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		return http
			.csrf(AbstractHttpConfigurer::disable)
			.formLogin(AbstractHttpConfigurer::disable)
			.httpBasic(AbstractHttpConfigurer::disable)
			.logout(AbstractHttpConfigurer::disable)
			.sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

			// 401: 토큰 없음 / 만료 / 위조
			// 403: 로그인은 했지만 권한 부족
			.exceptionHandling(exception -> exception
				.authenticationEntryPoint((request, response, authException) -> {
					ErrorCode errorCode = (ErrorCode)request.getAttribute("authError");
					writeError(response, errorCode != null ? errorCode : ErrorCode.USER_UNAUTHORIZED);
				})
				.accessDeniedHandler((request, response, accessDeniedException) ->
					writeError(response, ErrorCode.ACCESS_DENIED))
			)

			// 권한
			.authorizeHttpRequests(auth -> auth
				.requestMatchers(HttpMethod.GET, "/actuator/health").permitAll()
				.requestMatchers("/api/auth/signup", "/api/auth/login").permitAll()
				.requestMatchers(HttpMethod.GET, "/api/customer/products", "/api/customer/products/**").permitAll()
				.requestMatchers("/api/admin/admins/**").hasAuthority("ADMIN_SUPER")
				.requestMatchers("/api/admin/**").hasRole("ADMIN")
				.anyRequest().authenticated()
			)
			.addFilterBefore(new JwtAuthFilter(jwtProvider), UsernamePasswordAuthenticationFilter.class)
			.build();
	}

	// 401/403 공통 응답 작성
	private void writeError(HttpServletResponse response, ErrorCode errorCode) throws IOException {
		response.setStatus(errorCode.getStatus().value());
		response.setContentType("application/json;charset=UTF-8");
		response.getWriter().write(objectMapper.writeValueAsString(ApiResponse.error(errorCode)));
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
}

