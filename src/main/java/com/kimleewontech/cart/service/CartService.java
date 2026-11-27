package com.kimleewontech.cart.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kimleewontech.cart.dto.response.CartResponse;
import com.kimleewontech.cart.entity.Cart;
import com.kimleewontech.cart.entity.CartItem;
import com.kimleewontech.cart.repository.CartItemRepository;
import com.kimleewontech.cart.repository.CartRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
// 기본: 읽기 전용, 데이터 변경 메서드만 @Transactional
@Transactional(readOnly = true)
public class CartService {

	private final CartRepository cartRepository;
	private final CartItemRepository cartItemRepository;

	// 내 장바구니 조회 API
	public CartResponse findCart(Long userId) {

		// 1. 내 장바구니 조회 -> 없으면 빈 응답(조회만으로는 장바구니 만들지 않음)
		Optional<Cart> cart = cartRepository.findByUserId(userId);
		if (cart.isEmpty()) {
			return CartResponse.empty();
		}

		// 2. 장바구니 항목 + 상품 정보 조회
		// 응답 변환과 합계 계산 -> CartResponse.of()에서
		Long cartId = cart.get().getId();
		List<CartItem> cartItems = cartItemRepository.findAllWithProductByCartId(cartId);
		return CartResponse.of(cartId, cartItems);

	}
}
