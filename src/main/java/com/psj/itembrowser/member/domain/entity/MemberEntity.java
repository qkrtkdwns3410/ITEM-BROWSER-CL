package com.psj.itembrowser.member.domain.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.psj.itembrowser.member.domain.dto.request.MemberRequestDTO;
import com.psj.itembrowser.member.domain.dto.response.MemberResponseDTO;
import com.psj.itembrowser.member.domain.vo.Address;
import com.psj.itembrowser.member.domain.vo.Credentials;
import com.psj.itembrowser.member.domain.vo.Gender;
import com.psj.itembrowser.member.domain.vo.Member;
import com.psj.itembrowser.member.domain.vo.MemberShipType;
import com.psj.itembrowser.member.domain.vo.Name;
import com.psj.itembrowser.member.domain.vo.Role;
import com.psj.itembrowser.member.domain.vo.Status;
import com.psj.itembrowser.order.domain.entity.OrderEntity;
import com.psj.itembrowser.security.common.BaseDateTimeEntity;
import com.psj.itembrowser.shippingInfos.domain.entity.ShippingInfoEntity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "member")
@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberEntity extends BaseDateTimeEntity {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "MEMBER_NO", nullable = false)
	private Long memberNo;
	
	@Embedded
	private Credentials credentials;
	
	/**
	 * 성. 이름
	 */
	@Embedded
	private Name name;
	
	/**
	 * 휴대폰번호
	 */
	@Column(name = "phone_number")
	private String phoneNumber;
	
	/**
	 * 성별
	 */
	@Enumerated(EnumType.STRING)
	private Gender gender;
	
	/**
	 * 역할
	 */
	@Enumerated(EnumType.STRING)
	private Role role;
	
	/**
	 * 회원 상태. ACTIVE -> 활성화, READY -> 대기, DISABLED -> 비활성화
	 */
	@Enumerated(EnumType.STRING)
	private Status status = Status.ACTIVE;
	
	@Enumerated(EnumType.STRING)
	private MemberShipType memberShipType = MemberShipType.REGULAR;
	
	/**
	 * 주소
	 */
	@Embedded
	private Address address;
	
	/**
	 * 생년월일. 생년월일
	 */
	@Column(name = "birthday")
	private LocalDate birthday;
	
	/**
	 * 최종 로그인 일시
	 */
	@Column(name = "last_login_date")
	private LocalDateTime lastLoginDate;
	
	@OneToMany(mappedBy = "member", fetch = FetchType.LAZY)
	@ToString.Exclude
	private List<ShippingInfoEntity> shippingInfos = new ArrayList<>();
	
	@Builder
	private MemberEntity(LocalDateTime createdDate, LocalDateTime updatedDate, LocalDateTime deletedDate, Long memberNo, Credentials credentials,
		Name name, String phoneNumber, Gender gender, Role role, Status status, MemberShipType memberShipType, Address address, LocalDate birthday,
		LocalDateTime lastLoginDate, List<ShippingInfoEntity> shippingInfos, OrderEntity order) {
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
		this.shippingInfos = shippingInfos;
	}
	
	public boolean isSame(MemberEntity other) {
		if (other == null) {
			return false;
		}
		
		return this.getCredentials().getEmail().equals(other.getCredentials().getEmail());
	}
	
	public boolean hasRole(Role role) {
		return this.role == role;
	}
	
	public boolean isActivated() {
		return this.status == Status.ACTIVE;
	}
	
	public static MemberEntity from(MemberRequestDTO dto) {
		MemberEntity member = new MemberEntity();
		member.credentials = Credentials.builder().email(dto.getEmail()).password(dto.getPassword()).build();
		return member;
	}
	
	public static MemberEntity from(MemberResponseDTO dto) {
		if (dto == null) {
			return null;
		}
		
		return MemberEntity.builder()
			.memberNo(dto.getMemberNo())
			.credentials(Credentials.builder().email(dto.getEmail()).password(dto.getPassword()).build())
			.name(Name.builder().firstName(dto.getFirstName()).lastName(dto.getLastName()).build())
			.phoneNumber(dto.getPhoneNumber())
			.gender(dto.getGender())
			.role(dto.getRole())
			.status(dto.getStatus())
			.address(Address.builder().addressMain(dto.getAddressMain()).addressSub(dto.getAddressSub()).zipCode(dto.getZipCode()).build())
			.birthday(dto.getBirthday())
			.lastLoginDate(dto.getLastLoginDate())
			.build();
	}
	
	public static MemberEntity from(Member member, List<ShippingInfoEntity> shippingInfos) {
		if (member == null) {
			return null;
		}
		
		MemberEntity memberEntity = MemberEntity.builder()
			.credentials(member.getCredentials())
			.name(member.getName())
			.phoneNumber(member.getPhoneNumber())
			.gender(member.getGender())
			.role(member.getRole())
			.status(member.getStatus())
			.memberShipType(member.getMemberShipType())
			.address(member.getAddress())
			.birthday(member.getBirthday())
			.lastLoginDate(member.getLastLoginDate())
			.createdDate(member.getCreatedDate())
			.updatedDate(member.getUpdatedDate())
			.deletedDate(member.getDeletedDate())
			.build();
		
		if (shippingInfos != null) {
			memberEntity.shippingInfos = shippingInfos;
		}
		
		return memberEntity;
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof MemberEntity))
			return false;
		
		MemberEntity member = (MemberEntity)o;
		
		return memberNo.equals(member.memberNo);
	}
	
	@Override
	public int hashCode() {
		return memberNo.hashCode();
	}
}