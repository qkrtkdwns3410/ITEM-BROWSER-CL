package com.psj.itembrowser.order.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.util.ReflectionTestUtils;

import com.psj.itembrowser.config.annotation.ServiceWithDBTest;
import com.psj.itembrowser.member.domain.dto.response.MemberResponseDTO;
import com.psj.itembrowser.member.domain.entity.MemberEntity;
import com.psj.itembrowser.member.domain.vo.Address;
import com.psj.itembrowser.member.domain.vo.Credentials;
import com.psj.itembrowser.member.domain.vo.Name;
import com.psj.itembrowser.member.domain.vo.Status;
import com.psj.itembrowser.member.repository.MemberRepository;
import com.psj.itembrowser.order.domain.dto.request.OrderCreateRequestDTO;
import com.psj.itembrowser.order.domain.dto.response.OrderResponseDTO;
import com.psj.itembrowser.order.domain.vo.OrderStatus;
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
import com.psj.itembrowser.security.common.exception.BadRequestException;
import com.psj.itembrowser.security.common.exception.ErrorCode;
import com.psj.itembrowser.security.common.exception.NotAuthorizedException;
import com.psj.itembrowser.shippingInfos.domain.dto.response.ShippingInfoResponseDTO;
import com.psj.itembrowser.shippingInfos.domain.entity.ShippingInfoEntity;
import com.psj.itembrowser.shippingInfos.service.ShppingInfoValidationService;

@ServiceWithDBTest
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
	
	private static final LocalDateTime NOW = LocalDateTime.now();
	
	private MemberEntity member;
	
	private OrderCreateRequestDTO orderCreateRequestDTO;
	
	private ShippingInfoEntity shippingInfo;
	
	@BeforeEach
	void init() {
		OrderPersistence orderPersistence = new OrderPersistence(orderMapper, orderRepository, customOrderRepository);
		orderService = new OrderService(orderRepository, orderPersistence, orderMapper, orderCalculationService, authenticationService,
			productValidationHelper, shppingInfoValidationService, paymentService, productService);
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
		
		OrdersProductRelationResponseDTO ordersProductRelationResponseDTO = OrdersProductRelationResponseDTO.builder()
			.groupId(1L)
			.productId(1L)
			.productQuantity(1)
			.productResponseDTO(ProductResponseDTO.from(product))
			.build();
		
		orderCreateRequestDTO = OrderCreateRequestDTO.builder()
			.member(MemberResponseDTO.from(member))
			.products(List.of(ordersProductRelationResponseDTO))
			.shippingInfo(ShippingInfoResponseDTO.from(shippingInfo))
			.build();
	}
	
	@Test
	@DisplayName("주문 생성 - 모든 하위 서비스가 정상적으로 동작하는 경우 - 주문 생성 성공")
	void When_AllSubServiceIsCorrectlyWork_Expect_200() {
		// given
		OrderCalculationResult orderResult = OrderCalculationResult.of(
			BigDecimal.valueOf(1000),
			BigDecimal.valueOf(100),
			1000L,
			BigDecimal.valueOf(1900)
		);
		
		given(orderCalculationService.calculateOrderDetails(orderCreateRequestDTO, member)).willReturn(orderResult);
		
		// when
		OrderResponseDTO actualOrderResponseDTO = orderService.createOrder(member, orderCreateRequestDTO);
		
		// then
		assertThat(actualOrderResponseDTO).isNotNull();
		assertThat(actualOrderResponseDTO.getId()).isNotNull();
		assertThat(actualOrderResponseDTO.getMember().getCredentials().getEmail()).isNotNull().isEqualTo(member.getCredentials().getEmail());
		assertThat(actualOrderResponseDTO.getOrdererNumber()).isNotNull().isEqualTo(member.getMemberNo());
		
		assertThat(actualOrderResponseDTO.getOrdersProductRelations()).isNotNull().isNotEmpty();
		
		assertThat(actualOrderResponseDTO.getOrderStatus()).isNotNull().isEqualTo(OrderStatus.PENDING);
		assertThat(actualOrderResponseDTO.getShippingInfoId()).isNotNull().isEqualTo(shippingInfo.getId());
		
		assertThat(actualOrderResponseDTO.getPaidDate()).isNotNull().isAfter(NOW);
		assertThat(actualOrderResponseDTO.getCreatedDate()).isNotNull().isAfter(NOW);
		assertThat(actualOrderResponseDTO.getUpdatedDate()).isNotNull().isAfter(NOW);
		assertThat(actualOrderResponseDTO.getDeletedDate()).isNull();
	}
	
	@Test
	@DisplayName("주문 생성시 회원이 비활성화 상태인 경우 - 주문 생성 실패")
	void When_MemberIsNotActivated_Expect_400() {
		// given
		ReflectionTestUtils.setField(member, "status", Status.DISABLED);
		
		ProductEntity product = ProductEntity.builder().build();
		
		em.persist(product);
		
		ShippingInfoEntity shippingInfo = ShippingInfoEntity.builder()
			.memberNo(member.getMemberNo())
			.build();
		
		em.persist(shippingInfo);
		
		OrdersProductRelationResponseDTO ordersProductRelationResponseDTO = OrdersProductRelationResponseDTO.builder()
			.groupId(1L)
			.productId(1L)
			.productQuantity(1)
			.productResponseDTO(ProductResponseDTO.from(product))
			.build();
		
		OrderCreateRequestDTO dto = OrderCreateRequestDTO.builder()
			.member(MemberResponseDTO.from(member))
			.products(List.of(ordersProductRelationResponseDTO))
			.shippingInfo(ShippingInfoResponseDTO.from(shippingInfo))
			.build();
		
		// when
		assertThatThrownBy(() -> orderService.createOrder(member, dto))
			// then
			.isInstanceOf(NotAuthorizedException.class)
			.hasMessage(ErrorCode.NOT_ACTIVATED_MEMBER.getMessage());
	}
	
	@Test
	@DisplayName("주문 생성중에 주문상품 재고에 대한 검증이 실패하는 경우")
	void When_ProductValidationFailed_Expect_400() {
		// given
		doThrow(new BadRequestException(ErrorCode.PRODUCT_QUANTITY_NOT_ENOUGH))
			.when(productValidationHelper).validateProduct(anyList());
		
		// when
		assertThatThrownBy(() -> orderService.createOrder(member, orderCreateRequestDTO))
			// then
			.isInstanceOf(BadRequestException.class)
			.hasMessage(ErrorCode.PRODUCT_QUANTITY_NOT_ENOUGH.getMessage());
	}
	
	@Test
	@DisplayName("주문 생성시 주문한 상품이 없는 경우")
	void When_NoProduct_Expect_400() {
		// given
		OrderCreateRequestDTO dto = OrderCreateRequestDTO.builder()
			.member(MemberResponseDTO.from(member))
			.products(List.of())
			.shippingInfo(ShippingInfoResponseDTO.from(shippingInfo))
			.build();
		
		doThrow(new BadRequestException(ErrorCode.PRODUCT_NOT_FOUND))
			.when(productValidationHelper).validateProduct(anyList());
		
		// when
		assertThatThrownBy(() -> orderService.createOrder(member, dto))
			// then
			.isInstanceOf(BadRequestException.class)
			.hasMessage(ErrorCode.PRODUCT_NOT_FOUND.getMessage());
	}
}