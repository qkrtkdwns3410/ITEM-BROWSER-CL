package com.psj.itembrowser.discount.service;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;

import com.psj.itembrowser.member.domain.entity.MemberEntity;
import com.psj.itembrowser.member.domain.vo.MemberShipType;
import com.psj.itembrowser.product.domain.entity.ProductEntity;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 *packageName    : com.psj.itembrowser.order.service.impl
 * fileName       : FixedDiscountService
 * author         : ipeac
 * date           : 2024-02-12
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2024-02-12        ipeac       최초 생성
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PercentageDiscountService implements DiscountService {
	@Override
	public BigDecimal calculateDiscount(ProductEntity product, MemberEntity member) {
		if (member.getMemberShipType() == MemberShipType.WOW) {
			return product.calculateDiscount(product.getQuantity(), MemberShipType.WOW.getDiscountRate());
		}
		
		return BigDecimal.ZERO;
	}
}