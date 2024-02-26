package com.psj.itembrowser.product.service;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import com.psj.itembrowser.config.annotation.ServiceWithDBTest;
import com.psj.itembrowser.product.domain.entity.ProductEntity;
import com.psj.itembrowser.product.mapper.ProductMapper;
import com.psj.itembrowser.product.persistence.ProductPersistence;
import com.psj.itembrowser.product.repository.ProductRepository;
import com.psj.itembrowser.security.common.exception.BadRequestException;
import com.psj.itembrowser.security.common.exception.ErrorCode;

/**
 *packageName    : com.psj.itembrowser.product.service
 * fileName       : ProductValidationHelperTest
 * author         : ipeac
 * date           : 2024-02-24
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2024-02-24        ipeac       최초 생성
 */
@ServiceWithDBTest
class ProductValidationHelperTest {
	
	@Autowired
	private TestEntityManager em;
	
	private ProductPersistence productPersistence;
	
	private ProductValidationHelper productValidationHelper;
	
	@Autowired
	private ProductRepository productRepository;
	
	@Mock
	private ProductMapper productMapper;
	
	@BeforeEach
	void init() {
		productPersistence = new ProductPersistence(productMapper, productRepository);
		productValidationHelper = new ProductValidationHelper(productPersistence);
	}
	
	@Test
	@DisplayName("상품 검증 성공적으로 수행되는지 테스트")
	void When_validateProduct_Expect_Success() {
		//given
		ProductEntity orderTargetProduct = ProductEntity.builder()
			.quantity(10)
			.build();
		
		em.persist(orderTargetProduct);
		
		ProductEntity orderRequestProduct = ProductEntity.builder()
			.id(orderTargetProduct.getId())
			.quantity(10)
			.build();
		
		//when & then
		assertDoesNotThrow(() -> productValidationHelper.validateProduct(List.of(orderRequestProduct)),
			"상품 검증이 성공적으로 수행되어야 합니다.");
	}
	
	@Test
	@DisplayName("상품 검증 중 상품이 없을 경우 BadRequestException이 발생하는지 테스트")
	void When_validateProductNotFound_Expect_BadRequestException() {
		//when & then
		assertThrows(BadRequestException.class, () -> productValidationHelper.validateProduct(Collections.emptyList()),
			ErrorCode.PRODUCT_NOT_FOUND.getMessage());
	}
	
	@Test
	@DisplayName("상품 검증 중 상품의 재고가 부족할 경우 BadRequestException이 발생하는지 테스트")
	void When_validateProductNotEnough_Expect_BadRequestException() {
		//given
		ProductEntity orderTargetProduct = ProductEntity.builder()
			.quantity(5)
			.build();
		
		em.persist(orderTargetProduct);
		
		ProductEntity orderRequestProduct = ProductEntity.builder()
			.id(orderTargetProduct.getId())
			.quantity(10)
			.build();
		
		//when & then
		assertThrows(BadRequestException.class, () -> productValidationHelper.validateProduct(List.of(orderRequestProduct)),
			ErrorCode.PRODUCT_QUANTITY_NOT_ENOUGH.getMessage());
	}
}