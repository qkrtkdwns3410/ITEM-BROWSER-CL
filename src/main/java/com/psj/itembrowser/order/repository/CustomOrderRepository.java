package com.psj.itembrowser.order.repository;

import static com.psj.itembrowser.order.domain.entity.QOrderEntity.*;

import java.time.LocalDateTime;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.psj.itembrowser.order.domain.dto.request.OrderPageRequestDTO;
import com.psj.itembrowser.order.domain.entity.OrderEntity;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Repository
@RequiredArgsConstructor
@Slf4j
public class CustomOrderRepository {
	
	@PersistenceContext
	private EntityManager em;
	
	private JPAQueryFactory qf;
	
	@PostConstruct
	public void init() {
		this.qf = new JPAQueryFactory(em);
	}
	
	public Page<OrderEntity> selectOrdersWithPagination(OrderPageRequestDTO dto, Pageable pageable, Boolean isDeleted) {
		JPAQuery<OrderEntity> query = qf.selectFrom(orderEntity)
			.where(requestYearEq(dto).and(isDeletedEq(isDeleted)));
		
		List<OrderEntity> orders = query
			.orderBy(orderEntity.createdDate.desc())
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();
		
		long total = query
			.fetch()
			.size();
		
		return new PageImpl<>(orders, pageable, total);
	}
	
	private BooleanExpression isDeletedEq(Boolean isDeleted) {
		if (isDeleted == null) {
			return null;
		}
		
		if (isDeleted) {
			return orderEntity.deletedDate.isNotNull();
		}
		
		return orderEntity.deletedDate.isNull();
	}
	
	private BooleanExpression requestYearEq(OrderPageRequestDTO dto) {
		if (dto == null) {
			return null;
		}
		
		if (dto.getRequestYear() == null) {
			return orderEntity.createdDate.after(LocalDateTime.now().minusMonths(6));
		}
		
		return orderEntity.createdDate.year().eq(dto.getRequestYear().getYear());
	}
}