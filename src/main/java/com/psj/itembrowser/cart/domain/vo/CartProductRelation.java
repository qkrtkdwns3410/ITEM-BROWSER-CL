package com.psj.itembrowser.cart.domain.vo;

import static com.psj.itembrowser.security.common.exception.ErrorCode.*;

import com.psj.itembrowser.cart.domain.dto.response.CartProductRelationResponseDTO;
import com.psj.itembrowser.product.domain.vo.Product;
import com.psj.itembrowser.security.common.BaseDateTimeEntity;
import com.psj.itembrowser.security.common.exception.DatabaseOperationException;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * packageName    : com.psj.itembrowser.cart.domain.vo fileName       : CartProductRelation author :
 * ipeac date           : 2023-10-22 description    :
 * =========================================================== DATE              AUTHOR NOTE
 * ----------------------------------------------------------- 2023-10-22        ipeac       최초 생성
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@EqualsAndHashCode(of = {"cartId", "productId"}, callSuper = false)
@ToString
public class CartProductRelation extends BaseDateTimeEntity {

	private Long cartId;
	private Long productId;
	private Long productQuantity;

	private Cart cart;
	private Product product;

	public static CartProductRelation create(CartProductRelationResponseDTO dto) {
		CartProductRelation cartProductRelation = new CartProductRelation();

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
}