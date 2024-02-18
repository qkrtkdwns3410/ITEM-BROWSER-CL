package com.psj.itembrowser.product.service.impl;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import com.psj.itembrowser.product.domain.dto.request.ProductRequestDTO;
import com.psj.itembrowser.product.domain.dto.request.ProductUpdateDTO;
import com.psj.itembrowser.product.domain.dto.response.ProductResponseDTO;
import com.psj.itembrowser.product.domain.vo.DeliveryFeeType;
import com.psj.itembrowser.product.domain.vo.Product;
import com.psj.itembrowser.product.domain.vo.ProductStatus;
import com.psj.itembrowser.product.persistence.ProductPersistence;
import com.psj.itembrowser.product.service.FileService;
import com.psj.itembrowser.security.common.exception.ErrorCode;
import com.psj.itembrowser.security.common.exception.NotFoundException;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

	@Mock
	private ProductPersistence productPersistence;

	@Mock
	private FileService fileService;

	@InjectMocks
	private ProductServiceImpl productService;

	@Nested
	class CreateProduct {

		@Test
		@DisplayName("상품 생성 성공 테스트")
		void createProductSuccess() throws Exception {
			try (MockedStatic<Product> productMockedStatic = mockStatic(Product.class)) {
				// given
				ProductRequestDTO dto = mock(ProductRequestDTO.class);
				Product product = mock(Product.class);

				productMockedStatic.when(() -> Product.from(dto)).thenReturn(product);
				// when
				productService.createProduct(dto);

				// then
				verify(productPersistence, times(1)).createProduct(product);
				verify(fileService, times(1)).createProductImages(any(List.class), any(Long.class));
			}
		}

		@Test
		@DisplayName("상품 생성 판매 날짜 유효성 검증 실패")
		void createProductFailDueToInvalidSellDates() {
			try (MockedStatic<Product> productMockedStatic = mockStatic(Product.class)) {
				// given
				ProductRequestDTO dto = mock(ProductRequestDTO.class);
				LocalDateTime invalidSellStartDatetime = LocalDateTime.now().plusDays(2);
				LocalDateTime invalidSellEndDatetime = LocalDateTime.now().plusDays(1);

				Product product = new Product(
					1L,
					"섬유유연제",
					1,
					"상품 디테일",
					ProductStatus.APPROVED,
					10,
					1000,
					"qkrtkdwns3410",
					invalidSellStartDatetime,
					invalidSellEndDatetime,
					"섬유유연제",
					"섬유나라",
					DeliveryFeeType.FREE,
					"배송방법",
					5000,
					15000,
					"returnCenterCode",
					Collections.emptyList(),
					Collections.emptyList()
				);

				productMockedStatic.when(() -> Product.from(dto)).thenReturn(product);

				// when & then
				assertThrows(IllegalArgumentException.class, () -> productService.createProduct(dto),
					"The sell start datetime must not be before the sell end datetime.");

				verify(productPersistence, never()).createProduct(product);
				verify(fileService, never()).createProductImages(anyList(), anyLong());
			}
		}
	}

	@Nested
	class UpdateProduct {

		@Test
		@DisplayName("상품 업데이트 성공")
		void updateProductSuccess() {
			// given
			try (MockedStatic<Product> productMockedStatic = mockStatic(Product.class)) {
				Product product = mock(Product.class);
				ProductUpdateDTO productUpdateDTO = new ProductUpdateDTO();
				Long productId = 1L;

				productMockedStatic.when(() -> Product.from(productUpdateDTO)).thenReturn(product);

				doNothing().when(product).validateSellDates();
				doNothing().when(productPersistence).updateProduct(product);
				doNothing().when(fileService).updateProductImages(productUpdateDTO, productId);

				// when
				productService.updateProduct(productUpdateDTO, productId);

				// then
				verify(productPersistence, times(1)).findProductStatusForUpdate(productId);
				verify(productPersistence, times(1)).updateProduct(product);
				verify(fileService, times(1)).updateProductImages(productUpdateDTO, productId);

			}
		}

		@Test
		@DisplayName("상품을 찾을 수 없음 업데이트 실패")
		void updateProductProductNotFoundFailure() {
			// given
			ProductUpdateDTO productUpdateDTO = mock(ProductUpdateDTO.class);
			Long productId = 1L;

			when(productPersistence.findProductStatusForUpdate(productId)).thenThrow(
				new NotFoundException(ErrorCode.PRODUCT_NOT_FOUND));

			// when & then
			assertThrows(NotFoundException.class,
				() -> productService.updateProduct(productUpdateDTO, productId));

			verify(productPersistence, times(1)).findProductStatusForUpdate(productId);
			verify(productPersistence, never()).updateProduct(any());
			verify(fileService, never()).updateProductImages(any(), anyLong());
		}

		@Test
		@DisplayName("상품 업데이트 시 유효하지 않은 데이터로 인한 실패")
		void updateProductWithInvalidDataFailure() {
			// given
			ProductUpdateDTO productUpdateDTO = mock(ProductUpdateDTO.class);
			Long productId = 1L;
			when(Product.from(productUpdateDTO)).thenThrow(IllegalArgumentException.class);

			// when & then
			assertThrows(IllegalArgumentException.class,
				() -> productService.updateProduct(productUpdateDTO, productId));

			verify(productPersistence, never()).findProductStatusForUpdate(productId);
			verify(productPersistence, never()).updateProduct(any(Product.class));
			verify(fileService, never()).updateProductImages(any(ProductUpdateDTO.class), anyLong());
		}
	}

	@Nested
	class GetProduct {

		@Test
		@DisplayName("상품 조회 성공")
		void getProductSuccess() {
			// given
			Long productId = 1L;
			Product product = mock(Product.class);
			ProductResponseDTO expectedResponse = ProductResponseDTO.from(product);
			when(productPersistence.findProductById(productId)).thenReturn(product);

			// when
			ProductResponseDTO actualResponse = productService.getProduct(productId);

			// then
			assertThat(actualResponse).isNotNull().isEqualToComparingFieldByField(expectedResponse);
			verify(productPersistence, times(1)).findProductById(productId);
		}

		@Test
		@DisplayName("상품 조회 실패 - 상품을 찾을 수 없음")
		void getProductFailureProductNotFound() {
			// given
			Long productId = 1L;
			when(productPersistence.findProductById(productId)).thenThrow(
				new NotFoundException(ErrorCode.PRODUCT_NOT_FOUND));

			// when & then
			assertThrows(NotFoundException.class, () -> productService.getProduct(productId));
		}
	}

	@Nested
	class DeleteProduct {

		@Test
		@DisplayName("상품 삭제 성공")
		void deleteProductSuccess() {
			// given
			Long productId = 1L;
			Product product = mock(Product.class);
			when(productPersistence.findProductStatusForUpdate(productId)).thenReturn(product);
			doNothing().when(productPersistence).softDeleteProduct(productId);
			doNothing().when(fileService).deleteProductImages(productId);

			// when
			productService.deleteProduct(productId);

			// then
			verify(productPersistence, times(1)).findProductStatusForUpdate(productId);
			verify(productPersistence, times(1)).softDeleteProduct(productId);
			verify(fileService, times(1)).deleteProductImages(productId);
		}

		@Test
		@DisplayName("상품을 찾을 수 없음 삭제 실패")
		void deleteProductProductNotFoundFailure() {
			// given
			Long productId = 1L;
			doThrow(new NotFoundException(ErrorCode.PRODUCT_NOT_FOUND)).when(productPersistence)
				.findProductStatusForUpdate(productId);

			// when & then
			assertThrows(NotFoundException.class, () -> productService.deleteProduct(productId));

			verify(productPersistence, times(1)).findProductStatusForUpdate(productId);
			verify(productPersistence, never()).softDeleteProduct(productId);
			verify(fileService, never()).deleteProductImages(productId);
		}

		@Test
		@DisplayName("상품 삭제 시 연관된 이미지 삭제 실패")
		void deleteProductWithImageDeletionFailure() {
			// given
			Long productId = 1L;
			Product product = mock(Product.class);
			when(productPersistence.findProductStatusForUpdate(productId)).thenReturn(product);
			doNothing().when(productPersistence).softDeleteProduct(productId);
			doThrow(RuntimeException.class).when(fileService).deleteProductImages(productId);

			// when & then
			assertThrows(RuntimeException.class, () -> productService.deleteProduct(productId));

			verify(productPersistence, times(1)).findProductStatusForUpdate(productId);
			verify(productPersistence, times(1)).softDeleteProduct(productId);
			verify(fileService, times(1)).deleteProductImages(productId);
		}
	}
}