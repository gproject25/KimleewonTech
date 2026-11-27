package com.kimleewontech.cart.dto.response;

import java.util.ArrayList;
import java.util.List;

import com.kimleewontech.cart.entity.CartItem;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

// 최종 응답

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class CartResponse {

	// 장바구니 ID (장바구니가 아직 없으면 null)
	private final Long cartId;
	// 장바구니 항목 목록
	private final List<CartItemResponse> cartItems;
	// 전체 합계 (모든 subtotal의 합)
	private final int totalAmount;

	// 장바구니가 있을 때: 항목을 응답 한 줄씩
	// 바꾸면서 합계 계산
	public static CartResponse of(Long cartId, List<CartItem> cartItems) {
		// 3. 항목을 응답 한 줄씩으로 바꾸면서 합계 계산
		List<CartItemResponse> cartItemList = new ArrayList<>();
		int totalAmount = 0;
		for (CartItem cartItem : cartItems) {
			CartItemResponse cartItemResponse = CartItemResponse.from(cartItem);
			cartItemList.add(cartItemResponse);
			totalAmount += cartItemResponse.getSubtotal();
		}

		// 4. 장바구니 ID, 항목 목록, 전체 합계로 응답 생성
		return new CartResponse(cartId, cartItemList, totalAmount);
	}

	// 장바구니가 아직 없을 때: cartId null, 빈 목록, 합계 0
	public static CartResponse empty() {
		return new CartResponse(null, new ArrayList<>(), 0);
	}
}
