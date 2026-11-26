package com.kimleewontech.user.dto.response;

import com.kimleewontech.user.entity.User;

import lombok.Getter;

@Getter
public class CreateUserResponse {

	// 속성
	private final Long userId;
	private final String email;
	private final String name;

	// 생성자
	public CreateUserResponse(Long userId, String email, String name) {
		this.userId = userId;
		this.email = email;
		this.name = name;
	}

	// 기능
	public static CreateUserResponse from(User user) {

		return new CreateUserResponse(
			user.getId(),
			user.getEmail(),
			user.getName()
		);
	}
}
