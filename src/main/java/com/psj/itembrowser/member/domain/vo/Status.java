package com.psj.itembrowser.member.domain.vo;

import lombok.Getter;

@Getter
public enum Status {
	ACTIVE("활성화"),
	READY("대기"),
	DISABLED("비활성화");

	private final String name;
	private final String description;

	Status(String description) {
		this.description = description;
		this.name = this.name();
	}
}