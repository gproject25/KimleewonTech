package com.kimleewontech.user.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kimleewontech.user.dto.request.CreateUserRequest;
import com.kimleewontech.user.dto.response.CreateUserResponse;
import com.kimleewontech.user.service.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api")
public class UserController {

	//속성
	private final UserService userService;

	//생성자
	public UserController(UserService userService) {
		this.userService = userService;
	}

	//기능

	// 1. 회원가입
	@PostMapping("/auth/signup")
	public ResponseEntity<CreateUserResponse> createUser(@Valid @RequestBody CreateUserRequest request) {

		// 1. 서비스 처리
		CreateUserResponse response = userService.createUser(request);

		// 2. 반환
		return ResponseEntity.status(HttpStatus.CREATED).body(response);

	}

}
