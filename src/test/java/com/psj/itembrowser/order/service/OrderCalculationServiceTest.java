package com.psj.itembrowser.order.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import com.psj.itembrowser.config.annotation.ServiceWithDBTest;
import com.psj.itembrowser.discount.service.PercentageDiscountService;
import com.psj.itembrowser.member.domain.entity.MemberEntity;
import com.psj.itembrowser.member.domain.vo.MemberShipType;
import com.psj.itembrowser.order.domain.dto.request.OrderCreateRequestDTO;
import com.psj.itembrowser.order.domain.vo.OrdersProductRelationResponseDTO;
import com.psj.itembrowser.product.domain.entity.ProductEntity;
import com.psj.itembrowser.product.mapper.ProductMapper;
import com.psj.itembrowser.product.persistence.ProductPersistence;
import com.psj.itembrowser.product.repository.ProductRepository;
import com.psj.itembrowser.security.common.exception.BadRequestException;
import com.psj.itembrowser.security.common.exception.ErrorCode;
import com.psj.itembrowser.security.common.exception.NotFoundException;
import com.psj.itembrowser.shippingInfos.domain.vo.ShippingPolicy;
import com.psj.itembrowser.shippingInfos.service.ShippingPolicyService;

@ServiceWithDBTest
class OrderCalculationServiceTest {
	
	private ProductPersistence productPersistence;
	
	@Autowired
	private ProductRepository productRepository;
	
	@Autowired
	private TestEntityManager em;
	
	@Mock
	private ProductMapper productMapper;
	
	@Mock
	private PercentageDiscountService percentageDiscountService;
	
	@Mock
	private ShippingPolicyService shippingPolicyService;
	
	private OrderCalculationService orderCalculationService;
	
	@BeforeEach
	public void setUp() {
		productPersistence = new ProductPersistence(productMapper, productRepository);
		orderCalculationService = new OrderCalculationService(productPersistence, percentageDiscountService, shippingPolicyService);
	}
	
	@Test
	@DisplayName("calculateOrderDetails 정상 테스트 - WOW 회원의 경우( 10% 할인 - 무료 배송 )")
	void When_calculateOrderDetailsAndWowuserAndFreeShipping_Expect_Success() {
		//given
		MemberEntity wowMember = MemberEntity.builder()
			.memberShipType(MemberShipType.WOW)
			.build();
		
		BigDecimal expectedTotalPrice = BigDecimal.valueOf(20000);
		BigDecimal expectedTotalDiscount = BigDecimal.valueOf(2000);
		long expectedShippingFee = 0;
		BigDecimal expectedTotalNetPrice = BigDecimal.valueOf(18000);
		
		ProductEntity existProduct = ProductEntity.builder()
			.unitPrice(1000)
			.quantity(20)
			.build();
		
		em.persist(existProduct);
		
		OrdersProductRelationResponseDTO responseDTO = OrdersProductRelationResponseDTO.builder()
			.productId(existProduct.getId())
			.build();
		
		OrderCreateRequestDTO dto = OrderCreateRequestDTO.builder()
			.products(List.of(responseDTO))
			.build();
		
		given(percentageDiscountService.calculateDiscount(any(), any()))
			.willReturn(existProduct.calculateDiscount(existProduct.getQuantity(), wowMember.getMemberShipType().getDiscountRate()));
		
		given(shippingPolicyService.getCurrentShippingPolicy())
			.willReturn(ShippingPolicy.builder()
				.deliveryDefaultFee(ShippingPolicy.DeliveryFee.FREE)
				.freeShipOverAmount(15000)
				.build());
		
		//when
		OrderCalculationResult actualOrderCalculationResult = orderCalculationService.calculateOrderDetails(dto, wowMember);
		
		//then
		assertThat(actualOrderCalculationResult).isNotNull();
		assertThat(actualOrderCalculationResult.getTotalPrice()).isEqualByComparingTo(expectedTotalPrice);
		assertThat(actualOrderCalculationResult.getTotalDiscount()).isEqualByComparingTo(expectedTotalDiscount);
		assertThat(actualOrderCalculationResult.getShippingFee()).isEqualTo(expectedShippingFee);
		assertThat(actualOrderCalculationResult.getTotalNetPrice()).isEqualByComparingTo(expectedTotalNetPrice);
	}
	
