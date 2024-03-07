package com.psj.itembrowser.order.domain.dto.request;

import java.time.LocalDateTime;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;

import com.psj.itembrowser.order.domain.entity.OrdersProductRelationEntity;
import com.psj.itembrowser.order.domain.vo.OrdersProductRelation;
import com.psj.itembrowser.product.domain.dto.response.ProductResponseDTO;
import com.psj.itembrowser.security.common.exception.ErrorCode;
import com.psj.itembrowser.security.common.exception.NotFoundException;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO for {@link OrdersProductRelation}
 */
@Setter
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrdersProductRelationRequestDTO {
	
	@NotNull
	private Long groupId;
	
	@NotNull
	private Long productId;
	
	private ProductResponseDTO productResponseDTO;
	
	@NotNull
	@PositiveOrZero
	private Integer productQuantity;
	
	
	LocalDateTime createdDate;
	LocalDateTime updatedDate;
	LocalDateTime deletedDate;
	
	@Builder
	private OrdersProductRelationRequestDTO(Long groupId, Long productId, ProductResponseDTO productResponseDTO, Integer productQuantity,
                                            LocalDateTime createdDate, LocalDateTime updatedDate, LocalDateTime deletedDate) {
		this.groupId = groupId;
		this.productId = productId;
		this.productResponseDTO = productResponseDTO;
		this.productQuantity = productQuantity;
		this.createdDate = createdDate;
		this.updatedDate = updatedDate;
		this.deletedDate = deletedDate;
	}
	
	public static OrdersProductRelationRequestDTO from(OrdersProductRelation ordersProductRelation) {
		if (ordersProductRelation == null) {
			throw new NotFoundException(ErrorCode.ORDER_PRODUCTS_EMPTY);
		}
		
		ProductResponseDTO productResponseDTO = ProductResponseDTO.from(ordersProductRelation.getProduct());
		
		return OrdersProductRelationRequestDTO.builder()
			.groupId(ordersProductRelation.getGroupId())
			.productId(ordersProductRelation.getProductId())
			.productResponseDTO(productResponseDTO)
			.productQuantity(ordersProductRelation.getProductQuantity())
			.createdDate(ordersProductRelation.getCreatedDate())
			.updatedDate(ordersProductRelation.getUpdatedDate())
			.deletedDate(ordersProductRelation.getDeletedDate())
			.build();
	}
	
	public static OrdersProductRelationRequestDTO from(OrdersProductRelationEntity entity) {
		if (entity == null) {
			throw new NotFoundException(ErrorCode.ORDER_PRODUCTS_EMPTY);
		}
		
		ProductResponseDTO productResponseDTO = ProductResponseDTO.from(entity.getProduct());
		
		return OrdersProductRelationRequestDTO.builder()
			.groupId(entity.getGroupId())
			.productId(entity.getProductId())
			.productResponseDTO(productResponseDTO)
			.productQuantity(entity.getProductQuantity())
			.createdDate(entity.getCreatedDate())
			.updatedDate(entity.getUpdatedDate())
			.deletedDate(entity.getDeletedDate())
			.build();
	}
}