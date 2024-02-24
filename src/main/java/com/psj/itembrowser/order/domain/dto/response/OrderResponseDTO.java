package com.psj.itembrowser.order.domain.dto.response;

import static lombok.AccessLevel.*;

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
import com.psj.itembrowser.security.common.exception.ErrorCode;
import com.psj.itembrowser.security.common.exception.NotFoundException;
import com.psj.itembrowser.shippingInfos.domain.entity.ShippingInfoEntity;
import com.psj.itembrowser.shippingInfos.domain.vo.ShippingInfo;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO for {@link Order}
 */
@Getter
@Setter
@NoArgsConstructor(access = PROTECTED)
public class OrderResponseDTO {
	
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
	
	@Builder
	private OrderResponseDTO(Long id, Long ordererNumber, OrderStatus orderStatus, LocalDateTime paidDate, Long shippingInfoId,
		LocalDateTime createdDate,
		LocalDateTime updatedDate, LocalDateTime deletedDate, MemberResponseDTO member,
		List<OrdersProductRelationResponseDTO> ordersProductRelations) {
		this.id = id;
		this.ordererNumber = ordererNumber;
		this.orderStatus = orderStatus;
		this.paidDate = paidDate;
		this.shippingInfoId = shippingInfoId;
		this.createdDate = createdDate;
		this.updatedDate = updatedDate;
		this.deletedDate = deletedDate;
		this.member = member;
		this.ordersProductRelations = ordersProductRelations;
	}
	
	public static OrderResponseDTO from(Order vo) {
		if (vo == null) {
			throw new NotFoundException(ErrorCode.ORDER_NOT_FOUND);
		}
		
		OrderResponseDTO orderResponseDTO = new OrderResponseDTO();
		
		orderResponseDTO.setId(vo.getId());
		orderResponseDTO.setOrderStatus(vo.getOrderStatus());
		orderResponseDTO.setPaidDate(vo.getPaidDate());
		orderResponseDTO.setCreatedDate(vo.getCreatedDate());
		orderResponseDTO.setUpdatedDate(vo.getUpdatedDate());
		orderResponseDTO.setDeletedDate(vo.getDeletedDate());
		
		Member member = vo.getMember();
		
		if (member != null) {
			orderResponseDTO.setMember(MemberResponseDTO.from(member));
			orderResponseDTO.setOrdererNumber(member.getMemberNo());
		}
		
		ShippingInfo shippingInfo = vo.getShippingInfo();
		
		if (shippingInfo != null) {
			orderResponseDTO.setShippingInfoId(shippingInfo.getId());
		}
		
		if (vo.getProducts() != null && !vo.getProducts().isEmpty()) {
			vo.getProducts().stream()
				.map(OrdersProductRelationResponseDTO::from)
				.forEach(orderResponseDTO.getOrdersProductRelations()::add);
		}
		
		return orderResponseDTO;
	}
	
	public static OrderResponseDTO from(OrderEntity entity) {
		if (entity == null) {
			throw new NotFoundException(ErrorCode.ORDER_NOT_FOUND);
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
		
		if (entity.getOrdersProductRelations() != null && !entity.getOrdersProductRelations().isEmpty()) {
			entity.getOrdersProductRelations().stream()
				.map(OrdersProductRelationResponseDTO::from)
				.forEach(orderResponseDTO.getOrdersProductRelations()::add);
		}
		
		return orderResponseDTO;
	}
}