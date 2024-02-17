package com.psj.itembrowser.member.domain.vo;

import lombok.Getter;

@Getter
public enum Role {
	ROLE_CUSTOMER("일반 구매자"),
	ROLE_STORE_SELLER("상점 판매자"),
	ROLE_ADMIN("관리자");

	private final String name;
	private final String description;

	Role(String description) {
		this.description = description;
		this.name = this.name();
	}

	@Override
	public String toString() {
		return this.name;
	}
}