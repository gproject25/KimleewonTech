package com.kimleewontech.product.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kimleewontech.common.exception.BusinessException;
import com.kimleewontech.common.exception.ErrorCode;
import com.kimleewontech.product.dto.request.ProductStockUpdateRequest;
import com.kimleewontech.product.dto.response.ProductOrderInfo;
import com.kimleewontech.product.dto.response.ProductStockUpdateResponse;
import com.kimleewontech.product.entity.Product;
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

			// 상품이 존재하지 않거나 삭제된 경우
			if (!productRepository.existsByIdAndDeletedAtIsNull(productId)) {
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

	// 기능 : 주문 스냅샷용 상품 정보 조회
	public List<ProductOrderInfo> getOrderInfos(List<Long> productIds) {
		// 상품 ID 목록이 비어 있으면 빈 목록 반환
		if (productIds.isEmpty()) {
			return List.of();
		}

		// 주문에 필요한 상품 목록 조회
		List<Product> products = productRepository.findOrderProducts(productIds);

		// 요청한 상품이 모두 조회되었는지 확인
		if (productIds.size() != products.size()) {
			throw new BusinessException(ErrorCode.PRODUCT_NOT_FOUND);
		}

		// 주문 스냅샷용 DTO 목록 생성
		List<ProductOrderInfo> orderInfos = new ArrayList<>();

		// 상품 엔티티를 주문 스냅샷용 DTO로 변환
		for (Product product : products) {
			ProductOrderInfo orderInfo = ProductOrderInfo.from(product);
			orderInfos.add(orderInfo);
		}
		// 변환된 DTO 목록 반환
		return orderInfos;
	}

	// 기능 : 관리자 상품 재고 변경
	@Transactional
	public ProductStockUpdateResponse updateStock(Long productId, ProductStockUpdateRequest request) {
		// 삭제되지 않은 상품 조회 및 존재하지 않을 경우 예외 처리
		Product product = productRepository.findByIdAndDeletedAtIsNull(productId)
			.orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND));

		// 상품 재고 및 판매 상태 변경
		product.updateStock(request.getStock());

		// 변경된 상품 정보를 응답 DTO로 변환하여 반환
		return ProductStockUpdateResponse.from(product);
	}

	// 기능 : 주문 취소 및 환불 시 상품 재고 복구
	@Transactional
	public void restoreStock(Long productId, int quantity) {
		// 복구 수량 검증
		if (quantity <= 0) {
			throw new BusinessException(ErrorCode.INVALID_INPUT);
		}

		// 소프트 삭제된 상품을 포함하여 재고 복구
		int updatedRows = productRepository.restoreStock(productId, quantity);

		// 상품이 존재하지 않을 경우 예외 처리
		if (updatedRows == 0) {
			throw new BusinessException(ErrorCode.PRODUCT_NOT_FOUND);
		}

		// 재고가 복구된 품절 상품의 자동 판매 재개
		productRepository.markAsOnSale(productId);
	}
}
