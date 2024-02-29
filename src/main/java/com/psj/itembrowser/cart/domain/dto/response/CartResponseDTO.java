package com.psj.itembrowser.cart.domain.dto.response;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.psj.itembrowser.cart.domain.entity.CartEntity;
import com.psj.itembrowser.cart.domain.vo.Cart;
import com.psj.itembrowser.security.common.exception.ErrorCode;
import com.psj.itembrowser.security.common.exception.NotFoundException;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO for {@link Cart}
 */
@Setter
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CartResponseDTO implements Serializable {
	
	Long cartId;
	
	String userId;
	
	LocalDateTime createdDate;
	
	LocalDateTime updatedDate;
	
	List<CartProductRelationResponseDTO> products = new ArrayList<>();
	
	@Builder
	private CartResponseDTO(Long cartId, String userId, LocalDateTime createdDate, LocalDateTime updatedDate,
		List<CartProductRelationResponseDTO> products) {
		this.cartId = cartId;
		this.userId = userId;
		this.createdDate = createdDate;
		this.updatedDate = updatedDate;
		this.products = products == null ? new ArrayList<>() : products;
	}
	
	public static CartResponseDTO from(Cart cart) {
		if (cart == null) {
			throw new NotFoundException(ErrorCode.CART_NOT_FOUND);
		}
		
		return CartResponseDTO.builder()
			.cartId(cart.getId())
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
			.cartId(entity.getId())
			.userId(entity.getUserEmail())
			.createdDate(entity.getCreatedDate())
			.updatedDate(entity.getUpdatedDate())
			.products(entity.getCartProductRelations()
				.stream()
				.map(CartProductRelationResponseDTO::from)
				.collect(Collectors.toList()))
			.build();
		
	}
}