package com.psj.itembrowser.cart.controller;

import static com.psj.itembrowser.security.common.exception.ErrorCode.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper;
import com.epages.restdocs.apispec.ResourceDocumentation;
import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.psj.itembrowser.cart.domain.dto.request.CartProductDeleteRequestDTO;
import com.psj.itembrowser.cart.domain.dto.request.CartProductRequestDTO;
import com.psj.itembrowser.cart.domain.dto.request.CartProductUpdateRequestDTO;
import com.psj.itembrowser.cart.domain.dto.response.CartProductRelationResponseDTO;
import com.psj.itembrowser.cart.domain.dto.response.CartResponseDTO;
import com.psj.itembrowser.cart.service.CartService;
import com.psj.itembrowser.member.domain.dto.response.MemberResponseDTO;
import com.psj.itembrowser.member.domain.entity.MemberEntity;
import com.psj.itembrowser.member.domain.vo.Role;
import com.psj.itembrowser.security.common.exception.DatabaseOperationException;
import com.psj.itembrowser.security.common.exception.ErrorCode;
import com.psj.itembrowser.security.common.exception.NotFoundException;
import com.psj.itembrowser.security.service.impl.UserDetailsServiceImpl;

/**
 * packageName    : com.psj.itembrowser.cart.api fileName       : CartApiControllerTest author :
 * ipeac date           : 2023-10-25 description    :
 * =========================================================== DATE              AUTHOR NOTE
 * ----------------------------------------------------------- 2023-10-25        ipeac       최초 생성
 */
@ExtendWith({RestDocumentationExtension.class, SpringExtension.class})
@WebMvcTest(CartApiController.class)
@AutoConfigureMockMvc
@AutoConfigureRestDocs
class CartApiControllerTest {
	
	private final String BASE_URL = "/v1/api/cart";
	
	private final String USER_EMAIL = "test@test.com";
	
	@Autowired
	MockMvc mockMvc;
	
	@Autowired
	ObjectMapper objectMapper;
	
	@MockBean
	private CartService cartService;
	
	@MockBean
	private UserDetailsServiceImpl userDetailsService;
	
	@BeforeEach
	void setUp(WebApplicationContext webApplicationContext,
		RestDocumentationContextProvider restDocumentation) {
		this.mockMvc = MockMvcBuilders
			.webAppContextSetup(webApplicationContext)
			.apply(MockMvcRestDocumentation.documentationConfiguration(restDocumentation))
			.build();
	}
	
	@Nested
	class SelectTest {
		
