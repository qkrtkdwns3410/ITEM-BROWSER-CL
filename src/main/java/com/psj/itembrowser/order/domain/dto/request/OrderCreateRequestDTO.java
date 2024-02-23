package com.psj.itembrowser.order.domain.dto.request;

import java.util.List;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

import com.psj.itembrowser.member.domain.dto.response.MemberResponseDTO;
import com.psj.itembrowser.order.domain.vo.OrdersProductRelationResponseDTO;
import com.psj.itembrowser.shippingInfos.domain.dto.response.ShippingInfoResponseDTO;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO for {@link com.psj.itembrowser.order.domain.vo.Order}
 */
@Setter
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderCreateRequestDTO {
	@NotNull
	@Positive
	private Long ordererNumber;
	
	@NotEmpty
	private List<OrdersProductRelationResponseDTO> products;
	
	@NotNull
	private MemberResponseDTO member;
	
	@NotNull
	private ShippingInfoResponseDTO shippingInfo;
	
	@Builder
	private OrderCreateRequestDTO(Long ordererNumber, List<OrdersProductRelationResponseDTO> products, MemberResponseDTO member,
		ShippingInfoResponseDTO shippingInfo) {
		this.ordererNumber = ordererNumber;
		this.products = products;
		this.member = member;
		this.shippingInfo = shippingInfo;
	}
	
	public static OrderCreateRequestDTO create(Long ordererNumber, List<OrdersProductRelationResponseDTO> products,
		MemberResponseDTO member, ShippingInfoResponseDTO shippingInfo) {
		OrderCreateRequestDTO orderCreateRequestDTO = new OrderCreateRequestDTO();
		
		orderCreateRequestDTO.setOrdererNumber(ordererNumber);
		orderCreateRequestDTO.setProducts(products);
		orderCreateRequestDTO.setMember(member);
		orderCreateRequestDTO.setShippingInfo(shippingInfo);
		
		return orderCreateRequestDTO;
	}
}