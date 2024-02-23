package com.psj.itembrowser.member.domain.vo;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

import com.psj.itembrowser.member.domain.dto.request.MemberRequestDTO;
import com.psj.itembrowser.member.domain.dto.response.MemberResponseDTO;
import com.psj.itembrowser.security.common.BaseDateTimeEntity;
import com.psj.itembrowser.security.common.exception.ErrorCode;
import com.psj.itembrowser.security.common.exception.NotFoundException;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@EqualsAndHashCode(of = {"memberNo", "credentials"}, callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
public class Member extends BaseDateTimeEntity {

	private Long memberNo;

	/**
	 * 인증정보
	 */
	private Credentials credentials;

	/**
	 * 성. 이름
	 */
	private Name name;

	/**
	 * 휴대폰번호
	 */
	private String phoneNumber;

	/**
	 * 성별
	 */
	private Gender gender;

	/**
	 * 역할
	 */
	private Role role;

	/**
	 * 회원 상태. ACTIVE -> 활성화, READY -> 대기, DISABLED -> 비활성화
	 */
	private Status status = Status.ACTIVE;

	private MemberShipType memberShipType = MemberShipType.REGULAR;

	/**
	 * 주소
	 */
	private Address address;

	/**
	 * 생년월일. 생년월일
	 */
	private LocalDate birthday;

	/**
	 * 최종 로그인 일시
	 */
	private LocalDateTime lastLoginDate;

	@Builder
	private Member(LocalDateTime createdDate, LocalDateTime updatedDate, LocalDateTime deletedDate, Long memberNo, Credentials credentials, Name name,
		String phoneNumber, Gender gender, Role role, Status status, MemberShipType memberShipType, Address address, LocalDate birthday,
		LocalDateTime lastLoginDate) {
		super(createdDate, updatedDate, deletedDate);
		this.memberNo = memberNo;
		this.credentials = credentials;
		this.name = name;
		this.phoneNumber = phoneNumber;
		this.gender = gender;
		this.role = role;
		this.status = status;
		this.memberShipType = memberShipType;
		this.address = address;
		this.birthday = birthday;
		this.lastLoginDate = lastLoginDate;
	}

	public static Member from(MemberRequestDTO dto) {
		return Member.builder()
			.credentials(
				Credentials.builder()
					.email(dto.getEmail())
					.password(dto.getPassword())
					.build())
			.build();
	}

	public static Member from(MemberResponseDTO dto) {
		if (dto == null) {
			;
			throw new NotFoundException(ErrorCode.MEMBER_NOT_FOUND);
		}

		return Member.builder()
			.memberNo(dto.getMemberNo())
			.credentials(dto.getCredentials())
			.name(dto.getName())
			.phoneNumber(dto.getPhoneNumber())
			.gender(dto.getGender())
			.role(dto.getRole())
			.status(dto.getStatus())
			.address(dto.getAddress())
			.birthday(dto.getBirthday())
			.lastLoginDate(dto.getLastLoginDate())
			.build();
	}

	public boolean isSame(Member other) {
		if (other == null) {
			throw new NotFoundException(ErrorCode.MEMBER_NOT_FOUND);
		}

		return Objects.equals(this, other);
	}

	public boolean hasRole(Role role) {
		return this.role == role;
	}

	public boolean isActivated() {
		return this.status == Status.ACTIVE;
	}
}