package com.kimleewontech.cart.dto.response;

import com.kimleewontech.cart.entity.CartItem;
import com.kimleewontech.product.entity.Product;

import lombok.Getter;

// GET /api/customer/carts/me 응답의 cartItems[] 한 줄
@Getter
public class CartItemResponse {

	private final Long cartItemId;
	private final Long productId;
	private final String productName;
	// 현재 판매가
	private final int price;
	private final int quantity;
	// price x quantity
	private final int subtotal;
	// 품절이면 true (JSON 이름: soldOut)
	private final boolean soldOut;

	public CartItemResponse(Long cartItemId, Long productId, String productName, int price, int quantity, int subtotal,
		boolean soldOut) {
		this.cartItemId = cartItemId;
		this.productId = productId;
		this.productName = productName;
		this.price = price;
		this.quantity = quantity;
		this.subtotal = subtotal;
		this.soldOut = soldOut;
	}

	// 엔티티 -> 응답 DTO 변환
	public static CartItemResponse from(CartItem cartItem) {
		Product product = cartItem.getProduct();

		int subtotal = product.getPrice() * cartItem.getQuantity();
		// Todo: 상품 상태(ProductStatus) 개발 후 SOLDOUT 기준 변경
		boolean soldOut = product.getStock() <= 0;

		return new CartItemResponse(
			cartItem.getId(),
			product.getId(),
			product.getName(),
			product.getPrice(),
			cartItem.getQuantity(),
			subtotal,
			soldOut
		);
	}
}
