package com.kimleewontech.product.entity;

import com.kimleewontech.common.entity.BaseEntity;
import com.kimleewontech.common.exception.BusinessException;
import com.kimleewontech.common.exception.ErrorCode;
import com.kimleewontech.product.enums.ProductStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "products")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Product extends BaseEntity {

	// 속성
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, length = 100)
	private String name;

	// 상품 판매 가격 (원 단위, 0 이상)
	@Column(nullable = false)
	private Integer price;

	// 판매 가능한 재고 수량 (0 이상)
	// 주문 생성 시 차감하고 주문 취소 및 환불 시 복구
	@Column(nullable = false)
	private Integer stock;

	// 상품 판매 상태 (판매 중, 품절, 판매 중단)
	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private ProductStatus status;

	@Column(columnDefinition = "TEXT")
	private String description;

	@Column(name = "image_url", length = 500)
	private String imageUrl;

	// 생성자
	// 정적 팩토리 메서드를 통해서만 상품을 생성하도록 제한
	@Builder(access = AccessLevel.PRIVATE)
	private Product(String name, Integer price, Integer stock, ProductStatus status, String description,
		String imageUrl) {
		this.name = name;
		this.price = price;
		this.stock = stock;
		this.status = status;
		this.description = description;
		this.imageUrl = imageUrl;
	}

	// 기능
	// 상품 생성 (입력값 검증 및 초기 판매 상태 설정)
	public static Product create(String name, Integer price, Integer stock,
		ProductStatus status, String description, String imageUrl) {

		// 상품 가격은 필수이며 0 이상이어야 함
		if (price == null || price < 0) {
			throw new BusinessException(ErrorCode.INVALID_PRODUCT_PRICE);
		}

		// 초기 재고는 필수이며 0 이상이어야 함
		if (stock == null || stock < 0) {
			throw new BusinessException(ErrorCode.INVALID_PRODUCT_STOCK);
		}

		// 상품 상태 미입력 시 기본값은 판매 중
		if (status == null) {
			status = ProductStatus.ON_SALE;
		}

		// 재고가 0이고 판매 중단 상태가 아니면 자동 품절 처리
		if (stock == 0 && status != ProductStatus.DISCONTINUED) {
			status = ProductStatus.SOLDOUT;
		}

		return Product.builder()
			.name(name)
			.price(price)
			.stock(stock)
			.status(status)
			.description(description)
			.imageUrl(imageUrl)
			.build();
	}

	// 기능 : 관리자 재고 변경 및 상품 상태 자동 변경
	public void updateStock(Integer stock) {

		// 변경할 재고는 필수이며 0 이상이어야 함
		if (stock == null || stock < 0) {
			throw new BusinessException(ErrorCode.INVALID_PRODUCT_STOCK);
		}
		// 재고 수량 변경
		this.stock = stock;

		// 판매 중단 상품은 기존 상태 유지
		if (this.status == ProductStatus.DISCONTINUED) {
			return;
		}

		// 재고가 0이면 자동 품절 처리
		if (this.stock == 0) {
			this.status = ProductStatus.SOLDOUT;
		} else {
			// 재고가 있으면 판매 중 상태로 변경
			this.status = ProductStatus.ON_SALE;
		}
	}
}
