package com.kimleewontech.cart.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kimleewontech.cart.dto.request.CartItemCreateRequest;
import com.kimleewontech.cart.dto.response.CartItemCreateResponse;
import com.kimleewontech.cart.dto.response.CartResponse;
import com.kimleewontech.cart.entity.Cart;
import com.kimleewontech.cart.entity.CartItem;
import com.kimleewontech.cart.repository.CartItemRepository;
import com.kimleewontech.cart.repository.CartRepository;
import com.kimleewontech.common.exception.BusinessException;
import com.kimleewontech.common.exception.ErrorCode;
import com.kimleewontech.product.entity.Product;
import com.kimleewontech.product.enums.ProductStatus;
import com.kimleewontech.product.service.ProductService;
import com.kimleewontech.user.service.UserService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
// 기본: 읽기 전용, 데이터 변경 메서드만 @Transactional
@Transactional(readOnly = true)
public class CartService {

	private final CartRepository cartRepository;
	private final CartItemRepository cartItemRepository;
	// 다른 도메인 Repository가 아닌 Service로 호출
	private final ProductService productService;
	private final UserService userService;

	// 내 장바구니 조회 API
	// 필요: Product 엔티티(getName, getPrice, get
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

	// 장바구니 담기 API
	@Transactional
	public CartItemCreateResponse createCartItem(Long userId, CartItemCreateRequest request) {

		// 1. 상품 조회 (없거나 삭제된 상품이면 상품 쪽에서 404 PRODUCT_NOT_FOUND)
		// TODO: findProduct가 Product 엔티티를 반환하는지 상품 담당자분에게 확인
		Product product = productService.findProduct(request.getProductId());

		// 2. 판매 중단 상품이면 404(API 명세서)
		// TODO: 상품 쪽 에러코드 이름 확정 후 변경
		if (product.getStatus() == ProductStatus.DISCONTINUED) {
			throw new BusinessException(ErrorCode.PRODUCT_NOT_FOUND);
		}

		// 3. 품절(SOLDOUT 또는 재고 0)이면 409(API 명세)
		// TODO: 에러코드 이름 확정 후 변경
		if (product.getStatus() == ProductStatus.SOLDOUT || product.getStock() <= 0) {
			throw new BusinessException(ErrorCode.OUT_OF_STOCK);
		}

		// 4. 내 장바구니 조회 -> 없으면 새로 생성 (담기에서만 장바구니 생성)
		Cart cart = findOrCreateCart(userId);

		// 5. 같은 상품이 이미 담겨 있는지 확인 (UK(cart_id, product_id)라 최대 1개)
		Optional<CartItem> existingCartItem =
			cartItemRepository.findByCartIdAndProductId(cart.getId(), product.getId());

		// 6. 담긴 뒤 총 수량 계산 (이미 있으면 기존 수량 + 요청 수량)
		int totalQuantity = request.getQuantity();
		if (existingCartItem.isPresent()) {
			totalQuantity = existingCartItem.get().getQuantity() + request.getQuantity();
		}

		// 7. 총 수량이 재고보다 많으면 409 (API 명세)
		if (totalQuantity > product.getStock()) {
			throw new BusinessException(ErrorCode.OUT_OF_STOCK);
		}

		// 8-1. 이미 있으면 수량 합산 (변경 감지로 자동 UPDATE)
		if (existingCartItem.isPresent()) {
			CartItem cartItem = existingCartItem.get();
			cartItem.addQuantity(request.getQuantity());
			return CartItemCreateResponse.from(cartItem);
		}

		// 8-2. 없으면 새 항목으로 저장
		CartItem cartItem = CartItem.create(cart, product, request.getQuantity());
		CartItem savedCartItem = cartItemRepository.save(cartItem);
		return CartItemCreateResponse.from(savedCartItem);

	}
}

