package com.psj.itembrowser.order.domain.dto.request;

import java.time.LocalDate;
import java.util.Objects;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import javax.validation.constraints.PositiveOrZero;

import org.springframework.format.annotation.DateTimeFormat;

import com.psj.itembrowser.security.common.pagination.PageRequestDTO;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SuperBuilder
@Slf4j
public class OrderPageRequestDTO extends PageRequestDTO {
	
	@NotNull(message = "userNumber must not be null")
	private Long userNumber;
	
	@PastOrPresent(message = "requestYear must be past or present date")
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDate requestYear;
	
	private OrderPageRequestDTO(
		@PositiveOrZero(message = "pageNum must be positive number") int pageNum,
		@PositiveOrZero(message = "pageSize must be positive number") int pageSize,
		Long userNumber,
		LocalDate requestYear
	) {
		super(pageNum, pageSize);
		this.userNumber = userNumber;
		this.requestYear = requestYear;
	}
	
	public static OrderPageRequestDTO of(PageRequestDTO pageRequestDTO, Long userNumber, String requestYearString) {
		OrderPageRequestDTO orderPageRequestDTO = OrderPageRequestDTO.builder()
			.pageNum(pageRequestDTO.getPageNum())
			.pageSize(pageRequestDTO.getPageSize())
			.userNumber(userNumber)
			.build();
		
		if (Objects.nonNull(requestYearString)) {
			try {
				int year = Integer.parseInt(requestYearString);
				LocalDate requestYear = LocalDate.of(year, 1, 1);
				orderPageRequestDTO.setRequestYear(requestYear);
			} catch (NumberFormatException e) {
				log.info("requestYear parse error : {}", e.getMessage());
			}
		}
		
		return orderPageRequestDTO;
	}
}