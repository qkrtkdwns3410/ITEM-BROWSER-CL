package com.psj.itembrowser.order.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import com.psj.itembrowser.member.domain.dto.response.MemberResponseDTO;
import com.psj.itembrowser.member.domain.vo.Address;
import com.psj.itembrowser.member.domain.vo.Credentials;
import com.psj.itembrowser.member.domain.vo.Member;
import com.psj.itembrowser.member.domain.vo.MemberNo;
import com.psj.itembrowser.member.domain.vo.Name;
import com.psj.itembrowser.order.controller.OrderCreateRequestDTO;
import com.psj.itembrowser.order.domain.dto.response.OrderResponseDTO;
import com.psj.itembrowser.order.domain.vo.Order;
import com.psj.itembrowser.order.domain.vo.OrdersProductRelation;
import com.psj.itembrowser.order.domain.vo.OrdersProductRelationResponseDTO;
import com.psj.itembrowser.order.mapper.OrderMapper;
import com.psj.itembrowser.order.persistence.OrderPersistence;
import com.psj.itembrowser.order.service.impl.OrderCalculationResult;
import com.psj.itembrowser.order.service.impl.OrderCalculationServiceImpl;
import com.psj.itembrowser.order.service.impl.OrderServiceImpl;
import com.psj.itembrowser.order.service.impl.PaymentService;
import com.psj.itembrowser.order.service.impl.ShppingInfoValidationService;
import com.psj.itembrowser.product.domain.vo.DeliveryFeeType;
import com.psj.itembrowser.product.domain.vo.Product;
import com.psj.itembrowser.product.domain.vo.ProductStatus;
import com.psj.itembrowser.product.service.ProductService;
import com.psj.itembrowser.product.service.impl.ProductValidationHelper;
import com.psj.itembrowser.security.auth.service.AuthenticationService;
import com.psj.itembrowser.security.common.exception.BadRequestException;
import com.psj.itembrowser.security.common.exception.ErrorCode;
import com.psj.itembrowser.security.common.exception.NotAuthorizedException;
import com.psj.itembrowser.shippingInfos.domain.dto.response.ShippingInfoResponseDTO;
import com.psj.itembrowser.shippingInfos.domain.vo.ShippingInfo;

@ExtendWith(MockitoExtension.class)
class OrderInsertServiceTest {

	@Mock
	private OrderPersistence orderPersistence;

	@Mock
	private OrderMapper orderMapper;

	@Mock
	private OrderCalculationServiceImpl orderCalculationService;

	@Mock
	private AuthenticationService authenticationService;

	@Mock
	private ProductValidationHelper productValidationHelper;

	@Mock
	private ShppingInfoValidationService shippingInfoValidationService;

	@Mock
	private PaymentService paymentService;

	@Mock
	private ProductService productService;

	@InjectMocks
	private OrderServiceImpl orderService;

	private Member member;
	private OrderCreateRequestDTO orderCreateRequestDTO;
	private OrdersProductRelation ordersProductRelation;
	private Order expectedOrder;
	private Product product;

	private ShippingInfo shippingInfo;

	@BeforeEach
	void setUp() {
		member = new Member(MemberNo.create(1L),
			Credentials.create("qkrtkdwns3410@gmail.com", "1234"),
			Name.create("박", "상준"),
			"010-1234-5678",
			Member.Gender.MEN,
			Member.Role.ROLE_CUSTOMER,
			Member.Status.ACTIVE,
			Member.MemberShipType.REGULAR,
			Address.create("서울시 강남구 역삼동", "김밥천국 101동", "01111"),
			LocalDate.of(1993, 10, 10), LocalDateTime.now());

		ordersProductRelation = new OrdersProductRelation(
			1L,
			1L, 10,
			mock(OrderCalculationResult.class),
			LocalDateTime.now(),
			LocalDateTime.now(),
			null,
			mock(Product.class)
		);

		product = new Product(
			1L,
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
		);

		orderCreateRequestDTO = new OrderCreateRequestDTO(
			1L,
			List.of(OrdersProductRelationResponseDTO.create(ordersProductRelation)),
			MemberResponseDTO.from(member),
			mock(ShippingInfoResponseDTO.class),
			LocalDateTime.now()
		);

		expectedOrder = new Order(
			1L,
			1L,
			Order.OrderStatus.PENDING,
			Order.PaymentStatus.COMPLETE,
			LocalDateTime.now(),
			1L,
			LocalDateTime.now(),
			LocalDateTime.now(),
			null,
			List.of(mock(OrdersProductRelation.class)),
			mock(Member.class),
			mock(ShippingInfo.class),
			mock(OrderCalculationResult.class)
		);

		shippingInfo = new ShippingInfo(
			1L,
			"qkrtkdwns3410",
			"박상준",
			"서울시 강남구 역삼동",
			"김밥천국 101동",
			"01111",
			"010-1234-5678",
			"010-1234-5678",
			"배송요청사항",
			LocalDateTime.now(),
			LocalDateTime.now(),
			null
		);
	}

