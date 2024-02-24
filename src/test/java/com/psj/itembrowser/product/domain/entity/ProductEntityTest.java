package com.psj.itembrowser.product.domain.entity;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import com.psj.itembrowser.security.common.exception.BadRequestException;
import com.psj.itembrowser.security.common.exception.NotFoundException;

public class ProductEntityTest {
	
	private ProductEntity productEntity;
	private ProductEntity orderProductEntity;
	
	@BeforeEach
	public void setUp() {
		productEntity = new ProductEntity();
		orderProductEntity = new ProductEntity();
	}
	
	@Test
	@DisplayName("상품의 재고가 충분한지 검증하는 성공테스트")
	public void When_isEnoughStock_Expect_Success() {
		ReflectionTestUtils.setField(productEntity, "quantity", 10);
		ReflectionTestUtils.setField(orderProductEntity, "quantity", 5);
		
		assertThat(productEntity.isEnoughStock(orderProductEntity)).isTrue();
	}
	
	@Test
	@DisplayName("상품의 재고와 요청 주문상품이 동일한 경우 성공테스트")
	public void When_isStockSameAsOrderProduct_Expect_Success() {
		ReflectionTestUtils.setField(productEntity, "quantity", 10);
		ReflectionTestUtils.setField(orderProductEntity, "quantity", 10);
		
		assertThat(productEntity.isEnoughStock(orderProductEntity)).isTrue();
	}
	
	@Test
	@DisplayName("상품의 재고가 부족한 경우 실패테스트")
	public void When_isStockNotEnough_Expect_Fail() {
		ReflectionTestUtils.setField(productEntity, "quantity", 10);
		ReflectionTestUtils.setField(orderProductEntity, "quantity", 15);
		
		assertThat(productEntity.isEnoughStock(orderProductEntity)).isFalse();
	}
	
	@Test
	@DisplayName("주문상품이 null인 경우 NotFoundException 발생")
	public void When_orderProductIsNull_Expect_NotFoundException() {
		ReflectionTestUtils.setField(productEntity, "quantity", 10);
		
		assertThrows(NotFoundException.class, () -> productEntity.isEnoughStock(null));
	}
	
	@Test
	@DisplayName("주문상품의 수량이 음수인 경우 BadRequestException 발생")
	public void When_orderProductQuantityIsNegative_Expect_BadRequestException() {
		ReflectionTestUtils.setField(productEntity, "quantity", 10);
		ReflectionTestUtils.setField(orderProductEntity, "quantity", -5);
		
		assertThrows(BadRequestException.class, () -> productEntity.isEnoughStock(orderProductEntity));
	}
}