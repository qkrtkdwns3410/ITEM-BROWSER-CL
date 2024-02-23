package com.psj.itembrowser.security.common.pagination;

import javax.validation.constraints.PositiveOrZero;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class PageRequestDTO {
	
	@PositiveOrZero(message = "pageNum must be positive number")
	private int pageNum = 1;
	@PositiveOrZero(message = "pageSize must be positive number")
	private int pageSize = 10;
	
	public static PageRequestDTO create(int pageNum, int pageSize) {
		return new PageRequestDTO(pageNum, pageSize);
	}
}