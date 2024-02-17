package com.psj.itembrowser.order.domain.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.psj.itembrowser.product.domain.entity.ProductEntity;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@IdClass(OrdersProductRelationEntity.OrdersProductRelationEntityId.class)
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "orders_product_relation")
public class OrdersProductRelationEntity {

	@Id
	@Column(name = "GROUP_ID", nullable = false)
	private Long groupId;

	@Id
	@Column(name = "PRODUCT_ID", nullable = false)
	private Long productId;

	@Column(name = "PRODUCT_QUANTITY")
	private Integer productQuantity;

	@Column(name = "CREATED_DATE")
	private LocalDateTime createdDate;

	@Column(name = "UPDATED_DATE")
	private LocalDateTime updatedDate;

	@Column(name = "DELETED_DATE")
	private LocalDateTime deletedDate;

	@OneToOne(mappedBy = "ordersProductRelationEntity")
	private OrderEntity order;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "PRODUCT_ID", nullable = false)
	private ProductEntity product;

	@AllArgsConstructor
	@EqualsAndHashCode
	public static class OrdersProductRelationEntityId implements Serializable {
		private Long groupId;
		private Long productId;
	}
}