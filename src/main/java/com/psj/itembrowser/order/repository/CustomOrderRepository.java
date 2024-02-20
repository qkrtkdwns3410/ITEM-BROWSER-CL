package com.psj.itembrowser.order.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.psj.itembrowser.order.domain.dto.request.OrderPageRequestDTO;
import com.psj.itembrowser.order.domain.entity.OrderEntity;

@Repository
public interface CustomOrderRepository {

	Page<OrderEntity> selectOrdersWithPaginationAndNoCondition(OrderPageRequestDTO orderPageRequestDTO, Pageable pageable);

	Page<OrderEntity> selectOrdersWithPaginationAndNotDeleted(OrderPageRequestDTO requestDTO, Pageable pageable);
}