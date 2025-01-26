package com.example.mockbit.common.exception;

import lombok.Getter;

@Getter
public class MockBitException extends RuntimeException{

    private final MockbitErrorCode errorCode;

    public MockBitException(MockbitErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public MockBitException(MockbitErrorCode errorCode, Object... args) {
        super(String.format(errorCode.getMessage(), args));
        this.errorCode = errorCode;
    }

    public MockBitException(MockbitErrorCode errorCode, Throwable cause) {
        super(errorCode.getMessage(), cause);
        this.errorCode = errorCode;
    }
}
