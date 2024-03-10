package com.psj.itembrowser.cart.service;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import com.psj.itembrowser.cart.domain.dto.request.CartProductDeleteRequestDTO;
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
import com.psj.itembrowser.security.common.exception.NotAuthorizedException;
import com.psj.itembrowser.security.common.exception.NotFoundException;

/**
 * packageName    : com.psj.itembrowser.user.cart.service.impl fileName       : CartServiceImplTest
 * author         : ipeac date           : 2023-10-16 description    :
 * =========================================================== DATE              AUTHOR NOTE
 * ----------------------------------------------------------- 2023-10-16        ipeac       최초 생성
 */

@ServiceWithDBTest
class CartDeleteWithDBServiceTest {
	
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
	
	@BeforeEach
	void setUp() {
		cartPersistence = new CartPersistence(cartMapper, cartProductRelationEntityRepository, cartRepository);
		cartService = new CartService(cartProductRelationEntityRepository, cartPersistence, productValidationHelper);
	}
	
	@Test
	@DisplayName("장바구니 삭제시도시 존재하지 않는 장바구니 상품을 삭제하려고 하면 NotFoundException이 발생한다.")
	void When_NotExistCart_Then_ThrowNotFoundException() {
		//given
		MemberEntity member = MemberEntity.builder()
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
		
		CartProductDeleteRequestDTO dto = CartProductDeleteRequestDTO.builder()
			.cartId(1L)
			.productId(1L)
			.build();
		
		//when
		assertThatThrownBy(() -> cartService.removeCartProduct(dto, member))
			//then
			.isInstanceOf(NotFoundException.class)
			.hasMessage(ErrorCode.CART_NOT_FOUND.getMessage());
	}
	
	@Test
	@DisplayName("장바구니 삭제 시도시 member - customer 가 삭제하려는 장바구니가 현재 사용자 이메일과 다르다면, NotAuthorizedException이 발생한다.")
	void When_NotAuthorized_Then_ThrowNotAuthorizedException() {
		//given
		final String existEmail = "qkrtkdwns3410@gmail.com";
		final String notExistEmail = "asdjlakjdklajsldj@gmail.com";
		
		MemberEntity member = MemberEntity.builder()
			.address(
				Address.builder()
					.addressMain("서울시 강남구 역삼동 123-456번지")
					.addressSub("빌딩 7층 701호")
					.zipCode("12345")
					.build()
			)
			.credentials(
				Credentials.builder()
					.email(notExistEmail)
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
		
		CartEntity cart = CartEntity.builder()
			.userEmail(existEmail)
			.build();
		
		em.persist(cart);
		
		CartProductDeleteRequestDTO dto = CartProductDeleteRequestDTO.builder()
			.cartId(cart.getId())
			.productId(1L)
			.build();
		
		//when
		assertThatThrownBy(() -> cartService.removeCartProduct(dto, member))
			//then
			.isInstanceOf(NotAuthorizedException.class)
			.hasMessage(ErrorCode.CUSTOMER_NOT_AUTHORIZED.getMessage());
	}
	
	@Test
	@DisplayName("장바구니 삭제시 장바구니 - CartProductRelationEntity 가 존재하지 않는 경우 NotFoundException이 발생한다.")
	void When_NotExistCartProduct_Then_ThrowNotFoundException() {
		//given
		final String existEmail = "qkrtkdwns3410@gmail.com";
		
		MemberEntity member = MemberEntity.builder()
			.address(
				Address.builder()
					.addressMain("서울시 강남구 역삼동 123-456번지")
					.addressSub("빌딩 7층 701호")
					.zipCode("12345")
					.build()
			)
			.credentials(
				Credentials.builder()
					.email(existEmail)
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
		
		CartEntity cart = CartEntity.builder()
			.userEmail(existEmail)
			.build();
		
		em.persist(cart);
		
		CartProductDeleteRequestDTO dto = CartProductDeleteRequestDTO.builder()
			.cartId(cart.getId())
			.productId(1L)
			.build();
		
		//when
		assertThatThrownBy(() -> cartService.removeCartProduct(dto, member))
			//then
			.isInstanceOf(NotFoundException.class)
			.hasMessage(ErrorCode.CART_PRODUCT_NOT_FOUND.getMessage());
	}
	
	@Test
	@DisplayName("장바구니 삭제시 - 정상 처리")
	void When_DeleteCartProduct_Then_Success() {
		//given
		final String existEmail = "qkrtkdwns3410@gmail.com";
		
		MemberEntity member = MemberEntity.builder()
			.address(
				Address.builder()
					.addressMain("서울시 강남구 역삼동 123-456번지")
					.addressSub("빌딩 7층 701호")
					.zipCode("12345")
					.build()
			)
			.credentials(
				Credentials.builder()
					.email(existEmail)
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
		
		CartEntity cart = CartEntity.builder()
			.userEmail(existEmail)
			.build();
		
		em.persist(cart);
		
		CartProductRelationEntity cartProductRelation = CartProductRelationEntity.builder()
			.cartId(cart.getId())
			.productId(1L)
			.productQuantity(1L)
			.build();
		
		em.persist(cartProductRelation);
		
		CartProductDeleteRequestDTO dto = CartProductDeleteRequestDTO.builder()
			.cartId(cart.getId())
			.productId(1L)
			.build();
		
		//when
		cartService.removeCartProduct(dto, member);
		
		//then
		CartProductRelationEntity foundCartProductRelation = cartProductRelationEntityRepository.findByCartIdAndProductId(cart.getId(), 1L).get();
		
		assertThat(foundCartProductRelation).isNotNull();
		assertThat(foundCartProductRelation.getDeletedDate()).isNotNull().isAfter(NOW);
	}
}