package com.psj.itembrowser.order.controller;

import static com.psj.itembrowser.security.common.exception.ErrorCode.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.http.MediaType.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.oauth2.resource.OAuth2ResourceServerProperties.Jwt;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper;
import com.epages.restdocs.apispec.ResourceDocumentation;
import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.psj.itembrowser.member.annotation.MockMember;
import com.psj.itembrowser.member.domain.dto.response.MemberResponseDTO;
import com.psj.itembrowser.member.domain.entity.MemberEntity;
import com.psj.itembrowser.member.domain.vo.Address;
import com.psj.itembrowser.member.domain.vo.Credentials;
import com.psj.itembrowser.member.domain.vo.Gender;
import com.psj.itembrowser.member.domain.vo.Member;
import com.psj.itembrowser.member.domain.vo.MemberShipType;
import com.psj.itembrowser.member.domain.vo.Name;
import com.psj.itembrowser.member.domain.vo.Role;
import com.psj.itembrowser.member.domain.vo.Status;
import com.psj.itembrowser.order.domain.dto.request.OrderPageRequestDTO;
import com.psj.itembrowser.order.domain.dto.response.OrderResponseDTO;
import com.psj.itembrowser.order.domain.vo.Order;
import com.psj.itembrowser.order.domain.vo.OrderStatus;
import com.psj.itembrowser.order.domain.vo.OrdersProductRelation;
import com.psj.itembrowser.order.service.OrderService;
import com.psj.itembrowser.product.domain.vo.DeliveryFeeType;
import com.psj.itembrowser.product.domain.vo.Product;
import com.psj.itembrowser.product.domain.vo.ProductStatus;
import com.psj.itembrowser.security.common.exception.NotFoundException;
import com.psj.itembrowser.security.common.pagination.PageRequestDTO;
import com.psj.itembrowser.security.service.impl.UserDetailsServiceImpl;
import com.psj.itembrowser.shippingInfos.domain.vo.ShippingInfo;