	@Test
	@DisplayName("주문 생성 - 모든 하위 서비스가 정상적으로 동작하는 경우 - 주문 생성 성공")
	void When_CreateOrder_AllConditionAreMet_Then_ReturnOrderResponseDTO() {
		// given
		try (
			MockedStatic<ShippingInfo> shippingInfoMockedStatic = mockStatic(ShippingInfo.class);
			MockedStatic<Order> orderMockedStatic = mockStatic(Order.class);
			MockedStatic<OrderResponseDTO> orderResponseDTOMockedStatic = mockStatic(OrderResponseDTO.class);
		) {

			OrderResponseDTO expected = mock(OrderResponseDTO.class);
			given(expected.getOrdererNumber()).willReturn(1L);
			given(expected.getShippingInfoId()).willReturn(1L);
			given(expected.getOrdersProductRelations()).willReturn(List.of(mock(OrdersProductRelationResponseDTO.class)));

			Order mock = mock(Order.class);
			given(mock.getId()).willReturn(1L);

			OrderCreateRequestDTO mockedOrderCreateRequestDTO = mock(OrderCreateRequestDTO.class);

			ShippingInfoResponseDTO mockedShippingInfoResponseDTO = mock(ShippingInfoResponseDTO.class);

			shippingInfoMockedStatic.when(() -> ShippingInfo.from(mockedShippingInfoResponseDTO)).thenReturn(shippingInfo);

			given(mockedOrderCreateRequestDTO.getProducts()).willReturn(Collections.emptyList());

			orderMockedStatic.when(() -> Order.of(any(OrderCreateRequestDTO.class), any(OrderCalculationResult.class))).thenReturn(mock);

			given(orderCalculationService.calculateOrderDetails(any(OrderCreateRequestDTO.class),
				any(Member.class))).willReturn(mock(OrderCalculationResult.class));

			given(orderPersistence.getOrderWithNoCondition(mock.getId())).willReturn(expectedOrder);

			orderResponseDTOMockedStatic.when(() -> OrderResponseDTO.of(expectedOrder)).thenReturn(expected);

			// when
			OrderResponseDTO actual = orderService.createOrder(member, mockedOrderCreateRequestDTO);

			// then
			assertThat(actual).isNotNull().isExactlyInstanceOf(OrderResponseDTO.class);
			assertThat(actual.getOrdererNumber()).isEqualTo(expectedOrder.getOrdererNumber());
			assertThat(actual.getShippingInfoId()).isEqualTo(expectedOrder.getShippingInfoId());
			assertThat(actual.getOrdersProductRelations()).hasSize(expectedOrder.getProducts().size());
		}
	}

	@Test
	@DisplayName("주문 생성 - 멤버가 비활성화인 경우 NotAuthorizedException 발생")
	void When_CreateOrder_MemberIsNotActivated_Then_ThrowNotAuthorizedException() {
		// given
		Member member = mock(Member.class);
		given(member.isActivated()).willReturn(false);

		//when -  then
		assertThatThrownBy(() -> orderService.createOrder(member, orderCreateRequestDTO))
			.isInstanceOf(NotAuthorizedException.class)
			.hasMessage("Not Activated Member");
	}

	@Test
	@DisplayName("주문 생성 - 주문 상품에 대한 재고가 부족한 경우 - validateProduct 메서드에서 BadRequestException 발생")
	void When_CreateOrder_ProductStockIsNotEnough_Then_ThrowBadRequestException() {
		// given
		doThrow(new BadRequestException(ErrorCode.PRODUCT_QUANTITY_NOT_ENOUGH))
			.when(productValidationHelper).validateProduct(anyList());

		// when - then
		assertThatThrownBy(() -> orderService.createOrder(member, orderCreateRequestDTO))
			.isInstanceOf(BadRequestException.class)
			.hasMessage("Product Quantity is not enough");
	}

	@Test
	@DisplayName("주문 생성 - 배송지가 존재하지 않는 경우 - validateAddress 메서드에서 BadRequestException 발생")
	void When_CreateOrder_ShippingInfoIsNotExist_Then_ThrowBadRequestException() {
		// given
		doThrow(new BadRequestException(ErrorCode.ADDRESS_NOT_FOUND))
			.when(shippingInfoValidationService)
			.validateAddress(any(ShippingInfo.class));

		// when - then
		assertThatThrownBy(() -> orderService.createOrder(member, orderCreateRequestDTO))
			.isInstanceOf(BadRequestException.class)
			.hasMessage("Not Found Address");

		verify(productValidationHelper).validateProduct(anyList());
	}

	@Test
	@DisplayName("주문 생성 - insert 된 주문을 조회하여 반환시, 조회된 주문이 null인 경우 - NotFoundException")
	void When_CreateOrder_OrderIsNotExist_Then_ThrowNotFoundException() {
		// given
		try (
			MockedStatic<Order> orderMockedStatic = mockStatic(Order.class);
		) {
			Order mock = mock(Order.class);
			given(mock.getId()).willReturn(1L);

			orderMockedStatic.when(() -> Order.of(any(OrderCreateRequestDTO.class), any(OrderCalculationResult.class))).thenReturn(mock);

			given(orderCalculationService.calculateOrderDetails(any(OrderCreateRequestDTO.class),
				any(Member.class))).willReturn(mock(OrderCalculationResult.class));

			given(orderPersistence.getOrderWithNoCondition(anyLong())).willThrow(new BadRequestException(ErrorCode.ORDER_NOT_FOUND));

			// when - then
			assertThatThrownBy(() -> orderService.createOrder(member, orderCreateRequestDTO))
				.isInstanceOf(BadRequestException.class)
				.hasMessage("Not Found Order");
		}
	}
}