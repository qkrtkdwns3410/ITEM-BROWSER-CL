package com.psj.itembrowser.order.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.psj.itembrowser.order.domain.entity.OrderEntity;

public interface OrderRepository extends JpaRepository<OrderEntity, Long> {
	Optional<OrderEntity> findByIdAndDeletedDateIsNull(Long id);
}