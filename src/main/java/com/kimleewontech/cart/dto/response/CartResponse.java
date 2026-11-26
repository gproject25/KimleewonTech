package com.kimleewontech.cart.dto.response;

import java.util.List;

import lombok.Getter;

// 최종 응답

@Getter
public class CartResponse {

	// 장바구니 ID (장바구니가 아직 없으면 null)
	private final Long cartId;
	// 장바구니 항목 목록
	private final List<CartItemResponse> cartItems;
	// 전체 합계 (모든 subtotal의 합)
	private final int totalAmount;

	public CartResponse(Long cartId, List<CartItemResponse> cartItems, int totalAmount) {
		this.cartId = cartId;
		this.cartItems = cartItems;
		this.totalAmount = totalAmount;
	}
}
