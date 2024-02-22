package com.psj.itembrowser.order.service;

import org.springframework.data.domain.Page;

import com.psj.itembrowser.member.domain.entity.MemberEntity;
import com.psj.itembrowser.member.domain.vo.Member;
import com.psj.itembrowser.order.domain.dto.request.OrderCreateRequestDTO;
import com.psj.itembrowser.order.domain.dto.request.OrderPageRequestDTO;
import com.psj.itembrowser.order.domain.dto.response.OrderResponseDTO;

public interface OrderService {

	void removeOrder(long orderId);

	OrderResponseDTO getOrderWithNotDeleted(Long id);

	OrderResponseDTO getOrderWithNoCondition(Long id);

	Page<OrderResponseDTO> getOrdersWithPaginationAndNoCondition(MemberEntity member, OrderPageRequestDTO requestDTO);

	Page<OrderResponseDTO> getOrdersWithPaginationAndNotDeleted(MemberEntity member, OrderPageRequestDTO requestDTO);

	OrderResponseDTO createOrder(Member member, OrderCreateRequestDTO orderPageRequestDTO);
}