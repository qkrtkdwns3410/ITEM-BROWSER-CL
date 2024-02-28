package com.psj.itembrowser.cart.domain.dto.request;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;

import com.psj.itembrowser.cart.domain.vo.CartProductRelation;
import com.psj.itembrowser.security.common.exception.ErrorCode;
import com.psj.itembrowser.security.common.exception.NotFoundException;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CartProductUpdateRequestDTO {
	@NotNull
	private Long cartId;
	
	@NotNull
	private Long productId;
	
	@PositiveOrZero
	private long quantity;
	
	@Builder
	private CartProductUpdateRequestDTO(Long cartId, Long productId, long quantity) {
		this.cartId = cartId;
		this.productId = productId;
		this.quantity = quantity;
	}
	
	public static CartProductUpdateRequestDTO from(CartProductRelation vo) {
		if (vo == null) {
			throw new NotFoundException(ErrorCode.CART_PRODUCT_NOT_FOUND);
		}
		
		return CartProductUpdateRequestDTO.builder()
			.cartId(vo.getCartId())
			.productId(vo.getProductId())
			.quantity(vo.getProductQuantity())
			.build();
	}
}