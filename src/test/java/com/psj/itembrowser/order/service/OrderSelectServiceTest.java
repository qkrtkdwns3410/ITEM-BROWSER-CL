package com.psj.itembrowser.order.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.psj.itembrowser.member.domain.vo.Address;
import com.psj.itembrowser.member.domain.vo.Credentials;
import com.psj.itembrowser.member.domain.vo.Gender;
import com.psj.itembrowser.member.domain.vo.Member;
import com.psj.itembrowser.member.domain.vo.MemberShipType;
import com.psj.itembrowser.member.domain.vo.Name;
import com.psj.itembrowser.member.domain.vo.Role;
import com.psj.itembrowser.member.domain.vo.Status;
import com.psj.itembrowser.order.domain.vo.Order;
import com.psj.itembrowser.order.domain.vo.OrderStatus;
import com.psj.itembrowser.order.domain.vo.OrdersProductRelation;
import com.psj.itembrowser.order.persistence.OrderPersistence;
import com.psj.itembrowser.order.service.impl.OrderServiceImpl;
import com.psj.itembrowser.product.domain.vo.DeliveryFeeType;
import com.psj.itembrowser.product.domain.vo.Product;
import com.psj.itembrowser.product.domain.vo.ProductStatus;
import com.psj.itembrowser.security.auth.service.AuthenticationService;
import com.psj.itembrowser.shippingInfos.domain.vo.ShippingInfo;

@ExtendWith(MockitoExtension.class)
public class OrderSelectServiceTest {
	
	@InjectMocks
	private OrderServiceImpl orderService;
	
	@Mock
	private OrderPersistence orderPersistence;
	
	@Mock
	private AuthenticationService authenticationService;
	
	private Long validOrderId;
	
	private Long invalidOrderId;
	
	private Order validOrder;
	
