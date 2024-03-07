package com.psj.itembrowser.order.persistence;

import static com.psj.itembrowser.order.domain.vo.OrderStatus.*;
import static com.psj.itembrowser.security.common.exception.ErrorCode.*;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.psj.itembrowser.order.domain.dto.request.OrderDeleteRequestDTO;
import com.psj.itembrowser.order.domain.dto.request.OrderPageRequestDTO;
import com.psj.itembrowser.order.domain.entity.OrderEntity;
import com.psj.itembrowser.order.domain.entity.OrdersProductRelationEntity;
import com.psj.itembrowser.order.mapper.OrderMapper;
import com.psj.itembrowser.order.repository.CustomOrderRepository;
import com.psj.itembrowser.order.repository.OrderRepository;
import com.psj.itembrowser.order.repository.OrdersProductRelationRepository;
import com.psj.itembrowser.security.common.exception.NotFoundException;

import lombok.RequiredArgsConstructor;

/**
 * packageName    : com.psj.itembrowser.order.persistence fileName       : OrderPersistence author :
 * ipeac date           : 2023-11-09 description    :
 * =========================================================== DATE              AUTHOR NOTE
 * ----------------------------------------------------------- 2023-11-09        ipeac       최초 생성
 */
@Component
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OrderPersistence {
	
	private final OrderMapper orderMapper;
	private final OrderRepository orderRepository;
	private final CustomOrderRepository customOrderRepository;
	private final OrdersProductRelationRepository ordersProductRelationRepository;
	
	@Transactional(readOnly = false)
	public void removeOrder(long id) {
		OrderDeleteRequestDTO deleteOrderRequestDTO = OrderDeleteRequestDTO.builder()
			.id(id)
			.orderStatus(CANCELED)
			.build();
		
		orderMapper.deleteSoftly(deleteOrderRequestDTO);
	}
	
	@Transactional(readOnly = false)
	public void removeOrderProducts(long orderId) {
		orderMapper.deleteSoftlyOrderProducts(orderId);
	}
	
	public OrderEntity findOrderById(long id) {
		return orderRepository.findById(id).orElseThrow(() -> new NotFoundException(ORDER_NOT_FOUND));
	}
	
	public OrderEntity getOrderWithNotDeleted(long id) {
		return orderRepository.findByIdAndDeletedDateIsNull(id).orElseThrow(() -> new NotFoundException(ORDER_NOT_FOUND));
	}
	
	public OrderEntity getOrderWithNoCondition(Long id) {
		return orderRepository.findById(id).orElseThrow(() -> new NotFoundException(ORDER_NOT_FOUND));
	}
	
	public Page<OrderEntity> getOrdersWithPaginationAndNoCondition(OrderPageRequestDTO requestDTO, Pageable pageable) {
		Page<OrderEntity> foundOrders = customOrderRepository.selectOrdersWithPagination(requestDTO, pageable, null);
		
		if (foundOrders.getContent().isEmpty()) {
			throw new NotFoundException(ORDER_NOT_FOUND);
		}
		
		return foundOrders;
	}
	
	public Page<OrderEntity> getOrdersWithPaginationAndNotDeleted(OrderPageRequestDTO requestDTO, Pageable pageable) {
		Page<OrderEntity> foundOrders = customOrderRepository.selectOrdersWithPagination(requestDTO, pageable, false);
		
		if (foundOrders.getContent().isEmpty()) {
			throw new NotFoundException(ORDER_NOT_FOUND);
		}
		
		return foundOrders;
	}
	
	public OrdersProductRelationEntity findOrderProductWithPessimisticLock(long groupId, long productId) {
		return ordersProductRelationRepository.findWithPessimisticLockByGroupIdAndProductId(groupId, productId)
			.orElseThrow(() -> new NotFoundException(ORDER_PRODUCTS_EMPTY));
	}
}