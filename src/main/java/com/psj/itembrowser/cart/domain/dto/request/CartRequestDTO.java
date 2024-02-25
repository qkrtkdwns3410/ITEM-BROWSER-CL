package com.psj.itembrowser.cart.domain.dto.request;

import com.psj.itembrowser.cart.domain.vo.Cart;

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
public class CartRequestDTO {
	String userId;
	
	@Builder
	private CartRequestDTO(String userId) {
		this.userId = userId;
	}
	
	public static CartRequestDTO of(String userId) {
		return CartRequestDTO
			.builder()
			.userId(userId)
			.build();
	}
}