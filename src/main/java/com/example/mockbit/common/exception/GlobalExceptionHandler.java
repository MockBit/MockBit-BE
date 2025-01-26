package com.example.mockbit.common.exception;

import jakarta.persistence.NoResultException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.naming.AuthenticationException;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final String LOG_FORMAT = """
            \n\t{
                "Request URI": "{} {}",
                "RequestBody": {},
                "Error Message": "{}"
            \t}
            """;

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> authenticationException(HttpServletRequest req, com.example.mockbit.common.exception.AuthenticationException e) {
        log.warn(LOG_FORMAT, req.getMethod(), req.getRequestURI(), getRequestBody(req), e.getMessage(), e);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ErrorResponse.of(e.getErrorCode()));
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> noResourceException(HttpRequestMethodNotSupportedException e) {
        log.warn(e.getMessage(), e);
        return ResponseEntity.badRequest()
                .body(ErrorResponse.of(MockbitErrorCode.REQUEST_METHOD_NOT_SUPPORTED));
    }

    @ExceptionHandler(NoResultException.class)
    public ResponseEntity<ErrorResponse> noResourceException(NoResultException e) {
        log.warn(e.getMessage(), e);
        return ResponseEntity.badRequest()
                .body(ErrorResponse.of(MockbitErrorCode.NO_RESOURCE_REQUEST));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> httpMessageNotReadableException(HttpMessageNotReadableException e) {
        log.warn(e.getMessage(), e);
        return ResponseEntity.badRequest()
                .body(ErrorResponse.of(MockbitErrorCode.REQUEST_NOT_READABLE));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.warn(e.getMessage(), e);
        String errorMessage = e.getFieldErrors().stream()
                .map(error -> error.getField() + " " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));

        return ResponseEntity.badRequest()
                .body(ErrorResponse.of(MockbitErrorCode.REQUEST_EMPTY, errorMessage));
    }

    @ExceptionHandler(MockBitException.class)
    public ResponseEntity<ErrorResponse> mockbitException(HttpServletRequest request, MockBitException e) {
        log.warn(LOG_FORMAT, request.getMethod(), request.getRequestURI(), getRequestBody(request), e.getMessage(), e);
        return ResponseEntity.badRequest()
                .body(ErrorResponse.of(e.getErrorCode(), e.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(HttpServletRequest request, Exception e) {
        log.error(LOG_FORMAT, request.getMethod(), request.getRequestURI(), getRequestBody(request), e.getMessage(), e);
        return ResponseEntity.internalServerError()
                .body(ErrorResponse.of(MockbitErrorCode.INTERNAL_SERVER_ERROR));
    }

    private String getRequestBody(HttpServletRequest request) {
        try (BufferedReader reader = request.getReader()) {
            return reader.lines().collect(Collectors.joining(System.lineSeparator() + "\t"));
        } catch (IOException e) {
            log.error("Failed to read request body", e);
            return "";
        }
    }
}
