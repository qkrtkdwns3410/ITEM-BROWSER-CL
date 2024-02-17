package com.psj.itembrowser.member.domain.vo;

import lombok.Getter;

@Getter
public enum Gender {
	MEN("남성"),
	WOMEN("여성");

	private final String name;
	private final String description;

	Gender(String description) {
		this.description = description;
		this.name = this.name();
	}
}