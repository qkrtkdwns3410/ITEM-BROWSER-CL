package com.psj.itembrowser.order.service;

import com.psj.itembrowser.config.annotation.ServiceWithDBTest;
import com.psj.itembrowser.member.domain.entity.MemberEntity;
import com.psj.itembrowser.member.domain.vo.Address;
import com.psj.itembrowser.member.domain.vo.Credentials;
import com.psj.itembrowser.member.domain.vo.Name;
import com.psj.itembrowser.member.domain.vo.Status;
import com.psj.itembrowser.member.repository.MemberRepository;
import com.psj.itembrowser.order.domain.entity.ExchangeOrderEntityRepository;
import com.psj.itembrowser.order.domain.entity.OrderEntity;
import com.psj.itembrowser.order.domain.entity.OrdersProductRelationEntity;
import com.psj.itembrowser.order.domain.vo.OrderStatus;
import com.psj.itembrowser.order.mapper.OrderMapper;
import com.psj.itembrowser.order.persistence.OrderPersistence;
import com.psj.itembrowser.order.repository.CustomOrderRepository;
import com.psj.itembrowser.order.repository.OrderRepository;
import com.psj.itembrowser.order.repository.OrdersProductRelationRepository;
import com.psj.itembrowser.payment.service.PaymentService;
import com.psj.itembrowser.product.domain.entity.ProductEntity;
import com.psj.itembrowser.product.service.ProductService;
import com.psj.itembrowser.product.service.ProductValidationHelper;
import com.psj.itembrowser.security.auth.service.impl.AuthenticationService;
import com.psj.itembrowser.security.common.exception.BadRequestException;
import com.psj.itembrowser.security.common.exception.ErrorCode;
import com.psj.itembrowser.shippingInfos.domain.entity.ShippingInfoEntity;
import com.psj.itembrowser.shippingInfos.service.ShppingInfoValidationService;
import org.assertj.core.api.ThrowableAssert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

@ServiceWithDBTest
public class OrderDeleteWithDBServiceTest {
    
    @Autowired
    private OrderRepository orderRepository;
    
    @Autowired
    private OrdersProductRelationRepository ordersProductRelationRepository;
    
    @Autowired
    private ExchangeOrderEntityRepository exchangeOrderEntityRepository;
    
    @Autowired
    private MemberRepository memberRepository;
    
    @Autowired
    private CustomOrderRepository customOrderRepository;
    
    @Autowired
    private TestEntityManager em;
    
    @MockBean
    private OrderMapper orderMapper;
    
    @Mock
    private AuthenticationService authenticationService;
    
    @Mock
    private PaymentService paymentService;
    
    @Mock
    private OrderCalculationService orderCalculationService;
    
    @Mock
    private ProductValidationHelper productValidationHelper;
    
    @Mock
    private ShppingInfoValidationService shppingInfoValidationService;
    
    @Mock
    private ProductService productService;
    
    private OrderService orderService;
    
    private static final LocalDateTime NOW = LocalDateTime.now();
    
    private MemberEntity member;
    
    private ShippingInfoEntity shippingInfo;
    
    private ProductEntity product;
    
    @BeforeEach
    void init() {
        OrderPersistence orderPersistence = new OrderPersistence(orderMapper, orderRepository, customOrderRepository, ordersProductRelationRepository);
        orderService = new OrderService(orderRepository, exchangeOrderEntityRepository, orderPersistence, orderCalculationService, authenticationService, productValidationHelper, shppingInfoValidationService, paymentService, productService);
    }
    
    @BeforeEach
    void setUp() {
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
                .build();
        
        em.persist(member);
        
        product = ProductEntity.builder().quantity(10).build();
        
        em.persist(product);
        
        shippingInfo = ShippingInfoEntity.builder()
                .memberNo(member.getMemberNo())
                .build();
        
        em.persist(shippingInfo);
    }
    
    @ParameterizedTest
    @EnumSource(value = OrderStatus.class, names = {"PENDING", "ACCEPT", "INSTRUCT"}, mode = EnumSource.Mode.INCLUDE)
    @DisplayName("주문 삭제 성공에 대해 주문에 대한 상품들도 취소되어야함 - 삭제일 Order 에 올바르게 업데이트되는지")
    void When_DeleteOrder_Expect_OrderAndOrderProductsAreCanceled(OrderStatus cancelableStatus) {
        //given
        OrderEntity removableOrder = OrderEntity.builder()
                .orderStatus(cancelableStatus)
                .build();
        
        em.persist(removableOrder);
        
        OrdersProductRelationEntity removableOrderProduct = OrdersProductRelationEntity.builder()
                .groupId(removableOrder.getId())
                .productId(product.getId())
                .productQuantity(1)
                .product(product)
                .build();
        
        em.persist(removableOrderProduct);
        
        ReflectionTestUtils.setField(removableOrder, "ordersProductRelations", Collections.singletonList(removableOrderProduct));
        
        //when
        orderService.removeOrder(removableOrder.getId());
        
        //then
        OrderEntity foundOrder = em.find(OrderEntity.class, removableOrder.getId());
        
        assertAll(
                () -> assertThat(foundOrder.getDeletedDate()).isNotNull().isAfter(NOW),
                () -> assertThat(foundOrder.getOrderStatus()).isEqualTo(OrderStatus.CANCELED),
                () -> assertThat(foundOrder.getOrdersProductRelations()).isNotNull()
                        .extracting(OrdersProductRelationEntity::getDeletedDate)
                        .allSatisfy(deletedDate -> assertThat(deletedDate).isNotNull().isAfter(NOW))
        );
        
        assertThat(foundOrder.getOrdersProductRelations().get(0).getProduct().getQuantity()).isEqualTo(11);
    }
    
    @ParameterizedTest
    @EnumSource(value = OrderStatus.class, names = {"PENDING", "ACCEPT", "INSTRUCT"}, mode = EnumSource.Mode.EXCLUDE)
    @DisplayName("주문 삭제가 이루어지면 안되는 조건인 경우 - BadRequestException 이 발생해야함")
    void When_DeleteOrder_Expect_BadRequestException(OrderStatus nonCancelableStatus) {
        //given
        OrderEntity removableOrder = OrderEntity.builder()
                .orderStatus(nonCancelableStatus)
                .build();
        
        em.persist(removableOrder);
        
        //when
        ThrowableAssert.ThrowingCallable throwingCallable = () -> orderService.removeOrder(removableOrder.getId());
        
        //then
        assertThatThrownBy(throwingCallable)
                .isInstanceOf(BadRequestException.class)
                .hasMessage(ErrorCode.ORDER_NOT_CANCELABLE.getMessage());
    }
}