package com.psj.itembrowser.cart.service;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import com.psj.itembrowser.cart.domain.dto.response.CartResponseDTO;
import com.psj.itembrowser.cart.domain.entity.CartEntity;
import com.psj.itembrowser.cart.domain.entity.CartProductRelationEntity;
import com.psj.itembrowser.cart.domain.entity.CartProductRelationEntityRepository;
import com.psj.itembrowser.cart.mapper.CartMapper;
import com.psj.itembrowser.cart.persistance.CartPersistence;
import com.psj.itembrowser.cart.persistance.CartRepository;
import com.psj.itembrowser.config.annotation.ServiceWithDBTest;
import com.psj.itembrowser.product.domain.entity.ProductEntity;
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
class CartSelectWithDBServiceTest {
	
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
		cartService = new CartService(em.getEntityManager(), cartProductRelationEntityRepository, cartPersistence, productValidationHelper);
	}
	
	@Test
	@DisplayName("사용자 이름 단건 조회 서비스 - 정상 처리")
	void When_getCart_Then_returnCartResponseDTO() {
		//given
		final String memberEmail = "qkrtkdwns3410@gmail.com";
		
		CartEntity cart = CartEntity.builder()
			.userEmail(memberEmail)
			.build();
		
		ProductEntity product = ProductEntity.builder()
			.name("섬유유연제")
			.unitPrice(1000)
			.quantity(10)
			.build();
		
		em.persist(product);
		em.persist(cart);
		
		CartProductRelationEntity cartProductRelation = CartProductRelationEntity.builder()
			.cartId(cart.getId())
			.productId(product.getId())
			.build();
		
		cart.addCartProductRelation(cartProductRelation);
		
		em.persist(cartProductRelation);
		
		em.flush();
		
		//when
		CartResponseDTO actualCartResponseDTO = cartService.getCart(memberEmail);
		
		//then
		assertThat(actualCartResponseDTO).as("CartResponseDTO는 null이 아니어야 한다")
			.isNotNull();
		
		assertThat(actualCartResponseDTO.getProducts()).as("제품 목록은 null이 아니고 비어있지 않아야 한다")
			.isNotNull()
			.isNotEmpty();
		
		assertAll("CartResponseDTO 검증",
			() -> assertThat(actualCartResponseDTO.getUserId())
				.as("사용자 ID가 예상한 값과 일치해야 한다")
				.isEqualTo(memberEmail),
			
			() -> assertThat(actualCartResponseDTO.getCreatedDate())
				.as("생성 날짜가 NOW 이후여야 한다")
				.isAfter(NOW),
			
			() -> assertThat(actualCartResponseDTO.getProducts().get(0).getProductId())
				.as("첫 번째 제품의 ID가 예상한 값과 일치해야 한다")
				.isEqualTo(product.getId()),
			
			() -> assertThat(actualCartResponseDTO.getProducts().get(0).getProductQuantity())
				.as("첫 번째 제품의 수량이 예상한 값과 일치해야 한다")
				.isEqualTo(cartProductRelation.getProductQuantity())
		);
	}
	
	@Test
	@DisplayName("사용자 이름 단건 조회 서비스 - 장바구니 생성 내역 자체가 없는 경우 -  NotFoundException 발생")
	void When_getCart_Expect_NotFoundException() {
		//given
		final String memberEmail = "akdjladjajsjadaj";
		
		//when
		assertThatThrownBy(() -> cartService.getCart(memberEmail))
			//then
			.isInstanceOf(NotFoundException.class)
			.hasMessage(ErrorCode.CART_NOT_FOUND.getMessage());
	}
	
	@Test
	@DisplayName("장바구니 ID 단건 조회 서비스 - 정상 처리")
	void When_getCartById_Then_returnCartResponseDTO() {
		//given
		final String memberEmail = "qkrtkdwns3410@gmail.com";
		
		CartEntity cart = CartEntity.builder()
			.userEmail(memberEmail)
			.build();
		
		ProductEntity product = ProductEntity.builder()
			.name("섬유유연제")
			.unitPrice(1000)
			.quantity(10)
			.build();
		
		em.persist(product);
		em.persist(cart);
		
		CartProductRelationEntity cartProductRelation = CartProductRelationEntity.builder()
			.cartId(cart.getId())
			.productId(product.getId())
			.build();
		
		cart.addCartProductRelation(cartProductRelation);
		
		em.persist(cartProductRelation);
		
		em.flush();
		
		//when
		CartResponseDTO actualCartResponseDTO = cartService.getCart(cart.getId());
		
		//then
		assertThat(actualCartResponseDTO).as("CartResponseDTO는 null이 아니어야 한다")
			.isNotNull();
		
		assertThat(actualCartResponseDTO.getProducts()).as("제품 목록은 null이 아니고 비어있지 않아야 한다")
			.isNotNull()
			.isNotEmpty();
		
		assertAll("CartResponseDTO 검증",
			() -> assertThat(actualCartResponseDTO.getUserId())
				.as("사용자 ID가 예상한 값과 일치해야 한다")
				.isEqualTo(memberEmail),
			
			() -> assertThat(actualCartResponseDTO.getCreatedDate())
				.as("생성 날짜가 NOW 이후여야 한다")
				.isAfter(NOW),
			
			() -> assertThat(actualCartResponseDTO.getProducts().get(0).getProductId())
				.as("첫 번째 제품의 ID가 예상한 값과 일치해야 한다")
				.isEqualTo(product.getId()),
			
			() -> assertThat(actualCartResponseDTO.getProducts().get(0).getProductQuantity())
				.as("첫 번째 제품의 수량이 예상한 값과 일치해야 한다")
				.isEqualTo(cartProductRelation.getProductQuantity())
		);
	}
	
	@Test
	@DisplayName("장바구니 ID 단건 조회 서비스 - 장바구니 생성 내역 자체가 없는 경우 -  NotFoundException 발생")
	void When_getCartById_Expect_NotFoundException() {
		//given
		final Long cartId = 100L;
		
		//when
		assertThatThrownBy(() -> cartService.getCart(cartId))
			//then
			.isInstanceOf(NotFoundException.class)
			.hasMessage(ErrorCode.CART_NOT_FOUND.getMessage());
	}
}