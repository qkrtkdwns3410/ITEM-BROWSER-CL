package com.psj.itembrowser.order.domain.vo;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.Valid;

import com.psj.itembrowser.member.domain.vo.Member;
import com.psj.itembrowser.order.controller.OrderCreateRequestDTO;
import com.psj.itembrowser.order.service.impl.OrderCalculationResult;
import com.psj.itembrowser.security.common.exception.BadRequestException;
import com.psj.itembrowser.security.common.exception.ErrorCode;
import com.psj.itembrowser.shippingInfos.domain.vo.ShippingInfo;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.ToString;

@Entity
@Table(name = "orders")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@ToString
public class OrderEntity implements Cancelable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "orderer_number")
	private Long ordererNumber;

	@Enumerated(EnumType.STRING)
	private OrderStatus orderStatus = OrderStatus.PENDING;

	@Enumerated(EnumType.STRING)
	private PaymentStatus paymentStatus = PaymentStatus.PENDING;

	@Column(name = "paid_date")
	private LocalDateTime paidDate;

	@Column(name = "shipping_info_id")
	private Long shippingInfoId;

	@Column(name = "created_date")
	private LocalDateTime createdDate;

	@Column(name = "updated_date")
	private LocalDateTime updatedDate;

	@Column(name = "deleted_date")
	private LocalDateTime deletedDate;

	@OneToMany(mappedBy = "order", fetch = FetchType.LAZY)
	private List<OrdersProductRelationEntity> products;

	@OneToOne(mappedBy = "order")
	private Member member;

	@OneToOne(mappedBy = "order")
	private ShippingInfo shippingInfo;

	private OrderCalculationResult orderCalculationResult;

	@OneToMany(mappedBy = "orderEntity", cascade = CascadeType.PERSIST, orphanRemoval = true)
	private List<OrdersProductRelationEntity> ordersProductRelationEntities = new ArrayList<>();

	public void setOrdersProductRelationEntities(List<OrdersProductRelationEntity> ordersProductRelationEntities) {
		this.ordersProductRelationEntities = ordersProductRelationEntities;
	}

	public static OrderEntity of(
		Long id, Long ordererNumber, OrderStatus orderStatus, LocalDateTime paidDate,
		Long shippingInfoId, LocalDateTime createdDate, LocalDateTime updatedDate,
		LocalDateTime deletedDate, List<OrdersProductRelation> products, Member member,
		ShippingInfo shippingInfo
	) {
		OrderEntity order = new OrderEntity();

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

	public static OrderEntity of(@Valid OrderCreateRequestDTO orderCreateRequestDTO,
		OrderCalculationResult orderCalculationResult) {
		OrderEntity order = new OrderEntity();

		order.ordererNumber = orderCreateRequestDTO.getOrdererNumber();
		order.orderStatus = OrderStatus.PENDING;
		order.shippingInfoId = orderCreateRequestDTO.getShippingInfo().getId();
		order.createdDate = orderCreateRequestDTO.getCreatedDate();
		order.updatedDate = orderCreateRequestDTO.getCreatedDate();
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
		List<OrderStatus> cancelableStatus = List.of(OrderStatus.PENDING, OrderStatus.ACCEPT,
			OrderStatus.INSTRUCT);
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

	@Getter
	public enum PaymentStatus {
		PENDING("PENDING"), // 결제 대기중

		COMPLETE("COMPLETE"), // 결제 완료

		CANCELED("CANCELED"), // 결제 취소됨
		;

		private final String name;
		private final String value;

		PaymentStatus(@NonNull String value) {
			this.value = value;
			this.name = name();
		}

		public static PaymentStatus of(@NonNull String value) {
			for (PaymentStatus paymentStatus : PaymentStatus.values()) {
				if (Objects.equals(
					paymentStatus.getValue(), value)) {
					return paymentStatus;
				}
			}
			throw new IllegalArgumentException("결제 상태가 존재하지 않습니다.");
		}

	}

	@Getter
	public enum OrderStatus {
		PENDING("PENDING"), // 주문 대기중

		ACCEPT("ACCEPT"), // 주문 접수됨

		INSTRUCT("INSTRUCT"), // 	상품 준비중

		DEPARTURE("DEPARTURE"), // 배송 지시

		DELIVERING("DELIVERING"), // 배송중

		FINAL_DELIVERY("FINAL_DELIVERY"), // 배송 완료

		NONE_TRACKING("NONE_TRACKING"), // 업체 직접 배송

		CANCELED("CANCELED"), // 주문 취소됨
		;

		private final String name;
		private final String value;

		OrderStatus(@NonNull String value) {
			this.value = value;
			this.name = name();
		}

		public static OrderStatus of(@NonNull String value) {
			for (OrderStatus orderStatus : OrderStatus.values()) {
				if (Objects.equals(
					orderStatus.getValue(), value)) {
					return orderStatus;
				}
			}
			throw new IllegalArgumentException("주문 상태가 존재하지 않습니다.");
		}
	}
}