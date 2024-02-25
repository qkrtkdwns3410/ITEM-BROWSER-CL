package com.psj.itembrowser.product.domain.entity;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import com.psj.itembrowser.security.common.exception.BadRequestException;
import com.psj.itembrowser.security.common.exception.ErrorCode;
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
	
	@Test
	@DisplayName("calculateTotalPrice  unitPrice 와 quantity 가 음수이 경우 BadRequestException 발생")
	void test1() {
		//given
		ReflectionTestUtils.setField(productEntity, "unitPrice", -1);
		ReflectionTestUtils.setField(productEntity, "quantity", -1);
		
		//when
		assertThatThrownBy(() -> {
			productEntity.calculateTotalPrice();
		})
			//then
			.isInstanceOf(BadRequestException.class)
			.hasMessage(ErrorCode.PRODUCT_VALIDATION_FAIL.getMessage());
	}
	
	@Test
	@DisplayName("calculateTotalPrice unitPrice 와 quantity 가 INTEGER 범위는 넘는 연산의 경우 정상 처리")
	void test2() {
		//given
		BigDecimal expectedTotalPrice = BigDecimal.valueOf(Integer.MAX_VALUE).multiply(BigDecimal.valueOf(Integer.MAX_VALUE));
		
		ReflectionTestUtils.setField(productEntity, "unitPrice", Integer.MAX_VALUE);
		ReflectionTestUtils.setField(productEntity, "quantity", Integer.MAX_VALUE);
		
		//when
		BigDecimal actualTotalPrice = productEntity.calculateTotalPrice();
		
		//then
		assertThat(actualTotalPrice).isNotNull().isEqualTo(expectedTotalPrice);
	}
	
	@Test
	@DisplayName("calculateTotalPrice unitPrice 및 quantity 가 null 인 경우 NPE 발생")
	void test3() {
		//given
		ReflectionTestUtils.setField(productEntity, "unitPrice", null);
		ReflectionTestUtils.setField(productEntity, "quantity", null);
		
		//when
		assertThatThrownBy(() -> {
			productEntity.calculateTotalPrice();
		})
			//then
			.isInstanceOf(NullPointerException.class)
			.hasMessage("unitPrice must not be null");
	}
	
	@Test
	@DisplayName("calculateDiscount 정상케이스")
	void test4() {
		//given
		final int requestQuantity = 10;
		final int requestDiscountRate = 50;
		
		ReflectionTestUtils.setField(productEntity, "unitPrice", 1000);
		
		//when
		BigDecimal actualDiscount = productEntity.calculateDiscount(requestQuantity, requestDiscountRate);
		
		//then
		assertThat(actualDiscount).isNotNull().isEqualTo("5000.0");
	}
	
	@Test
	@DisplayName("calculateDiscount unitPrice 가 null 인 경우 NPE 발생")
	void test5() {
		//given
		final int requestQuantity = 10;
		final int requestDiscountRate = 50;
		
		ReflectionTestUtils.setField(productEntity, "unitPrice", null);
		
		//when
		assertThatThrownBy(() -> {
			productEntity.calculateDiscount(requestQuantity, requestDiscountRate);
		})
			//then
			.isInstanceOf(NullPointerException.class)
			.hasMessage("unitPrice must not be null");
	}
	
	@Test
	@DisplayName("calculateDiscount requestQuantity 가 음수인 경우 BadRequestException 발생")
	void test6() {
		//given
		final int requestQuantity = -10;
		final int requestDiscountRate = 50;
		
		ReflectionTestUtils.setField(productEntity, "unitPrice", 1000);
		
		//when
		assertThatThrownBy(() -> {
			productEntity.calculateDiscount(requestQuantity, requestDiscountRate);
		})
			//then
			.isInstanceOf(BadRequestException.class)
			.hasMessage(ErrorCode.PRODUCT_VALIDATION_FAIL.getMessage());
	}
	
	@Test
	@DisplayName("calculateDiscount requestDiscountRate 가 음수인 경우 BadRequestException 발생")
	void test7() {
		//given
		final int requestQuantity = 10;
		final int requestDiscountRate = -50;
		
		ReflectionTestUtils.setField(productEntity, "unitPrice", 1000);
		
		//when
		assertThatThrownBy(() -> {
			productEntity.calculateDiscount(requestQuantity, requestDiscountRate);
		})
			//then
			.isInstanceOf(BadRequestException.class)
			.hasMessage(ErrorCode.PRODUCT_VALIDATION_FAIL.getMessage());
	}
	
	@Test
	@DisplayName("calculateDiscount discountRate 가 100 초과인 경우 BadRequestException 발생")
	void test8() {
		//given
		final int requestQuantity = 10;
		final int requestDiscountRate = 101;
		
		ReflectionTestUtils.setField(productEntity, "unitPrice", 1000);
		
		//when
		assertThatThrownBy(() -> {
			productEntity.calculateDiscount(requestQuantity, requestDiscountRate);
		})
			//then
			.isInstanceOf(BadRequestException.class)
			.hasMessage(ErrorCode.PRODUCT_VALIDATION_FAIL.getMessage());
	}
	
	@Test
	@DisplayName("calculateDiscount unitPrice 와 quantityDecimal 가 INTEGER 범위를 넘는 경우 정상 처리")
	void test9() {
		//given
		final int requestQuantity = Integer.MAX_VALUE;
		final int requestDiscountRate = 50;
		
		ReflectionTestUtils.setField(productEntity, "unitPrice", Integer.MAX_VALUE);
		
		//when
		BigDecimal actualDiscount = productEntity.calculateDiscount(requestQuantity, requestDiscountRate);
		
		//then
		assertThat(actualDiscount).isNotNull().isEqualTo("2305843007066210304.5");
	}
}