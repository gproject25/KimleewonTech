package com.kimleewontech.common.security.auth;

import com.kimleewontech.domain.admin.entity.AdminLevel;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class AuthMember {

	private final Long memberId;
	private final AuthRole role;
	private final AdminLevel adminLevel;

	public static AuthMember user(Long memberId) {
		return new AuthMember(memberId, AuthRole.USER, null);
	}

	public static AuthMember admin(Long memberId, AdminLevel level) {
		return new AuthMember(memberId, AuthRole.ADMIN, level);
	}

	public boolean isSuperAdmin() {
		return role == AuthRole.ADMIN && adminLevel == AdminLevel.SUPER;
	}
}