		@Test
		@DisplayName("장바구니 목록 조회 API 테스트 -> 정상 조회 테스트")
		void given_GetCart_Expect_Success() throws Exception {
			// given
			CartResponseDTO cartResponseDTO = CartResponseDTO
				.builder()
				.userEmail(USER_EMAIL)
				.products(List.of(CartProductRelationResponseDTO.builder().build()))
				.build();
			
			given(cartService.getCart(anyString())).willReturn(cartResponseDTO);
			
			// when + then
			mockMvc
				.perform(
					RestDocumentationRequestBuilders
						.get(BASE_URL + "/{userId}", USER_EMAIL)
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.userEmail").value(cartResponseDTO.getUserEmail()))
				.andExpect(jsonPath("$.products").isArray())
				.andExpect(
					jsonPath("$.products.length()").value(cartResponseDTO.getProducts().size()))
				.andDo(MockMvcRestDocumentationWrapper.document(
					"get-cart",
					preprocessRequest(prettyPrint()),
					preprocessResponse(prettyPrint()),
					ResourceDocumentation.resource(ResourceSnippetParameters.builder()
						.tag("cart")
						.summary("장바구니 목록 조회 API")
						.pathParameters(
							parameterWithName("userId").description("조회할 사용자의 ID")
						)
						.responseFields(
							fieldWithPath("cartId").description("장바구니 ID").optional()
								.type(JsonFieldType.NUMBER),
							fieldWithPath("userEmail").description("사용자 이메일"),
							fieldWithPath("createdDate").description("생성 일자"),
							fieldWithPath("updatedDate").description("업데이트 일자"),
							fieldWithPath("products").description("장바구니에 담긴 상품 목록"),
							fieldWithPath("products[].cartId").description("장바구니 ID").optional()
								.type(JsonFieldType.NUMBER),
							fieldWithPath("products[].productId").description("상품 ID").optional()
								.type(JsonFieldType.NUMBER),
							fieldWithPath("products[].productQuantity").description("상품 수량")
								.optional().type(JsonFieldType.NUMBER),
							fieldWithPath("products[].cart").description("장바구니 정보").optional()
								.type(JsonFieldType.OBJECT),
							fieldWithPath("products[].product").description("상품 정보").optional()
								.type(JsonFieldType.OBJECT)
						)
						.build()
					)
				));
		}
		
		@Test
		@DisplayName("장바구니 빈값 조회시 API 오류 발생")
		void given_CartIsNull_Expect_Exception() throws Exception {
			// given
			given(cartService.getCart(anyString())).willThrow(
				new NotFoundException(ErrorCode.CART_NOT_FOUND));
			
			// given + then
			mockMvc
				.perform(RestDocumentationRequestBuilders
					.get(BASE_URL + "/{userId}", USER_EMAIL)
					.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.message").value(ErrorCode.CART_NOT_FOUND.getMessage()))
				.andExpect(jsonPath("$.status").value(404))
				.andDo(MockMvcRestDocumentationWrapper.document(
					"get-cart-not-found",
					preprocessRequest(prettyPrint()),
					preprocessResponse(prettyPrint()),
					ResourceDocumentation.resource(
						ResourceSnippetParameters.builder()
							.tag("cart")
							.summary("장바구니 목록 조회 API")
							.pathParameters(
								parameterWithName("userId").description("조회할 사용자의 ID")
							)
							.responseFields(
								fieldWithPath("status").description("오류 상태 코드"),
								fieldWithPath("error").description("에러 메시지"),
								fieldWithPath("message").description("오류 메시지"),
								fieldWithPath("path").description("URI"),
								fieldWithPath("timestamp").description("시간")
							)
							.build()
					)
				));
		}
	}
	
	@Nested
	class InsertTest {
		
		CartProductRequestDTO mock;
		
		@BeforeEach
		void setUp() {
			// given
			mock = CartProductRequestDTO
				.builder()
				.cartId(1L)
				.productId(1L)
				.quantity(1)
				.email(USER_EMAIL)
				.build();
		}
		
		@Test
		@DisplayName("장바구니에 상품을 올바르게 삽입시, 정상 응답 테스트")
		void given_CorrectInsertProductInCart_Expect_Success() throws Exception {
			// given + then
			given(userDetailsService.loadUserByJwt(any())).willReturn(
				new UserDetailsServiceImpl.CustomUserDetails(MemberResponseDTO.builder().role(Role.ROLE_CUSTOMER).build()));
			
			mockMvc
				.perform(RestDocumentationRequestBuilders
					.post(BASE_URL + "/")
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(mock))
					.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.message").value("cart add affected : 1 / 1"))
				.andDo(MockMvcRestDocumentationWrapper.document(
					"add-cart",
					preprocessRequest(prettyPrint()),
					preprocessResponse(prettyPrint()),
					
					ResourceDocumentation.resource(
						ResourceSnippetParameters.builder()
							.tag("cart")
							.summary("장바구니에 상품 추가 API")
							.requestFields(
								fieldWithPath("cartId").description("장바구니 ID"),
								fieldWithPath("productId").description("상품 ID"),
								fieldWithPath("email").description("사용자 이메일"),
								fieldWithPath("quantity").description("상품 수량")
							)
							.responseFields(
								fieldWithPath("message").description("응답 메시지")
							)
							.build()
					)
				));
		}
		
		@Test
		@DisplayName("장바구니에 상품을 잘못 삽입시, 오류 응답 테스트")
		void given_IncorrectInsertProductInCart_Expect_Exception() throws Exception {
			// given + then
			given(userDetailsService.loadUserByJwt(any())).willReturn(
				new UserDetailsServiceImpl.CustomUserDetails(MemberResponseDTO.builder().build()));
			
			doThrow(new DatabaseOperationException(CART_PRODUCT_INSERT_FAIL))
				.when(cartService)
				.addCartProduct(any(MemberEntity.class), any(CartProductRequestDTO.class));
			
			mockMvc
				.perform(RestDocumentationRequestBuilders
					.post(BASE_URL + "/")
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(mock))
					.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest())
				.andDo(MockMvcRestDocumentationWrapper.document(
					"add-cart-fail",
					preprocessRequest(prettyPrint()),
					preprocessResponse(prettyPrint()),
					
					ResourceDocumentation.resource(
						ResourceSnippetParameters.builder()
							.tag("cart")
							.summary("장바구니에 상품 추가 API")
							.requestFields(
								fieldWithPath("cartId").description("장바구니 ID"),
								fieldWithPath("productId").description("상품 ID"),
								fieldWithPath("email").description("사용자 ID"),
								fieldWithPath("quantity").description("상품 수량")
							)
							.responseFields(
								fieldWithPath("status").description("오류 상태 코드"),
								fieldWithPath("error").description("에러 메시지"),
								fieldWithPath("message").description("오류 메시지"),
								fieldWithPath("path").description("URI"),
								fieldWithPath("timestamp").description("시간")
							)
							.build()
					)
				));
		}
	}
	
