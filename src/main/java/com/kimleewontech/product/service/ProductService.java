package com.kimleewontech.product.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kimleewontech.common.exception.BusinessException;
import com.kimleewontech.common.exception.ErrorCode;
import com.kimleewontech.product.repository.ProductRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductService {

	// 속성
	private final ProductRepository productRepository;

	// 기능 : 주문 생성 시 상품 재고 차감
	@Transactional
	public void deductStock(Long productId, int quantity) {

		// 차감 수량 검증
		if (quantity <= 0) {
			throw new BusinessException(ErrorCode.INVALID_INPUT);
		}
		// 조건부 UPDATE로 재고 차감
		int updatedRows = productRepository.deductStock(productId, quantity);

		// 재고 차감 실패 시 원인 확인
		if (updatedRows == 0) {

			// 상품이 존재하지 않는 경우
			if (!productRepository.existsById(productId)) {
				throw new BusinessException(ErrorCode.PRODUCT_NOT_FOUND);
			}
			// 재고 부족 또는 주문 불가 상태인 경우
			throw new BusinessException(ErrorCode.OUT_OF_STOCK);
		}
	}

	// 기능 : 재고가 0인 상품 자동 품절 처리
	@Transactional
	public void markSoldOut(List<Long> productIds) {
		// 상품 ID 목록이 비어 있으면 처리하지 않음
		if (productIds.isEmpty()) {
			return;
		}
		productRepository.markAsSoldOut(productIds);
	}
}
