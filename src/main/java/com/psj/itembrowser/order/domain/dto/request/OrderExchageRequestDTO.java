package com.psj.itembrowser.order.domain.dto.request;

import java.time.LocalDateTime;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderExchageRequestDTO {
	private Long exchangeOrderId;
	private Long exchangeOrderProductId;
	private String exchangeReason;
	
	private LocalDateTime exchangeRequestDate;
	
	@Builder
	private OrderExchageRequestDTO(Long exchangeOrderId, Long exchangeOrderProductId, String exchangeReason, LocalDateTime exchangeRequestDate) {
		this.exchangeOrderId = exchangeOrderId;
		this.exchangeOrderProductId = exchangeOrderProductId;
		this.exchangeReason = exchangeReason;
		this.exchangeRequestDate = exchangeRequestDate;
	}
}