package com.psj.itembrowser.member.domain.vo;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
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
@Embeddable
@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Name {
	/**
	 * 이름
	 */
	@Column(name = "first_name", nullable = false)
	private String firstName;
	/**
	 * 성
	 */
	@Column(name = "last_name", nullable = false)
	private String lastName;

	public static Name create(String firstName, String lastName) {
		return new Name(firstName, lastName);
	}
}