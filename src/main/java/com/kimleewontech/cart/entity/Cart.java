package com.kimleewontech.cart.entity;

import com.kimleewontech.common.entity.BaseEntity;
import com.kimleewontech.user.entity.User;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// carts 테이블과 연결되는 엔티티 (회원 1명당 장바구니 1개)
@Entity
@Table(name = "carts")
@Getter
// 외부에서 빈 객체 생성 금지 (팀 규칙)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Cart extends BaseEntity {

	// PK, 자동 증가
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	// 장바구니 주인 (users.id, UNIQUE → 회원당 1개)
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", unique = true)
	private User user;

	// 생성자는 private으로 숨기고 빌더로만 생성 (팀 규칙)
	@Builder
	private Cart(User user) {
		this.user = user;
	}

	// 장바구니 생성 (정적 팩토리 안에서 빌더 사용 - 팀 규칙)
	public static Cart create(User user) {
		return Cart.builder()
			.user(user)
			.build();
	}
}