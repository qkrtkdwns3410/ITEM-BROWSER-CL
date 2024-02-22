package com.psj.itembrowser.product.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.psj.itembrowser.product.domain.entity.ProductEntity;

public interface ProductRepository extends JpaRepository<ProductEntity, Long> {
}