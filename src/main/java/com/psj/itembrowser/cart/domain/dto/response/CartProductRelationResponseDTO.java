package com.psj.itembrowser.cart.domain.dto.response;

import javax.validation.constraints.PositiveOrZero;

import com.psj.itembrowser.cart.domain.entity.CartProductRelationEntity;
import com.psj.itembrowser.cart.domain.vo.CartProductRelation;
import com.psj.itembrowser.product.domain.dto.response.ProductResponseDTO;
import com.psj.itembrowser.security.common.exception.ErrorCode;
import com.psj.itembrowser.security.common.exception.NotFoundException;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * packageName    : com.psj.itembrowser.cart.domain.dto.response fileName       :
 * CartProductRelationResponseDTO author         : ipeac date           : 2023-10-23 description :
 * =========================================================== DATE              AUTHOR NOTE
 * ----------------------------------------------------------- 2023-10-23        ipeac 최초 생성
 */
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CartProductRelationResponseDTO {
	
	Long cartId;
	
	Long productId;
	
	@PositiveOrZero
	Long productQuantity;
	
	ProductResponseDTO product;
	
	@Builder
	private CartProductRelationResponseDTO(Long cartId, Long productId, Long productQuantity, ProductResponseDTO product) {
		this.cartId = cartId;
		this.productId = productId;
		this.productQuantity = productQuantity;
		this.product = product;
	}
	
	public static CartProductRelationResponseDTO of(Long cartId, Long productId,
		Long productQuantity, ProductResponseDTO product) {
		return new CartProductRelationResponseDTO(cartId, productId, productQuantity, product);
	}
	
	public static CartProductRelationResponseDTO from(CartProductRelationEntity entity) {
		if (entity == null) {
			throw new NotFoundException(ErrorCode.CART_PRODUCT_NOT_FOUND);
		}
		
		return CartProductRelationResponseDTO.builder()
			.cartId(entity.getCartId())
			.productId(entity.getProductId())
			.productQuantity(entity.getProductQuantity())
			.build();
	}
	
	public static CartProductRelationResponseDTO from(CartProductRelation vo) {
		if (vo == null) {
			throw new NotFoundException(ErrorCode.CART_PRODUCT_NOT_FOUND);
		}
		
		return CartProductRelationResponseDTO.builder()
			.cartId(vo.getCartId())
			.productId(vo.getProductId())
			.productQuantity(vo.getProductQuantity())
			.product(ProductResponseDTO.from(vo.getProduct()))
			.build();
	}
}