package com.kimleewontech.user.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kimleewontech.common.response.ApiResponse;
import com.kimleewontech.user.dto.request.UserCreateRequest;
import com.kimleewontech.user.dto.response.UserCreateResponse;
import com.kimleewontech.user.service.AuthService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class AuthController {

	//속성
	private final AuthService authService;

	//기능
	// 1. 회원가입
	@PostMapping("/auth/signup")
	public ResponseEntity<ApiResponse<UserCreateResponse>> createUser(@Valid @RequestBody UserCreateRequest request) {

		// 1. 서비스 처리
		UserCreateResponse response = authService.createUser(request);

		// 2. 반환
		return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok(response, HttpStatus.CREATED));

	}

}
