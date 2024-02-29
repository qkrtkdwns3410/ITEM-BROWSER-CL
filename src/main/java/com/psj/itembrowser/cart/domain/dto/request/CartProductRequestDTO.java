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
	
	private String email;
	
	@PositiveOrZero
	private long quantity;
	
	@Builder
	private CartProductRequestDTO(Long cartId, Long productId, String email, long quantity) {
		this.cartId = cartId;
		this.productId = productId;
		this.email = email;
		this.quantity = quantity;
	}
}