package com.psj.itembrowser.order.repository;

import static com.psj.itembrowser.order.domain.entity.QOrderEntity.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

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
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.extern.slf4j.Slf4j;

@Repository
@Slf4j
public class CustomOrderRepositoryImpl implements CustomOrderRepository {
	
	@PersistenceContext
	private EntityManager em;
	
	private JPAQueryFactory qf;
	
	@PostConstruct
	public void init() {
		this.qf = new JPAQueryFactory(em);
	}
	
	public Optional<Page<OrderEntity>> selectOrdersWithPagination(OrderPageRequestDTO dto, Pageable pageable, Boolean isDeleted) {
		List<OrderEntity> orders = qf.selectFrom(orderEntity)
			.where(requestYearEq(dto).and(isDeletedEq(isDeleted)))
			.orderBy(orderEntity.createdDate.desc())
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();
		
		long total = qf.selectFrom(orderEntity)
			.where(requestYearEq(dto).and(isDeletedEq(isDeleted)))
			.fetch()
			.size();
		
		return Optional.of(new PageImpl<>(orders, pageable, total));
	}
	
	private BooleanExpression isDeletedEq(Boolean isDeleted) {
		if (isDeleted == null) {
			return null;
		}
		
		if (isDeleted) {
			return orderEntity.deletedDate.isNotNull();
		} else {
			return orderEntity.deletedDate.isNull();
		}
	}
	
	private BooleanExpression requestYearEq(OrderPageRequestDTO dto) {
		if (dto == null) {
			return null;
		}
		
		if (dto.getRequestYear() == null) {
			return orderEntity.createdDate.after(LocalDateTime.now().minusMonths(6));
		} else {
			return orderEntity.createdDate.year().eq(dto.getRequestYear().getYear());
		}
	}
}