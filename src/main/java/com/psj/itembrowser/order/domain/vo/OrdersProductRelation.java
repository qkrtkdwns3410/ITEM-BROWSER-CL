package com.psj.itembrowser.order.domain.vo;

import java.time.LocalDateTime;

import com.psj.itembrowser.order.service.impl.OrderCalculationResult;
import com.psj.itembrowser.product.domain.vo.Product;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@EqualsAndHashCode(of = {"groupId", "productId", "productQuantity"})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
public class OrdersProductRelation {
	/**
	 * 주문그룹ID
	 */
	private Long groupId;
	
	/**
	 * 상품ID
	 */
	private Long productId;
	
	/**
	 * 상품수량
	 */
	private Integer productQuantity;
	
	private OrderCalculationResult orderCalculationResult;
	
	private LocalDateTime createdDate;
	
	private LocalDateTime updatedDate;
	
	private LocalDateTime deletedDate;
	
	private Product product;
	
	@Builder
	public OrdersProductRelation(Long groupId, Long productId, Integer productQuantity, OrderCalculationResult orderCalculationResult,
		LocalDateTime createdDate, LocalDateTime updatedDate, LocalDateTime deletedDate, Product product) {
		this.groupId = groupId;
		this.productId = productId;
		this.productQuantity = productQuantity;
		this.orderCalculationResult = orderCalculationResult;
		this.createdDate = createdDate;
		this.updatedDate = updatedDate;
		this.deletedDate = deletedDate;
		this.product = product;
	}
	
	public static OrdersProductRelation of(
		Long groupId,
		Long productId,
		int productQuantity,
		LocalDateTime createdDate,
		LocalDateTime updatedDate,
		LocalDateTime deletedDate,
		Product product
	) {
		OrdersProductRelation ordersProductRelation = new OrdersProductRelation();
		
		ordersProductRelation.groupId = groupId;
		ordersProductRelation.productId = productId;
		ordersProductRelation.productQuantity = productQuantity;
		ordersProductRelation.createdDate = createdDate;
		ordersProductRelation.updatedDate = updatedDate;
		ordersProductRelation.deletedDate = deletedDate;
		ordersProductRelation.product = product;
		
		return ordersProductRelation;
	}
	
	public static OrdersProductRelation of(OrdersProductRelationResponseDTO ordersProductRelationResponseDTO) {
		OrdersProductRelation ordersProductRelation = new OrdersProductRelation();
		
		ordersProductRelation.groupId = ordersProductRelationResponseDTO.getGroupId();
		ordersProductRelation.productId = ordersProductRelationResponseDTO.getProductId();
		ordersProductRelation.productQuantity = ordersProductRelationResponseDTO.getProductQuantity();
		ordersProductRelation.createdDate = ordersProductRelationResponseDTO.getCreatedDate();
		ordersProductRelation.updatedDate = ordersProductRelationResponseDTO.getUpdatedDate();
		ordersProductRelation.deletedDate = ordersProductRelationResponseDTO.getDeletedDate();
		
		return ordersProductRelation;
	}
}