package com.psj.itembrowser.product.service;

import java.util.List;

import org.springframework.stereotype.Component;

import com.psj.itembrowser.product.domain.entity.ProductEntity;
import com.psj.itembrowser.product.persistence.ProductPersistence;
import com.psj.itembrowser.security.common.exception.BadRequestException;
import com.psj.itembrowser.security.common.exception.ErrorCode;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 *packageName    : com.psj.itembrowser.product.service.impl
 * fileName       : ProductValidationHelper
 * author         : ipeac
 * date           : 2024-02-12
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2024-02-12        ipeac       최초 생성
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class ProductValidationHelper {
	private final ProductPersistence productPersistence;
	
	public void validateProduct(List<ProductEntity> orderProducts) {
		if (orderProducts == null || orderProducts.isEmpty()) {
			throw new BadRequestException(ErrorCode.PRODUCT_NOT_FOUND);
		}
		
		orderProducts.forEach(orderProduct -> {
			ProductEntity foundProduct = productPersistence.findWithPessimisticLockById(orderProduct.getId());
			
			if (foundProduct.isEnoughStock(orderProduct) == false) {
				throw new BadRequestException(ErrorCode.PRODUCT_QUANTITY_NOT_ENOUGH);
			}
		});
	}
}