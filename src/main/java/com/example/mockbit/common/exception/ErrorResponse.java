package com.example.mockbit.common.exception;

public record ErrorResponse(
        String errorCode,
        String message
) {

    public static ErrorResponse of(MockbitErrorCode errorCode) {
        return new ErrorResponse(errorCode.name(), errorCode.getMessage());
    }

    public static ErrorResponse of(MockbitErrorCode errorCode, String message) {
        return new ErrorResponse(errorCode.name(), message);
    }
}
