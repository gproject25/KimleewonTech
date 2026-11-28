package com.kimleewontech.product.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kimleewontech.common.response.ApiResponse;
import com.kimleewontech.product.dto.request.ProductStockUpdateRequest;
import com.kimleewontech.product.dto.response.ProductStockUpdateResponse;
import com.kimleewontech.product.service.ProductService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/products")
public class AdminProductController {

	// 속성
	private final ProductService productService;

	// 기능 : 관리자 상품 재고 변경
	@PatchMapping("/{productId}/stocks")
	public ResponseEntity<ApiResponse<ProductStockUpdateResponse>> updateStock(
		@PathVariable Long productId,
		@Valid @RequestBody ProductStockUpdateRequest request) {
		// 상품 재고 변경 서비스 호출
		ProductStockUpdateResponse response = productService.updateStock(productId, request);

		// 공통 응답 형식으로 변환하여 반환
		return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.ok(response, HttpStatus.OK));
	}
}
