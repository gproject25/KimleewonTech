package com.kimleewontech.cart.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

// POST /api/customer/carts/me/items 요청 본문

@Getter
@NoArgsConstructor
public class CartItemCreateRequest {

	// 담을 상품 ID (필수)
	@NotNull(message = "상품 ID는 필수입니다.")
	private Long productId;

	// 담을 수량 (필수, 1 이상)
	@NotNull(message = "수량은 필수입니다.")
	@Min(value = 1, message = "수량은 1 이상이어야 합니다.")
	private Integer quantity;
}
