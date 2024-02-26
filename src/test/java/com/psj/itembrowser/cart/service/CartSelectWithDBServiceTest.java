package com.psj.itembrowser.cart.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import com.psj.itembrowser.cart.domain.dto.response.CartResponseDTO;
import com.psj.itembrowser.cart.domain.entity.CartEntity;
import com.psj.itembrowser.cart.domain.entity.CartProductRelationEntity;
import com.psj.itembrowser.cart.mapper.CartMapper;
import com.psj.itembrowser.cart.persistance.CartPersistence;
import com.psj.itembrowser.cart.persistance.CartRepository;
import com.psj.itembrowser.config.annotation.ServiceWithDBTest;
import com.psj.itembrowser.product.domain.entity.ProductEntity;

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
	
	@Mock
	private CartMapper cartMapper;
	
	private CartPersistence cartPersistence;
	
	private CartService cartService;
	
	@BeforeEach
	void setUp() {
		cartPersistence = new CartPersistence(cartMapper, cartRepository);
		cartService = new CartService(cartPersistence, cartMapper);
	}
	
	@Test
	@DisplayName("사용자 이름 단건 조회 서비스 - 정상 처리")
	void test1() {
		//given
		CartEntity cart = CartEntity.builder()
			.userEmail("qkrtkdwns3410@gmail.com")
			.build();
		
		ProductEntity product = ProductEntity.builder()
			.name("섬유유연제")
			.unitPrice(1000)
			.build();
		
		em.persist(cart);
		em.persist(product);
		
		CartProductRelationEntity cartProductRelation = CartProductRelationEntity.builder()
			.cartId(cart.getId())
			.productId(product.getId())
			.product(product)
			.build();
		
		cart.addCartProductRelation(cartProductRelation);
		
		//when
		CartResponseDTO actualCartResponseDTO = cartService.getCart("qkrtkdwns3410@gmail.com");
		
		//then
		
	}
}