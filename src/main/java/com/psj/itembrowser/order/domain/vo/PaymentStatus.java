package com.psj.itembrowser.order.domain.vo;

import java.util.Objects;

import lombok.Getter;
import lombok.NonNull;

@Getter
public enum PaymentStatus {
	PENDING("PENDING"), // 결제 대기중

	COMPLETE("COMPLETE"), // 결제 완료

	CANCELED("CANCELED"), // 결제 취소됨
	;

	private final String name;
	private final String value;

	PaymentStatus(@NonNull String value) {
		this.value = value;
		this.name = name();
	}

	public static PaymentStatus of(@NonNull String value) {
		for (PaymentStatus paymentStatus : PaymentStatus.values()) {
			if (Objects.equals(
				paymentStatus.getValue(), value)) {
				return paymentStatus;
			}
		}
		throw new IllegalArgumentException("결제 상태가 존재하지 않습니다.");
	}

}