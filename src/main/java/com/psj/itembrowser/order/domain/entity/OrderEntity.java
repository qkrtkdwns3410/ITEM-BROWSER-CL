package com.psj.itembrowser.order.domain.entity;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
import javax.validation.constraints.Size;

import com.psj.itembrowser.member.domain.entity.MemberEntity;
import com.psj.itembrowser.order.domain.vo.OrderStatus;
import com.psj.itembrowser.shippingInfos.domain.entity.ShippingInfoEntity;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "orders")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class OrderEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID", nullable = false)
	private Long id;

	@Size(max = 200)
	@Enumerated(EnumType.STRING)
	@Column(name = "ORDER_STATUS", length = 200)
	private OrderStatus orderStatus;

	@Column(name = "PAID_DATE")
	private Instant paidDate;

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

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "order")
	private List<OrdersProductRelationEntity> ordersProductRelationEntity = new ArrayList<>();
}