package com.psj.itembrowser.order.domain.vo;

import java.time.LocalDateTime;

import com.psj.itembrowser.product.domain.vo.Product;
import com.psj.itembrowser.security.common.exception.ErrorCode;
import com.psj.itembrowser.security.common.exception.NotFoundException;

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
	private OrdersProductRelation(Long groupId, Long productId, Integer productQuantity, OrderCalculationResult orderCalculationResult,
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
		return OrdersProductRelation.builder()
			.groupId(groupId)
			.productId(productId)
			.productQuantity(productQuantity)
			.createdDate(createdDate)
			.updatedDate(updatedDate)
			.deletedDate(deletedDate)
			.product(product)
			.build();
	}
	
	public static OrdersProductRelation of(OrdersProductRelationResponseDTO ordersProductRelationResponseDTO) {
		if (ordersProductRelationResponseDTO == null) {
			throw new NotFoundException(ErrorCode.ORDER_PRODUCTS_EMPTY);
		}
		
		return OrdersProductRelation.builder()
			.groupId(ordersProductRelationResponseDTO.getGroupId())
			.productId(ordersProductRelationResponseDTO.getProductId())
			.productQuantity(ordersProductRelationResponseDTO.getProductQuantity())
			.createdDate(ordersProductRelationResponseDTO.getCreatedDate())
			.updatedDate(ordersProductRelationResponseDTO.getUpdatedDate())
			.deletedDate(ordersProductRelationResponseDTO.getDeletedDate())
			.product(Product.from(ordersProductRelationResponseDTO.getProductResponseDTO()))
			.build();
	}
}