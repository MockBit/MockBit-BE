package com.example.mockbit.common.exception;

import lombok.Getter;

@Getter
public class AuthenticationException extends RuntimeException{

    private final MockbitErrorCode errorCode;
    private final String message;

    public AuthenticationException(MockbitErrorCode errorCode) {
        this(errorCode, errorCode.getMessage());
    }

    public AuthenticationException(MockbitErrorCode errorCode, String message) {
        this.errorCode = errorCode;
        this.message = message;
    }
}
