package com.kimleewontech.user.dto.response;

import com.kimleewontech.admin.enums.AdminLevel;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class UserLoginResponse {

	// 상수
	private static final String TOKEN_TYPE = "Bearer";

	// 속성
	private final String accessToken;
	private final String tokenType;
	private final boolean admin;
	private final AdminLevel adminLevel;

	// 기능
	public static UserLoginResponse of(String accessToken, AdminLevel adminLevel) {
		return new UserLoginResponse(accessToken, TOKEN_TYPE, adminLevel != null, adminLevel);
	}

}
