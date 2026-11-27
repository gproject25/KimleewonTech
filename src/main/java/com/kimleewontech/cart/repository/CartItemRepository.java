package com.kimleewontech.cart.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.kimleewontech.cart.entity.CartItem;

// cart_items 테이블 조회 및 저장
public interface CartItemRepository extends JpaRepository<CartItem, Long> {

	// 장바구니 항목 + 상품 정보를 쿼리 한 번으로 조회 (N+1 방지), 담은 순서대로
	// 특정 장바구니ID(cartId)에 담긴 모든 장바구니 항목과 연결된 상품(Product) 정보를 한번에 조회
	@Query("select ci from CartItem ci join fetch ci.product where ci.cart.id = :cartId order by ci.id asc")
	List<CartItem> findAllWithProductByCartId(@Param("cartId") Long cartId);

	// 같은 장바구니에 같은 상품이 이미 담겨있는지 조회(UK(cart_id, product_id)라 최대 1개)
	Optional<CartItem> findByCartIdAndProductId(Long cartId, Long productId);
}
