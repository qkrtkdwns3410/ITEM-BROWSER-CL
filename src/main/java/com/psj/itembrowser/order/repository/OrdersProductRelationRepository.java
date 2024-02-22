package com.psj.itembrowser.order.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.psj.itembrowser.order.domain.entity.OrdersProductRelationEntity;

public interface OrdersProductRelationRepository
	extends JpaRepository<OrdersProductRelationEntity, OrdersProductRelationEntity.OrdersProductRelationEntityId> {
}