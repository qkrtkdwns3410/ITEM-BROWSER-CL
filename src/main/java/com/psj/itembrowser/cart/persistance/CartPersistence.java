package com.psj.itembrowser.cart.persistance;

import com.psj.itembrowser.cart.domain.dto.request.CartProductDeleteRequestDTO;
import com.psj.itembrowser.cart.domain.dto.request.CartProductRequestDTO;
import com.psj.itembrowser.cart.domain.dto.request.CartProductUpdateRequestDTO;
import com.psj.itembrowser.cart.domain.dto.response.CartProductRelationResponseDTO;
import com.psj.itembrowser.cart.domain.dto.response.CartResponseDTO;
import com.psj.itembrowser.cart.domain.entity.CartEntity;
import com.psj.itembrowser.cart.domain.entity.CartProductRelationEntity;
import com.psj.itembrowser.cart.domain.entity.CartProductRelationEntityRepository;
import com.psj.itembrowser.cart.mapper.CartMapper;
import com.psj.itembrowser.security.common.exception.DatabaseOperationException;
import com.psj.itembrowser.security.common.exception.NotFoundException;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import static com.psj.itembrowser.security.common.exception.ErrorCode.*;

/**
 * packageName    : com.psj.itembrowser.cart.persistance fileName       : CartPersistence author
 *     : ipeac date           : 2023-10-06 description    :
 * =========================================================== DATE              AUTHOR
 * NOTE ----------------------------------------------------------- 2023-10-06        ipeac       최초
 * 생성
 */
@Component
@RequiredArgsConstructor
public class CartPersistence {

	private final CartMapper cartMapper;
	private final CartProductRelationEntityRepository cartProductRelationEntityRepository;
	private final CartRepository cartRepository;

	public CartResponseDTO getCart(@NonNull String userEmail) {
		CartEntity cart = cartRepository.findByUserEmail(userEmail).orElseThrow(() -> new NotFoundException(CART_NOT_FOUND));

		return CartResponseDTO.from(cart);
	}

	public CartResponseDTO getCart(@NonNull Long cartId) {
		CartEntity cart = cartRepository.findById(cartId).orElseThrow(() -> new NotFoundException(CART_NOT_FOUND));

		return CartResponseDTO.from(cart);
	}

	public void insertCartProduct(@NonNull CartProductRequestDTO cartProductRequestDTO) {
		boolean isNotInserted = !cartMapper.insertCartProduct(cartProductRequestDTO);

		if (isNotInserted) {
			throw new DatabaseOperationException(CART_PRODUCT_INSERT_FAIL);
		}
	}

	public void modifyCartProduct(@NonNull CartProductUpdateRequestDTO cartProductUpdateRequestDTO) {
		boolean isNotModified = !cartMapper.updateCartProductRelation(cartProductUpdateRequestDTO);

		if (isNotModified) {
			throw new DatabaseOperationException(CART_PRODUCT_UPDATE_FAIL);
		}
	}

	public void deleteCart(@NonNull CartProductDeleteRequestDTO cartProductDeleteRequestDTO) {
		boolean isNotDeleted = !cartMapper.deleteCartProductRelation(cartProductDeleteRequestDTO);

		if (isNotDeleted) {
			throw new DatabaseOperationException(CART_PRODUCT_DELETE_FAIL);
		}
	}

	public CartResponseDTO createCart(@NonNull String userId) {
		CartEntity saved = cartRepository.save(CartEntity.builder().userEmail(userId).build());

		return CartResponseDTO.from(saved);
	}

	public CartProductRelationResponseDTO addCartProductRelation(@NonNull CartProductRequestDTO requestDTO) {
		CartProductRelationEntity saved = cartProductRelationEntityRepository.save(CartProductRelationEntity.from(requestDTO));

		return CartProductRelationResponseDTO.from(saved);
	}

	public CartProductRelationResponseDTO getCartProductRelation(@NonNull Long cartId, @NonNull Long productId) {
		CartProductRelationEntity found = cartProductRelationEntityRepository.findByCartIdAndProductId(cartId, productId)
			.orElseThrow(() -> new NotFoundException(CART_PRODUCT_NOT_FOUND));

		return CartProductRelationResponseDTO.from(found);
	}
}