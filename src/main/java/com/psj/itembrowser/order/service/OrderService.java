package com.psj.itembrowser.order.service;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.psj.itembrowser.member.domain.entity.MemberEntity;
import com.psj.itembrowser.order.domain.dto.request.OrderCreateRequestDTO;
import com.psj.itembrowser.order.domain.dto.request.OrderPageRequestDTO;
import com.psj.itembrowser.order.domain.dto.response.OrderResponseDTO;
import com.psj.itembrowser.order.domain.entity.OrderEntity;
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
		
		//해당 주문상품이 존재하는지 확인 && 각 상품에 대한 재고확인 수행
		List<ProductEntity> foundProducts = productValidationHelper.validateProduct(orderCreateRequestDTO.getProducts());
		
		//주문 상품에 대한 가격, 수량, 할인, 배송비 등을 계산한다.
		OrderCalculationResult orderCalculationResult = orderCalculationService.calculateOrderDetails(orderCreateRequestDTO, member);
		
		//주문자의 배송지에 대한 검증 수행한다. -> 존재하는 배송지인지 확인한다.
		ShippingInfo shippingInfo = ShippingInfo.from(orderCreateRequestDTO.getShippingInfo());
		shippingInfoValidationService.validateAddress(shippingInfo);
		
		//결제 처리
		//TODO 결제 추후 구현
		paymentService.pay(orderCalculationResult);
		
		//주문 상태를 PENDING 으로 -> 결제상태 `결제완료` 처리한다.
		OrderEntity order = OrderEntity.from(orderCreateRequestDTO, orderCalculationResult);
		order.completePayment();
		
		//DB 주문추가
		OrderEntity savedOrder = orderRepository.save(order);
		
		//TODO 상품 재고 수량을 감소시킨다. - 락이 필요하다.
		foundProducts.forEach(productService::decreaseStock);
		
		//결제 히스토리를 DB에 기록한다.
		//TODO 추후 필요
		
		return OrderResponseDTO.from(savedOrder);
	}
}