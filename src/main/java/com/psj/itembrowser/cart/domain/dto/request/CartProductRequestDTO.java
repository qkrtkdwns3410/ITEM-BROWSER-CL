package com.psj.itembrowser.cart.domain.dto.request;

import javax.validation.constraints.PositiveOrZero;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CartProductRequestDTO {
	
	private Long cartId;
	
	private Long productId;
	
	private String userId;
	
	@PositiveOrZero
	private long quantity;
	
	@Builder
	private CartProductRequestDTO(Long cartId, Long productId, String userId, long quantity) {
		this.cartId = cartId;
		this.productId = productId;
		this.userId = userId;
		this.quantity = quantity;
	}
}