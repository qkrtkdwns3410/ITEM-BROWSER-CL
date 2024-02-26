package com.psj.itembrowser.cart.persistance;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.psj.itembrowser.cart.domain.entity.CartEntity;

public interface CartRepository extends JpaRepository<CartEntity, Long> {
	Optional<CartEntity> findByUserEmail(String userEmail);
}