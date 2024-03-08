package com.psj.itembrowser.cart.service;

import com.psj.itembrowser.cart.domain.dto.request.CartProductRequestDTO;
import com.psj.itembrowser.cart.domain.entity.CartEntity;
import com.psj.itembrowser.cart.domain.entity.CartProductRelationEntity;
import com.psj.itembrowser.cart.domain.entity.CartProductRelationEntityRepository;
import com.psj.itembrowser.cart.mapper.CartMapper;
import com.psj.itembrowser.cart.persistance.CartPersistence;
import com.psj.itembrowser.cart.persistance.CartRepository;
import com.psj.itembrowser.config.annotation.ServiceWithDBTest;
import com.psj.itembrowser.member.domain.entity.MemberEntity;
import com.psj.itembrowser.member.domain.vo.*;
import com.psj.itembrowser.product.service.ProductValidationHelper;
import com.psj.itembrowser.security.common.exception.ErrorCode;
import com.psj.itembrowser.security.common.exception.NotAuthorizedException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

/**
 * packageName    : com.psj.itembrowser.user.cart.service.impl fileName       : CartServiceImplTest
 * author         : ipeac date           : 2023-10-16 description    :
 * =========================================================== DATE              AUTHOR NOTE
 * ----------------------------------------------------------- 2023-10-16        ipeac       최초 생성
 */

@ServiceWithDBTest
class CartInsertWithDBServiceTest {
    
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
    
    private MemberEntity member;
    private CartProductRequestDTO requestDTO;
    
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
                .role(Role.ROLE_CUSTOMER)
                .status(Status.ACTIVE)
                .build();
        
        em.persist(member);
        
        requestDTO = CartProductRequestDTO.builder()
                .cartId(1L)
                .email(member.getCredentials().getEmail())
                .productId(1L)
                .quantity(1L)
                .build();
    }
    
    @Test
    @DisplayName("기존에 존재하는 장바구니가 없는 경우 새로운 장바구니를 생성하고, 해당 장바구니에 상품을 추가한다.")
    void When_AddCartProduct_Expect_CreateNewCartAndAddProduct() {
        //when
        cartService.addCartProduct(member, requestDTO);
        
        //then
        CartEntity foundCart = cartRepository.findByUserEmail(member.getCredentials().getEmail()).get();
        CartProductRelationEntity foundCartProduct = em.find(CartProductRelationEntity.class,
                new CartProductRelationEntity.CartProductRelationEntityId(foundCart.getId(), 1L));
        
        assertThat(foundCart).isNotNull();
        assertThat(foundCart.getUserEmail()).isEqualTo(member.getCredentials().getEmail());
        assertThat(foundCartProduct.getProductQuantity()).isEqualTo(requestDTO.getQuantity());
    }
    
    @Test
    @DisplayName("장바구니 요청과 실제 사용자의 이메일이 동일한지 체크한다.")
    void When_AddCartProduct_Expect_ValidateMemberAuth() {
        ReflectionTestUtils.setField(requestDTO, "email", "qkrtkdwnsFake@gmail.com");
        
        //when
        assertThatThrownBy(() -> cartService.addCartProduct(member, requestDTO))
                .isInstanceOf(NotAuthorizedException.class)
                .hasMessageContaining(ErrorCode.CUSTOMER_NOT_AUTHORIZED.getMessage());
    }
    
    @Test
    @DisplayName("장바구니는 존재하고, 해당 장바구니 상품이 존재하지 않는 경우 추가 테스트")
    void When_AddCartProduct_Expect_AddNewProduct() {
        CartEntity cart = CartEntity.builder()
                .userEmail(member.getCredentials().getEmail())
                .build();
        
        em.persist(cart);
        
        //when
        cartService.addCartProduct(member, requestDTO);
        
        //then
        CartProductRelationEntity foundCartProduct = em.find(CartProductRelationEntity.class,
                new CartProductRelationEntity.CartProductRelationEntityId(cart.getId(), 1L));
        
        assertThat(foundCartProduct).isNotNull();
        
        assertAll(
                () -> assertThat(foundCartProduct.getCartId()).isEqualTo(cart.getId()),
                () -> assertThat(foundCartProduct.getProductId()).isEqualTo(1L),
                () -> assertThat(foundCartProduct.getProductQuantity()).isEqualTo(1L)
        );
    }
    
    @Test
    @DisplayName("장바구니는 존재하고, 해당 장바구니 상품이 존재하는 경우 수정이 올바르게 되는지 테스트")
    void When_AddCartProduct_Expect_UpdateProductQuantity() {
        //given
        CartEntity cart = CartEntity.builder()
                .userEmail(member.getCredentials().getEmail())
                .build();
        
        CartEntity savedCart = em.persist(cart);
        
        CartProductRelationEntity cartProductRelation = CartProductRelationEntity.builder()
                .cartId(savedCart.getId())
                .productId(1L)
                .productQuantity(1L)
                .build();
        
        em.persist(cartProductRelation);
        
        //when
        cartService.addCartProduct(member, requestDTO);
        
        //then
        CartProductRelationEntity foundCartProduct = em.find(CartProductRelationEntity.class,
                new CartProductRelationEntity.CartProductRelationEntityId(savedCart.getId(), 1L));
        
        assertThat(foundCartProduct).isNotNull();
        assertThat(foundCartProduct.getProductQuantity()).isEqualTo(2L);
    }
}