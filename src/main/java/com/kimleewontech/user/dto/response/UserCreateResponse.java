package com.kimleewontech.user.dto.response;

import com.kimleewontech.user.entity.User;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class UserCreateResponse {

	private final Long userId;
	private final String email;
	private final String name;

	public static UserCreateResponse from(User user) {
		return new UserCreateResponse(
			user.getId(),
			user.getEmail(),
			user.getName()
		);
	}
}