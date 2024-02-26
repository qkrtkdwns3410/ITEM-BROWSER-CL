package com.psj.itembrowser.order.domain.entity;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.psj.itembrowser.order.domain.vo.OrderStatus;
import com.psj.itembrowser.order.domain.vo.PaymentStatus;
import com.psj.itembrowser.security.common.exception.BadRequestException;
import com.psj.itembrowser.security.common.exception.ErrorCode;

class OrderEntityTest {
	private OrderEntity orderEntity;
	private static final LocalDateTime NOW = LocalDateTime.now();
	
	@BeforeEach
	void setUp() {
		orderEntity = OrderEntity.builder()
			.orderStatus(OrderStatus.PENDING)
			.paymentStatus(PaymentStatus.PENDING)
			.build();
	}
	
	@Test
	@DisplayName("결제 완료 시 주문 상태가 완료로 변경")
	void When_CompletePayment_Expect_OrderStatusIsComplete() {
		// when
		orderEntity.completePayment();
		
		// then
		assertThat(orderEntity.getPaymentStatus()).isEqualTo(PaymentStatus.COMPLETE);
		assertThat(orderEntity.getPaidDate()).isAfter(NOW);
	}
	
	@Test
	@DisplayName("결제 완료 시 주문 상태가 완료가 아니면 BadRequestException")
	void When_CompletePaymentAndPaymentStatusIsComplete_Expect_BadRequestException() {
		// given
		orderEntity = OrderEntity.builder()
			.orderStatus(OrderStatus.PENDING)
			.paymentStatus(PaymentStatus.COMPLETE)
			.build();
		
		//when
		assertThatThrownBy(orderEntity::completePayment)
			//then
			.isInstanceOf(BadRequestException.class)
			.hasMessage(ErrorCode.ALREADY_COMPLETE_PAYMENT.getMessage());
	}
}