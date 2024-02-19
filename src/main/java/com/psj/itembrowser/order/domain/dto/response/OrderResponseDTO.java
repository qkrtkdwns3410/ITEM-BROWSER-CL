package com.psj.itembrowser.order.domain.dto.response;

import static lombok.AccessLevel.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.psj.itembrowser.member.domain.dto.response.MemberResponseDTO;
import com.psj.itembrowser.member.domain.entity.MemberEntity;
import com.psj.itembrowser.member.domain.vo.Member;
import com.psj.itembrowser.order.domain.entity.OrderEntity;
import com.psj.itembrowser.order.domain.vo.Order;
import com.psj.itembrowser.order.domain.vo.OrderStatus;
import com.psj.itembrowser.order.domain.vo.OrdersProductRelationResponseDTO;
import com.psj.itembrowser.shippingInfos.domain.entity.ShippingInfoEntity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO for {@link Order}
 */
@Getter
@Setter
@NoArgsConstructor(access = PROTECTED)
@AllArgsConstructor
public class OrderResponseDTO implements Serializable {
	
	private Long id;
	private Long ordererNumber;
	private OrderStatus orderStatus;
	private LocalDateTime paidDate;
	private Long shippingInfoId;
	private LocalDateTime createdDate;
	private LocalDateTime updatedDate;
	private LocalDateTime deletedDate;
	
	private MemberResponseDTO member;
	private List<OrdersProductRelationResponseDTO> ordersProductRelations = new ArrayList<>();
	
	public static OrderResponseDTO from(Order order) {
		OrderResponseDTO orderResponseDTO = new OrderResponseDTO();
		
		orderResponseDTO.setId(order.getId());
		orderResponseDTO.setOrdererNumber(order.getOrdererNumber());
		orderResponseDTO.setOrderStatus(order.getOrderStatus());
		orderResponseDTO.setPaidDate(order.getPaidDate());
		orderResponseDTO.setShippingInfoId(order.getShippingInfoId());
		orderResponseDTO.setCreatedDate(order.getCreatedDate());
		orderResponseDTO.setUpdatedDate(order.getUpdatedDate());
		orderResponseDTO.setDeletedDate(order.getDeletedDate());
		
		Member member = order.getMember();
		orderResponseDTO.setMember(MemberResponseDTO.from(member));
		
		order.getProducts().stream()
			.map(OrdersProductRelationResponseDTO::from)
			.forEach(orderResponseDTO.getOrdersProductRelations()::add);
		
		return orderResponseDTO;
	}
	
	public static OrderResponseDTO from(OrderEntity entity) {
		if (entity == null) {
			return null;
		}
		
		OrderResponseDTO orderResponseDTO = new OrderResponseDTO();
		
		orderResponseDTO.setId(entity.getId());
		orderResponseDTO.setOrderStatus(entity.getOrderStatus());
		orderResponseDTO.setPaidDate(entity.getPaidDate());
		orderResponseDTO.setCreatedDate(entity.getCreatedDate());
		orderResponseDTO.setUpdatedDate(entity.getUpdatedDate());
		orderResponseDTO.setDeletedDate(entity.getDeletedDate());
		
		MemberEntity member = entity.getMember();
		
		if (member != null) {
			orderResponseDTO.setMember(MemberResponseDTO.from(member));
			orderResponseDTO.setOrdererNumber(member.getMemberNo());
		}
		
		ShippingInfoEntity shippingInfo = entity.getShippingInfo();
		
		if (shippingInfo != null) {
			orderResponseDTO.setShippingInfoId(shippingInfo.getId());
		}
		
		entity.getOrdersProductRelations().stream()
			.map(OrdersProductRelationResponseDTO::from)
			.forEach(orderResponseDTO.getOrdersProductRelations()::add);
		
		return orderResponseDTO;
	}
}