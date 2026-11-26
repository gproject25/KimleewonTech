package com.kimleewontech.cart.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kimleewontech.cart.entity.Cart;

// carts 테이블 조회 및 저장
public interface CartRepository extends JpaRepository<Cart, Long> {
	// 회원의 장바구니 조회 (user_id UNIQUE라 최대 1개, 아직 안 담았으면 없을 수 있어서 Optional)
	Optional<Cart> findByUserId(Long userId);
}
