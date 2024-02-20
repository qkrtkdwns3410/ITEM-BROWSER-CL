package com.psj.itembrowser.order.repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.psj.itembrowser.order.domain.dto.request.OrderPageRequestDTO;
import com.psj.itembrowser.order.domain.entity.OrderEntity;
import com.querydsl.jpa.impl.JPAQueryFactory;

@Repository
public class CustomOrderRepositoryImpl implements CustomOrderRepository {

	@PersistenceContext
	private EntityManager em;

	private JPAQueryFactory qf;

	@Override
	public Page<OrderEntity> selectOrdersWithPaginationAndNoCondition(OrderPageRequestDTO orderPageRequestDTO, Pageable pageable) {
		return null;
	}

	@Override
	public Page<OrderEntity> selectOrdersWithPaginationAndNotDeleted(OrderPageRequestDTO requestDTO, Pageable pageable) {
		return null;
	}
}