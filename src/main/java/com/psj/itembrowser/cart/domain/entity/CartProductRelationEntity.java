package com.psj.itembrowser.cart.domain.entity;

import static com.psj.itembrowser.security.common.exception.ErrorCode.*;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.psj.itembrowser.cart.domain.dto.response.CartProductRelationResponseDTO;
import com.psj.itembrowser.security.common.BaseDateTimeEntity;
import com.psj.itembrowser.security.common.exception.DatabaseOperationException;
import com.psj.itembrowser.security.common.exception.ErrorCode;
import com.psj.itembrowser.security.common.exception.NotFoundException;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@IdClass(CartProductRelationEntity.CartProductRelationEntityId.class)
@Entity
@Table(name = "cart_product_relation")
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class CartProductRelationEntity extends BaseDateTimeEntity {
	@Id
	@Column(name = "cart_id")
	private Long cartId;
	
	@Id
	@Column(name = "product_id")
	private Long productId;
	
	@Column(name = "product_quantity")
	private Long productQuantity;
	
	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
	@JoinColumn(name = "CART_ID", insertable = false, updatable = false, referencedColumnName = "ID")
	private CartEntity cartEntity;
	
	@Builder
	private CartProductRelationEntity(Long cartId, Long productId, Long productQuantity, CartEntity cartEntity, LocalDateTime deletedDate) {
		this.cartId = cartId;
		this.productId = productId;
		this.productQuantity = productQuantity;
		this.cartEntity = cartEntity;
		this.deletedDate = deletedDate;
	}
	
	public static CartProductRelationEntity from(CartProductRelationResponseDTO dto) {
		if (dto == null) {
			throw new NotFoundException(ErrorCode.CART_PRODUCT_RELATION_NOT_FOUND);
		}
		
		return CartProductRelationEntity.builder()
			.cartId(dto.getCartId())
			.productId(dto.getProductId())
			.productQuantity(dto.getProductQuantity())
			.build();
	}
	
	public void addProductQuantity(long quantity) {
		if (quantity < 0) {
			throw new DatabaseOperationException(CART_PRODUCT_QUANTITY_NOT_POSITIVE);
		}
		
		this.productQuantity += quantity;
	}
	
	public void setCartEntity(CartEntity cartEntity) {
		this.cartEntity = cartEntity;
		
		if (!cartEntity.getCartProductRelations().contains(this)) {
			cartEntity.getCartProductRelations().add(this);
		}
	}
	
	@NoArgsConstructor
	@EqualsAndHashCode
	public static class CartProductRelationEntityId implements Serializable {
		private Long cartId;
		private Long productId;
		
		public CartProductRelationEntityId(Long cartId, Long productId) {
			this.cartId = cartId;
			this.productId = productId;
		}
	}
}