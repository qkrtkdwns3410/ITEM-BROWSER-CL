package com.psj.itembrowser.member.domain.vo;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import com.fasterxml.jackson.annotation.JsonProperty;

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
@Embeddable
@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Credentials {
	/**
	 * 아이디
	 */
	@Column(name = "email", nullable = false, unique = true)
	private String email;
	/**
	 * 비밀번호
	 */
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	@Column(name = "password", nullable = false)
	private String password;

	@Builder
	private Credentials(String email, String password) {
		this.email = email;
		this.password = password;
	}

	public static Credentials create(String email, String password) {
		return new Credentials(email, password);
	}
}