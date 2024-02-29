package com.psj.itembrowser.cart.domain.entity;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CartProductRelationEntityRepository
	extends JpaRepository<CartProductRelationEntity, CartProductRelationEntity.CartProductRelationEntityId> {
	Optional<CartProductRelationEntity> findByCartIdAndProductId(Long cartId, Long productId);
}