package com.psj.itembrowser.security.common.exception.ancestor;

import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;

import com.psj.itembrowser.security.common.exception.ErrorCode;

import lombok.Getter;
import lombok.NonNull;

/**
 * packageName    : com.psj.itembrowser.common.exception
 * fileName       : CustomAuthenticationException
 * author         : ipeac
 * date           : 2024-01-30
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2024-01-30        ipeac       최초 생성
 */
@Getter
public class CustomAuthenticationException extends AuthenticationCredentialsNotFoundException {
	private final ErrorCode errorCode;

	public CustomAuthenticationException(@NonNull ErrorCode e) {
		super(e.getMessage());
		this.errorCode = e;
	}
}