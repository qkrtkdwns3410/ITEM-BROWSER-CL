package com.psj.itembrowser.member.domain.dto.request;

import com.psj.itembrowser.member.domain.entity.MemberEntity;
import com.psj.itembrowser.member.domain.vo.Role;
import com.psj.itembrowser.security.common.exception.BadRequestException;
import com.psj.itembrowser.security.common.exception.ErrorCode;
import lombok.*;

import javax.validation.constraints.NotNull;

/**
 * DTO for {@link com.psj.itembrowser.member.domain.vo.Member}
 */
@Setter
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberRequestDTO {
    
    private String password;
    @NotNull
    private String firstName;
    @NotNull
    private String lastName;
    @NotNull
    private String email;
    @NotNull
    private String phone;
    
    private Role role;
    
    @Builder
    private MemberRequestDTO(String password, String firstName, String lastName, String email, String phone, Role role) {
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
        this.role = role;
    }
    
    public static MemberRequestDTO from(MemberEntity entity) {
        if (entity == null) {
            throw new BadRequestException(ErrorCode.MEMBER_NOT_FOUND);
        }
        
        return MemberRequestDTO.builder()
                .password(entity.getCredentials().getPassword())
                .firstName(entity.getName().getFirstName())
                .lastName(entity.getName().getLastName())
                .email(entity.getCredentials().getEmail())
                .phone(entity.getPhoneNumber())
                .role(entity.getRole())
                .build();
    }
}