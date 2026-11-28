package com.kimleewontech.product.dto.response;

import com.kimleewontech.product.entity.Product;
import com.kimleewontech.product.enums.ProductStatus;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class ProductStockUpdateResponse {

	// 속성
	private final Long productId; // 상품 ID
	private final Integer stock; // 변경된 재고 수량
	private final boolean soldOut; // 품절 여부

	// 기능 : 상품 엔티티를 재고 변경 응답 DTO로 변환
	public static ProductStockUpdateResponse from(Product product) {
		return new ProductStockUpdateResponse(
			product.getId(),
			product.getStock(),
			// 재고 수량이 아닌 상품 판매 상태를 기준으로 품절 여부 판단
			product.getStatus() == ProductStatus.SOLDOUT
		);
	}
}