	@Nested
	class UpdateTest {
		
		CartProductUpdateRequestDTO mock;
		
		@BeforeEach
		void setUp() {
			// given
			mock = CartProductUpdateRequestDTO
				.builder()
				.cartId(1L)
				.productId(1L)
				.quantity(10)
				.build();
		}
		
		@Test
		@DisplayName("장바구니 상품 수정 API -> 정상 응답 테스트")
		void given_UpdateProductInCart_Expect_Success() throws Exception {
			// given
			given(userDetailsService.loadUserByJwt(any())).willReturn(
				new UserDetailsServiceImpl.CustomUserDetails(MemberResponseDTO.builder().role(Role.ROLE_CUSTOMER).build()));
			
			// then
			mockMvc
				.perform(RestDocumentationRequestBuilders
					.put(BASE_URL + "/")
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(mock))
					.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.message").value("cart update affected : 1 / 1"))
				.andDo(MockMvcRestDocumentationWrapper.document(
					"update-cart",
					preprocessRequest(prettyPrint()),
					preprocessResponse(prettyPrint()),
					
					ResourceDocumentation.resource(
						ResourceSnippetParameters.builder()
							.tag("cart")
							.summary("장바구니 상품 수정 API")
							.requestFields(
								fieldWithPath("cartId").description("장바구니 ID"),
								fieldWithPath("productId").description("상품 ID"),
								fieldWithPath("quantity").description("상품 수량")
							)
							.responseFields(
								fieldWithPath("message").description("응답 메시지")
							)
							.build()
					)
				));
		}
		
		@Test
		@DisplayName("장바구니 상품 수정 API -> 장바구니 상품이 존재하지 않을 경우 오류 발생")
		void given_UpdateProductInCart_Expect_Exception() throws Exception {
			//given
			CartProductUpdateRequestDTO dto = CartProductUpdateRequestDTO
				.builder()
				.cartId(1L)
				.productId(1L)
				.quantity(10)
				.build();
			
			given(userDetailsService.loadUserByJwt(any())).willReturn(
				new UserDetailsServiceImpl.CustomUserDetails(MemberResponseDTO.builder().role(Role.ROLE_CUSTOMER).build())
			);
			
			//when -  then
			doThrow(new NotFoundException(CART_PRODUCT_UPDATE_FAIL))
				.when(cartService)
				.modifyCartProduct(any(CartProductUpdateRequestDTO.class), any(MemberEntity.class));
			
			mockMvc
				.perform(RestDocumentationRequestBuilders
					.put(BASE_URL + "/")
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(dto))
					.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.message").value(CART_PRODUCT_UPDATE_FAIL.getMessage()))
				.andDo(MockMvcRestDocumentationWrapper.document(
					"update-cart-fail",
					preprocessRequest(prettyPrint()),
					preprocessResponse(prettyPrint()),
					
					ResourceDocumentation.resource(
						ResourceSnippetParameters.builder()
							.tag("cart")
							.summary("장바구니 상품 수정 API")
							.requestFields(
								fieldWithPath("cartId").description("장바구니 ID"),
								fieldWithPath("productId").description("상품 ID"),
								fieldWithPath("quantity").description("상품 수량")
							)
							.responseFields(
								fieldWithPath("status").description("오류 상태 코드"),
								fieldWithPath("error").description("에러 메시지"),
								fieldWithPath("message").description("오류 메시지"),
								fieldWithPath("path").description("URI"),
								fieldWithPath("timestamp").description("시간")
							)
							.build()
					)
				));
		}
	}
	
