package com.psj.itembrowser.cart.persistance;

import org.springframework.data.jpa.repository.JpaRepository;

import com.psj.itembrowser.cart.domain.entity.CartEntity;

public interface CartRepository extends JpaRepository<CartEntity, Long> {
	CartEntity findByUserId(String userId);
}