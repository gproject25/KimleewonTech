package com.kimleewontech.cart.dto.response;

import com.kimleewontech.cart.entity.CartItem;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class CartItemCreateResponse {

	private final Long cartItemId;
	private final Long productId;
	// 담긴 뒤 수량(이미 담긴 상품이면 합친 수량)
	private final int quantity;

	// 엔티티 -> 응답 DTO 변환
	public static CartItemCreateResponse from(CartItem cartItem) {
		return new CartItemCreateResponse(
			cartItem.getId(),
			cartItem.getProduct().getId(),
			cartItem.getQuantity()
		);
	}
}
