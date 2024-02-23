package com.psj.itembrowser.order.service;

import static org.mockito.BDDMockito.*;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.psj.itembrowser.config.annotation.RepositoryTest;
import com.psj.itembrowser.member.domain.dto.response.MemberResponseDTO;
import com.psj.itembrowser.member.domain.entity.MemberEntity;
import com.psj.itembrowser.member.domain.vo.Status;
import com.psj.itembrowser.member.repository.MemberRepository;
import com.psj.itembrowser.order.domain.dto.request.OrderCreateRequestDTO;
import com.psj.itembrowser.order.domain.dto.response.OrderResponseDTO;
import com.psj.itembrowser.order.domain.vo.OrdersProductRelationResponseDTO;
import com.psj.itembrowser.order.mapper.OrderMapper;
import com.psj.itembrowser.order.persistence.OrderPersistence;
import com.psj.itembrowser.order.repository.CustomOrderRepository;
import com.psj.itembrowser.order.repository.OrderRepository;
import com.psj.itembrowser.payment.service.PaymentService;
import com.psj.itembrowser.product.domain.dto.response.ProductResponseDTO;
import com.psj.itembrowser.product.domain.entity.ProductEntity;
import com.psj.itembrowser.product.service.ProductService;
import com.psj.itembrowser.product.service.ProductValidationHelper;
import com.psj.itembrowser.security.auth.service.impl.AuthenticationService;
import com.psj.itembrowser.shippingInfos.domain.dto.response.ShippingInfoResponseDTO;
import com.psj.itembrowser.shippingInfos.domain.entity.ShippingInfoEntity;
import com.psj.itembrowser.shippingInfos.service.ShppingInfoValidationService;

@RepositoryTest
public class OrderInsertWithDBServiceTest {
	
	@Autowired
	private OrderRepository orderRepository;
	
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
	
	@BeforeEach
	void init() {
		OrderPersistence orderPersistence = new OrderPersistence(orderMapper, orderRepository, customOrderRepository);
		orderService = new OrderService(orderRepository, orderPersistence, orderMapper, orderCalculationService, authenticationService,
			productValidationHelper, shppingInfoValidationService, paymentService, productService);
	}
	
	@BeforeEach
	public void setUp() {
	
	}
	
	@Test
	@DisplayName("주문 생성 - 모든 하위 서비스가 정상적으로 동작하는 경우 - 주문 생성 성공")
	public void When_AllSubServiceIsCorrectlyWork_Expect_200() {
		// given
		MemberEntity member = MemberEntity.builder()
			.status(Status.ACTIVE)
			.build();
		
		ProductEntity product = ProductEntity.builder().build();
		
		ProductEntity savedProduct = em.persistFlushFind(product);
		
		ShippingInfoEntity shippingInfo = ShippingInfoEntity.builder().build();
		
		OrdersProductRelationResponseDTO ordersProductRelationResponseDTO = OrdersProductRelationResponseDTO.builder()
			.groupId(1L)
			.productId(1L)
			.productQuantity(1)
			.productResponseDTO(ProductResponseDTO.from(savedProduct))
			.build();
		
		OrderCreateRequestDTO dto = OrderCreateRequestDTO.builder()
			.member(MemberResponseDTO.from(member))
			.products(List.of(ordersProductRelationResponseDTO))
			.shippingInfo(ShippingInfoResponseDTO.from(shippingInfo))
			.build();
		
		OrderCalculationResult orderResult = OrderCalculationResult.of(
			BigDecimal.valueOf(1000),
			BigDecimal.valueOf(100),
			1000L,
			BigDecimal.valueOf(1900)
		);
		
		given(orderCalculationService.calculateOrderDetails(dto, member)).willReturn(orderResult);
		
		// when
		OrderResponseDTO actualOrderResponseDTO = orderService.createOrder(member, dto);
		
		// then
		//TODO 이어서 진행해야합니다.
	}
}