	@Test
	@DisplayName("calculateOrderDetails 정상 테스트 - 일반 회원의 경우 (할인 없음 , 배송비포함)")
	void When_calculateOrderDetailsAndNormalUser_Expect_Success() {
		//given
		MemberEntity normalMember = MemberEntity.builder()
			.memberShipType(MemberShipType.REGULAR)
			.build();
		
		BigDecimal expectedTotalPrice = BigDecimal.valueOf(20000);
		BigDecimal expectedTotalDiscount = BigDecimal.ZERO;
		long expectedShippingFee = ShippingPolicy.DeliveryFee.DEFAULT.getFee();
		BigDecimal expectedTotalNetPrice = BigDecimal.valueOf(23000);
		
		ProductEntity existProduct = ProductEntity.builder()
			.unitPrice(1000)
			.quantity(20)
			.build();
		
		em.persist(existProduct);
		
		OrdersProductRelationResponseDTO responseDTO = OrdersProductRelationResponseDTO.builder()
			.productId(existProduct.getId())
			.build();
		
		OrderCreateRequestDTO dto = OrderCreateRequestDTO.builder()
			.products(List.of(responseDTO))
			.build();
		
		given(percentageDiscountService.calculateDiscount(any(), any()))
			.willReturn(BigDecimal.ZERO);
		
		given(shippingPolicyService.getCurrentShippingPolicy())
			.willReturn(ShippingPolicy.builder()
				.deliveryDefaultFee(ShippingPolicy.DeliveryFee.DEFAULT)
				.freeShipOverAmount(30000)
				.build());
		
		//when
		OrderCalculationResult actualOrderCalculationResult = orderCalculationService.calculateOrderDetails(dto, normalMember);
		
		//then
		assertThat(actualOrderCalculationResult).isNotNull();
		assertThat(actualOrderCalculationResult.getTotalPrice()).isEqualByComparingTo(expectedTotalPrice);
		assertThat(actualOrderCalculationResult.getTotalDiscount()).isEqualByComparingTo(expectedTotalDiscount);
		assertThat(actualOrderCalculationResult.getShippingFee()).isEqualTo(expectedShippingFee);
		assertThat(actualOrderCalculationResult.getTotalNetPrice()).isEqualByComparingTo(expectedTotalNetPrice);
	}
	
	@Test
	@DisplayName("calculateOrderDetails OrderCreateRequestDTO 의 주문 상품이 비어있는 경우 BadRequestException 발생")
	void test() {
		//given
		OrderCreateRequestDTO orderCreateRequestDTO = OrderCreateRequestDTO.builder()
			.products(Collections.emptyList())
			.build();
		MemberEntity member = MemberEntity.builder().build();
		
		//when
		assertThatThrownBy(() -> orderCalculationService.calculateOrderDetails(orderCreateRequestDTO, member))
			// then
			.isInstanceOf(BadRequestException.class)
			.hasMessage(ErrorCode.ORDER_PRODUCTS_EMPTY.getMessage());
	}
	
	@Test
	@DisplayName("calculateOrderDetails  상품 조회시 해당 상품이 없는 경우 NotFoundException 발생")
	void test2() {
		//given
		OrdersProductRelationResponseDTO responseDTO = OrdersProductRelationResponseDTO.builder()
			.productId(1L)
			.build();
		
		OrderCreateRequestDTO dto = OrderCreateRequestDTO.builder()
			.products(List.of(responseDTO))
			.build();
		
		MemberEntity member = MemberEntity.builder().build();
		
		//when
		assertThatThrownBy(() -> orderCalculationService.calculateOrderDetails(dto, member))
			// then
			.isInstanceOf(NotFoundException.class)
			.hasMessage(ErrorCode.PRODUCT_NOT_FOUND.getMessage());
	}
	
	@Test
	@DisplayName("calculateOrderDetails 할인계산 서비스에서  에러 발생시 하위 로직 수행안되어야합니다.")
	void test1() {
		//given
		ProductEntity exsitProduct = ProductEntity.builder()
			.unitPrice(1000)
			.quantity(-10)
			.build();
		
		em.persist(exsitProduct);
		
		OrdersProductRelationResponseDTO responseDTO = OrdersProductRelationResponseDTO.builder()
			.productId(exsitProduct.getId())
			.build();
		
		OrderCreateRequestDTO dto = OrderCreateRequestDTO.builder()
			.products(List.of(responseDTO))
			.build();
		
		MemberEntity member = MemberEntity.builder().build();
		
		given(percentageDiscountService.calculateDiscount(any(), any()))
			.willThrow(new BadRequestException(ErrorCode.PRODUCT_VALIDATION_FAIL));
		
		//when
		assertThatThrownBy(() -> {
			orderCalculationService.calculateOrderDetails(dto, member);
		})
			//then
			.isInstanceOf(BadRequestException.class)
			.hasMessage(ErrorCode.PRODUCT_VALIDATION_FAIL.getMessage());
	}
}