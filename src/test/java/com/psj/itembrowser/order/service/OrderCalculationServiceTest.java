package com.psj.itembrowser.order.service;

import static org.assertj.core.api.Assertions.*;

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
import com.psj.itembrowser.order.domain.dto.request.OrderCreateRequestDTO;
import com.psj.itembrowser.order.domain.vo.OrdersProductRelationResponseDTO;
import com.psj.itembrowser.product.mapper.ProductMapper;
import com.psj.itembrowser.product.persistence.ProductPersistence;
import com.psj.itembrowser.product.repository.ProductRepository;
import com.psj.itembrowser.security.common.exception.BadRequestException;
import com.psj.itembrowser.security.common.exception.ErrorCode;
import com.psj.itembrowser.security.common.exception.NotFoundException;
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
	@DisplayName("calculateOrderDetails OrderCreateRequestDTO 의 주문 상품이 비어있는 경우 BadRequestException 발생")
	void test() {
		//given
		OrderCreateRequestDTO orderCreateRequestDTO = OrderCreateRequestDTO.builder()
			.products(Collections.emptyList())
			.build();
		MemberEntity member = MemberEntity.builder().build();
		
		//when
		orderCalculationService.calculateOrderDetails(orderCreateRequestDTO, member);
		
		//then
		assertThatThrownBy(() -> orderCalculationService.calculateOrderDetails(orderCreateRequestDTO, member))
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
}