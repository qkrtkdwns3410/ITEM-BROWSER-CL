package com.psj.itembrowser.member.domain.dto.response;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.psj.itembrowser.member.domain.entity.MemberEntity;
import com.psj.itembrowser.member.domain.vo.Gender;
import com.psj.itembrowser.member.domain.vo.Member;
import com.psj.itembrowser.member.domain.vo.MemberShipType;
import com.psj.itembrowser.member.domain.vo.Role;
import com.psj.itembrowser.member.domain.vo.Status;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO for {@link Member}
 */
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberResponseDTO {
	private Long memberNo;
	private String email;
	@JsonIgnore
	private String password;
	private String firstName;
	private String lastName;
	private String phoneNumber;
	private String addressMain;
	private String addressSub;
	private String zipCode;
	private Gender gender;
	private Role role;
	private Status status;
	private MemberShipType memberShipType;
	private LocalDate birthday;
	private LocalDateTime lastLoginDate;
	private LocalDateTime createdDate;
	private LocalDateTime updatedDate;
	private LocalDateTime deletedDate;
	
	@Builder
	private MemberResponseDTO(Long memberNo, String email, String password, String firstName, String lastName, String phoneNumber, String addressMain,
		String addressSub, String zipCode, Gender gender, Role role, Status status, MemberShipType memberShipType, LocalDate birthday,
		LocalDateTime lastLoginDate, LocalDateTime createdDate, LocalDateTime updatedDate, LocalDateTime deletedDate) {
		this.memberNo = memberNo;
		this.email = email;
		this.password = password;
		this.firstName = firstName;
		this.lastName = lastName;
		this.phoneNumber = phoneNumber;
		this.addressMain = addressMain;
		this.addressSub = addressSub;
		this.zipCode = zipCode;
		this.gender = gender;
		this.role = role;
		this.status = status;
		this.memberShipType = memberShipType;
		this.birthday = birthday;
		this.lastLoginDate = lastLoginDate;
		this.createdDate = createdDate;
		this.updatedDate = updatedDate;
		this.deletedDate = deletedDate;
	}
	
	public static MemberResponseDTO from(Member member) {
		MemberResponseDTO memberResponseDTO = new MemberResponseDTO();
		
		memberResponseDTO.setMemberNo(member.getMemberNo());
		memberResponseDTO.setPassword(member.getCredentials().getPassword());
		memberResponseDTO.setEmail(member.getCredentials().getEmail());
		memberResponseDTO.setFirstName(member.getName().getFirstName());
		memberResponseDTO.setLastName(member.getName().getLastName());
		memberResponseDTO.setPhoneNumber(member.getPhoneNumber());
		memberResponseDTO.setAddressMain(member.getAddress().getAddressMain());
		memberResponseDTO.setAddressSub(member.getAddress().getAddressSub());
		memberResponseDTO.setZipCode(member.getAddress().getZipCode());
		memberResponseDTO.setGender(member.getGender());
		memberResponseDTO.setRole(member.getRole());
		memberResponseDTO.setStatus(member.getStatus());
		memberResponseDTO.setMemberShipType(member.getMemberShipType());
		memberResponseDTO.setBirthday(member.getBirthday());
		memberResponseDTO.setLastLoginDate(member.getLastLoginDate());
		memberResponseDTO.setCreatedDate(member.getCreatedDate());
		memberResponseDTO.setUpdatedDate(member.getUpdatedDate());
		memberResponseDTO.setDeletedDate(member.getDeletedDate());
		
		return memberResponseDTO;
	}
	
	public static MemberResponseDTO from(MemberEntity entity) {
		if (entity == null) {
			return null;
		}
		
		MemberResponseDTO memberResponseDTO = new MemberResponseDTO();
		
		memberResponseDTO.setMemberNo(entity.getMemberNo());
		memberResponseDTO.setEmail(entity.getCredentials().getEmail());
		memberResponseDTO.setPassword(entity.getCredentials().getPassword());
		memberResponseDTO.setFirstName(entity.getName().getFirstName());
		memberResponseDTO.setLastName(entity.getName().getLastName());
		memberResponseDTO.setPhoneNumber(entity.getPhoneNumber());
		memberResponseDTO.setAddressMain(entity.getAddress().getAddressMain());
		memberResponseDTO.setAddressSub(entity.getAddress().getAddressSub());
		memberResponseDTO.setZipCode(entity.getAddress().getZipCode());
		memberResponseDTO.setGender(entity.getGender());
		memberResponseDTO.setRole(entity.getRole());
		memberResponseDTO.setStatus(entity.getStatus());
		memberResponseDTO.setMemberShipType(entity.getMemberShipType());
		memberResponseDTO.setBirthday(entity.getBirthday());
		memberResponseDTO.setLastLoginDate(entity.getLastLoginDate());
		memberResponseDTO.setCreatedDate(entity.getCreatedDate());
		memberResponseDTO.setUpdatedDate(entity.getUpdatedDate());
		memberResponseDTO.setDeletedDate(entity.getDeletedDate());
		
		return memberResponseDTO;
	}
}