package com.psj.itembrowser.order.domain.vo;

import java.time.LocalDateTime;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;

import com.psj.itembrowser.product.domain.dto.response.ProductResponseDTO;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for {@link OrdersProductRelation}
 */
@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class OrdersProductRelationResponseDTO {

	@NotNull
	Long groupId;

	@NotNull
	Long productId;

	ProductResponseDTO productResponseDTO;

	@NotNull
	@PositiveOrZero
	Integer productQuantity;

	LocalDateTime createdDate;
	LocalDateTime updatedDate;
	LocalDateTime deletedDate;

	public static OrdersProductRelationResponseDTO from(OrdersProductRelation ordersProductRelation) {
		if (ordersProductRelation == null) {
			return null;
		}

		ProductResponseDTO productResponseDTO = ProductResponseDTO.from(ordersProductRelation.getProduct());

		return new OrdersProductRelationResponseDTO(
			ordersProductRelation.getGroupId(),
			ordersProductRelation.getProductId(),
			productResponseDTO,
			ordersProductRelation.getProductQuantity(),
			ordersProductRelation.getCreatedDate(),
			ordersProductRelation.getUpdatedDate(),
			ordersProductRelation.getDeletedDate()
		);
	}
}