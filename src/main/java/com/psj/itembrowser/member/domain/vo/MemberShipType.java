package com.psj.itembrowser.member.domain.vo;

import lombok.Getter;

@Getter
public enum MemberShipType {
	REGULAR("일반 회원", 0),
	WOW("와우 회원", 10);

	private final String name;
	private final String description;
	private final int discountRate;

	MemberShipType(String description, int discountRate) {
		this.name = this.name();
		this.description = description;
		this.discountRate = discountRate;
	}

	@Override
	public String toString() {
		return this.name();
	}
}