package com.psj.itembrowser.order.service;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.psj.itembrowser.member.domain.entity.MemberEntity;
import com.psj.itembrowser.member.domain.vo.Address;
import com.psj.itembrowser.member.domain.vo.Credentials;
import com.psj.itembrowser.member.domain.vo.Gender;
import com.psj.itembrowser.member.domain.vo.Member;
import com.psj.itembrowser.member.domain.vo.MemberShipType;
import com.psj.itembrowser.member.domain.vo.Name;
import com.psj.itembrowser.member.domain.vo.Role;
import com.psj.itembrowser.member.domain.vo.Status;
import com.psj.itembrowser.order.domain.dto.response.OrderResponseDTO;
import com.psj.itembrowser.order.domain.entity.OrderEntity;
import com.psj.itembrowser.order.domain.vo.Order;
import com.psj.itembrowser.order.domain.vo.OrderStatus;
import com.psj.itembrowser.order.domain.vo.OrdersProductRelation;
import com.psj.itembrowser.order.mapper.OrderMapper;
import com.psj.itembrowser.order.persistence.OrderPersistence;
import com.psj.itembrowser.order.service.impl.OrderCalculationServiceImpl;
import com.psj.itembrowser.order.service.impl.PaymentService;
import com.psj.itembrowser.order.service.impl.ShppingInfoValidationService;
import com.psj.itembrowser.product.domain.entity.ProductEntity;
import com.psj.itembrowser.product.domain.vo.DeliveryFeeType;
import com.psj.itembrowser.product.domain.vo.Product;
import com.psj.itembrowser.product.domain.vo.ProductStatus;
import com.psj.itembrowser.product.service.impl.ProductServiceImpl;
import com.psj.itembrowser.product.service.impl.ProductValidationHelper;
import com.psj.itembrowser.security.auth.service.impl.AuthenticationServiceImpl;
import com.psj.itembrowser.security.common.exception.NotFoundException;
import com.psj.itembrowser.security.data.config.MemberRepository;
import com.psj.itembrowser.security.data.config.OrderRepository;
import com.psj.itembrowser.security.data.config.ProductRepository;
import com.psj.itembrowser.security.data.config.ShippingInfoRepository;
import com.psj.itembrowser.shippingInfos.domain.entity.ShippingInfoEntity;
import com.psj.itembrowser.shippingInfos.domain.vo.ShippingInfo;

@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
@Import({OrderPersistence.class, OrderCalculationServiceImpl.class, ProductValidationHelper.class, ShppingInfoValidationService.class,
	AuthenticationServiceImpl.class, ProductServiceImpl.class})
public class OrderSelectWithDBServiceTest {
	
	@Autowired
	private OrderService orderService;
	
	@Autowired
	private OrderRepository orderRepository;
	
	@Autowired
	private MemberRepository memberRepository;
	
	@Autowired
	private ProductRepository productRepository;
	
	@Autowired
	private ShippingInfoRepository shippingInfoRepository;
	
	@MockBean
	private PaymentService paymentService;
	
	@MockBean
	private OrderMapper orderMapper;
	
	@PersistenceContext
	EntityManager em;
	
	private Long validOrderId;
	
	private Long invalidOrderId;
	
	private OrderEntity validOrder;
	
	@BeforeEach
	public void setUp() {
		validOrderId = 1L;
		invalidOrderId = 99L;
		
		Member member = new Member(null,
			Credentials.create("test@test.com", "test"),
			Name.create("홍", "길동"),
			"010-1234-1234",
			Gender.MEN,
			Role.ROLE_CUSTOMER, Status.ACTIVE,
			MemberShipType.REGULAR,
			Address.create("서울시 강남구", "김밥빌딩 101동 302호", "01012"),
			LocalDate.of(1995, 11, 3),
			LocalDateTime.now());
		
		MemberEntity expectedMember = MemberEntity.from(member, null, null);
		
		ShippingInfo expectedShppingInfo = new ShippingInfo(null,
			1L,
			"홍길동",
			"test",
			"test",
			"010-1235-1234",
			"01111",
			"010-1234-1234", "test",
			LocalDateTime.now(),
			null,
			null);
		
		ShippingInfoEntity expectedShippingInfoEntity = ShippingInfoEntity.from(expectedShppingInfo);
		
		OrdersProductRelation expectedOrderRelation = OrdersProductRelation.of(1L, 1L, 1L,
			LocalDateTime.now(),
			null,
			null,
			new Product(
				null,
				"섬유유연제",
				1,
				"상품 디테일",
				ProductStatus.APPROVED,
				10,
				1000,
				"qkrtkdwns3410",
				LocalDateTime.now(),
				LocalDateTime.now().plusDays(10),
				"섬유유연제",
				"섬유나라",
				DeliveryFeeType.FREE,
				"배송방법",
				5000,
				15000,
				"returnCenterCode",
				Collections.emptyList(),
				Collections.emptyList()
			));
		
		this.validOrder = OrderEntity.from(Order.of(
			null,
			1L,
			OrderStatus.ACCEPT,
			LocalDateTime.now(),
			1L,
			LocalDateTime.now(),
			null,
			null,
			List.of(
				expectedOrderRelation
			),
			null,
			null
		));
		
		ProductEntity productEntity = ProductEntity.builder().name("섬유유연제").unitPrice(1000).quantity(10).build();
		
		em.persist(expectedMember);
		em.persist(productEntity);
		em.persist(expectedShippingInfoEntity);
		
		em.flush();
	}
	
