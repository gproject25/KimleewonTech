package com.kimleewontech.common.filter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.kimleewontech.common.exception.ErrorCode;
import com.kimleewontech.common.security.auth.AuthMember;
import com.kimleewontech.common.security.jwt.JwtProvider;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

/**
 * JWT 인증 필터
 * - 요청마다 한 번씩 실행 -> (OncePerRequestFilter)
 * - Authorization 헤더의 Bearer 토큰을 꺼내 검증 -> 유효하면 AuthMember를 SecurityContext에 저장
 * <p>
 * <p>
 * User -> ROLE_USER
 * MANAGER -> ROLE_ADMIN, ADMIN_MANAGER
 * SUPER MANAGER -> ROLE_ADMIN, ADMIN_SUPER
 */

@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {
	private static final String AUTHORIZATION_HEADER = "Authorization";
	private static final String BEARER_PREFIX = "Bearer ";

	private final JwtProvider jwtProvider;

	@Override
	protected void doFilterInternal(HttpServletRequest request,
		HttpServletResponse response,
		FilterChain filterChain) throws ServletException, IOException {

		// 1. 요청 헤더에서 토큰 추출 (없으면 null)
		String token = resolveToken(request);

		// 2. 토큰이 있고, 서명·만료 시간이 유효한 경우에만 인증 처리
		if (token != null) {
			if (jwtProvider.isValid(token)) {
				AuthMember authMember = jwtProvider.getAuthMember(token);

				// 3. Spring Security가 이해하는 Authentication 객체 생성
				//    - principal: AuthMember (컨트롤러에서 @AuthenticationPrincipal로 꺼냄)
				//    - credentials: null (이미 토큰으로 인증했으므로 비밀번호 불필요)
				//    - authorities: 권한 목록
				UsernamePasswordAuthenticationToken authentication =
					new UsernamePasswordAuthenticationToken(authMember, null, toAuthorities(authMember));

				// 4. SecurityContext에 저장 → 이 요청 동안 "로그인된 사용자"로 인식됨
				SecurityContextHolder.getContext().setAuthentication(authentication);
			} else {
				// 토큰이 만료/위조됨 → 여기서 응답하지 않고 표시만
				request.setAttribute("authError", ErrorCode.INVALID_TOKEN);
			}
		}

		filterChain.doFilter(request, response);   // always continue
	}

	// "Bearer " 뒤의 토큰 문자열만 꺼냅니다
	private String resolveToken(HttpServletRequest request) {
		String header = request.getHeader(AUTHORIZATION_HEADER);
		if (StringUtils.hasText(header) && header.startsWith(BEARER_PREFIX)) {
			return header.substring(BEARER_PREFIX.length());
		}
		return null;
	}

	// AuthMember를 Spring Security 권한 목록으로 변환
	private List<GrantedAuthority> toAuthorities(AuthMember member) {
		List<GrantedAuthority> authorities = new ArrayList<>();
		authorities.add(new SimpleGrantedAuthority("ROLE_" + member.getRole().name()));      // ROLE_USER / ROLE_ADMIN
		if (member.getAdminLevel() != null) {
			authorities.add(
				new SimpleGrantedAuthority(
					"ADMIN_" + member.getAdminLevel().name()));      // ADMIN_SUPER / ADMIN_MANAGER
		}
		return authorities;
	}
}
