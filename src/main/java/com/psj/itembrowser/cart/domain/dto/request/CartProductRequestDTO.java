package com.psj.itembrowser.cart.domain.dto.request;

import javax.validation.constraints.PositiveOrZero;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CartProductRequestDTO {
	Long cartId;
	
	Long productId;
	
	String userId;
	
	@PositiveOrZero
	long quantity;
	
	@Builder
	private CartProductRequestDTO(Long cartId, Long productId, String userId, long quantity) {
		this.cartId = cartId;
		this.productId = productId;
		this.userId = userId;
		this.quantity = quantity;
	}
}