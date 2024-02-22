package com.psj.itembrowser.member.domain.vo;

import javax.persistence.Column;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * packageName    : com.psj.itembrowser.member.domain.vo
 * fileName       : MemberName
 * author         : ipeac
 * date           : 2024-01-06
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2024-01-06        ipeac       최초 생성
 */
@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Address {
	/**
	 * 주소 메인
	 */
	@Column(name = "address_main", nullable = false, columnDefinition = "VARCHAR(255)")
	private String addressMain;
	/**
	 * 주소 서브
	 */
	@Column(name = "address_sub", nullable = false)
	private String addressSub;
	/**
	 * 우편번호
	 */
	@Column(name = "zip_code", nullable = false)
	private String zipCode;
	
	@Builder
	private Address(String addressMain, String addressSub, String zipCode) {
		this.addressMain = addressMain;
		this.addressSub = addressSub;
		this.zipCode = zipCode;
	}
	
	public static Address create(String addressMain, String addressSub, String zipCode) {
		return new Address(addressMain, addressSub, zipCode);
	}
}