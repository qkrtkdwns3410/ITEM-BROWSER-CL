package com.psj.itembrowser.cart.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.psj.itembrowser.cart.domain.dto.request.CartProductDeleteRequestDTO;
import com.psj.itembrowser.cart.domain.dto.request.CartProductRequestDTO;
import com.psj.itembrowser.cart.domain.dto.request.CartProductUpdateRequestDTO;
import com.psj.itembrowser.cart.domain.dto.response.CartResponseDTO;
import com.psj.itembrowser.cart.domain.vo.CartProductRelation;
import com.psj.itembrowser.cart.mapper.CartMapper;
import com.psj.itembrowser.cart.persistance.CartPersistence;
import com.psj.itembrowser.security.common.exception.NotFoundException;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * packageName    : com.psj.itembrowser.test.service.impl fileName       : TestServiceImpl author
 *      : ipeac date           : 2023-09-27 description    :
 * =========================================================== DATE              AUTHOR
 * NOTE ----------------------------------------------------------- 2023-09-27        ipeac       최초
 * 생성
 */
@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CartService {

	private final CartPersistence cartPersistence;
	private final CartMapper cartMapper;
	
	public CartResponseDTO getCart(String userEmail) {
		return cartPersistence.getCart(userEmail);
	}

	public CartResponseDTO getCart(Long cartId) {
		return cartPersistence.getCart(cartId);
	}

	public void addCart(@NonNull String userId) {
		cartPersistence.addCart(userId);
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public void addCartProduct(CartProductRequestDTO requestDTO) {
		CartResponseDTO cart = null;

		try {
			cart = getCart(requestDTO.getUserId());
		} catch (NotFoundException e) {
			log.info("cart not found, add cart");
		}

		if (cart == null) {
			addCart(requestDTO.getUserId());
		}

		CartProductRelation findCartProduct = cartMapper.getCartProductRelation(requestDTO.getCartId(), requestDTO.getProductId());

		if (findCartProduct != null) {
			findCartProduct.addProductQuantity(requestDTO.getQuantity());
			cartPersistence.modifyCartProduct(CartProductUpdateRequestDTO.from(findCartProduct));
			return;
		}

		cartPersistence.insertCartProduct(requestDTO);
	}

	@Transactional(readOnly = false)
	public void modifyCartProduct(CartProductUpdateRequestDTO cartProductUpdateRequestDTO) {
		cartPersistence.modifyCartProduct(cartProductUpdateRequestDTO);
	}

	@Transactional(readOnly = false)
	public void removeCart(@NonNull CartProductDeleteRequestDTO cartProductDeleteRequestDTO) {
		cartPersistence.deleteCart(cartProductDeleteRequestDTO);
	}
}