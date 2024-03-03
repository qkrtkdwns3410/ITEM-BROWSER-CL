package com.psj.itembrowser.order.service;

import com.psj.itembrowser.discount.service.PercentageDiscountService;
import com.psj.itembrowser.member.domain.entity.MemberEntity;
import com.psj.itembrowser.order.domain.dto.request.OrderCreateRequestDTO;
import com.psj.itembrowser.order.domain.dto.request.OrdersProductRelationRequestDTO;
import com.psj.itembrowser.order.domain.vo.OrderCalculationResult;
import com.psj.itembrowser.product.domain.entity.ProductEntity;
import com.psj.itembrowser.product.persistence.ProductPersistence;
import com.psj.itembrowser.security.common.exception.BadRequestException;
import com.psj.itembrowser.security.common.exception.ErrorCode;
import com.psj.itembrowser.shippingInfos.service.ShippingPolicyService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * packageName    : com.psj.itembrowser.order.service.impl fileName       : OrderCalculationService author         : ipeac date           : 2024-02-12 description    : =========================================================== DATE              AUTHOR             NOTE ----------------------------------------------------------- 2024-02-12        ipeac       최초 생성
 */
@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderCalculationService {
    
    private final ProductPersistence productPersistence;
    private final PercentageDiscountService percentageDiscountService;
    private final ShippingPolicyService shippingPolicyService;
    
    public OrderCalculationResult calculateOrderDetails(@NonNull OrderCreateRequestDTO orderCreateRequestDTO, @NonNull MemberEntity member) {
        validateOrderProduct(orderCreateRequestDTO);
        
        long totalPrice = 0;
        long totalDiscount = 0;
        long shippingFee = 0;
        
        List<Long> orderProductsIds = orderCreateRequestDTO.getProducts()
                .stream()
                .map(OrdersProductRelationRequestDTO::getProductId)
                .collect(Collectors.toList());
        
        List<ProductEntity> foundProducts = productPersistence.findWithPessimisticLockByIds(orderProductsIds);
        
        for (ProductEntity foundProduct : foundProducts) {
            long productPrice = foundProduct.calculateTotalPrice();
            
            totalPrice += productPrice;
            
            BigDecimal discount = percentageDiscountService.calculateDiscount(foundProduct, member);
            
            totalDiscount += discount.longValue();
        }
        
        shippingFee = shippingPolicyService.getCurrentShippingPolicy().calculateShippingFee(totalPrice, member).getFee();
        
        long totalNetPrice = totalPrice - totalDiscount + shippingFee;
        
        return OrderCalculationResult.of(totalPrice, totalDiscount, shippingFee, totalNetPrice);
    }
    
    private static void validateOrderProduct(OrderCreateRequestDTO orderCreateRequestDTO) {
        if (CollectionUtils.isEmpty(orderCreateRequestDTO.getProducts())) {
            throw new BadRequestException(ErrorCode.ORDER_PRODUCTS_EMPTY);
        }
    }
}