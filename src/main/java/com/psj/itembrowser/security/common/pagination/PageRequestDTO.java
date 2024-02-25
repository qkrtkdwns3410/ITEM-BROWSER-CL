package com.psj.itembrowser.security.common.pagination;

import javax.validation.constraints.PositiveOrZero;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PageRequestDTO {
	
	@PositiveOrZero(message = "pageNum must be positive number")
	private int pageNum = 1;
	@PositiveOrZero(message = "pageSize must be positive number")
	private int pageSize = 10;
	
	@Builder
	protected PageRequestDTO(int pageNum, int pageSize) {
		this.pageNum = pageNum;
		this.pageSize = pageSize;
	}
	
	public static PageRequestDTO create(int pageNum, int pageSize) {
		return new PageRequestDTO(pageNum, pageSize);
	}
}