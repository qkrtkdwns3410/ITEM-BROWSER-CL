package com.psj.itembrowser.security.data.config;

import org.springframework.data.jpa.repository.JpaRepository;

import com.psj.itembrowser.cart.domain.entity.CartProductRelationEntity;

public interface CartProductRelationRepository extends JpaRepository<CartProductRelationEntity, Long> {
}