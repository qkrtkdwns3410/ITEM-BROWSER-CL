package com.psj.itembrowser.cart.domain.dto.response;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import com.psj.itembrowser.cart.domain.entity.CartEntity;
import com.psj.itembrowser.cart.domain.vo.Cart;
import com.psj.itembrowser.security.common.exception.ErrorCode;
import com.psj.itembrowser.security.common.exception.NotFoundException;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * DTO for {@link Cart}
 */
@Data
@Builder
@NoArgsConstructor
@EqualsAndHashCode(of = {"userId"})
@AllArgsConstructor
public class CartResponseDTO implements Serializable {
	
	String userId;
	
	LocalDateTime createdDate;
	
	LocalDateTime updatedDate;
	
	List<CartProductRelationResponseDTO> products;
	
	public static CartResponseDTO from(Cart cart) {
		if (cart == null) {
			throw new NotFoundException(ErrorCode.CART_NOT_FOUND);
		}
		
		return CartResponseDTO.builder()
			.userId(cart.getUserId())
			.createdDate(cart.getCreatedDate())
			.updatedDate(cart.getUpdatedDate())
			.products(cart.getCartProductRelations()
				.stream()
				.map(CartProductRelationResponseDTO::from)
				.collect(Collectors.toList()))
			.build();
	}
	
	public static CartResponseDTO from(CartEntity entity) {
		if (entity == null) {
			throw new NotFoundException(ErrorCode.CART_NOT_FOUND);
		}
		
		return CartResponseDTO.builder()
			.userId(entity.getUserId())
			.createdDate(entity.getCreatedDate())
			.updatedDate(entity.getUpdatedDate())
			.products(entity.getCartProductRelations()
				.stream()
				.map(CartProductRelationResponseDTO::from)
				.collect(Collectors.toList()))
			.build();
		
	}
}