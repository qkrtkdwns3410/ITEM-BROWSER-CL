package com.psj.itembrowser.order.service;

import com.github.pagehelper.PageInfo;
import com.psj.itembrowser.member.domain.entity.MemberEntity;
import com.psj.itembrowser.member.domain.vo.Member;
import com.psj.itembrowser.order.domain.dto.request.OrderCreateRequestDTO;
import com.psj.itembrowser.order.domain.dto.request.OrderPageRequestDTO;
import com.psj.itembrowser.order.domain.dto.response.OrderResponseDTO;

public interface OrderService {

	void removeOrder(long orderId);

	OrderResponseDTO getOrderWithNotDeleted(Long id);

	OrderResponseDTO getOrderWithNoCondition(Long id);
	
	PageInfo<OrderResponseDTO> getOrdersWithPaginationAndNoCondition(MemberEntity member, OrderPageRequestDTO requestDTO);
	
	PageInfo<OrderResponseDTO> getOrdersWithPaginationAndNotDeleted(MemberEntity member, OrderPageRequestDTO requestDTO);

	OrderResponseDTO createOrder(Member member, OrderCreateRequestDTO orderPageRequestDTO);
}