package com.psj.itembrowser.order.service;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.psj.itembrowser.member.domain.dto.response.MemberResponseDTO;
import com.psj.itembrowser.member.domain.entity.MemberEntity;
import com.psj.itembrowser.order.domain.dto.request.OrderCreateRequestDTO;
import com.psj.itembrowser.order.domain.dto.request.OrderExchageRequestDTO;
import com.psj.itembrowser.order.domain.dto.request.OrderPageRequestDTO;
import com.psj.itembrowser.order.domain.dto.request.OrdersProductRelationRequestDTO;
import com.psj.itembrowser.order.domain.dto.response.OrderResponseDTO;
import com.psj.itembrowser.order.domain.entity.ExchangeOrderEntity;
import com.psj.itembrowser.order.domain.entity.ExchangeOrderEntityRepository;
import com.psj.itembrowser.order.domain.entity.OrderEntity;
import com.psj.itembrowser.order.domain.entity.OrdersProductRelationEntity;
import com.psj.itembrowser.order.domain.vo.OrderCalculationResult;
import com.psj.itembrowser.order.persistence.OrderPersistence;
import com.psj.itembrowser.order.repository.OrderRepository;
import com.psj.itembrowser.payment.service.PaymentService;
import com.psj.itembrowser.product.domain.entity.ProductEntity;
import com.psj.itembrowser.product.service.ProductService;
import com.psj.itembrowser.product.service.ProductValidationHelper;
import com.psj.itembrowser.security.auth.service.impl.AuthenticationService;
import com.psj.itembrowser.security.common.exception.ErrorCode;
import com.psj.itembrowser.security.common.exception.NotAuthorizedException;
import com.psj.itembrowser.shippingInfos.domain.vo.ShippingInfo;
import com.psj.itembrowser.shippingInfos.service.ShppingInfoValidationService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderService {
	private final OrderRepository orderRepository;
	private final ExchangeOrderEntityRepository exchangeOrderEntityRepository;
	
	private final OrderPersistence orderPersistence;
	private final OrderCalculationService orderCalculationService;
	
	private final AuthenticationService authenticationService;
	private final ProductValidationHelper productValidationHelper;
	private final ShppingInfoValidationService shippingInfoValidationService;
	private final PaymentService paymentService;
	private final ProductService productService;
	
	@Transactional(readOnly = false, timeout = 4)
	public void removeOrder(long orderId) {
		OrderEntity foundOrder = orderPersistence.findOrderById(orderId);
		
		foundOrder.cancel();
	}
	
	public OrderResponseDTO getOrderWithNotDeleted(Long id) {
		OrderEntity orderWithNotDeleted = orderPersistence.getOrderWithNotDeleted(id);
		
		return OrderResponseDTO.from(orderWithNotDeleted);
	}
	
	public OrderResponseDTO getOrderWithNoCondition(Long id) {
		OrderEntity orderWithNoCondition = orderPersistence.getOrderWithNoCondition(id);
		
		return OrderResponseDTO.from(orderWithNoCondition);
	}
	
	public Page<OrderResponseDTO> getOrdersWithPaginationAndNoCondition(MemberEntity member, @NotNull OrderPageRequestDTO requestDTO) {
		Pageable pageRequest = PageRequest.of(requestDTO.getPageNum(), requestDTO.getPageSize());
		
		Page<OrderEntity> ordersWithPaginationAndNoCondition = orderPersistence.getOrdersWithPaginationAndNoCondition(requestDTO, pageRequest);
		
		return ordersWithPaginationAndNoCondition.map(OrderResponseDTO::from);
	}
	
	public Page<OrderResponseDTO> getOrdersWithPaginationAndNotDeleted(MemberEntity member, @NotNull OrderPageRequestDTO requestDTO) {
		Pageable pageable = PageRequest.of(requestDTO.getPageNum(), requestDTO.getPageSize());
		
		Page<OrderEntity> ordersWithPaginationAndNotDeleted = orderPersistence.getOrdersWithPaginationAndNotDeleted(requestDTO, pageable);
		
		authenticationService.authorizeOrdersWhenCustomer(ordersWithPaginationAndNotDeleted, member);
		
		return ordersWithPaginationAndNotDeleted.map(OrderResponseDTO::from);
	}
	
	@Transactional(readOnly = false, timeout = 10)
	public OrderResponseDTO createOrder(MemberEntity member, @Valid OrderCreateRequestDTO orderCreateRequestDTO) {
		if (!member.isActivated()) {
			throw new NotAuthorizedException(ErrorCode.NOT_ACTIVATED_MEMBER);
		}
		
		List<ProductEntity> foundProducts = productValidationHelper.validateProduct(orderCreateRequestDTO.getProducts());
		
		OrderCalculationResult orderCalculationResult = orderCalculationService.calculateOrderDetails(orderCreateRequestDTO, member);
		
		ShippingInfo shippingInfo = ShippingInfo.from(orderCreateRequestDTO.getShippingInfo());
		shippingInfoValidationService.validateAddress(shippingInfo);
		
		//TODO 결제 추후 구현
		paymentService.pay(orderCalculationResult);
		
		OrderEntity order = OrderEntity.from(orderCreateRequestDTO, orderCalculationResult);
		order.completePayment();
		
		OrderEntity savedOrder = orderRepository.save(order);
		
		for (ProductEntity foundProduct : foundProducts) {
			productService.decreaseStock(foundProduct);
		}
		
		//TODO 추후 필요 - 결제 히스토리를 DB에 기록한다.
		
		return OrderResponseDTO.from(savedOrder);
	}
	
	@Transactional(readOnly = false, timeout = 5)
	public OrderResponseDTO requestExchangeOrder(MemberResponseDTO member, @Valid OrderExchageRequestDTO orderCreateRequestDTO) {
		//취소 가능한 주문인 것
		OrderEntity order = orderPersistence.findOrderById(orderCreateRequestDTO.getExchangeOrderId());
		order.isExchangeable();
		
		//재고 확인 수행 & 존재하는 상품인지 체크
		OrdersProductRelationEntity foundOrderProduct = orderPersistence.findOrderProductWithPessimisticLock(
			orderCreateRequestDTO.getExchangeOrderId(),
			orderCreateRequestDTO.getExchangeOrderProductId()
		);
		
		OrdersProductRelationRequestDTO requestDTO = OrdersProductRelationRequestDTO.from(foundOrderProduct);
		
		//상품 재고 체크수행
		productValidationHelper.validateProduct(List.of(requestDTO));
		
		//주문을 교환 신청 상태로 변경
		order.requestExchange();
		
		//DB 에 교환 기록을 남긴다.
		exchangeOrderEntityRepository.save(ExchangeOrderEntity.from(orderCreateRequestDTO));
		
		return OrderResponseDTO.from(order);
	}
}