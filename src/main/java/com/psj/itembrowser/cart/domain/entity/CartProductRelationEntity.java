package com.psj.itembrowser.cart.domain.entity;

import static com.psj.itembrowser.security.common.exception.ErrorCode.*;

import java.time.LocalDateTime;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.psj.itembrowser.cart.domain.dto.request.CartProductUpdateRequestDTO;
import com.psj.itembrowser.cart.domain.dto.response.CartProductRelationResponseDTO;
import com.psj.itembrowser.cart.domain.vo.CartProductRelationMockDTO;
import com.psj.itembrowser.product.domain.entity.ProductEntity;
import com.psj.itembrowser.security.common.exception.DatabaseOperationException;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "cart_product_relation")
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@AllArgsConstructor
public class CartProductRelationEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "cart_id")
	private Long cartId;

	@Column(name = "product_id")
	private Long productId;

	@Column(name = "product_quantity")
	private Long productQuantity;

	@Column(name = "created_date")
	private LocalDateTime createdDate;

	@Column(name = "updated_date")
	private LocalDateTime updatedDate;

	@Column(name = "deleted_date")
	private LocalDateTime deletedDate;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "PRODUCT_ID", insertable = false, updatable = false)
	private ProductEntity product;

	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
	@JoinColumn(name = "CART_ID", insertable = false, updatable = false)
	private CartEntity cartEntity;

	public static CartProductRelationEntity create(CartProductRelationResponseDTO dto) {
		CartProductRelationEntity cartProductRelation = new CartProductRelationEntity();

		cartProductRelation.cartId = dto.getCartId();
		cartProductRelation.productId = dto.getProductId();
		cartProductRelation.productQuantity = dto.getProductQuantity();
		//TODO 추후 변환하여 사용
		cartProductRelation.product = null;

		return cartProductRelation;
	}

	public void addProductQuantity(long quantity) {
		if (quantity < 0) {
			throw new DatabaseOperationException(CART_PRODUCT_QUANTITY_NOT_POSITIVE);
		}
		this.productQuantity += quantity;
	}

	public CartProductUpdateRequestDTO toCartProductUpdateRequestDTO() {
		return new CartProductUpdateRequestDTO(
			this.cartId,
			this.productId,
			this.productQuantity
		);
	}

	public CartProductRelationMockDTO toCartProductRelationSimpleDTO() {
		return new CartProductRelationMockDTO(
			this.cartId,
			this.productId,
			this.productQuantity
		);
	}
}