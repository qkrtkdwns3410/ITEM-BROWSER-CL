package com.psj.itembrowser.order.domain.vo;

import java.time.LocalDateTime;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.psj.itembrowser.product.domain.vo.Product;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "orders_product_relation")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@ToString
public class OrdersProductRelationEntity {
	/**
	 * 주문그룹ID
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long groupId;

	@Column(name = "product_id")
	private Long productId;

	@Column(name = "product_quantity")
	private Integer productQuantity;

	@Column(name = "created_date")
	private LocalDateTime createdDate;

	@Column(name = "updated_date")
	private LocalDateTime updatedDate;

	@Column(name = "deleted_date")
	private LocalDateTime deletedDate;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "product_id", insertable = false, updatable = false)
	private Product product;

	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
	@JoinColumn(name = "order_entity_id")
	private OrderEntity orderEntity;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "group_id", insertable = false, updatable = false)

	public static OrdersProductRelationEntity of(
		Long groupId,
		Long productId,
		int productQuantity,
		LocalDateTime createdDate,
		LocalDateTime updatedDate,
		LocalDateTime deletedDate,
		Product product
	) {
		OrdersProductRelationEntity ordersProductRelation = new OrdersProductRelationEntity();

		ordersProductRelation.groupId = groupId;
		ordersProductRelation.productId = productId;
		ordersProductRelation.productQuantity = productQuantity;
		ordersProductRelation.createdDate = createdDate;
		ordersProductRelation.updatedDate = updatedDate;
		ordersProductRelation.deletedDate = deletedDate;
		ordersProductRelation.product = product;

		return ordersProductRelation;
	}

	public static OrdersProductRelationEntity of(OrdersProductRelationResponseDTO ordersProductRelationResponseDTO) {
		OrdersProductRelationEntity ordersProductRelation = new OrdersProductRelationEntity();

		ordersProductRelation.groupId = ordersProductRelationResponseDTO.getGroupId();
		ordersProductRelation.productId = ordersProductRelationResponseDTO.getProductId();
		ordersProductRelation.productQuantity = ordersProductRelationResponseDTO.getProductQuantity();
		ordersProductRelation.createdDate = ordersProductRelationResponseDTO.getCreatedDate();
		ordersProductRelation.updatedDate = ordersProductRelationResponseDTO.getUpdatedDate();
		ordersProductRelation.deletedDate = ordersProductRelationResponseDTO.getDeletedDate();

		return ordersProductRelation;
	}
}