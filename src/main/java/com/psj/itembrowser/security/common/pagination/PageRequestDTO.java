package com.psj.itembrowser.security.common.pagination;

import javax.validation.constraints.Positive;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class PageRequestDTO {

	@Positive(message = "pageNum must be positive number")
	private int pageNum = 1;
	@Positive(message = "pageSize must be positive number")
	private int pageSize = 10;

	public static PageRequestDTO create(int pageNum, int pageSize) {
		return new PageRequestDTO(pageNum, pageSize);
	}
}