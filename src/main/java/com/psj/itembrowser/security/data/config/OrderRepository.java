package com.psj.itembrowser.security.data.config;

import org.springframework.data.jpa.repository.JpaRepository;

import com.psj.itembrowser.order.domain.entity.OrderEntity;

public interface OrderRepository extends JpaRepository<OrderEntity, Long> {
}