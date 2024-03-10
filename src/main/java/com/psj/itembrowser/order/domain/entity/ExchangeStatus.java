package com.psj.itembrowser.order.domain.entity;

import lombok.Getter;
import lombok.NonNull;

/**
 *packageName    : com.psj.itembrowser.order.domain.entity
 * fileName       : ExchangeStatus
 * author         : ipeac
 * date           : 2024-03-07
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2024-03-07        ipeac       최초 생성
 */
@Getter
public enum ExchangeStatus {
	REQUESTED("REQUESTED"),
	PROCESSING("PROCESSING"),
	COMPLETED("COMPLETED"),
	REJECTED("REJECTED");
	
	private final String name;
	private final String value;
	
	ExchangeStatus(String value) {
		this.name = name();
		this.value = value;
	}
	
	public static ExchangeStatus of(@NonNull String value) {
		for (ExchangeStatus exchangeStatus : ExchangeStatus.values()) {
			if (exchangeStatus.getValue().equals(value)) {
				return exchangeStatus;
			}
		}
		
		throw new IllegalArgumentException("No matching constant for [" + value + "]");
	}
}