package com.psj.itembrowser.security.common.convertor;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.slf4j.Logger;

class YearStringToLocalDateConverterTest {
	
	private YearStringToLocalDateConverter converter;
	
	@Mock
	private Logger log;
	
	@BeforeEach
	void setUp() {
		converter = new YearStringToLocalDateConverter();
	}
	
	@Test
	@DisplayName("yearString 정상 변환")
	void test1() {
		String validYearString = "2024";
		
		LocalDate result = converter.convert(validYearString);
		
		assertThat(result).isEqualTo(LocalDate.of(2024, 1, 1));
	}
	
	@Test
	@DisplayName("yearString이 4자리가 아닐 때 null 반환")
	void test2() {
		String invalidYearString = "20222";
		
		LocalDate result = converter.convert(invalidYearString);
		
		assertThat(result).isNull();
	}
	
	@Test
	@DisplayName("yearString이 빈 문자일때 null 반환")
	void testConvertWhenEmptyStringThenReturnNull() {
		String emptyString = "";
		
		LocalDate result = converter.convert(emptyString);
		
		assertThat(result).isNull();
	}
	
	@Test
	@DisplayName("yearString이 숫자가 아닐때 NFE 발생")
	public void testConvertWhenNotNumberThenReturnNull() {
		String notNumberString = "abcd";
		
		assertThatThrownBy(() -> converter.convert(notNumberString))
			.isInstanceOf(NumberFormatException.class);
	}
	
	@Test
	@DisplayName("yearString이 null일때 null 반환")
	void testConvertWhenNullThenReturnNull() {
		String nullString = null;
		
		LocalDate result = converter.convert(nullString);
		
		assertThat(result).isNull();
	}
}