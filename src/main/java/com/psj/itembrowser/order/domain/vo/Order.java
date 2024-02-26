package com.psj.itembrowser.order.domain.vo;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import com.psj.itembrowser.member.domain.vo.Member;
import com.psj.itembrowser.order.domain.dto.request.OrderCreateRequestDTO;
import com.psj.itembrowser.order.domain.state.Cancelable;
import com.psj.itembrowser.security.common.exception.BadRequestException;
import com.psj.itembrowser.security.common.exception.ErrorCode;
import com.psj.itembrowser.shippingInfos.domain.vo.ShippingInfo;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
//TODO 엔티티로 전부 변경후 제거될 예정

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@EqualsAndHashCode(of = {"id", "ordererNumber", "orderStatus"})
@ToString
public class Order implements Cancelable {
	private Long id;
	private Long ordererNumber;
	private OrderStatus orderStatus = OrderStatus.PENDING;
	private PaymentStatus paymentStatus = PaymentStatus.PENDING;
	private LocalDateTime paidDate;
	private Long shippingInfoId;
	private LocalDateTime createdDate;
	private LocalDateTime updatedDate;
	private LocalDateTime deletedDate;
	private List<OrdersProductRelation> products;
	private Member member;
	private ShippingInfo shippingInfo;
	private OrderCalculationResult orderCalculationResult;
	
	public static Order of(
		Long id, Long ordererNumber, OrderStatus orderStatus, LocalDateTime paidDate,
		Long shippingInfoId, LocalDateTime createdDate, LocalDateTime updatedDate,
		LocalDateTime deletedDate, List<OrdersProductRelation> products, Member member,
		ShippingInfo shippingInfo
	) {
		Order order = new Order();
		
		order.id = id;
		order.ordererNumber = ordererNumber;
		order.orderStatus = orderStatus;
		order.paidDate = paidDate;
		order.shippingInfoId = shippingInfoId;
		order.createdDate = createdDate;
		order.updatedDate = updatedDate;
		order.deletedDate = deletedDate;
		order.products = products;
		order.member = member;
		order.shippingInfo = shippingInfo;
		
		return order;
	}
	
	public static Order of(@Valid OrderCreateRequestDTO orderCreateRequestDTO,
		OrderCalculationResult orderCalculationResult) {
		Order order = new Order();
		
		order.ordererNumber = orderCreateRequestDTO.getOrdererNumber();
		order.orderStatus = OrderStatus.PENDING;
		order.shippingInfoId = orderCreateRequestDTO.getShippingInfo().getId();
		order.products = orderCreateRequestDTO.getProducts().stream()
			.map(OrdersProductRelation::of)
			.collect(Collectors.toList());
		order.member = Member.from(orderCreateRequestDTO.getMember());
		order.shippingInfo = ShippingInfo.from(orderCreateRequestDTO.getShippingInfo());
		order.orderCalculationResult = orderCalculationResult;
		
		return order;
	}
	
	@Override
	public boolean isNotCancelable() {
		List<OrderStatus> cancelableStatus = List.of(OrderStatus.PENDING, OrderStatus.ACCEPT, OrderStatus.INSTRUCT);
		
		return cancelableStatus.stream()
			.noneMatch(orderStatus::equals);
	}
	
	//결제완료 처리한다.
	public void completePayment() {
		if (this.paymentStatus == PaymentStatus.COMPLETE) {
			throw new BadRequestException(ErrorCode.ALREADY_COMPLETE_PAYMENT);
		}
		
		this.paymentStatus = PaymentStatus.COMPLETE;
		this.paidDate = LocalDateTime.now();
	}
	
}