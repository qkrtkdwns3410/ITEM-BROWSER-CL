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
		
		CartResponseDTO cart = null;
		
		try {
			cart = getCart(requestDTO.getEmail());
		} catch (NotFoundException e) {
			log.info(e.getMessage());
		}
		
		cart = createCartIfEmpty(requestDTO, cart);
		
		CartProductRelationResponseDTO dto = null;
		
		try {
			dto = cartPersistence.getCartProductRelation(cart.getCartId(), requestDTO.getProductId());
		} catch (NotFoundException e) {
			log.info(e.getMessage());
		}
		
		if (dto == null) {
			cartPersistence.addCartProductRelation(requestDTO);
			return;
		}
		
		CartProductRelationEntity foundCartProduct = CartProductRelationEntity.from(dto);
		
		foundCartProduct.addProductQuantity(requestDTO.getQuantity());
		
		cartProductRelationEntityRepository.save(foundCartProduct);
	}
	
	private CartResponseDTO createCartIfEmpty(CartProductRequestDTO requestDTO, CartResponseDTO cart) {
		if (cart == null) {
			cart = createCart(requestDTO.getEmail());
			requestDTO.setCartId(cart.getCartId());
		}
		return cart;
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