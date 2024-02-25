package com.psj.itembrowser.product.domain.dto.request;

import com.psj.itembrowser.product.domain.vo.Product;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO for {@link Product}
 */
@Getter
@Setter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class ProductQuantityUpdateRequestDTO {
	
	private Long id;
	private Integer quantity;
	
	@Builder
	private ProductQuantityUpdateRequestDTO(Long id, Integer quantity) {
		this.id = id;
		this.quantity = quantity;
	}
}