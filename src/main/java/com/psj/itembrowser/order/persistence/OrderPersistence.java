package com.psj.itembrowser.order.persistence;

import static com.psj.itembrowser.order.domain.vo.OrderStatus.*;
import static com.psj.itembrowser.security.common.exception.ErrorCode.*;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Component;

import com.psj.itembrowser.order.domain.dto.request.OrderPageRequestDTO;
import com.psj.itembrowser.order.domain.entity.OrderEntity;
import com.psj.itembrowser.order.domain.vo.Order;
import com.psj.itembrowser.order.mapper.OrderDeleteRequestDTO;
import com.psj.itembrowser.order.mapper.OrderMapper;
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

	public OrderEntity getOrderWithNotDeleted(long id) {
		Optional<OrderEntity> findOrder = orderRepository.findByIdAndDeletedDateIsNull(id);

		if (findOrder.isEmpty()) {
			throw new NotFoundException(ORDER_NOT_FOUND);
		}

		return findOrder.get();
	}

	public OrderEntity getOrderWithNoCondition(Long id) {
		Optional<OrderEntity> findOrder = orderRepository.findById(id);

		if (findOrder.isEmpty()) {
			throw new NotFoundException(ORDER_NOT_FOUND);
		}

		return findOrder.get();
	}

	public List<Order> getOrdersWithPaginationAndNoCondition(OrderPageRequestDTO requestDTO) {
		List<Order> orders = orderMapper.selectOrdersWithPaginationAndNoCondition(requestDTO);

		if (orders.isEmpty()) {
			throw new NotFoundException(ORDER_NOT_FOUND);
		}

		return orders;
	}

	public List<Order> getOrdersWithPaginationAndNotDeleted(OrderPageRequestDTO requestDTO) {
		List<Order> orders = orderMapper.selectOrdersWithPaginationAndNotDeleted(requestDTO);

		if (orders.isEmpty()) {
			throw new NotFoundException(ORDER_NOT_FOUND);
		}

		return orders;
	}
}