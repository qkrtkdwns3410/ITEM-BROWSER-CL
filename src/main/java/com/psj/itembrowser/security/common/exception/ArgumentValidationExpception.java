package com.psj.itembrowser.security.common.exception;

import com.psj.itembrowser.security.common.exception.ancestor.CustomRuntimeException;

import lombok.Getter;
import lombok.NonNull;

@Getter
public class ArgumentValidationExpception extends CustomRuntimeException {
	public ArgumentValidationExpception(@NonNull ErrorCode e) {
		super(e);
	}
}