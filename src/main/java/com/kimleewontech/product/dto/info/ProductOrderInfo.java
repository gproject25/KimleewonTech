package com.kimleewontech.product.dto.info;

import com.kimleewontech.product.entity.Product;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class ProductOrderInfo {

	// 속성
	private final Long productId;
	private final String name;
	private final int price;

	// 기능
	public static ProductOrderInfo from(Product product) {
		return new ProductOrderInfo(
			product.getId(),
			product.getName(),
			product.getPrice()
		);
	}
}
