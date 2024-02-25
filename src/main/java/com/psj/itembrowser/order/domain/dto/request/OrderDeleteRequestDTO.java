package com.psj.itembrowser.order.domain.dto.request;

import com.psj.itembrowser.order.domain.vo.OrderStatus;

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
public class OrderDeleteRequestDTO {
	private Long id;
	
	private OrderStatus orderStatus;
	
	@Builder
	private OrderDeleteRequestDTO(Long id, OrderStatus orderStatus) {
		this.id = id;
		this.orderStatus = orderStatus;
	}
}