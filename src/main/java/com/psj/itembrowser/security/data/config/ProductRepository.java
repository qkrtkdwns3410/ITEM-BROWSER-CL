package com.psj.itembrowser.security.data.config;

import org.springframework.data.jpa.repository.JpaRepository;

import com.psj.itembrowser.product.domain.entity.ProductEntity;

public interface ProductRepository extends JpaRepository<ProductEntity, Long> {
}