@ExtendWith({RestDocumentationExtension.class, SpringExtension.class})
@WebMvcTest(OrderApiController.class)
@AutoConfigureRestDocs
class OrderSelectApiControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private OrderService orderService;

	@MockBean
	private UserDetailsServiceImpl userDetailsService;

	@Mock
	private Jwt jwt;

	private Order expectedOrderWithADMINUser;
	private Order expectedOrderWithCUSTOMERUser;

	@BeforeEach
	public void setUp(WebApplicationContext webApplicationContext,
		RestDocumentationContextProvider restDocumentation) {
		this.mockMvc = MockMvcBuilders
			.webAppContextSetup(webApplicationContext)
			.apply(MockMvcRestDocumentation.documentationConfiguration(restDocumentation))
			.build();

		Member expectedAdminMember = Member.builder()
			.memberNo(1L)
			.credentials(Credentials.builder()
				.email("qkrtkdwns3410@gmail.com").password("3410").build())
			.name(Name.builder().firstName("홍").lastName("길동").build())
			.phoneNumber("010-1234-1234")
			.gender(Gender.MEN)
			.role(Role.ROLE_ADMIN)
			.status(Status.ACTIVE)
			.memberShipType(MemberShipType.REGULAR)
			.address(
				Address.builder()
					.addressMain("서울시 강남구")
					.addressSub("김밥빌딩 101동 302호")
					.zipCode("01012").build())
			.birthday(LocalDate.of(1995, 11, 3))
			.lastLoginDate(LocalDateTime.now())
			.build();

		Member expectedCustomerMember = Member.builder()
			.memberNo(1L)
			.credentials(Credentials.builder()
				.email("qkrtkdwns3410@gmail.com").password("3410").build())
			.name(Name.builder().firstName("홍").lastName("길동").build())
			.phoneNumber("010-1234-1234")
			.gender(Gender.MEN)
			.role(Role.ROLE_CUSTOMER)
			.status(Status.ACTIVE)
			.memberShipType(MemberShipType.REGULAR)
			.address(
				Address.builder()
					.addressMain("서울시 강남구")
					.addressSub("김밥빌딩 101동 302호")
					.zipCode("01012").build())
			.birthday(LocalDate.of(1995, 11, 3))
			.lastLoginDate(LocalDateTime.now())
			.build();

		ShippingInfo expectedShppingInfo = new ShippingInfo(1L,
			1L,
			"홍길동",
			"test",
			"test",
			"01111",
			"010-1235-1234",
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
			null, new Product(
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

		this.expectedOrderWithADMINUser = Order.of(
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
			expectedAdminMember,
			expectedShppingInfo);

		this.expectedOrderWithCUSTOMERUser = Order.of(
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
			expectedCustomerMember,
			expectedShppingInfo);
	}

	@Test
	@MockMember
	@DisplayName("권한 - CUSTOMER 의 경우 주문 단건 조회시 200 성공과 올바른 응답값이 오는지 확인합니다.")
	void When_GetOrderWithCustomer_Expect_Status200() throws Exception {
		// given
		long orderId = 1L;
		OrderResponseDTO expectedOrderResponseDTO = OrderResponseDTO.from(expectedOrderWithCUSTOMERUser);
		given(orderService.getOrderWithNotDeleted(orderId)).willReturn(expectedOrderResponseDTO);
		given(userDetailsService.loadUserByJwt(any())).willReturn(
			new UserDetailsServiceImpl.CustomUserDetails(expectedOrderResponseDTO.getMember()));

		// when - then
		ResultActions response = mockMvc.perform(get("/v1/api/orders/{orderId}", orderId)
				.contentType(APPLICATION_JSON)
				.accept(APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.id").value(expectedOrderResponseDTO.getId()))
			.andExpect(
				jsonPath("$.ordererNumber").value(expectedOrderResponseDTO.getOrdererNumber()))
			.andExpect(jsonPath("$.member.memberNo").value(expectedOrderResponseDTO.getMember().getMemberNo()))
			.andExpect(
				jsonPath("$.member.email").value(expectedOrderResponseDTO.getMember().getEmail()))
			.andExpect(jsonPath("$.member.role").value(Role.ROLE_CUSTOMER.name()));

		response
			.andDo(MockMvcRestDocumentationWrapper.document(
				"get-order-customer",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				ResourceDocumentation.resource(ResourceSnippetParameters.builder()
					.tag("order")
					.summary("주문 단건 조회 API - CUSTOMER 권한의 경우")
					.pathParameters(
						parameterWithName("orderId").description("주문 ID")
					)
					.responseFields(
						fieldWithPath("id").description("주문 ID"),
						fieldWithPath("ordererNumber").description("주문자 번호"),
						fieldWithPath("orderStatus").description("주문 상태"),
						fieldWithPath("paidDate").description("결제 일자"),
						fieldWithPath("shippingInfoId").description("배송지 ID"),
						fieldWithPath("createdDate").description("생성 일자"),
						fieldWithPath("updatedDate").description("수정 일자"),
						fieldWithPath("deletedDate").description("삭제 일자"),
						fieldWithPath("member").description("주문자 정보"),
						fieldWithPath("member.memberNo").description("주문자 번호"),
						fieldWithPath("member.email").description("이메일"),
						fieldWithPath("member.firstName").description("이름"),
						fieldWithPath("member.lastName").description("성"),
						fieldWithPath("member.phoneNumber").description("전화번호"),
						fieldWithPath("member.addressMain").description("주소"),
						fieldWithPath("member.addressSub").description("상세주소"),
						fieldWithPath("member.zipCode").description("우편번호"),
						fieldWithPath("member.gender").description("성별"),
						fieldWithPath("member.memberShipType").description("회원의 멤버십 유형"),
						fieldWithPath("member.role").description("역할"),
						fieldWithPath("member.status").description("상태"),
						fieldWithPath("member.birthday").description("생년월일"),
						fieldWithPath("member.lastLoginDate").description("마지막 로그인 일자"),
						fieldWithPath("member.createdDate").optional().description("생성 일자"),
						fieldWithPath("member.updatedDate").optional().description("수정 일자"),
						fieldWithPath("member.deletedDate").optional().description("삭제 일자"),
						fieldWithPath("ordersProductRelations").description("주문-상품 관계 리스트"),
						fieldWithPath("ordersProductRelations[].groupId").description("주문 ID"),
						fieldWithPath("ordersProductRelations[].productId").description("상품 ID"),
						fieldWithPath("ordersProductRelations[].productQuantity").description(
							"상품 수량"),
						fieldWithPath("ordersProductRelations[].createdDate").description("생성 일자"),
						fieldWithPath("ordersProductRelations[].updatedDate").description("수정 일자"),
						fieldWithPath("ordersProductRelations[].deletedDate").description("삭제 일자"),
						subsectionWithPath("ordersProductRelations[].productResponseDTO").description(
							"상품에 대한 응답 데이터")
					)
					.build()
				)
			));
	}

	@Test
	@DisplayName("권한 - ADMIN 의 경우 주문 단건 조회시 200 성공과 올바른 응답값이 오는지 확인합니다.")
	@MockMember(role = Role.ROLE_ADMIN)
	void When_GetOrderWithAdmin_Expect_Status200() throws Exception {
		// given
		long orderId = 1L;
		OrderResponseDTO expectedOrderResponseDTO = OrderResponseDTO.from(expectedOrderWithADMINUser);
		given(orderService.getOrderWithNoCondition(orderId)).willReturn(expectedOrderResponseDTO);
		given(userDetailsService.loadUserByJwt(any())).willReturn(
			new UserDetailsServiceImpl.CustomUserDetails(expectedOrderResponseDTO.getMember()));

		// when - then
		ResultActions response = mockMvc.perform(get("/v1/api/orders/{orderId}", orderId)
				.contentType(APPLICATION_JSON)
				.accept(APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.id").value(expectedOrderResponseDTO.getId()))
			.andExpect(
				jsonPath("$.ordererNumber").value(expectedOrderResponseDTO.getOrdererNumber()))
			.andExpect(jsonPath("$.member.memberNo").value(
				expectedOrderResponseDTO.getMember().getMemberNo()))
			.andExpect(
				jsonPath("$.member.email").value(expectedOrderResponseDTO.getMember().getEmail()))
			.andExpect(jsonPath("$.member.role").value(Role.ROLE_ADMIN.name()));

		response
			.andDo(MockMvcRestDocumentationWrapper.document(
				"get-order-customer",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				ResourceDocumentation.resource(ResourceSnippetParameters.builder()
					.tag("order")
					.summary("주문 단건 조회 API - ADMIN 권한의 경우")
					.pathParameters(
						parameterWithName("orderId").description("주문 ID")
					)
					.responseFields(
						fieldWithPath("id").description("주문 ID"),
						fieldWithPath("ordererNumber").description("주문자 번호"),
						fieldWithPath("orderStatus").description("주문 상태"),
						fieldWithPath("paidDate").description("결제 일자"),
						fieldWithPath("shippingInfoId").description("배송지 ID"),
						fieldWithPath("createdDate").description("생성 일자"),
						fieldWithPath("updatedDate").description("수정 일자"),
						fieldWithPath("deletedDate").description("삭제 일자"),
						fieldWithPath("member").description("주문자 정보"),
						fieldWithPath("member.memberNo").description("주문자 번호"),
						fieldWithPath("member.email").description("이메일"),
						fieldWithPath("member.firstName").description("이름"),
						fieldWithPath("member.lastName").description("성"),
						fieldWithPath("member.phoneNumber").description("전화번호"),
						fieldWithPath("member.addressMain").description("주소"),
						fieldWithPath("member.addressSub").description("상세주소"),
						fieldWithPath("member.zipCode").description("우편번호"),
						fieldWithPath("member.gender").description("성별"),
						fieldWithPath("member.role").description("역할"),
						fieldWithPath("member.status").description("상태"),
						fieldWithPath("member.memberShipType").description("회원의 멤버십 유형"),
						fieldWithPath("member.birthday").description("생년월일"),
						fieldWithPath("member.lastLoginDate").description("마지막 로그인 일자"),
						fieldWithPath("member.createdDate").optional().description("생성 일자"),
						fieldWithPath("member.updatedDate").optional().description("수정 일자"),
						fieldWithPath("member.deletedDate").optional().description("삭제 일자"),
						fieldWithPath("ordersProductRelations").description("주문-상품 관계 리스트"),
						fieldWithPath("ordersProductRelations[].groupId").description("주문 ID"),
						fieldWithPath("ordersProductRelations[].productId").description("상품 ID"),
						fieldWithPath("ordersProductRelations[].productQuantity").description(
							"상품 수량"),
						fieldWithPath("ordersProductRelations[].createdDate").description("생성 일자"),
						fieldWithPath("ordersProductRelations[].updatedDate").description("수정 일자"),
						fieldWithPath("ordersProductRelations[].deletedDate").description("삭제 일자"),
						subsectionWithPath("ordersProductRelations[].productResponseDTO").description(
							"상품에 대한 응답 데이터")
					)
					.build()
				)
			));
	}

	@Test
	@MockMember(role = Role.ROLE_CUSTOMER)
	@DisplayName("권한 - CUSTOMER 인 경우, 주문 단건 조회 실패 케이스의 경우 404 반환과 올바른 응답값이 오는지 확인합니다.")
	void When_GetOrderWithCUSTOMER_Expect_Status404() throws Exception {
		// given
		long orderId = 1L;
		given(orderService.getOrderWithNotDeleted(orderId)).willThrow(new NotFoundException(ORDER_NOT_FOUND));
		given(userDetailsService.loadUserByJwt(any())).willReturn(
			new UserDetailsServiceImpl.CustomUserDetails(
				MemberResponseDTO.from(expectedOrderWithCUSTOMERUser.getMember())));

		// when - then
		ResultActions response = mockMvc.perform(get("/v1/api/orders/{orderId}", orderId)
				.contentType(APPLICATION_JSON)
				.accept(APPLICATION_JSON))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.message").value(ORDER_NOT_FOUND.getMessage()));

		response
			.andDo(MockMvcRestDocumentationWrapper.document(
				"get-order-fail-customer",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				ResourceDocumentation.resource(ResourceSnippetParameters.builder()
					.tag("order")
					.summary("주문 단건 조회 실패 API")
					.pathParameters(
						parameterWithName("orderId").description("주문 ID")
					)
					.responseFields(
						fieldWithPath("status").description("오류 상태 코드"),
						fieldWithPath("error").description("에러 메시지"),
						fieldWithPath("message").description("오류 메시지"),
						fieldWithPath("path").description("URI"),
						fieldWithPath("timestamp").description("시간")
					).build()
				)
			));
	}

	@Test
	@MockMember(role = Role.ROLE_ADMIN)
	@DisplayName("권한 - ADMIN 인 경우, 주문 단건 조회 실패 케이스의 경우 404 반환과 올바른 응답값이 오는지 확인합니다.")
	void When_GetOrderWithADMIN_Expect_Status404() throws Exception {
		// given
		long orderId = 1L;
		given(orderService.getOrderWithNoCondition(orderId)).willThrow(new NotFoundException(ORDER_NOT_FOUND));
		given(userDetailsService.loadUserByJwt(any())).willReturn(new UserDetailsServiceImpl.CustomUserDetails(
			MemberResponseDTO.from(expectedOrderWithADMINUser.getMember())));

		// when - then
		ResultActions response = mockMvc.perform(get("/v1/api/orders/{orderId}", orderId)
				.contentType(APPLICATION_JSON)
				.accept(APPLICATION_JSON))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.message").value(ORDER_NOT_FOUND.getMessage()));

		response
			.andDo(MockMvcRestDocumentationWrapper.document(
				"get-order-fail-admin",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				ResourceDocumentation.resource(ResourceSnippetParameters.builder()
					.tag("order")
					.summary("주문 단건 조회 실패 API")
					.pathParameters(
						parameterWithName("orderId").description("주문 ID")
					)
					.responseFields(
						fieldWithPath("status").description("오류 상태 코드"),
						fieldWithPath("error").description("에러 메시지"),
						fieldWithPath("message").description("오류 메시지"),
						fieldWithPath("path").description("URI"),
						fieldWithPath("timestamp").description("시간")
					).build()
				)
			));
	}

	//다건 조회 테스트
	@Test
	@MockMember(role = Role.ROLE_CUSTOMER)
	@DisplayName("권한 - CUSTOMER 인 경우, 삭제되지 않은 주문을 조회하는 서비스를 호출 시 200 성공을 기대합니다.")
	void When_GetOrdersWithCustomer_Expect_Status200()
		throws Exception {
		// given
		long userNumber = 1L;
		int pageNum = 0;
		int pageSize = 10;
		String requestYear = "2024";

		Pageable pageable = PageRequest.of(pageNum, pageSize);

		OrderResponseDTO expectedOrderResponseDTO = OrderResponseDTO.from(expectedOrderWithCUSTOMERUser);
		expectedOrderResponseDTO.setDeletedDate(null);

		MemberResponseDTO memberDTO = MemberResponseDTO.builder().build();

		Page<OrderResponseDTO> expectedOrderResponseDTOPage = new PageImpl<>(List.of(expectedOrderResponseDTO), pageable, 1L);

		given(orderService.getOrdersWithPaginationAndNotDeleted(any(MemberEntity.class), any(OrderPageRequestDTO.class)))
			.willReturn(expectedOrderResponseDTOPage);

		given(userDetailsService.loadUserByJwt(any()))
			.willReturn(new UserDetailsServiceImpl.CustomUserDetails(memberDTO));

		// when - then
		ResultActions response = mockMvc.perform(
				get("/v1/api/orders/users/{userNumber}", userNumber)
					.param("pageNum", String.valueOf(pageNum))
					.param("pageSize", String.valueOf(pageSize))
					.param("requestYear", requestYear)
					.param("userNumber", String.valueOf(userNumber))
					.contentType(APPLICATION_JSON)
					.accept(APPLICATION_JSON))
			.andExpect(status().isOk());

		response
			.andDo(MockMvcRestDocumentationWrapper.document(
				"get-orders-customer",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				ResourceDocumentation.resource(ResourceSnippetParameters.builder()
					.tag("order")
					.summary("주문 다건 조회 API - CUSTOMER 권한의 경우")
					.pathParameters(
						parameterWithName("userNumber").description("사용자 번호")
					)
					.responseFields(
						fieldWithPath("content").description("결과 데이터의 배열"),
						fieldWithPath("content[].id").description("주문 ID"),
						fieldWithPath("content[].ordererNumber").description("주문자 번호"),
						fieldWithPath("content[].orderStatus").description("주문 상태"),
						fieldWithPath("content[].paidDate").description("결제 일자"),
						fieldWithPath("content[].shippingInfoId").description("배송지 ID"),
						fieldWithPath("content[].createdDate").description("생성 일자"),
						fieldWithPath("content[].updatedDate").description("수정 일자").optional().type(JsonFieldType.NULL),
						fieldWithPath("content[].deletedDate").description("삭제 일자").optional().type(JsonFieldType.NULL),
						// member
						fieldWithPath("content[].member").description("주문자 정보"),
						fieldWithPath("content[].member.memberNo").description("회원 번호"),
						fieldWithPath("content[].member.email").description("이메일"),
						fieldWithPath("content[].member.firstName").description("이름"),
						fieldWithPath("content[].member.lastName").description("성"),
						fieldWithPath("content[].member.phoneNumber").description("전화번호"),
						fieldWithPath("content[].member.addressMain").description("주소"),
						fieldWithPath("content[].member.addressSub").description("상세주소"),
						fieldWithPath("content[].member.zipCode").description("우편번호"),
						fieldWithPath("content[].member.gender").description("성별"),
						fieldWithPath("content[].member.memberShipType").description("멤버십 유형"),
						fieldWithPath("content[].member.role").description("역할"),
						fieldWithPath("content[].member.status").description("상태"),
						fieldWithPath("content[].member.birthday").description("생년월일"),
						fieldWithPath("content[].member.lastLoginDate").description("마지막 로그인 일자"),
						fieldWithPath("content[].member.createdDate").optional().description("생성 일자"),
						fieldWithPath("content[].member.updatedDate").optional().description("수정 일자").optional().type(JsonFieldType.NULL),
						fieldWithPath("content[].member.deletedDate").optional().description("삭제 일자").optional().type(JsonFieldType.NULL),
						// ordersProductRelations
						fieldWithPath("content[].ordersProductRelations").description("주문-상품 관계 리스트"),
						fieldWithPath("content[].ordersProductRelations[].groupId").description("그룹 ID"),
						fieldWithPath("content[].ordersProductRelations[].productId").description("상품 ID"),
						fieldWithPath("content[].ordersProductRelations[].productQuantity").description("상품 수량"),
						fieldWithPath("content[].ordersProductRelations[].createdDate").description("생성 일자"),
						fieldWithPath("content[].ordersProductRelations[].updatedDate").description("수정 일자").optional().type(JsonFieldType.NULL),
						fieldWithPath("content[].ordersProductRelations[].deletedDate").description("삭제 일자").optional().type(JsonFieldType.NULL),
						// productResponseDTO
						subsectionWithPath("content[].ordersProductRelations[].productResponseDTO").description("상품 응답 데이터"),
						// pageable
						fieldWithPath("pageable").description("페이지네이션 정보"),
						fieldWithPath("pageable.sort").description("정렬 정보"),
						fieldWithPath("pageable.sort.empty").description("정렬이 비어있는지 여부"),
						fieldWithPath("pageable.sort.unsorted").description("정렬되지 않았는지 여부"),
						fieldWithPath("pageable.sort.sorted").description("정렬되었는지 여부"),
						fieldWithPath("pageable.offset").description("페이지 오프셋"),
						fieldWithPath("pageable.pageNumber").description("페이지 번호"),
						fieldWithPath("pageable.pageSize").description("페이지 크기"),
						fieldWithPath("pageable.unpaged").description("페이징 되지 않았는지 여부"),
						fieldWithPath("pageable.paged").description("페이징 되었는지 여부"),
						fieldWithPath("last").description("마지막 페이지인지 여부"),
						fieldWithPath("totalPages").description("전체 페이지 수"),
						fieldWithPath("totalElements").description("전체 요소 수"),
						fieldWithPath("number").description("현재 페이지 번호"),
						fieldWithPath("sort").description("정렬 정보"),
						fieldWithPath("sort.empty").description("정렬이 비어있는지 여부"),
						fieldWithPath("sort.unsorted").description("정렬되지 않았는지 여부"),
						fieldWithPath("sort.sorted").description("정렬되었는지 여부"),
						fieldWithPath("first").description("첫 번째 페이지인지 여부"),
						fieldWithPath("numberOfElements").description("현재 페이지의 요소 수"),
						fieldWithPath("empty").description("결과가 비어있는지 여부"),
						fieldWithPath("size").description("페이지 크기")
					).build())));
	}

	// 404 NOT_FOUND_EXCEPTION 이 터지는 경우 테스트
	@Test
	@MockMember(role = Role.ROLE_CUSTOMER)
	@DisplayName("권한 - CUSTOMER 인 경우, 주문 다건 조회 실패 케이스의 경우 404 반환과 올바른 응답값이 오는지 확인합니다.")
	void When_GetOrdersWithCustomer_Pagination_SpecificYear_Expect_Status404() throws Exception {
		// given
		long userNumber = 1L;
		int pageNum = 100;
		int pageSize = 0;
		String requestYear = "2024";

		Member member = Member.from(MemberResponseDTO.from(expectedOrderWithCUSTOMERUser.getMember()));

		OrderPageRequestDTO orderPageRequestDTO = OrderPageRequestDTO.create(PageRequestDTO.create(pageNum, pageSize),
			1L, requestYear);

		given(orderService.getOrdersWithPaginationAndNotDeleted(any(MemberEntity.class), any(OrderPageRequestDTO.class))).willThrow(
			new NotFoundException(ORDER_NOT_FOUND));

		given(userDetailsService.loadUserByJwt(any())).willReturn(
			new UserDetailsServiceImpl.CustomUserDetails(
				MemberResponseDTO.from(expectedOrderWithCUSTOMERUser.getMember())));

		// when - then
		ResultActions response = mockMvc.perform(
				get("/v1/api/orders/users/{userNumber}", userNumber)
					.param("pageNum", String.valueOf(pageNum))
					.param("pageSize", String.valueOf(pageSize))
					.param("requestYear", requestYear)
					.param("userNumber", String.valueOf(userNumber))
					.contentType(APPLICATION_JSON)
					.accept(APPLICATION_JSON))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.message").value(ORDER_NOT_FOUND.getMessage()));

		response
			.andDo(MockMvcRestDocumentationWrapper.document(
				"get-orders-fail-customer",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				ResourceDocumentation.resource(ResourceSnippetParameters.builder()
					.tag("order")
					.summary("주문 다건 조회 실패 API - CUSTOMER 권한의 경우")
					.pathParameters(
						parameterWithName("userNumber").description("사용자 번호")
					)
					.responseFields(
						fieldWithPath("status").description("오류 상태 코드"),
						fieldWithPath("error").description("에러 메시지"),
						fieldWithPath("message").description("오류 메시지"),
						fieldWithPath("path").description("URI"),
						fieldWithPath("timestamp").description("시간")
					).build()
				)
			));
	}

	@Test
	@MockMember(role = Role.ROLE_ADMIN)
	@DisplayName("권한 - ADMIN 인 경우, 삭제되지 않은 주문을 조회하는 서비스를 호출 시 200 성공을 기대합니다.")
	void When_GetOrdersWithAdmin_Expect_Status200() throws Exception {
		// given
		long userNumber = 1L;
		int pageNum = 0;
		int pageSize = 10;
		String requestYear = "2024";

		Pageable pageable = PageRequest.of(pageNum, pageSize);

		OrderResponseDTO expectedOrderResponseDTO = OrderResponseDTO.from(expectedOrderWithADMINUser);
		expectedOrderResponseDTO.setDeletedDate(null);

		MemberResponseDTO memberDTO = MemberResponseDTO.builder().build();

		Page<OrderResponseDTO> expectedOrderResponseDTOPage = new PageImpl<>(List.of(expectedOrderResponseDTO), pageable, 1L);

		given(orderService.getOrdersWithPaginationAndNotDeleted(any(MemberEntity.class), any(OrderPageRequestDTO.class)))
			.willReturn(expectedOrderResponseDTOPage);

		given(userDetailsService.loadUserByJwt(any()))
			.willReturn(new UserDetailsServiceImpl.CustomUserDetails(memberDTO));

		// when - then
		ResultActions response = mockMvc.perform(
				get("/v1/api/orders/users/{userNumber}", userNumber)
					.param("pageNum", String.valueOf(pageNum))
					.param("pageSize", String.valueOf(pageSize))
					.param("requestYear", requestYear)
					.param("userNumber", String.valueOf(userNumber))
					.contentType(APPLICATION_JSON)
					.accept(APPLICATION_JSON))
			.andExpect(status().isOk());

		response
			.andDo(MockMvcRestDocumentationWrapper.document(
				"get-orders-admin",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				ResourceDocumentation.resource(ResourceSnippetParameters.builder()
					.tag("order")
					.summary("주문 다건 조회 API - ADMIN 권한의 경우")
					.pathParameters(
						parameterWithName("userNumber").description("사용자 번호")
					)
					.responseFields(
						fieldWithPath("content").description("결과 데이터의 배열"),
						fieldWithPath("content[].id").description("주문 ID"),
						fieldWithPath("content[].ordererNumber").description("주문자 번호"),
						fieldWithPath("content[].orderStatus").description("주문 상태"),
						fieldWithPath("content[].paidDate").description("결제 일자"),
						fieldWithPath("content[].shippingInfoId").description("배송지 ID"),
						fieldWithPath("content[].createdDate").description("생성 일자"),
						fieldWithPath("content[].updatedDate").description("수정 일자").optional().type(JsonFieldType.NULL),
						fieldWithPath("content[].deletedDate").description("삭제 일자").optional().type(JsonFieldType.NULL),
						// member
						fieldWithPath("content[].member").description("주문자 정보"),
						fieldWithPath("content[].member.memberNo").description("회원 번호"),
						fieldWithPath("content[].member.email").description("이메일"),
						fieldWithPath("content[].member.firstName").description("이름"),
						fieldWithPath("content[].member.lastName").description("성"),
						fieldWithPath("content[].member.phoneNumber").description("전화번호"),
						fieldWithPath("content[].member.addressMain").description("주소"),
						fieldWithPath("content[].member.addressSub").description("상세주소"),
						fieldWithPath("content[].member.zipCode").description("우편번호"),
						fieldWithPath("content[].member.gender").description("성별"),
						fieldWithPath("content[].member.memberShipType").description("멤버십 유형"),
						fieldWithPath("content[].member.role").description("역할"),
						fieldWithPath("content[].member.status").description("상태"),
						fieldWithPath("content[].member.birthday").description("생년월일"),
						fieldWithPath("content[].member.lastLoginDate").description("마지막 로그인 일자"),
						fieldWithPath("content[].member.createdDate").optional().description("생성 일자"),
						fieldWithPath("content[].member.updatedDate").optional().description("수정 일자").optional().type(JsonFieldType.NULL),
						fieldWithPath("content[].member.deletedDate").optional().description("삭제 일자").optional().type(JsonFieldType.NULL),
						// ordersProductRelations
						fieldWithPath("content[].ordersProductRelations").description("주문-상품 관계 리스트"),
						fieldWithPath("content[].ordersProductRelations[].groupId").description("그룹 ID"),
						fieldWithPath("content[].ordersProductRelations[].productId").description("상품 ID"),
						fieldWithPath("content[].ordersProductRelations[].productQuantity").description("상품 수량"),
						fieldWithPath("content[].ordersProductRelations[].createdDate").description("생성 일자"),
						fieldWithPath("content[].ordersProductRelations[].updatedDate").description("수정 일자").optional().type(JsonFieldType.NULL),
						fieldWithPath("content[].ordersProductRelations[].deletedDate").description("삭제 일자").optional().type(JsonFieldType.NULL),
						// productResponseDTO
						subsectionWithPath("content[].ordersProductRelations[].productResponseDTO").description("상품 응답 데이터"),
						// pageable
						fieldWithPath("pageable").description("페이지네이션 정보"),
						fieldWithPath("pageable.sort").description("정렬 정보"),
						fieldWithPath("pageable.sort.empty").description("정렬이 비어있는지 여부"),
						fieldWithPath("pageable.sort.unsorted").description("정렬되지 않았는지 여부"),
						fieldWithPath("pageable.sort.sorted").description("정렬되었는지 여부"),
						fieldWithPath("pageable.offset").description("페이지 오프셋"),
						fieldWithPath("pageable.pageNumber").description("페이지 번호"),
						fieldWithPath("pageable.pageSize").description("페이지 크기"),
						fieldWithPath("pageable.unpaged").description("페이징 되지 않았는지 여부"),
						fieldWithPath("pageable.paged").description("페이징 되었는지 여부"),
						fieldWithPath("last").description("마지막 페이지인지 여부"),
						fieldWithPath("totalPages").description("전체 페이지 수"),
						fieldWithPath("totalElements").description("전체 요소 수"),
						fieldWithPath("number").description("현재 페이지 번호"),
						fieldWithPath("sort").description("정렬 정보"),
						fieldWithPath("sort.empty").description("정렬이 비어있는지 여부"),
						fieldWithPath("sort.unsorted").description("정렬되지 않았는지 여부"),
						fieldWithPath("sort.sorted").description("정렬되었는지 여부"),
						fieldWithPath("first").description("첫 번째 페이지인지 여부"),
						fieldWithPath("numberOfElements").description("현재 페이지의 요소 수"),
						fieldWithPath("empty").description("결과가 비어있는지 여부"),
						fieldWithPath("size").description("페이지 크기")
					).build())));
	}

	@Test
	@MockMember(role = Role.ROLE_ADMIN)
	@DisplayName("권한 - ADMIN 인 경우, 주문 다건 조회 실패 케이스의 경우 404 반환과 올바른 응답값이 오는지 확인합니다.")
	void When_GetOrdersWithAdmin_Pagination_SpecificYear_Expect_Status404() throws Exception {
		// given
		long userNumber = 1L;
		int pageNum = 100;
		int pageSize = 0;
		String requestYear = "2024";

		OrderPageRequestDTO orderPageRequestDTO = OrderPageRequestDTO.create(PageRequestDTO.create(pageNum, pageSize),
			1L, requestYear);

		Member member = Member.from(MemberResponseDTO.from(expectedOrderWithADMINUser.getMember()));

		given(orderService.getOrdersWithPaginationAndNoCondition(any(MemberEntity.class), any(OrderPageRequestDTO.class))).willThrow(
			new NotFoundException(ORDER_NOT_FOUND));

		given(userDetailsService.loadUserByJwt(any())).willReturn(
			new UserDetailsServiceImpl.CustomUserDetails(
				MemberResponseDTO.from(expectedOrderWithADMINUser.getMember())));

		// when - then
		ResultActions response = mockMvc.perform(
				get("/v1/api/orders/users/{userNumber}", userNumber)
					.param("pageNum", String.valueOf(pageNum))
					.param("pageSize", String.valueOf(pageSize))
					.param("requestYear", requestYear)
					.param("userNumber", String.valueOf(userNumber))
					.contentType(APPLICATION_JSON)
					.accept(APPLICATION_JSON))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.message").value(ORDER_NOT_FOUND.getMessage()));

		response
			.andDo(MockMvcRestDocumentationWrapper.document(
				"get-orders-fail-admin",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				ResourceDocumentation.resource(ResourceSnippetParameters.builder()
					.tag("order")
					.summary("주문 다건 조회 실패 API - ADMIN 권한의 경우")
					.pathParameters(
						parameterWithName("userNumber").description("사용자 번호")
					)
					.responseFields(
						fieldWithPath("status").description("오류 상태 코드"),
						fieldWithPath("error").description("에러 메시지"),
						fieldWithPath("message").description("오류 메시지"),
						fieldWithPath("path").description("URI"),
						fieldWithPath("timestamp").description("시간")
					).build()
				)
			));
	}
}