package com.kimleewontech.common.config;

import java.util.Optional;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.kimleewontech.common.security.auth.AuthMember;

@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorAware")
public class PersistenceConfig {

	// 로그인 사용자가 없는 요청 (회원가입, 서버 시작 시 SUPER 관리자 생성, 웹훅)
	private static final Long SYSTEM_USER_ID = 0L;

	// @CreatedBy, @LastModifiedBy에 들어갈 값: 로그인한 회원의 memberId
	@Bean
	public AuditorAware<Long> auditorAware() {
		return () -> {
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			if (auth != null && auth.getPrincipal() instanceof AuthMember member) {
				return Optional.of(member.getMemberId());
			}
			return Optional.of(SYSTEM_USER_ID);
		};
	}
}