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
import javax.persistence.Table;

import com.psj.itembrowser.order.domain.vo.OrdersProductRelation;
import com.psj.itembrowser.product.domain.entity.ProductEntity;
import com.psj.itembrowser.security.common.BaseDateTimeEntity;
import com.psj.itembrowser.security.common.exception.ErrorCode;
import com.psj.itembrowser.security.common.exception.NotFoundException;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@IdClass(OrdersProductRelationEntity.OrdersProductRelationEntityId.class)
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "orders_product_relation")
public class OrdersProductRelationEntity extends BaseDateTimeEntity {

	@Id
	@Column(name = "GROUP_ID", nullable = false)
	private Long groupId;

	@Id
	@Column(name = "PRODUCT_ID", nullable = false)
	private Long productId;

	@Column(name = "PRODUCT_QUANTITY")
	private Integer productQuantity;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "GROUP_ID", nullable = false, insertable = false, updatable = false, referencedColumnName = "ID")
	private OrderEntity order;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "PRODUCT_ID", nullable = false, insertable = false, updatable = false, referencedColumnName = "ID")
	private ProductEntity product;

	@NoArgsConstructor(access = AccessLevel.PROTECTED)
	@AllArgsConstructor
	@EqualsAndHashCode
	public static class OrdersProductRelationEntityId implements Serializable {
		private Long groupId;
		private Long productId;
	}

	@Builder
	private OrdersProductRelationEntity(Long groupId, Long productId, Integer productQuantity,
		LocalDateTime deletedDate, OrderEntity order, ProductEntity product) {
		this.groupId = groupId;
		this.productId = productId;
		this.productQuantity = productQuantity;
		this.order = order;
		this.product = product;
		this.deletedDate = deletedDate;
	}

	public static OrdersProductRelationEntity from(OrdersProductRelation ordersProductRelation) {
		if (ordersProductRelation == null) {
			throw new NotFoundException(ErrorCode.ORDER_PRODUCTS_EMPTY);
		}

		return OrdersProductRelationEntity.builder()
			.groupId(ordersProductRelation.getGroupId())
			.productId(ordersProductRelation.getProductId())
			.productQuantity(ordersProductRelation.getProductQuantity())
			.product(ProductEntity.from(ordersProductRelation.getProduct()))
			.build();
	}

}