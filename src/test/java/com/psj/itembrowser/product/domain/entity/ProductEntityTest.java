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

class ProductEntityTest {
	
	private ProductEntity productEntity;
	
	@BeforeEach
	void setUp() {
		productEntity = new ProductEntity();
	}
	
	@Test
	@DisplayName("상품의 재고가 충분한지 검증하는 성공테스트")
	void When_isEnoughStock_Expect_Success() {
		ReflectionTestUtils.setField(productEntity, "quantity", 10);
		int requestQuantity = 5;
		
		assertThat(productEntity.checkStockAvailability(requestQuantity)).isTrue();
	}
	
	@Test
	@DisplayName("상품의 재고와 요청 주문상품이 동일한 경우 성공테스트")
	void When_isStockSameAsOrderProduct_Expect_Success() {
		ReflectionTestUtils.setField(productEntity, "quantity", 10);
		int requestQuantity = 10;
		
		assertThat(productEntity.checkStockAvailability(requestQuantity)).isTrue();
	}
	
	@Test
	@DisplayName("상품의 재고가 부족한 경우 실패테스트")
	void When_isStockNotEnough_Expect_Fail() {
		ReflectionTestUtils.setField(productEntity, "quantity", 10);
		int requestQuantity = 15;
		
		assertThat(productEntity.checkStockAvailability(requestQuantity)).isFalse();
	}
	
	@Test
	@DisplayName("주문상품의 수량이 음수인 경우 BadRequestException 발생")
	void When_orderProductQuantityIsNegative_Expect_BadRequestException() {
		ReflectionTestUtils.setField(productEntity, "quantity", 10);
		int requestQuantity = -5;
		
		assertThrows(BadRequestException.class, () -> productEntity.checkStockAvailability(requestQuantity));
	}
	
	@Test
	@DisplayName("calculateTotalPrice  unitPrice 와 quantity 가 음수이 경우 BadRequestException 발생")
	void When_unitPriceAndQuantityIsNegative_Expect_BadRequestException() {
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
	void When_unitPriceAndQuantityIsMaxValue_Expect_Success() {
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
	void When_unitPriceAndQuantityIsNull_Expect_NullPointerException() {
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
	void When_calculateDiscount_Expect_Success() {
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
	void When_unitPriceIsNull_Expect_NullPointerException() {
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
	void When_requestQuantityIsNegative_Expect_BadRequestException() {
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
	void When_requestDiscountRateIsNegative_Expect_BadRequestException() {
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
	void When_requestDiscountRateIsOver100_Expect_BadRequestException() {
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
	void When_CalculateDiscountUnitPriceAndQuantityIsMaxValue_Expect_Success() {
		//given
		final int requestQuantity = Integer.MAX_VALUE;
		final int requestDiscountRate = 50;
		
		ReflectionTestUtils.setField(productEntity, "unitPrice", Integer.MAX_VALUE);
		
		//when
		BigDecimal actualDiscount = productEntity.calculateDiscount(requestQuantity, requestDiscountRate);
		
		//then
		assertThat(actualDiscount).isNotNull().isEqualTo("2305843007066210304.5");
	}
	
	@Test
	@DisplayName("increaseStock 정상케이스")
	void When_increaseStock_Expect_Success() {
		//given
		final int increaseQuantity = 10;
		final int expectedQuantity = 20;
		
		ReflectionTestUtils.setField(productEntity, "quantity", 10);
		
		//when
		productEntity.increaseStock(increaseQuantity);
		
		//then
		assertThat(productEntity.getQuantity()).isEqualTo(expectedQuantity);
	}
	
	@Test
	@DisplayName("increaseStock increaseQuantity 가 음수인 경우 BadRequestException 발생")
	void When_increaseQuantityIsNegative_Expect_BadRequestException() {
		//given
		final int increaseQuantity = -10;
		
		ReflectionTestUtils.setField(productEntity, "quantity", 10);
		
		//when
		assertThatThrownBy(() -> {
			productEntity.increaseStock(increaseQuantity);
		})
			//then
			.isInstanceOf(BadRequestException.class)
			.hasMessage(ErrorCode.PRODUCT_QUANTITY_LESS_THAN_ZERO.getMessage());
	}
	
	@Test
	@DisplayName("decreaseStock 정상케이스")
	void When_decreaseStock_Expect_Success() {
		//given
		final int decreaseQuantity = 5;
		final int expectedQuantity = 5;
		
		ReflectionTestUtils.setField(productEntity, "quantity", 10);
		
		//when
		productEntity.decreaseStock(decreaseQuantity);
		
		//then
		assertThat(productEntity.getQuantity()).isEqualTo(expectedQuantity);
	}
	
	@Test
	@DisplayName("decreaseStock decreaseQuantity 가 음수인 경우 BadRequestException 발생")
	void When_decreaseQuantityIsNegative_Expect_BadRequestException() {
		//given
		final int decreaseQuantity = -5;
		
		ReflectionTestUtils.setField(productEntity, "quantity", 10);
		
		//when
		assertThatThrownBy(() -> {
			productEntity.decreaseStock(decreaseQuantity);
		})
			//then
			.isInstanceOf(BadRequestException.class)
			.hasMessage(ErrorCode.PRODUCT_QUANTITY_LESS_THAN_ZERO.getMessage());
	}
	
	@Test
	@DisplayName("decreaseStock restStock 이 음수인 경우 BadRequestException 발생")
	void When_restStockIsNegative_Expect_BadRequestException() {
		//given
		final int decreaseQuantity = 15;
		
		ReflectionTestUtils.setField(productEntity, "quantity", 10);
		
		//when
		assertThatThrownBy(() -> {
			productEntity.decreaseStock(decreaseQuantity);
		})
			//then
			.isInstanceOf(BadRequestException.class)
			.hasMessage(ErrorCode.PRODUCT_QUANTITY_LESS_THAN_ZERO.getMessage());
	}
}