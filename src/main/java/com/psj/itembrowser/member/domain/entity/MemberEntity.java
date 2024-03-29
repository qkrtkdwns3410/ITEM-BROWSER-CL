package com.psj.itembrowser.member.domain.entity;

import com.psj.itembrowser.member.domain.dto.request.MemberRequestDTO;
import com.psj.itembrowser.member.domain.dto.response.MemberResponseDTO;
import com.psj.itembrowser.member.domain.vo.*;
import com.psj.itembrowser.security.common.BaseDateTimeEntity;
import com.psj.itembrowser.security.common.exception.ErrorCode;
import com.psj.itembrowser.security.common.exception.NotFoundException;
import com.psj.itembrowser.shippingInfos.domain.entity.ShippingInfoEntity;
import lombok.*;
import org.hibernate.proxy.HibernateProxy;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
    @AttributeOverrides({
            @AttributeOverride(name = "email", column = @Column(name = "email", nullable = false, unique = true)),
            @AttributeOverride(name = "password", column = @Column(name = "password", nullable = false))
    })
    private Credentials credentials;

    /**
     * 성. 이름
     */
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "firstName", column = @Column(name = "first_name", nullable = false)),
            @AttributeOverride(name = "lastName", column = @Column(name = "last_name", nullable = false))
    })
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
    @AttributeOverrides({
            @AttributeOverride(name = "addressMain", column = @Column(name = "address_main", nullable = false, columnDefinition = "VARCHAR(255)")),
            @AttributeOverride(name = "addressSub", column = @Column(name = "address_sub", nullable = true)),
            @AttributeOverride(name = "zipCode", column = @Column(name = "zip_code", nullable = false))
    })
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
    private MemberEntity(Long memberNo, Credentials credentials, Name name, String phoneNumber, Gender gender, Role role, Status status,
                         MemberShipType memberShipType,
                         Address address, LocalDate birthday,
                         LocalDateTime lastLoginDate, List<ShippingInfoEntity> shippingInfos, LocalDateTime deletedDate) {
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
        this.deletedDate = deletedDate;
    }
    
    public boolean isWowMember() {
        return this.memberShipType == MemberShipType.WOW;
    }

    public boolean hasRole(Role role) {
        return this.role == role;
    }

    public boolean isActivated() {
        return this.status == Status.ACTIVE;
    }

    public boolean isAdmin() {
        return this.role == Role.ROLE_ADMIN;
    }

    public static MemberEntity from(MemberResponseDTO dto) {
        if (dto == null) {
            throw new NotFoundException(ErrorCode.MEMBER_NOT_FOUND);
        }

        return MemberEntity.builder()
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
    
    public static MemberEntity from(MemberRequestDTO dto) {
        if (dto == null) {
            throw new NotFoundException(ErrorCode.MEMBER_NOT_FOUND);
        }

        return MemberEntity.builder()
                .credentials(Credentials.builder()
                        .email(dto.getEmail())
                        .password(dto.getPassword())
                        .build())
                .build();
    }

    public static MemberEntity from(Member member, List<ShippingInfoEntity> shippingInfos) {
        if (member == null) {
            throw new NotFoundException(ErrorCode.MEMBER_NOT_FOUND);
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
                .build();

        if (shippingInfos != null) {
            memberEntity.shippingInfos = shippingInfos;
        }

        return memberEntity;
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null)
            return false;
        Class<?> oEffectiveClass =
                o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() :
                        o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ?
                ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass)
            return false;
        MemberEntity member = (MemberEntity) o;
        return getMemberNo() != null && Objects.equals(getMemberNo(), member.getMemberNo());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() :
                getClass().hashCode();
    }
}