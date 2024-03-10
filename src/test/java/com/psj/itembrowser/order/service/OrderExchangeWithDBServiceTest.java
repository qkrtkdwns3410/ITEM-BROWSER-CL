package com.psj.itembrowser.order.service;

import com.psj.itembrowser.config.annotation.ServiceWithDBTest;
import com.psj.itembrowser.member.domain.dto.request.MemberRequestDTO;
import com.psj.itembrowser.member.domain.entity.MemberEntity;
import com.psj.itembrowser.member.domain.vo.Address;
import com.psj.itembrowser.member.domain.vo.Credentials;
import com.psj.itembrowser.member.domain.vo.Name;
import com.psj.itembrowser.member.domain.vo.Status;
import com.psj.itembrowser.order.domain.dto.request.OrderCreateRequestDTO;
import com.psj.itembrowser.order.domain.dto.request.OrdersProductRelationRequestDTO;
import com.psj.itembrowser.order.domain.entity.ExchangeOrderEntityRepository;
import com.psj.itembrowser.order.mapper.OrderMapper;
import com.psj.itembrowser.order.persistence.OrderPersistence;
import com.psj.itembrowser.order.repository.CustomOrderRepository;
import com.psj.itembrowser.order.repository.OrderRepository;
import com.psj.itembrowser.order.repository.OrdersProductRelationRepository;
import com.psj.itembrowser.payment.service.PaymentService;
import com.psj.itembrowser.product.domain.dto.response.ProductResponseDTO;
import com.psj.itembrowser.product.domain.entity.ProductEntity;
import com.psj.itembrowser.product.service.ProductService;
import com.psj.itembrowser.product.service.ProductValidationHelper;
import com.psj.itembrowser.security.auth.service.impl.AuthenticationService;
import com.psj.itembrowser.shippingInfos.domain.dto.request.ShippingInfoRequestDTO;
import com.psj.itembrowser.shippingInfos.domain.entity.ShippingInfoEntity;
import com.psj.itembrowser.shippingInfos.service.ShppingInfoValidationService;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.LocalDateTime;
import java.util.List;

@ServiceWithDBTest
public class OrderExchangeWithDBServiceTest {
    
    @Autowired
    private OrderRepository orderRepository;
    
    @Autowired
    private OrdersProductRelationRepository ordersProductRelationRepository;
    
    @Autowired
    private ExchangeOrderEntityRepository exchangeOrderEntityRepository;
    
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
    
    private OrderCreateRequestDTO orderCreateRequestDTO;
    
    private ShippingInfoEntity shippingInfo;
    
    
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
        
        ProductEntity product = ProductEntity.builder().build();
        
        em.persist(product);
        
        shippingInfo = ShippingInfoEntity.builder()
                .memberNo(member.getMemberNo())
                .build();
        
        em.persist(shippingInfo);
        
        OrdersProductRelationRequestDTO ordersProductRelationResponseDTO = OrdersProductRelationRequestDTO.builder()
                .groupId(1L)
                .productId(1L)
                .productQuantity(1)
                .productResponseDTO(ProductResponseDTO.from(product))
                .build();
        
        orderCreateRequestDTO = OrderCreateRequestDTO.builder()
                .ordererNumber(member.getMemberNo())
                .member(MemberRequestDTO.from(member))
                .products(List.of(ordersProductRelationResponseDTO))
                .shippingInfo(ShippingInfoRequestDTO.from(shippingInfo))
                .build();
    }
}