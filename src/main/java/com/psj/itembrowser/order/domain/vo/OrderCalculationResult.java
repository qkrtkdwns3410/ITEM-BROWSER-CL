package com.psj.itembrowser.order.domain.vo;

import java.io.Serializable;

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
	private final Long totalPrice;
	private final Long totalDiscount;
	private final Long shippingFee;
	private final Long totalNetPrice;
	
	@Builder
	private OrderCalculationResult(Long totalPrice, Long totalDiscount, Long shippingFee, Long totalNetPrice) {
		validateTotalPrice(totalPrice);
		validateTotalDiscount(totalDiscount);
		validateShippingFee(shippingFee);
		validateOrderTotal(totalNetPrice);
		
		this.totalPrice = totalPrice;
		this.totalDiscount = totalDiscount;
		this.shippingFee = shippingFee;
		this.totalNetPrice = totalNetPrice;
	}
	
	public static OrderCalculationResult of(Long totalPrice, Long totalDiscount, Long shippingFee, Long totalNetPrice) {
		return OrderCalculationResult.builder()
			.totalPrice(totalPrice)
			.totalDiscount(totalDiscount)
			.shippingFee(shippingFee)
			.totalNetPrice(totalNetPrice)
			.build();
	}
	
	private void validateTotalPrice(Long totalPrice) {
		if (totalPrice == null || totalPrice < 0) {
			throw new ArgumentValidationExpception(ErrorCode.ORDER_VALIDATION_FAIL);
		}
	}
	
	private void validateTotalDiscount(Long totalDiscount) {
		if (totalDiscount == null || totalDiscount < 0) {
			throw new ArgumentValidationExpception(ErrorCode.ORDER_VALIDATION_FAIL);
		}
	}
	
	private void validateShippingFee(Long shippingFee) {
		if (shippingFee == null || shippingFee < 0) {
			throw new ArgumentValidationExpception(ErrorCode.ORDER_VALIDATION_FAIL);
		}
	}
	
	private void validateOrderTotal(Long orderTotal) {
		if (orderTotal == null || orderTotal < 0) {
			throw new ArgumentValidationExpception(ErrorCode.ORDER_VALIDATION_FAIL);
		}
	}
}