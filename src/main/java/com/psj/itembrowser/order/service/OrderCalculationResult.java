package com.psj.itembrowser.order.service;

import java.io.Serializable;
import java.math.BigDecimal;

import com.psj.itembrowser.security.common.exception.ArgumentValidationExpception;
import com.psj.itembrowser.security.common.exception.ErrorCode;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 *packageName    : com.psj.itembrowser.order.service.impl
 * fileName       : OrderCalculationResult
 * author         : ipeac
 * date           : 2024-02-12
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2024-02-12        ipeac       최초 생성
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED, force = true)
public final class OrderCalculationResult implements Serializable {
	private final BigDecimal totalPrice;
	private final BigDecimal totalDiscount;
	private final Long shippingFee;
	private final BigDecimal totalNetPrice;
	
	@Builder
	private OrderCalculationResult(BigDecimal totalPrice, BigDecimal totalDiscount, Long shippingFee, BigDecimal totalNetPrice) {
		validateTotalPrice(totalPrice);
		validateTotalDiscount(totalDiscount);
		validateShippingFee(BigDecimal.valueOf(shippingFee));
		validateOrderTotal(totalNetPrice);
		
		this.totalPrice = totalPrice;
		this.totalDiscount = totalDiscount;
		this.shippingFee = shippingFee;
		this.totalNetPrice = totalNetPrice;
	}
	
	public static OrderCalculationResult of(BigDecimal totalPrice, BigDecimal totalDiscount, Long shippingFee, BigDecimal totalNetPrice) {
		return OrderCalculationResult.builder()
			.totalPrice(totalPrice)
			.totalDiscount(totalDiscount)
			.shippingFee(shippingFee)
			.totalNetPrice(totalNetPrice)
			.build();
	}
	
	private void validateTotalPrice(BigDecimal totalPrice) {
		if (totalPrice == null || totalPrice.compareTo(BigDecimal.ZERO) < 0) {
			throw new ArgumentValidationExpception(ErrorCode.ORDER_VALIDATION_FAIL);
		}
	}
	
	private void validateTotalDiscount(BigDecimal totalDiscount) {
		if (totalDiscount == null || totalDiscount.compareTo(BigDecimal.ZERO) < 0) {
			throw new ArgumentValidationExpception(ErrorCode.ORDER_VALIDATION_FAIL);
		}
	}
	
	private void validateShippingFee(BigDecimal shippingFee) {
		if (shippingFee == null || shippingFee.compareTo(BigDecimal.ZERO) < 0) {
			throw new ArgumentValidationExpception(ErrorCode.ORDER_VALIDATION_FAIL);
		}
	}
	
	private void validateOrderTotal(BigDecimal orderTotal) {
		if (orderTotal == null || orderTotal.compareTo(BigDecimal.ZERO) < 0) {
			throw new ArgumentValidationExpception(ErrorCode.ORDER_VALIDATION_FAIL);
		}
	}
}