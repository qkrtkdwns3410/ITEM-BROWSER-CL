package com.psj.itembrowser.security.common.exception;

import com.psj.itembrowser.security.common.exception.ancestor.CustomRuntimeException;

import lombok.NonNull;

public class StorageException extends CustomRuntimeException {

    public StorageException(@NonNull ErrorCode e) {
        super(e);
    }
}