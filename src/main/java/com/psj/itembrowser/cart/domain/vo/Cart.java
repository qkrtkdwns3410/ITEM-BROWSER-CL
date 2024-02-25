package com.psj.itembrowser.cart.domain.vo;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@EqualsAndHashCode(of = {"id", "userId"}, callSuper = false)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Cart {
	
	/**
	 * pk값
	 */
	private Long id;
	
	/**
	 * 유저ID
	 */
	private String userId;
	
	private List<CartProductRelation> cartProductRelations;
	
	private LocalDateTime createdDate;
	
	private LocalDateTime updatedDate;
	
	private LocalDateTime deletedDate;
	
	@Builder
	private Cart(LocalDateTime createdDate, LocalDateTime updatedDate, LocalDateTime deletedDate, Long id, String userId,
		List<CartProductRelation> cartProductRelations) {
		this.id = id;
		this.userId = userId;
		this.cartProductRelations = cartProductRelations == null ? new ArrayList<>() : cartProductRelations;
		this.createdDate = createdDate;
		this.updatedDate = updatedDate;
		this.deletedDate = deletedDate;
	}
}