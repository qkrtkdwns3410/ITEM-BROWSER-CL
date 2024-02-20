package com.psj.itembrowser.order.domain.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.psj.itembrowser.member.domain.entity.MemberEntity;
import com.psj.itembrowser.order.domain.vo.Order;
import com.psj.itembrowser.order.domain.vo.OrderStatus;
import com.psj.itembrowser.order.service.impl.OrderCalculationResult;
import com.psj.itembrowser.shippingInfos.domain.entity.ShippingInfoEntity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "orders")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID", nullable = false)
	private Long id;

	@Enumerated(EnumType.STRING)
	@Column(name = "ORDER_STATUS", length = 200)
	private OrderStatus orderStatus;

	@Column(name = "PAID_DATE")
	private LocalDateTime paidDate;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "SHIPPING_INFO_ID", referencedColumnName = "ID")
	private ShippingInfoEntity shippingInfo;

	@Column(name = "CREATED_DATE")
	private LocalDateTime createdDate;

	@Column(name = "UPDATED_DATE")
	private LocalDateTime updatedDate;

	@Column(name = "DELETED_DATE")
	private LocalDateTime deletedDate;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ORDERER_NUMBER", referencedColumnName = "MEMBER_NO")
	private MemberEntity member;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "order", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
	private List<OrdersProductRelationEntity> ordersProductRelations = new ArrayList<>();

	@Transient
	private OrderCalculationResult orderCalculationResult;

	@Builder
	private OrderEntity(Long id, OrderStatus orderStatus, LocalDateTime paidDate, ShippingInfoEntity shippingInfo, LocalDateTime createdDate, LocalDateTime updatedDate,
		LocalDateTime deletedDate, MemberEntity member, List<OrdersProductRelationEntity> ordersProductRelations, OrderCalculationResult orderCalculationResult) {
		this.id = id;
		this.orderStatus = orderStatus;
		this.paidDate = paidDate;
		this.shippingInfo = shippingInfo;
		this.createdDate = createdDate;
		this.updatedDate = updatedDate;
		this.deletedDate = deletedDate;
		this.member = member;
		this.ordersProductRelations = ordersProductRelations;
		this.orderCalculationResult = orderCalculationResult;
	}

	public static OrderEntity from(Order order) {
		OrderEntity orderEntity = new OrderEntity();

		orderEntity.id = order.getId();
		orderEntity.orderStatus = order.getOrderStatus();
		orderEntity.paidDate = order.getPaidDate();
		orderEntity.shippingInfo = ShippingInfoEntity.from(order.getShippingInfo());
		orderEntity.createdDate = order.getCreatedDate();
		orderEntity.updatedDate = order.getUpdatedDate();
		orderEntity.deletedDate = order.getDeletedDate();
		orderEntity.member = MemberEntity.from(order.getMember(), null, null);
		orderEntity.ordersProductRelations = order.getProducts().stream()
			.map(OrdersProductRelationEntity::from)
			.collect(Collectors.toList());

		return orderEntity;
	}
}