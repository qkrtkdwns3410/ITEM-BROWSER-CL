package com.psj.itembrowser.member.domain.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
import javax.persistence.OneToOne;
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
	
	@OneToOne(mappedBy = "member")
	private OrderEntity order;
	
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
		this.order = order;
	}
	
	public boolean isSame(MemberEntity other) {
		if (other == null) {
			return false;
		}
		
		return Objects.equals(this, other);
	}
	
	public boolean hasRole(Role role) {
		return this.role == role;
	}
	
	public boolean isActivated() {
		return this.status == Status.ACTIVE;
	}
	
	public static MemberEntity from(MemberRequestDTO dto) {
		MemberEntity member = new MemberEntity();
		member.credentials = new Credentials(dto.getMemberId(), dto.getPassword());
		return member;
	}
	
	public static MemberEntity from(MemberResponseDTO dto) {
		MemberEntity member = new MemberEntity();
		
		member.memberNo = dto.getMemberNo();
		member.credentials = new Credentials(dto.getEmail(), dto.getPassword());
		member.name = Name.builder().firstName(dto.getFirstName()).lastName(dto.getLastName()).build();
		member.phoneNumber = dto.getPhoneNumber();
		member.gender = dto.getGender(); // Gender enum에 맞게 변환
		member.role = dto.getRole(); // Role enum에 맞게 변환
		member.status = dto.getStatus(); // Status enum에 맞게 변환
		member.address = Address.builder().addressMain(dto.getAddressMain()).addressSub(dto.getAddressSub()).zipCode(dto.getZipCode()).build();
		member.birthday = dto.getBirthday();
		member.lastLoginDate = dto.getLastLoginDate();
		
		return member;
	}
	
	public static MemberEntity from(Member member, List<ShippingInfoEntity> shippingInfos, OrderEntity order) {
		if (member == null) {
			return null;
		}
		MemberEntity memberEntity = new MemberEntity();
		
		memberEntity.credentials = member.getCredentials();
		memberEntity.name = member.getName();
		memberEntity.phoneNumber = member.getPhoneNumber();
		memberEntity.gender = member.getGender();
		memberEntity.role = member.getRole();
		memberEntity.status = member.getStatus();
		memberEntity.memberShipType = member.getMemberShipType();
		memberEntity.address = member.getAddress();
		memberEntity.birthday = member.getBirthday();
		memberEntity.lastLoginDate = member.getLastLoginDate();
		memberEntity.createdDate = member.getCreatedDate();
		memberEntity.updatedDate = member.getUpdatedDate();
		memberEntity.deletedDate = member.getDeletedDate();
		if (shippingInfos != null) {
			memberEntity.shippingInfos = shippingInfos;
		}
		
		memberEntity.order = order;
		
		return memberEntity;
	}
}