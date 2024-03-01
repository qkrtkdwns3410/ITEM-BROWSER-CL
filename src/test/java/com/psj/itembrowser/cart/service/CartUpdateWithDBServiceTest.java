package com.psj.itembrowser.cart.service;

import static org.assertj.core.api.AssertionsForInterfaceTypes.*;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import com.psj.itembrowser.cart.domain.dto.request.CartProductUpdateRequestDTO;
import com.psj.itembrowser.cart.domain.entity.CartEntity;
import com.psj.itembrowser.cart.domain.entity.CartProductRelationEntity;
import com.psj.itembrowser.cart.domain.entity.CartProductRelationEntityRepository;
import com.psj.itembrowser.cart.mapper.CartMapper;
import com.psj.itembrowser.cart.persistance.CartPersistence;
import com.psj.itembrowser.cart.persistance.CartRepository;
import com.psj.itembrowser.config.annotation.ServiceWithDBTest;
import com.psj.itembrowser.member.domain.entity.MemberEntity;
import com.psj.itembrowser.member.domain.vo.Address;
import com.psj.itembrowser.member.domain.vo.Credentials;
import com.psj.itembrowser.member.domain.vo.Name;
import com.psj.itembrowser.member.domain.vo.Role;
import com.psj.itembrowser.member.domain.vo.Status;
import com.psj.itembrowser.product.service.ProductValidationHelper;
import com.psj.itembrowser.security.common.exception.ErrorCode;
import com.psj.itembrowser.security.common.exception.NotFoundException;

/**
 * packageName    : com.psj.itembrowser.user.cart.service.impl fileName       : CartServiceImplTest
 * author         : ipeac date           : 2023-10-16 description    :
 * =========================================================== DATE              AUTHOR NOTE
 * ----------------------------------------------------------- 2023-10-16        ipeac       최초 생성
 */

@ServiceWithDBTest
class CartUpdateWithDBServiceTest {
	
	@Autowired
	private TestEntityManager em;
	
	@Autowired
	private CartRepository cartRepository;
	
	@Autowired
	private CartProductRelationEntityRepository cartProductRelationEntityRepository;
	
	@Mock
	private ProductValidationHelper productValidationHelper;
	
	@Mock
	private CartMapper cartMapper;
	
	private CartPersistence cartPersistence;
	
	private CartService cartService;
	
	private final static LocalDateTime NOW = LocalDateTime.now();
	
	private MemberEntity member;
	
	@BeforeEach
	void setUp() {
		cartPersistence = new CartPersistence(cartMapper, cartProductRelationEntityRepository, cartRepository);
		cartService = new CartService(cartProductRelationEntityRepository, cartPersistence, productValidationHelper);
		
		member = MemberEntity.builder()
			.address(
				Address.builder()
					.addressMain("서울시 강남구 역삼동 123-456번지")
					.addressSub("빌딩 7층 701호")
					.zipCode("12345")
					.build()
			)
			.credentials(
				Credentials.builder()
					.email("qkrtkdwns3410@gmail.com")
					.password("JavaHelper123!@#")
					.build()
			)
			.name(
				Name.builder()
					.firstName("홍")
					.lastName("길동")
					.build()
			)
			.status(Status.ACTIVE)
			.role(Role.ROLE_CUSTOMER)
			.build();
	}
	
	@Test
	@DisplayName("장바구니 수정 성공 케이스")
	void test1() {
		// given
		CartEntity cart = CartEntity.builder()
			.userEmail(member.getCredentials().getEmail())
			.build();
		
		em.persist(cart);
		
		CartProductRelationEntity cartProductRelationEntity = CartProductRelationEntity.builder()
			.cartId(cart.getId())
			.productId(1L)
			.productQuantity(1L)
			.build();
		
		em.persist(cartProductRelationEntity);
		
		CartProductUpdateRequestDTO request = CartProductUpdateRequestDTO.builder()
			.cartId(cart.getId())
			.productId(1L)
			.quantity(10L)
			.build();
		
		// when
		cartService.modifyCartProduct(request, member);
		
		// then
		CartEntity foundCart = em.find(CartEntity.class, cart.getId());
		CartProductRelationEntity foundCartProduct = em.find(CartProductRelationEntity.class,
			new CartProductRelationEntity.CartProductRelationEntityId(cart.getId(), 1L));
		
		assertThat(foundCart).isNotNull();
		assertThat(foundCartProduct).isNotNull();
		
		assertAll(
			() -> assertThat(foundCartProduct.getProductId()).isEqualTo(1L),
			() -> assertThat(foundCartProduct.getProductQuantity()).isEqualTo(10L)
		);
	}
	
	@Test
	@DisplayName("장바구니 수정 실패 케이스 - 장바구니에 해당 상품이 없는 경우")
	void test2() {
		// given
		CartEntity cart = CartEntity.builder()
			.userEmail(member.getCredentials().getEmail())
			.build();
		
		em.persist(cart);
		
		CartProductUpdateRequestDTO request = CartProductUpdateRequestDTO.builder()
			.cartId(cart.getId())
			.productId(1L)
			.quantity(10L)
			.build();
		
		// when
		assertThatThrownBy(() -> cartService.modifyCartProduct(request, member))
			.isInstanceOf(NotFoundException.class)
			.hasMessage(ErrorCode.CART_PRODUCT_NOT_FOUND.getMessage());
	}
}