	@BeforeEach
	public void setUp() {
		validOrderId = 1L;
		invalidOrderId = 2L;
		Member expectedMember = new Member(1L,
			Credentials.create("test@test.com", "test"),
			Name.create("홍", "길동"),
			"010-1234-1234",
			Gender.MEN,
			Role.ROLE_CUSTOMER, Status.ACTIVE,
			MemberShipType.REGULAR,
			Address.create("서울시 강남구", "김밥빌딩 101동 302호", "01012"),
			LocalDate.of(1995, 11, 3),
			LocalDateTime.now());
		
		ShippingInfo expectedShppingInfo = new ShippingInfo(1L,
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
		
		OrdersProductRelation expectedOrderRelation1 = OrdersProductRelation.of(1L, 1L, 1,
			LocalDateTime.now(),
			null,
			null,
			new Product(
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
			));
		OrdersProductRelation expectedOrderRelation2 = OrdersProductRelation.of(2L, 1L, 1,
			LocalDateTime.now(),
			null,
			null,
			new Product(
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
			));
		
		this.validOrder = Order.of(
			1L,
			1L,
			OrderStatus.ACCEPT,
			LocalDateTime.now(),
			1L,
			LocalDateTime.now(),
			null,
			null,
			List.of(
				expectedOrderRelation1,
				expectedOrderRelation2
			),
			expectedMember,
			expectedShppingInfo);
	}
	
	/*@Test
	@DisplayName("다건 주문 조회(getOrdersWithPaginationAndNoCondition) - 모든 정보 조회시 주문 정보가 없을 경우 NotFoundException 발생")
	void When_GetOrdersWithPaginationAndNoCondition_Expect_ThrowNotFoundException() {
		try (MockedStatic<PageMethod> mockStatic = mockStatic(PageMethod.class)) {
			//given
			OrderPageRequestDTO requestDTO = mock(OrderPageRequestDTO.class);
			given(requestDTO.getPageNum()).willReturn(0);
			given(requestDTO.getPageSize()).willReturn(10);
			
			MemberEntity member = mock(MemberEntity.class);
			
			mockStatic.when(
					() -> PageMethod.startPage(requestDTO.getPageNum(), requestDTO.getPageSize()))
				.thenReturn(mock(Page.class));
			
			given(orderPersistence.getOrdersWithPaginationAndNoCondition(requestDTO)).willThrow(
				new NotFoundException(ErrorCode.ORDER_NOT_FOUND));
			
			//when - then
			assertThatThrownBy(() -> orderService.getOrdersWithPaginationAndNoCondition(member, requestDTO))
				.isInstanceOf(NotFoundException.class)
				.hasMessageContaining("Not Found Order");
		}
	}
	
	@Test
	@Disabled("실제 DB 통신으로 변경")
	@DisplayName("다건 주문 조회 (getOrdersWithPaginationAndNoCondition) - 모든 정보 조회시 주문 정보가 있을 경우 주문 정보 리스트 반환")
	void When_GetOrdersWithPaginationAndNoCondition_Expect_ReturnOrderResponseDTOList() {
		try (MockedStatic<PageMethod> mockStatic = mockStatic(PageMethod.class)) {
			//given
			OrderPageRequestDTO requestDTO = mock(OrderPageRequestDTO.class);
			given(requestDTO.getPageNum()).willReturn(0);
			given(requestDTO.getPageSize()).willReturn(10);
			
			mockStatic.when(() -> PageMethod.startPage(requestDTO.getPageNum(), requestDTO.getPageSize()))
				.thenReturn(mock(Page.class));
			
			MemberEntity member = mock(MemberEntity.class);
			
			given(orderPersistence.getOrdersWithPaginationAndNoCondition(requestDTO))
				.willReturn(List.of(validOrder));
			
			//when
			PageInfo<OrderResponseDTO> actualPages = orderService.getOrdersWithPaginationAndNoCondition(
				member, requestDTO);
			
			List<OrderResponseDTO> actualList = actualPages.getList();
			long actualTotal = actualPages.getTotal();
			
			//then
			assertThat(actualList).isNotEmpty();
			assertThat(actualTotal).isEqualTo(1);
			assertThat(actualList.get(0).getOrdererNumber()).isEqualTo(
				validOrder.getOrdererNumber());
			assertThat(actualList.get(0).getOrderStatus()).isEqualTo(validOrder.getOrderStatus());
			assertThat(actualList.get(0).getPaidDate()).isEqualTo(validOrder.getPaidDate());
			assertThat(actualList.get(0).getShippingInfoId()).isEqualTo(
				validOrder.getShippingInfoId());
			assertThat(actualList.get(0).getCreatedDate()).isEqualTo(validOrder.getCreatedDate());
			assertThat(actualList.get(0).getUpdatedDate()).isEqualTo(validOrder.getUpdatedDate());
			assertThat(actualList.get(0).getDeletedDate()).isEqualTo(validOrder.getDeletedDate());
		}
	}
	
	@Test
	@Disabled("실제 DB 통신으로 변경")
	@DisplayName("다건 주문 조회(getOrdersWithPaginationAndNoCondition) - .PageHelper.startPage() 호출 시 pageNum과 pageSize가 0 이하일 경우 IllegalArgumentException 발생")
	void When_GetOrdersWithPaginationAndNoCondition_PageHelper_Fail_Expect_ThrowIllegalArgumentException() {
		try (MockedStatic<PageMethod> mockStatic = mockStatic(PageMethod.class)) {
			//given
			OrderPageRequestDTO requestDTO = mock(OrderPageRequestDTO.class);
			given(requestDTO.getPageNum()).willReturn(-1);
			given(requestDTO.getPageSize()).willReturn(0);
			
			MemberEntity member = mock(MemberEntity.class);
			
			mockStatic.when(
					() -> PageMethod.startPage(requestDTO.getPageNum(), requestDTO.getPageSize()))
				.thenThrow(
					new RuntimeException("PageNum and PageSize must be greater than 0"));
			
			//when - then
			assertThatThrownBy(() -> orderService.getOrdersWithPaginationAndNoCondition(member, requestDTO))
				.isInstanceOf(RuntimeException.class)
				.hasMessageContaining("PageNum and PageSize must be greater than 0");
		}
	}
	
	@Test
	@Disabled("실제 DB 통신으로 변경")
	@DisplayName("다건 주문 조회(getOrdersWithPaginationAndNotDeleted) - 삭제되지 않은 주문 조회시 주문 정보가 없을 경우 NotFoundException 발생")
	void When_GetOrdersWithPaginationAndNotDeleted_Expect_ThrowNotFoundException() {
		try (MockedStatic<PageMethod> mockStatic = mockStatic(PageMethod.class)) {
			//given
			OrderPageRequestDTO requestDTO = mock(OrderPageRequestDTO.class);
			given(requestDTO.getPageNum()).willReturn(0);
			given(requestDTO.getPageSize()).willReturn(10);
			
			MemberEntity member = mock(MemberEntity.class);
			
			mockStatic.when(
					() -> PageMethod.startPage(requestDTO.getPageNum(), requestDTO.getPageSize()))
				.thenReturn(mock(Page.class));
			
			given(orderPersistence.getOrdersWithPaginationAndNotDeleted(requestDTO)).willThrow(
				new NotFoundException(ErrorCode.ORDER_NOT_FOUND));
			
			//when - then
			assertThatThrownBy(() -> orderService.getOrdersWithPaginationAndNotDeleted(member, requestDTO))
				.isInstanceOf(NotFoundException.class)
				.hasMessageContaining("Not Found Order");
		}
	}
	
	@Test
	@Disabled("실제 DB 통신으로 변경")
	@DisplayName("다건 주문 조회(getOrdersWithPaginationAndNotDeleted) - 삭제되지 않은 주문 조회시 주문 정보가 있을 경우 주문 정보 리스트 반환")
	void When_GetOrdersWithPaginationAndNotDeleted_Expect_ReturnOrderResponseDTOList() {
		try (MockedStatic<PageMethod> mockStatic = mockStatic(PageMethod.class)) {
			//given
			OrderPageRequestDTO requestDTO = mock(OrderPageRequestDTO.class);
			given(requestDTO.getPageNum()).willReturn(0);
			given(requestDTO.getPageSize()).willReturn(10);
			
			Member member = mock(Member.class);
			
			mockStatic.when(
					() -> PageMethod.startPage(requestDTO.getPageNum(), requestDTO.getPageSize()))
				.thenReturn(mock(Page.class));
			
			given(orderPersistence.getOrdersWithPaginationAndNotDeleted(requestDTO))
				.willReturn(List.of(validOrder));
			
			doNothing().when(authenticationService)
				.authorizeOrdersWhenCustomer(List.of(validOrder), member);
			
			//when
			PageInfo<OrderResponseDTO> actualPages = orderService.getOrdersWithPaginationAndNotDeleted(member, requestDTO);
			
			List<OrderResponseDTO> actualList = actualPages.getList();
			long actualTotal = actualPages.getTotal();
			
			//then
			assertThat(actualList).isNotEmpty();
			assertThat(actualTotal).isEqualTo(1);
			assertThat(actualList.get(0).getOrdererNumber()).isEqualTo(
				validOrder.getOrdererNumber());
			assertThat(actualList.get(0).getOrderStatus()).isEqualTo(validOrder.getOrderStatus());
			assertThat(actualList.get(0).getPaidDate()).isEqualTo(validOrder.getPaidDate());
			assertThat(actualList.get(0).getShippingInfoId()).isEqualTo(
				validOrder.getShippingInfoId());
			assertThat(actualList.get(0).getCreatedDate()).isEqualTo(validOrder.getCreatedDate());
			assertThat(actualList.get(0).getUpdatedDate()).isEqualTo(validOrder.getUpdatedDate());
			assertThat(actualList.get(0).getDeletedDate()).isEqualTo(validOrder.getDeletedDate());
		}
	}
	
	@Test
	@Disabled("실제 DB 통신으로 변경")
	@DisplayName("다건 주문 조회(getOrdersWithPaginationAndNotDeleted) - .PageHelper.startPage() 호출 시 pageNum과 pageSize가 0 이하일 경우 RuntimeException 발생")
	void When_GetOrdersWithPaginationAndNotDeleted_PageHelper_Fail_Expect_ThrowRuntimeException() {
		try (MockedStatic<PageMethod> mockStatic = mockStatic(PageMethod.class)) {
			//given
			OrderPageRequestDTO requestDTO = mock(OrderPageRequestDTO.class);
			given(requestDTO.getPageNum()).willReturn(-1);
			given(requestDTO.getPageSize()).willReturn(0);
			
			MemberEntity member = mock(MemberEntity.class);
			
			mockStatic.when(
					() -> PageMethod.startPage(requestDTO.getPageNum(), requestDTO.getPageSize()))
				.thenThrow(
					new RuntimeException("PageNum and PageSize must be greater than 0"));
			
			//when - then
			assertThatThrownBy(() -> orderService.getOrdersWithPaginationAndNotDeleted(member, requestDTO))
				.isInstanceOf(RuntimeException.class)
				.hasMessageContaining("PageNum and PageSize must be greater than 0");
		}
	}
	
	@Test
	@Disabled("실제 DB 통신으로 변경")
	@DisplayName("다건 주문 조회(getOrdersWithPaginationAndNotDeleted) - authorizeOrdersWhenCustomer 호출시 권한 문제 발생의 경우 BadRequestException 발생")
	void When_GetOrdersWithPaginationAndNotDeleted_Expect_ThrowBadRequestException() {
		try (MockedStatic<PageMethod> mockStatic = mockStatic(PageMethod.class)) {
			//given
			OrderPageRequestDTO requestDTO = mock(OrderPageRequestDTO.class);
			given(requestDTO.getPageNum()).willReturn(0);
			given(requestDTO.getPageSize()).willReturn(10);
			
			MemberEntity member = mock(MemberEntity.class);
			
			mockStatic.when(
					() -> PageMethod.startPage(requestDTO.getPageNum(), requestDTO.getPageSize()))
				.thenReturn(mock(Page.class));
			
			given(orderPersistence.getOrdersWithPaginationAndNotDeleted(requestDTO))
				.willReturn(List.of(validOrder));
			
			doThrow(new BadRequestException(ErrorCode.ORDER_NOT_CANCELABLE)).when(
					authenticationService)
				.authorizeOrdersWhenCustomer(List.of(validOrder), member);
			
			//when - then
			assertThatThrownBy(() -> orderService.getOrdersWithPaginationAndNotDeleted(member, requestDTO))
				.isInstanceOf(BadRequestException.class);
		}
	}*/
}