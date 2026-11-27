package com.kimleewontech.common.security;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpMethod;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kimleewontech.admin.enums.AdminLevel;
import com.kimleewontech.common.config.SecurityConfig;
import com.kimleewontech.common.security.auth.AuthMember;
import com.kimleewontech.common.security.jwt.JwtProperties;
import com.kimleewontech.common.security.jwt.JwtProvider;

// 인증·인가 규칙 테스트 (DB · 엔티티 · 실제 컨트롤러 없이 웹 계층만)
@WebMvcTest(SecurityAuthorizationTest.DummyController.class)
@TestPropertySource(properties = {
	"jwt.secret=a2ltbGVld29udGVjaC10ZXN0LWp3dC1zZWNyZXQta2V5LTAxMjM0NTY3ODk=",
	"jwt.access-expiration=3600000"
})
class SecurityAuthorizationTest {

	@TestConfiguration
	@EnableConfigurationProperties(JwtProperties.class)
	@Import({SecurityConfig.class, JwtProvider.class, SecurityAuthorizationTest.DummyController.class})
	static class Config {
	}

	// 보안을 통과하면 무조건 200을 돌려주는 테스트용 컨트롤러 (모든 /api/** 요청을 받음)
	@RestController
	static class DummyController {
		@RequestMapping("/api/**")
		public String ok() {
			return "ok";
		}
	}

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private JwtProvider jwtProvider;

	@ParameterizedTest(name = "{0} {1} [{2}] → {3}")
	@CsvSource({
		// 공개
		"GET,    /api/customer/products,     NONE,    200",   // 비로그인도 상품 목록 조회 가능
		"GET,    /api/customer/products/1,   NONE,    200",   // 비로그인도 상품 상세 조회 가능
		"POST,   /api/customer/products,     NONE,    401",   // permitAll은 GET만 → POST는 로그인 필요

		// USER
		"GET,    /api/customer/users/me,     NONE,    401",   // 토큰 없음 → 인증 필요
		"GET,    /api/customer/users/me,     USER,    200",   // 일반 회원은 내 정보 조회 가능
		"POST,   /api/customer/orders,       USER,    200",   // 일반 회원은 주문 생성 가능
		"GET,    /api/customer/carts/me,     MANAGER, 200",   // 관리자도 회원이라 고객 API 사용 가능

		// ADMIN
		"GET,    /api/admin/products,        NONE,    401",   // 토큰 없음 → 인증 필요
		"GET,    /api/admin/products,        USER,    403",   // 로그인은 했지만 ROLE_ADMIN 없음 → 권한 부족
		"GET,    /api/admin/products,        MANAGER, 200",   // MANAGER는 ROLE_ADMIN 있음 → 통과
		"GET,    /api/admin/products,        SUPER,   200",   // SUPER도 ROLE_ADMIN 있음 → 통과

		// SUPER
		"POST,   /api/admin/admins,          USER,    403",   // 관리자가 아님 → 권한 부족
		"POST,   /api/admin/admins,          MANAGER, 403",   // 관리자지만 ADMIN_SUPER 없음 → 임명 불가
		"POST,   /api/admin/admins,          SUPER,   200",   // ADMIN_SUPER 있음 → 관리자 임명 가능
		"DELETE, /api/admin/admins/1,        MANAGER, 403",   // ADMIN_SUPER 없음 → 해제 불가
		"DELETE, /api/admin/admins/1,        SUPER,   200",   // ADMIN_SUPER 있음 → 관리자 해제 가능

		// 잘못된 토큰
		"GET,    /api/customer/users/me,     INVALID, 401"    // 위조·만료 토큰 → 인증 실패
	})
	void authorization(HttpMethod method, String url, String who, int expectedStatus) throws Exception {
		MockHttpServletRequestBuilder req = request(method, url);

		String token = tokenOf(who);
		if (token != null) {
			req.header("Authorization", "Bearer " + token);
		}

		mockMvc.perform(req).andExpect(status().is(expectedStatus));
	}

	private String tokenOf(String who) {
		return switch (who) {
			case "USER" -> jwtProvider.createAccessToken(AuthMember.user(1L));
			case "MANAGER" -> jwtProvider.createAccessToken(AuthMember.admin(2L, AdminLevel.MANAGER));
			case "SUPER" -> jwtProvider.createAccessToken(AuthMember.admin(3L, AdminLevel.SUPER));
			case "INVALID" -> "not.a.valid.token";
			default -> null;   // NONE
		};
	}
}