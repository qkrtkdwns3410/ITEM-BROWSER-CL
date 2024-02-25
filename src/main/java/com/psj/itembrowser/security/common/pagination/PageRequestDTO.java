package com.psj.itembrowser.security.common.pagination;

import javax.validation.constraints.PositiveOrZero;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SuperBuilder
public class PageRequestDTO {
	
	@PositiveOrZero(message = "pageNum must be positive number")
	private int pageNum = 1;
	@PositiveOrZero(message = "pageSize must be positive number")
	private int pageSize = 10;
	
	protected PageRequestDTO(int pageNum, int pageSize) {
		this.pageNum = pageNum;
		this.pageSize = pageSize;
	}
	
	public static PageRequestDTO create(int pageNum, int pageSize) {
		return PageRequestDTO.builder()
			.pageNum(pageNum)
			.pageSize(pageSize)
			.build();
	}
}