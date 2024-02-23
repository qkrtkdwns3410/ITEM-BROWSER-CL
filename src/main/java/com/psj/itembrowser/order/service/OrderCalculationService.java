package com.psj.itembrowser.order.service;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.psj.itembrowser.discount.service.PercentageDiscountService;
import com.psj.itembrowser.member.domain.entity.MemberEntity;
import com.psj.itembrowser.order.domain.dto.request.OrderCreateRequestDTO;
import com.psj.itembrowser.order.domain.vo.OrdersProductRelationResponseDTO;
import com.psj.itembrowser.product.domain.entity.ProductEntity;
import com.psj.itembrowser.product.service.ProductService;
import com.psj.itembrowser.security.common.exception.BadRequestException;
import com.psj.itembrowser.security.common.exception.ErrorCode;
import com.psj.itembrowser.shippingInfos.service.ShippingPolicyService;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * packageName    : com.psj.itembrowser.order.service.impl fileName       : OrderCalculationService author         : ipeac date           : 2024-02-12 description    : =========================================================== DATE              AUTHOR             NOTE ----------------------------------------------------------- 2024-02-12        ipeac       최초 생성
 */
@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderCalculationService {
	
	private final ProductService productService;
	private final PercentageDiscountService percentageDiscountService;
	private final ShippingPolicyService shippingPolicyService;
	
	public OrderCalculationResult calculateOrderDetails(@NonNull OrderCreateRequestDTO orderCreateRequestDTO, @NonNull MemberEntity member) {
		validateOrderProduct(orderCreateRequestDTO);
		
		BigDecimal totalPrice = BigDecimal.ZERO;
		BigDecimal totalDiscount = BigDecimal.ZERO;
		long shippingFee = 0;
		
		for (OrdersProductRelationResponseDTO ordersProductRelationResponseDTO : orderCreateRequestDTO.getProducts()) {
			ProductEntity product = ProductEntity.from(productService.getProduct(ordersProductRelationResponseDTO.getProductId()));
			
			BigDecimal productPrice = product.calculateTotalPrice();
			
			totalPrice = totalPrice.add(productPrice);
			
			BigDecimal discount = percentageDiscountService.calculateDiscount(product, member);
			
			totalDiscount = totalDiscount.add(discount);
		}
		
		shippingFee = shippingPolicyService.getCurrentShippingPolicy().calculateShippingFee(totalPrice).getFee();
		
		BigDecimal totalNetPrice = totalPrice.add(totalDiscount).add(BigDecimal.valueOf(shippingFee));
		
		return OrderCalculationResult.of(totalPrice, totalDiscount, shippingFee, totalNetPrice);
	}
	
	private static void validateOrderProduct(OrderCreateRequestDTO orderCreateRequestDTO) {
		if (orderCreateRequestDTO.getProducts().isEmpty()) {
			throw new BadRequestException(ErrorCode.ORDER_PRODUCTS_EMPTY);
		}
	}
}