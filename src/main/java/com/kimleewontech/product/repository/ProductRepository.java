package com.kimleewontech.product.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.kimleewontech.product.entity.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {

	// 주문 생성 시 조건부 UPDATE로 상품 재고 차감
	@Modifying
	@Query("UPDATE Product p SET p.stock = p.stock - :quantity WHERE p.id = :productId "
		+ "AND p.stock >= :quantity "
		+ "AND p.status = com.kimleewontech.product.enums.ProductStatus.ON_SALE "
		+ "AND p.deletedAt IS NULL")
	int deductStock(@Param("productId") Long productId, @Param("quantity") Integer quantity);

	// 재고가 0인 판매 중 상품들을 품절 상태로 변경
	@Modifying
	@Query("UPDATE Product p SET p.status = com.kimleewontech.product.enums.ProductStatus.SOLDOUT "
		+ "WHERE p.id IN :productIds "
		+ "AND p.stock = 0 "
		+ "AND p.status = com.kimleewontech.product.enums.ProductStatus.ON_SALE "
		+ "AND p.deletedAt IS NULL")
	int markAsSoldOut(@Param("productIds") List<Long> productIds);

	// 주문 취소 및 환불 시 재고 복구
	@Modifying
	@Query("UPDATE Product p SET p.stock = p.stock + :quantity "
		+ "WHERE p.id = :productId")
	int restoreStock(@Param("productId") Long productId, @Param("quantity") Integer quantity);

	// 재고가 복구된 품절 상품을 판매 중 상태로 변경
	@Modifying
	@Query("UPDATE Product p SET p.status = com.kimleewontech.product.enums.ProductStatus.ON_SALE "
		+ "WHERE p.id = :productId "
		+ "AND p.stock > 0 "
		+ "AND p.status = com.kimleewontech.product.enums.ProductStatus.SOLDOUT "
		+ "AND p.deletedAt IS NULL")
	int markAsOnSale(@Param("productId") Long productId);

	// 주문 스냅샷용 상품 정보 조회
	@Query("SELECT p FROM Product p WHERE p.id IN :productIds "
		+ "AND p.deletedAt IS NULL")
	List<Product> findOrderProducts(@Param("productIds") List<Long> productIds);

	// 삭제되지 않은 상품의 존재 여부 확인
	boolean existsByIdAndDeletedAtIsNull(Long productId);
}