package com.psj.itembrowser.discount.service;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;

import com.psj.itembrowser.member.domain.entity.MemberEntity;
import com.psj.itembrowser.product.domain.entity.ProductEntity;

/**
 *packageName    : com.psj.itembrowser.order.service.impl
 * fileName       : DiscountService
 * author         : ipeac
 * date           : 2024-02-12
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2024-02-12        ipeac       최초 생성
 */
@Service
public interface DiscountService {
	BigDecimal calculateDiscount(ProductEntity product, MemberEntity member);
}