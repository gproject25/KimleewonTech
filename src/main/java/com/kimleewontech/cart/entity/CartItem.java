package com.kimleewontech.cart.entity;

import com.kimleewontech.common.entity.BaseEntity;
import com.kimleewontech.product.entity.Product;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// cart_items 테이블과 연결되는 엔티티 (장바구니에 담긴 상품 한 줄)
@Entity
@Table(
	name = "cart_items",
	// 같은 장바구니에 같은 상품을 두 줄로 담지 못하게 막음
	uniqueConstraints = @UniqueConstraint(name = "uk_cart_product", columnNames = {"cart_id", "product_id"})
)
@Getter
// 외부에서 빈 객체 생성 금지 (팀 규칙)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CartItem extends BaseEntity {

	// PK, 자동 증가
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	// 이 항목이 속한 장바구니 (carts.id, 지연 로딩)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "cart_id")
	private Cart cart;

	// 담은 상품 (products.id, 지연 로딩)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "product_id")
	private Product product;

	// 수량 (1 이상)
	@Column(nullable = false)
	private int quantity;

	// 생성자는 private으로 숨기고 빌더로만 생성 (팀 규칙)
	@Builder
	private CartItem(Cart cart, Product product, int quantity) {
		this.cart = cart;
		this.product = product;
		this.quantity = quantity;
	}

	// 이미 담긴 상품을 다시 담으면 수량을 더함 (장바구니 담기 API)
	public void addQuantity(int quantity) {
		this.quantity += quantity;
	}

	// 수량을 새 값으로 바꿈 (수량 변경 API)
	public void updateQuantity(int quantity) {
		this.quantity = quantity;
	}

	// 장바구니 항목 생성 (정적 팩토리 안에서 빌더 사용 - 팀 규칙)
	public static CartItem create(Cart cart, Product product, int quantity) {
		return CartItem.builder()
			.cart(cart)
			.product(product)
			.quantity(quantity)
			.build();
	}
}