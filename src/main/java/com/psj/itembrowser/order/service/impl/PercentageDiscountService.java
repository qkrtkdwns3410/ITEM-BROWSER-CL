package com.psj.itembrowser.order.service.impl;

import org.springframework.stereotype.Service;

import com.psj.itembrowser.member.domain.vo.Member;
import com.psj.itembrowser.member.domain.vo.MemberShipType;
import com.psj.itembrowser.product.domain.vo.Product;

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
	public double calculateDiscount(Product product, Member member) {
		if (member.getMemberShipType() == MemberShipType.WOW) {
			return product.calculateDiscount(product.getQuantity(), MemberShipType.WOW.getDiscountRate());
		}

		return 0;
	}
}