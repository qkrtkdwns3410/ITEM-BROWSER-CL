package com.psj.itembrowser.cart.domain.dto.request;

import javax.validation.constraints.NotNull;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CartProductDeleteRequestDTO {
	@NotNull
	Long cartId;
	
	@NotNull
	Long productId;
	
	@Builder
	private CartProductDeleteRequestDTO(Long cartId, Long productId) {
		this.cartId = cartId;
		this.productId = productId;
	}
}