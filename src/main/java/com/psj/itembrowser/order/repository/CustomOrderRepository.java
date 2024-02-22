package com.psj.itembrowser.order.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.psj.itembrowser.order.domain.dto.request.OrderPageRequestDTO;
import com.psj.itembrowser.order.domain.entity.OrderEntity;

@Repository
public interface CustomOrderRepository {
	
	Optional<Page<OrderEntity>> selectOrdersWithPagination(OrderPageRequestDTO orderPageRequestDTO, Pageable pageable, Boolean isDeleted);
}