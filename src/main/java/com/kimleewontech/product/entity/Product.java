package com.kimleewontech.product.entity;

import org.hibernate.annotations.SoftDelete;
import org.hibernate.annotations.SoftDeleteType;

import com.kimleewontech.common.BaseTimeEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "products")
// 삭제 시 deleted_at에 삭제 시간을 기록하고 일반 조회에서 제외
@SoftDelete(strategy = SoftDeleteType.TIMESTAMP, columnName = "deleted_at")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Product extends BaseTimeEntity {

	// 속성
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, length = 100)
	private String name;

	// 상품 판매 가격 (원 단위, 0 이상)
	@Column(nullable = false)
	private Integer price;

	// 판매 가능한 재고 수량 (0 이상, 0이면 품절)
	// 주문 생성 시 차감하고 주문 취소 및 환불 시 복구
	@Column(nullable = false)
	private Integer stock;

	@Column(columnDefinition = "TEXT")
	private String description;

	@Column(name = "image_url", length = 500)
	private String imageUrl;

	// 생성자
	public Product(String name, Integer price, Integer stock, String description, String imageUrl) {

		if (price == null || price < 0) {
			throw new IllegalArgumentException("가격은 0 이상이어야 합니다.");
		}

		if (stock == null || stock < 0) {
			throw new IllegalArgumentException("재고는 0 이상이어야 합니다.");
		}

		this.name = name;
		this.price = price;
		this.stock = stock;
		this.description = description;
		this.imageUrl = imageUrl;
	}
}
