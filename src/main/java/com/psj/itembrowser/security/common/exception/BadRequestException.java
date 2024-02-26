package com.psj.itembrowser.security.common.exception;

import com.psj.itembrowser.security.common.exception.ancestor.CustomIllegalStateException;

import lombok.Getter;
import lombok.NonNull;

/**
 * packageName    : com.psj.itembrowser.common.exception
 * fileName       : DatabaseOperationException
 * author         : ipeac
 * date           : 2023-11-05
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2023-11-05        ipeac       최초 생성
 */
@Getter
public class BadRequestException extends CustomIllegalStateException {

	public BadRequestException(@NonNull ErrorCode e) {
		super(e);
	}
}