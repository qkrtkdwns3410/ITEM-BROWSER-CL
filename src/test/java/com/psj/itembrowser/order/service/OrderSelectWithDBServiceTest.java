package com.psj.itembrowser.order.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;

import com.psj.itembrowser.config.annotation.RepositoryTest;
import com.psj.itembrowser.member.domain.entity.MemberEntity;
import com.psj.itembrowser.member.domain.vo.Address;
import com.psj.itembrowser.member.domain.vo.Credentials;
import com.psj.itembrowser.member.domain.vo.Name;
import com.psj.itembrowser.member.domain.vo.Role;
import com.psj.itembrowser.member.repository.MemberRepository;
import com.psj.itembrowser.order.domain.dto.request.OrderPageRequestDTO;
import com.psj.itembrowser.order.domain.dto.response.OrderResponseDTO;
import com.psj.itembrowser.order.domain.entity.OrderEntity;
import com.psj.itembrowser.order.domain.entity.OrdersProductRelationEntity;
import com.psj.itembrowser.order.domain.vo.OrderStatus;
import com.psj.itembrowser.order.mapper.OrderMapper;
import com.psj.itembrowser.order.persistence.OrderPersistence;
import com.psj.itembrowser.order.repository.CustomOrderRepository;
import com.psj.itembrowser.order.repository.OrderRepository;
import com.psj.itembrowser.product.domain.entity.ProductEntity;
import com.psj.itembrowser.product.service.ProductService;
import com.psj.itembrowser.product.service.ProductValidationHelper;
import com.psj.itembrowser.security.auth.service.impl.AuthenticationService;
import com.psj.itembrowser.security.common.exception.NotFoundException;
import com.psj.itembrowser.shippingInfos.domain.entity.ShippingInfoEntity;

@RepositoryTest
public class OrderSelectWithDBServiceTest {

	@Autowired
	private OrderRepository orderRepository;

	@Autowired
	private MemberRepository memberRepository;

	@Autowired
	private CustomOrderRepository customOrderRepository;

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

	@PersistenceContext
	EntityManager em;

	private Long validOrderId;

	private Long invalidOrderId;

	private OrderEntity validOrder;

	private MemberEntity savedMember;

	@BeforeEach
	void init() {
		OrderPersistence orderPersistence = new OrderPersistence(orderMapper, orderRepository, customOrderRepository);
		orderService = new OrderService(orderPersistence, orderMapper, orderCalculationService, authenticationService, productValidationHelper,
			shppingInfoValidationService, paymentService, productService);
	}

