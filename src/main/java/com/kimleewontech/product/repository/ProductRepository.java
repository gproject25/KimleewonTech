package com.kimleewontech.product.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kimleewontech.product.entity.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {
}
