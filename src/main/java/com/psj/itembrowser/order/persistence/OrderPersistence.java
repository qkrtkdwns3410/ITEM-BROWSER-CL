package com.psj.itembrowser.order.persistence;

import static com.psj.itembrowser.order.domain.vo.OrderStatus.*;
import static com.psj.itembrowser.security.common.exception.ErrorCode.*;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import com.psj.itembrowser.order.domain.dto.request.OrderDeleteRequestDTO;
import com.psj.itembrowser.order.domain.dto.request.OrderPageRequestDTO;
import com.psj.itembrowser.order.domain.entity.OrderEntity;
import com.psj.itembrowser.order.domain.vo.Order;
import com.psj.itembrowser.order.mapper.OrderMapper;
import com.psj.itembrowser.order.repository.CustomOrderRepository;
import com.psj.itembrowser.order.repository.OrderRepository;
import com.psj.itembrowser.security.common.exception.NotFoundException;

import lombok.RequiredArgsConstructor;

/**
 * packageName    : com.psj.itembrowser.order.persistence fileName       : OrderPersistence author :
 * ipeac date           : 2023-11-09 description    :
 * =========================================================== DATE              AUTHOR NOTE
 * ----------------------------------------------------------- 2023-11-09        ipeac       최초 생성
 */
@Component
@RequiredArgsConstructor
public class OrderPersistence {
	
	private final OrderMapper orderMapper;
	private final OrderRepository orderRepository;
	private final CustomOrderRepository customOrderRepository;
	
	public void removeOrder(long id) {
		OrderDeleteRequestDTO deleteOrderRequestDTO = OrderDeleteRequestDTO.builder()
			.id(id)
			.orderStatus(CANCELED)
			.build();
		
		orderMapper.deleteSoftly(deleteOrderRequestDTO);
	}
	
	public void removeOrderProducts(long orderId) {
		orderMapper.deleteSoftlyOrderProducts(orderId);
	}
	
	public Order findOrderStatusForUpdate(long orderId) {
		Order findOrder = orderMapper.selectOrderWithPessimissticLock(orderId);
		
		if (findOrder == null) {
			throw new NotFoundException(ORDER_NOT_FOUND);
		}
		
		return findOrder;
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
}