package com.psj.itembrowser.order.controller;

import com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper;
import com.epages.restdocs.apispec.ResourceDocumentation;
import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.psj.itembrowser.member.annotation.MockMember;
import com.psj.itembrowser.member.domain.dto.request.MemberRequestDTO;
import com.psj.itembrowser.member.domain.dto.response.MemberResponseDTO;
import com.psj.itembrowser.member.domain.vo.Role;
import com.psj.itembrowser.order.domain.dto.request.OrderCreateRequestDTO;
import com.psj.itembrowser.order.domain.dto.request.OrdersProductRelationRequestDTO;
import com.psj.itembrowser.order.domain.dto.response.OrderResponseDTO;
import com.psj.itembrowser.order.service.OrderService;
import com.psj.itembrowser.product.domain.entity.ProductEntity;
import com.psj.itembrowser.product.domain.vo.DeliveryFeeType;
import com.psj.itembrowser.security.service.impl.UserDetailsServiceImpl;
import com.psj.itembrowser.shippingInfos.domain.dto.request.ShippingInfoRequestDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDateTime;
import java.util.List;

import static com.epages.restdocs.apispec.ResourceDocumentation.headerWithName;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith({RestDocumentationExtension.class, SpringExtension.class})
@WebMvcTest(OrderApiController.class)
@AutoConfigureRestDocs
public class OrderInsertApiControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @MockBean
    private OrderService orderService;
    
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
    
    @Test
    @DisplayName("주문 생성 - 정상 요청")
    @MockMember(role = Role.ROLE_CUSTOMER)
    public void When_CreateOrder_With_ValidRequest_Then_ReturnCreated() throws Exception {
        // given
        final long userNumber = 1L;
        
        ProductEntity existProduct = ProductEntity.builder()
                .id(1L)
                .name("섬유유연제")
                .brand("다우니")
                .category(1)
                .deliveryDefaultFee(3000)
                .deliveryFeeType(DeliveryFeeType.FREE)
                .detail("세탁 시 사용하는 섬유유연제")
                .sellStartDatetime(LocalDateTime.now().minusDays(1))
                .sellEndDatetime(LocalDateTime.now().plusDays(1))
                .unitPrice(1000)
                .quantity(20)
                .build();
        
        OrdersProductRelationRequestDTO responseDTO = OrdersProductRelationRequestDTO.builder()
                .productId(existProduct.getId())
                .build();
        
        ShippingInfoRequestDTO infoResponseDTO = ShippingInfoRequestDTO.builder()
                .id(1L)
                .memberNo(1L)
                .receiver("홍길동")
                .mainAddress("서울시 강남구")
                .subAddress("테헤란로 427")
                .build();
        
        MemberRequestDTO member = MemberRequestDTO.builder()
                .email("qkrtkdwns3410@gmail.com")
                .firstName("길동")
                .lastName("홍")
                .phone("010-1234-5678")
                .role(Role.ROLE_CUSTOMER)
                .build();
        
        OrderCreateRequestDTO orderCreateRequestDTO = OrderCreateRequestDTO.builder()
                .ordererNumber(userNumber)
                .products(List.of(responseDTO))
                .shippingInfo(infoResponseDTO)
                .member(member)
                .build();
        
        given(orderService.createOrder(any(), any())).willReturn(OrderResponseDTO.builder().build());
        given(userDetailsService.loadUserByJwt(any())).willReturn(new UserDetailsServiceImpl.CustomUserDetails(MemberResponseDTO.from(member)));
        
        //when
        ResultActions response = mockMvc.perform(RestDocumentationRequestBuilders.post("/v1/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderCreateRequestDTO)))
                .andExpect(status().isCreated());
        
        response
                .andDo(MockMvcRestDocumentationWrapper.document(
                        "orders/create",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        ResourceDocumentation.resource(ResourceSnippetParameters.builder()
                                .tag("order")
                                .summary("주문 생성")
                                .description("주문을 생성한다.")
                                .responseHeaders(
                                        headerWithName("Location").description("생성된 주문의 URI")
                                )
                                .build())));
    }
    
    @Test
    @DisplayName("주문 생성 - 잘못된 요청")
    public void When_CreateOrder_With_InvalidRequest_Then_ReturnBadRequest() throws Exception {
        // Arrange
        OrderCreateRequestDTO orderCreateRequestDTO = OrderCreateRequestDTO.builder()
                .ordererNumber(1L)
                .build();
        
        Jwt jwt = Mockito.mock(Jwt.class);
        
        // Act & Assert
        ResultActions response = mockMvc.perform(RestDocumentationRequestBuilders.post("/v1/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(orderCreateRequestDTO.toString())
                        .header("Authorization", "Bearer " + jwt.getTokenValue()))
                .andExpect(status().isBadRequest());
        
        response
                .andDo(MockMvcRestDocumentationWrapper.document(
                        "orders/create",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        ResourceDocumentation.resource(ResourceSnippetParameters.builder()
                                .tag("order")
                                .summary("주문 생성")
                                .description("주문을 생성한다.")
                                .build())));
    }
}