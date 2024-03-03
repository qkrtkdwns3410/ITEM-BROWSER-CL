package com.psj.itembrowser.product.service;

import com.psj.itembrowser.order.domain.dto.request.OrdersProductRelationRequestDTO;
import com.psj.itembrowser.product.domain.entity.ProductEntity;
import com.psj.itembrowser.product.persistence.ProductPersistence;
import com.psj.itembrowser.security.common.exception.BadRequestException;
import com.psj.itembrowser.security.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.validation.constraints.NotEmpty;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * packageName    : com.psj.itembrowser.product.service.impl
 * fileName       : ProductValidationHelper
 * author         : ipeac
 * date           : 2024-02-12
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2024-02-12        ipeac       최초 생성
 */
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class ProductValidationHelper {
    private final ProductPersistence productPersistence;
    
    public List<ProductEntity> validateProduct(@NotEmpty List<OrdersProductRelationRequestDTO> orderRequestProducts) {
        if (CollectionUtils.isEmpty(orderRequestProducts)) {
            throw new BadRequestException(ErrorCode.PRODUCT_NOT_FOUND);
        }
        
        List<Long> orderIds = orderRequestProducts.stream()
                .map(OrdersProductRelationRequestDTO::getProductId)
                .collect(Collectors.toList());
        
        List<ProductEntity> foundProducts = productPersistence.findWithPessimisticLockByIds(orderIds);
        
        Map<Long, ProductEntity> productMap = foundProducts.stream()
                .collect(Collectors.toMap(ProductEntity::getId, product -> product));
        
        for (OrdersProductRelationRequestDTO orderProduct : orderRequestProducts) {
            ProductEntity product = productMap.get(orderProduct.getProductId());
            
            int requestedQuantity = orderProduct.getProductQuantity();
            
            if (!product.checkStockAvailability(requestedQuantity)) {
                throw new BadRequestException(ErrorCode.PRODUCT_QUANTITY_NOT_ENOUGH);
            }
        }
        
        return foundProducts;
    }
    
    public void validateProduct(Long productId) {
        productPersistence.findProductByIdWithRepository(productId);
    }
}