	@Test
	@DisplayName("조건 없이 주문 조회 후 주문 정보 반환이 올바르게 되는지 테스트")
	void When_GetOrderWithNoCondition_Expect_ReturnOrderResponseDTO() {
		//given
		OrderEntity saved = orderRepository.save(validOrder);
		
		//when
		OrderResponseDTO orderResponseDTO = orderService.getOrderWithNoCondition(saved.getId());
		
		//then
		assertThat(orderResponseDTO).isNotNull();
		assertThat(orderResponseDTO.getId()).isEqualTo(saved.getId());
		assertThat(orderResponseDTO.getOrderStatus()).isEqualTo(saved.getOrderStatus());
		assertThat(orderResponseDTO.getOrdersProductRelations()).hasSize(1);
		assertThat(orderResponseDTO.getOrdersProductRelations().get(0).getProductId()).isEqualTo(1L);
	}
	
	@Test
	@DisplayName("삭제되지 않은 주문 조회 후 주문 정보 반환이 올바르게 되는지 테스트")
	void When_GetOrderWithNotDeleted_Expect_ReturnOrderResponseDTO() {
		//given
		OrderEntity saved = orderRepository.save(validOrder);
		
		//when
		OrderResponseDTO orderResponseDTO = orderService.getOrderWithNotDeleted(saved.getId());
		
		//then
		assertThat(orderResponseDTO).isNotNull();
		assertThat(orderResponseDTO.getId()).isEqualTo(saved.getId());
		assertThat(orderResponseDTO.getOrderStatus()).isEqualTo(saved.getOrderStatus());
		assertThat(orderResponseDTO.getOrdersProductRelations()).hasSize(1);
		assertThat(orderResponseDTO.getOrdersProductRelations().get(0).getProductId()).isEqualTo(1L);
	}
	
	@Test
	@DisplayName("주문에 대한 단건 조회의 경우 메서드가 정상적으로 orderResponseDTO 를 반환하는지")
	void test() {
		//given
		OrderEntity saved = orderRepository.save(validOrder);
		
		//when
		OrderResponseDTO orderResponseDTO = orderService.getOrderWithNoCondition(saved.getId());
		
		//then
		assertThat(orderResponseDTO).isNotNull();
		assertThat(orderResponseDTO.getId()).isEqualTo(saved.getId());
		assertThat(orderResponseDTO.getOrderStatus()).isEqualTo(saved.getOrderStatus());
		assertThat(orderResponseDTO.getOrdersProductRelations()).hasSize(1);
		assertThat(orderResponseDTO.getOrdersProductRelations().get(0).getProductId()).isEqualTo(1L);
	}
	
	@Test
	@DisplayName("조건 없이 주문 조회 시 주문 정보가 없을 경우 NotFoundException 발생")
	void When_GetOrderWithNoCondition_Expect_ThrowNotFoundException() {
		//when - then
		assertThatThrownBy(() -> orderService.getOrderWithNoCondition(invalidOrderId))
			.isInstanceOf(NotFoundException.class)
			.hasMessageContaining("Not Found Order");
	}
	
	@Test
	@DisplayName("삭제되지 않은 주문 조회 시 주문 정보가 없을 경우 NotFoundException 발생")
	void When_GetOrder_Expect_ThrowNotFoundException() {
		// when - then
		assertThatThrownBy(() -> orderService.getOrderWithNotDeleted(invalidOrderId))
			.isInstanceOf(NotFoundException.class).hasMessageContaining("Not Found Order");
	}
}