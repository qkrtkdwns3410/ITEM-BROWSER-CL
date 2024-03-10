package com.psj.itembrowser.order.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.psj.itembrowser.order.domain.entity.OrdersProductRelationEntity;

public interface OrdersProductRelationRepository
	extends JpaRepository<OrdersProductRelationEntity, OrdersProductRelationEntity.OrdersProductRelationEntityId> {
	
	Optional<OrdersProductRelationEntity> findWithPessimisticLockByGroupIdAndProductId(long groupId, long productId);
}