	@Nested
	class DeleteTest {
		
		CartProductDeleteRequestDTO mock;
		
		@BeforeEach
		void setUp() {
			// given
			mock = CartProductDeleteRequestDTO
				.builder()
				.cartId(1L)
				.productId(1L)
				.build();
		}
		
		@Test
		@DisplayName("장바구니에서 상품 삭제 API -> 정상 응답 테스트")
		void given_DeleteProductInCart_Expect_Success() throws Exception {
			// given
			given(userDetailsService.loadUserByJwt(any())).willReturn(
				new UserDetailsServiceImpl.CustomUserDetails(MemberResponseDTO.builder().role(Role.ROLE_CUSTOMER).build()));
			
			mockMvc
				.perform(RestDocumentationRequestBuilders
					.delete(BASE_URL + "/")
					.contentType(MediaType.APPLICATION_JSON_VALUE)
					.content(objectMapper.writeValueAsString(mock))
					.accept(MediaType.APPLICATION_JSON_VALUE))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.message").value("cart delete affected : 1 / 1"))
				.andDo(MockMvcRestDocumentationWrapper.document(
					"delete-cart",
					preprocessRequest(prettyPrint()),
					preprocessResponse(prettyPrint()),
					
					ResourceDocumentation.resource(
						ResourceSnippetParameters.builder()
							.tag("cart")
							.summary("장바구니 상품 삭제 API")
							.requestFields(
								fieldWithPath("cartId").description("장바구니 ID"),
								fieldWithPath("productId").description("상품 ID")
							)
							.responseFields(
								fieldWithPath("message").description("응답 메시지")
							)
							.build()
					)
				));
		}
		
		@Test
		@DisplayName("장바구니 상품 삭제 API -> 장바구니 상품이 존재하지 않을 경우 오류 발생")
		void given_DeleteProductInCart_Expect_Exception() throws Exception {
			// given + then
			given(userDetailsService.loadUserByJwt(any())).willReturn(
				new UserDetailsServiceImpl.CustomUserDetails(MemberResponseDTO.builder().role(Role.ROLE_CUSTOMER).build()));
			
			doThrow(new NotFoundException(CART_PRODUCT_DELETE_FAIL))
				.when(cartService)
				.removeCartProduct(any(CartProductDeleteRequestDTO.class), any(MemberEntity.class));
			
			mockMvc
				.perform(RestDocumentationRequestBuilders
					.delete(BASE_URL + "/")
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(mock))
					.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.message").value(CART_PRODUCT_DELETE_FAIL.getMessage()))
				.andDo(MockMvcRestDocumentationWrapper.document(
					"delete-cart-fail",
					preprocessRequest(prettyPrint()),
					preprocessResponse(prettyPrint()),
					ResourceDocumentation.resource(
						ResourceSnippetParameters.builder()
							.tag("cart")
							.summary("장바구니 상품 삭제 API")
							.requestFields(
								fieldWithPath("cartId").description("장바구니 ID"),
								fieldWithPath("productId").description("상품 ID")
							)
							.responseFields(
								fieldWithPath("status").description("오류 상태 코드"),
								fieldWithPath("error").description("에러 메시지"),
								fieldWithPath("message").description("오류 메시지"),
								fieldWithPath("path").description("URI"),
								fieldWithPath("timestamp").description("시간")
							)
							.build()
					)
				));
		}
	}
}