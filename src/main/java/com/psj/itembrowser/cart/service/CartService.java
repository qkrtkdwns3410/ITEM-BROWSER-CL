package com.psj.itembrowser.cart.service;

import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.psj.itembrowser.cart.domain.dto.request.CartProductDeleteRequestDTO;
import com.psj.itembrowser.cart.domain.dto.request.CartProductRequestDTO;
import com.psj.itembrowser.cart.domain.dto.request.CartProductUpdateRequestDTO;
import com.psj.itembrowser.cart.domain.dto.response.CartProductRelationResponseDTO;
import com.psj.itembrowser.cart.domain.dto.response.CartResponseDTO;
import com.psj.itembrowser.cart.domain.entity.CartProductRelationEntity;
import com.psj.itembrowser.cart.domain.entity.CartProductRelationEntityRepository;
import com.psj.itembrowser.cart.persistance.CartPersistence;
import com.psj.itembrowser.member.domain.entity.MemberEntity;
import com.psj.itembrowser.product.service.ProductValidationHelper;
import com.psj.itembrowser.security.common.exception.ErrorCode;
import com.psj.itembrowser.security.common.exception.NotAuthorizedException;
import com.psj.itembrowser.security.common.exception.NotFoundException;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * packageName    : com.psj.itembrowser.test.service.impl fileName       : TestServiceImpl author
 * : ipeac date           : 2023-09-27 description    :
 * =========================================================== DATE              AUTHOR
 * NOTE ----------------------------------------------------------- 2023-09-27        ipeac       최초
 * 생성
 */
@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CartService {
	private final CartProductRelationEntityRepository cartProductRelationEntityRepository;
	
	private final CartPersistence cartPersistence;
	
	private final ProductValidationHelper productValidationHelper;
	
	public CartResponseDTO getCart(String userEmail) {
		return cartPersistence.getCart(userEmail);
	}
	
	public CartResponseDTO getCart(Long cartId) {
		return cartPersistence.getCart(cartId);
	}
	
	@Transactional(readOnly = false)
	public CartResponseDTO createCart(@NonNull String userId) {
		return cartPersistence.createCart(userId);
	}
	
	@Transactional(readOnly = false)
	public void addCartProduct(MemberEntity member, CartProductRequestDTO requestDTO) {
		validateMemberAuth(member, requestDTO);
		
		productValidationHelper.validateProduct(requestDTO.getProductId());
		
		CartResponseDTO cart = getOrCreateCart(requestDTO.getEmail());
		requestDTO.setCartId(cart.getCartId());
		
		CartProductRelationResponseDTO existingProduct = findExistingProduct(cart.getCartId(), requestDTO.getProductId());
		
		if (existingProduct == null) {
			cartPersistence.addCartProductRelation(requestDTO);
			return;
		}
		
		updateExistingProductQuantity(existingProduct, requestDTO.getQuantity());
	}
	
	private CartResponseDTO getOrCreateCart(String userEmail) {
		try {
			return getCart(userEmail);
		} catch (NotFoundException e) {
			log.info("Creating new cart for user: {}", userEmail);
			return createCart(userEmail);
		}
	}
	
	private CartProductRelationResponseDTO findExistingProduct(Long cartId, Long productId) {
		try {
			return cartPersistence.getCartProductRelation(cartId, productId);
		} catch (NotFoundException e) {
			log.info("Product not found in cart, adding new one.");
			return null;
		}
	}
	
	private void updateExistingProductQuantity(CartProductRelationResponseDTO existingProduct, long quantityToAdd) {
		CartProductRelationEntity foundCartProduct = CartProductRelationEntity.from(existingProduct);
		foundCartProduct.addProductQuantity(quantityToAdd);
		cartProductRelationEntityRepository.save(foundCartProduct);
	}
	
	private static void validateMemberAuth(MemberEntity member, CartProductRequestDTO requestDTO) {
		if (!Objects.equals(member.getCredentials().getEmail(), requestDTO.getEmail())) {
			throw new NotAuthorizedException(ErrorCode.CUSTOMER_NOT_AUTHORIZED);
		}
	}
	
	@Transactional(readOnly = false)
	public void modifyCartProduct(CartProductUpdateRequestDTO cartProductUpdateRequestDTO, MemberEntity member) {
		validateMemberAuth(member, cartProductUpdateRequestDTO.getCartId());
		
		cartPersistence.modifyCartProduct(cartProductUpdateRequestDTO);
	}
	
	@Transactional(readOnly = false)
	public void removeCartProduct(@NonNull CartProductDeleteRequestDTO cartProductDeleteRequestDTO, MemberEntity member) {
		validateMemberAuth(member, cartProductDeleteRequestDTO.getCartId());
		
		cartPersistence.removeCartProduct(cartProductDeleteRequestDTO);
	}
	
	private void validateMemberAuth(MemberEntity member, Long cartId) {
		if (member.isAdmin()) {
			return;
		}
		
		CartResponseDTO found = cartPersistence.getCart(cartId);
		
		if (!Objects.equals(member.getCredentials().getEmail(), found.getUserEmail())) {
			throw new NotAuthorizedException(ErrorCode.CUSTOMER_NOT_AUTHORIZED);
		}
	}
}