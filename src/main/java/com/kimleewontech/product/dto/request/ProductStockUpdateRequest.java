package com.kimleewontech.product.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ProductStockUpdateRequest {

	// 속성
	@NotNull(message = "재고 수량은 필수입니다.")
	@Min(value = 0, message = "재고는 0개 이상이어야 합니다.")
	private Integer stock;
}