	@BeforeEach
	public void setUp() {
		validOrderId = 1L;
		invalidOrderId = 99L;

		MemberEntity expectedMember = MemberEntity.builder()
			.credentials(Credentials.builder().email("qkrtkdwns3410@gmail.com").password("PasswordIsHard!@#").build())
			.address(Address.builder().addressMain("포항시 남구 연일읍 유강길 10 - 44").addressSub("김밥아파트 101동 302호").zipCode("08593").build())
			.name(Name.builder().firstName("홍").lastName("길동").build())
			.build();

		savedMember = memberRepository.save(expectedMember);

		ShippingInfoEntity expectedShippingInfoEntity = ShippingInfoEntity.builder()
			.memberNo(savedMember.getMemberNo())
			.build();

		ProductEntity productEntity = ProductEntity.builder().name("섬유유연제").unitPrice(1000).quantity(10).build();

		OrdersProductRelationEntity expectedOrderRelation = OrdersProductRelationEntity.builder()
			.groupId(1L)
			.productId(1L)
			.productQuantity(1)
			.createdDate(LocalDateTime.now())
			.updatedDate(LocalDateTime.now())
			.deletedDate(null)
			.product(productEntity)
			.build();

		this.validOrder = OrderEntity.builder()
			.id(1L)
			.member(expectedMember)
			.orderStatus(OrderStatus.ACCEPT)
			.paidDate(LocalDateTime.now())
			.shippingInfo(expectedShippingInfoEntity)
			.ordersProductRelations(List.of(expectedOrderRelation))
			.createdDate(LocalDateTime.now())
			.updatedDate(LocalDateTime.now())
			.deletedDate(null)
			.build();

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

	@Test
	@DisplayName("다건 주문 조회 (getOrdersWithPaginationAndNoCondition) - 모든 정보 조회시 주문 정보가 있을 경우 주문 정보 리스트 반환")
	void When_GetOrdersWithPaginationAndNoCondition_Expect_ReturnOrderResponseDTOList() {
		//given
		MemberEntity member = MemberEntity.builder().role(Role.ROLE_CUSTOMER).build();

		OrderEntity expectedOrder = orderRepository.save(validOrder);

		OrderPageRequestDTO dto = OrderPageRequestDTO.builder().pageNum(0).pageSize(1).userNumber(1L).build();

		//when
		Page<OrderResponseDTO> ordersWithPaginationAndNoCondition = orderService.getOrdersWithPaginationAndNoCondition(member, dto);

		//then
		assertThat(ordersWithPaginationAndNoCondition).isNotNull();
		assertThat(ordersWithPaginationAndNoCondition.getContent()).isNotNull();
		assertThat(ordersWithPaginationAndNoCondition.getTotalPages()).isEqualTo(1);
		assertThat(ordersWithPaginationAndNoCondition.getContent().get(0).getId()).isEqualTo(expectedOrder.getId());
		assertThat(ordersWithPaginationAndNoCondition.getContent().get(0).getOrderStatus()).isEqualTo(expectedOrder.getOrderStatus());
		assertThat(ordersWithPaginationAndNoCondition.getContent().get(0).getOrdersProductRelations()).hasSize(1);
		assertThat(ordersWithPaginationAndNoCondition.getContent().get(0).getOrdersProductRelations().get(0).getProductId()).isEqualTo(1L);
	}

	@Test
	@DisplayName("다건 주문 조회 (getOrdersWithPaginationAndNoCondition) - 모든 정보 조회시 주문 정보가 없을 경우 NotFoundException 발생")
	void When_GetOrdersWithPaginationAndNoCondition_Expect_ThrowNotFoundException() {
		//given
		MemberEntity member = MemberEntity.builder().role(Role.ROLE_CUSTOMER).build();

		OrderPageRequestDTO dto = OrderPageRequestDTO.builder().pageNum(0).pageSize(1).userNumber(1L).build();

		//when - then
		assertThatThrownBy(() -> orderService.getOrdersWithPaginationAndNoCondition(member, dto))
			.isInstanceOf(NotFoundException.class)
			.hasMessageContaining("Not Found Order");
	}

	@Test
	@DisplayName("다건 주문 조회 (getOrdersWithPaginationAndNotDeleted) - 삭제되지 않은 주문 조회시 주문 정보가 있을 경우 주문 정보 리스트 반환")
	void When_GetOrdersWithPaginationAndNotDeleted_Expect_ReturnOrderResponseDTOList() {
		//given
		MemberEntity member = MemberEntity.builder()
			.memberNo(2L)
			.credentials(Credentials.builder().email("qkrtkdwns34102@gmail.com").password("PasswordIsHard!@#").build())
			.address(Address.builder().addressMain("포항시 남구 연일읍 유강길 10 - 44").addressSub("김밥아파트 101동 302호").zipCode("08593").build())
			.name(Name.builder().firstName("홍").lastName("길동").build())
			.role(Role.ROLE_CUSTOMER)
			.build();

		MemberEntity savedMember = memberRepository.save(member);

		OrderEntity deletedOrder = OrderEntity.builder()
			.id(1L)
			.orderStatus(OrderStatus.CANCELED)
			.member(member)
			.deletedDate(LocalDateTime.now().minusDays(1))
			.createdDate(LocalDateTime.now().minusDays(1))
			.updatedDate(LocalDateTime.now().minusDays(1))
			.build();

		orderRepository.save(deletedOrder);

		OrderEntity notDeletedOrder = OrderEntity.builder()
			.id(2L)
			.orderStatus(OrderStatus.ACCEPT)
			.member(member)
			.deletedDate(null)
			.createdDate(LocalDateTime.now().minusDays(1))
			.updatedDate(LocalDateTime.now().minusDays(1))
			.build();

		OrderEntity expectedOrder = orderRepository.save(notDeletedOrder);

		OrderPageRequestDTO dto = OrderPageRequestDTO.builder().pageNum(0).pageSize(10).userNumber(savedMember.getMemberNo()).build();

		doNothing().when(authenticationService).authorizeOrdersWhenCustomer(any(Page.class), any(MemberEntity.class));

		//when
		Page<OrderResponseDTO> ordersWithPaginationAndNotDeleted = orderService.getOrdersWithPaginationAndNotDeleted(member, dto);

		//then
		assertThat(ordersWithPaginationAndNotDeleted).isNotNull();
		assertThat(ordersWithPaginationAndNotDeleted.getContent()).isNotNull();
		assertThat(ordersWithPaginationAndNotDeleted.getTotalPages()).isEqualTo(1);
		assertThat(ordersWithPaginationAndNotDeleted.getContent().get(0).getId()).isEqualTo(expectedOrder.getId());
		assertThat(ordersWithPaginationAndNotDeleted.getContent().get(0).getMember().getMemberNo()).isEqualTo(
			expectedOrder.getMember().getMemberNo());
		assertThat(ordersWithPaginationAndNotDeleted.getContent().get(0).getOrderStatus()).isEqualTo(expectedOrder.getOrderStatus());
		assertThat(ordersWithPaginationAndNotDeleted.getContent().get(0).getOrdersProductRelations()).hasSize(0);
	}

	@Test
	@DisplayName("삭제되지 않은 주문 조회 시 주문 정보가 없을 경우 NotFoundException 발생")
	void When_GetOrdersWithPaginationAndNotDeleted_Expect_ThrowNotFoundException() {
		//given
		MemberEntity member = MemberEntity.builder().role(Role.ROLE_CUSTOMER).build();

		OrderPageRequestDTO dto = OrderPageRequestDTO.builder().pageNum(0).pageSize(1).userNumber(1L).build();

		//when - then
		assertThatThrownBy(() -> orderService.getOrdersWithPaginationAndNotDeleted(member, dto))
			.isInstanceOf(NotFoundException.class)
			.hasMessageContaining("Not Found Order");
	}
}