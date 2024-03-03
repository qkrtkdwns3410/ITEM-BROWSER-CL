package com.psj.itembrowser.member.domain.dto.response;

import com.psj.itembrowser.member.domain.dto.request.MemberRequestDTO;
import com.psj.itembrowser.member.domain.entity.MemberEntity;
import com.psj.itembrowser.member.domain.vo.*;
import com.psj.itembrowser.security.common.exception.ErrorCode;
import com.psj.itembrowser.security.common.exception.NotFoundException;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * DTO for {@link Member}
 */
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberResponseDTO {
    private Long memberNo;
    private Credentials credentials;
    private Name name;
    private String phoneNumber;
    private Address address;
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
    private MemberResponseDTO(Long memberNo, Credentials credentials, Name name, String phoneNumber, Address address, Gender gender, Role role, Status status,
                              MemberShipType memberShipType, LocalDate birthday, LocalDateTime lastLoginDate, LocalDateTime createdDate, LocalDateTime updatedDate, LocalDateTime deletedDate) {
        this.memberNo = memberNo;
        this.credentials = credentials;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.address = address;
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
        if (member == null) {
            throw new NotFoundException(ErrorCode.MEMBER_NOT_FOUND);
        }
        
        return MemberResponseDTO.builder()
                .memberNo(member.getMemberNo())
                .credentials(member.getCredentials())
                .name(member.getName())
                .phoneNumber(member.getPhoneNumber())
                .address(member.getAddress())
                .gender(member.getGender())
                .role(member.getRole())
                .status(member.getStatus())
                .memberShipType(member.getMemberShipType())
                .birthday(member.getBirthday())
                .lastLoginDate(member.getLastLoginDate())
                .createdDate(member.getCreatedDate())
                .updatedDate(member.getUpdatedDate())
                .deletedDate(member.getDeletedDate())
                .build();
    }
    
    public static MemberResponseDTO from(MemberEntity entity) {
        if (entity == null) {
            throw new NotFoundException(ErrorCode.MEMBER_NOT_FOUND);
        }
        
        return MemberResponseDTO.builder()
                .memberNo(entity.getMemberNo())
                .credentials(entity.getCredentials())
                .name(entity.getName())
                .phoneNumber(entity.getPhoneNumber())
                .address(entity.getAddress())
                .gender(entity.getGender())
                .role(entity.getRole())
                .status(entity.getStatus())
                .memberShipType(entity.getMemberShipType())
                .birthday(entity.getBirthday())
                .lastLoginDate(entity.getLastLoginDate())
                .createdDate(entity.getCreatedDate())
                .updatedDate(entity.getUpdatedDate())
                .deletedDate(entity.getDeletedDate())
                .build();
    }
    
    public static MemberResponseDTO from(MemberRequestDTO member) {
        if (member == null) {
            throw new NotFoundException(ErrorCode.MEMBER_NOT_FOUND);
        }
        
        return MemberResponseDTO.builder()
                .credentials(
                        Credentials.builder()
                                .email(member.getEmail())
                                .password(member.getPassword())
                                .build()
                )
                .name(
                        Name.builder()
                                .firstName(member.getFirstName())
                                .lastName(member.getLastName())
                                .build()
                )
                .phoneNumber(member.getPhone())
                .role(member.getRole())
                .build();
    }
}