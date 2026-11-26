package com.kimleewontech.cart.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kimleewontech.cart.dto.response.CartResponse;
import com.kimleewontech.cart.service.CartService;
import com.kimleewontech.common.response.ApiResponse;

import lombok.RequiredArgsConstructor;

// 장바구니 API(요청 검증 + 응답 변환만, 로직은 Service)
@RestController
@RequiredArgsConstructor
// me = 로그인한 나 (토큰으로 판단)
@RequestMapping("/api/customer/carts/me")
public class CartController {

	private final CartService cartService;

	// 내 장바구니 조회
	@GetMapping
	public ResponseEntity<ApiResponse<CartResponse>> findCart(@AuthenticationPrincipal Long userId) {
		
		CartResponse response = cartService.findCart(userId);
		return ResponseEntity.status(HttpStatus.OK)
			.body(ApiResponse.ok(response, HttpStatus.OK));

	}
}
