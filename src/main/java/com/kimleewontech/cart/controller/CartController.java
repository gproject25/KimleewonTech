package com.kimleewontech.cart.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kimleewontech.cart.dto.response.CartResponse;
import com.kimleewontech.cart.service.CartService;
import com.kimleewontech.common.response.ApiResponse;
import com.kimleewontech.common.security.auth.AuthMember;

import lombok.RequiredArgsConstructor;

// 장바구니 API(요청 검증 + 응답 변환만, 로직은 Service)
@RestController
@RequiredArgsConstructor
// me = 로그인한 나 (토큰으로 판단)
@RequestMapping("/api/customer/carts")
public class CartController {

	private final CartService cartService;

	// 내 장바구니 조회
	@GetMapping("/me")
	public ResponseEntity<ApiResponse<CartResponse>> findCart(@AuthenticationPrincipal AuthMember member) {

		CartResponse response = cartService.findCart(member.getMemberId());
		return ResponseEntity.status(HttpStatus.OK)
			.body(ApiResponse.ok(response, HttpStatus.OK));

	}
}
