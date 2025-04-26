package com.example.mockbit.common.exception;

import lombok.Getter;

@Getter
public enum MockbitErrorCode {
    /* Login */
    ID_PASSWORD_INVALID("아이디 또는 비밀번호가 일치하지 않습니다."),
    ONLY_FOR_MEMBER("로그인이 필요한 서비스입니다."),
    TOKEN_NOT_FOUND("토큰이 존재하지 않습니다."),
    TOKEN_NOT_VALID("토큰이 유효하지 않습니다."),

    /* Signup */
    USER_ID_LENGTH_INVALID("사용자 아이디는 %d자 이상 %d자 이하만 가능합니다."),
    USER_ID_ALREADY_EXIST("이미 존재하는 아이디입니다."),
    USER_PASSWORD_LENGTH_INVALID("사용자 비밀번호는 %d자 이상 %d자 이하만 가능합니다."),
    USER_PASSWORD_FORMAT_INVALID("비밀번호는 적어도 하나의 문자, 숫자, 특수문자가 포함되어야 합니다."),
    USER_NICKNAME_LENGTH_INVALID("사용자 닉네임은 %d자 이상 %d자 이하만 가능합니다."),
    USER_NICKNAME_ALREADY_EXIST("이미 존재하는 닉네임입니다."),
    USER_EMAIL_ADDRESS_FORMAT_INVALID("지원하지 않는 이메일 형식입니다."),
    USER_EMAIL_ADDRESS_EMPTY("이메일을 입력해주세요."),
    USER_EMAIL_CODE_NOT_EQUAL("인증 번호가 일치하지 않습니다."),

    /* Request Validation */
    REQUEST_LIMIT("주문은 5000원 이상부터 가능합니다."),
    REQUEST_METHOD_NOT_SUPPORTED("지원하지 않는 요청입니다."),
    REQUEST_NOT_READABLE("읽을 수 없는 요청입니다."),
    REQUEST_OVER("주문 금액이 주문 가능 금액보다 큽니다."),
    REQUEST_EMPTY("요청이 존재하지 않습니다."),

    /* System */
    NO_RESOURCE_REQUEST("존재하지 않는 자원입니다."),
    INTERNAL_SERVER_ERROR("서버에서 예기치 못한 오류가 발생했습니다. 잠시 후에 다시 시도해주세요"),
    REDIS_SERIALIZE_ERROR("레디스 직렬화 오류가 발생했습니다."),
    REDIS_DESERIALIZE_ERROR("레디스 역직렬화 오류가 발생했습니다."),

    /* Order */
    NO_ORDER_RESOURCE("존재하지 않는 주문입니다."),
    INTERNAL_REDIS_ORDER_ERROR("주문 조회를 실패했습니다."),
    ORDER_ERROR("주문 처리 중 오류가 발생했습니다."),
    LIMIT_ORDER_ERROR("지정가 주문 중 오류가 발생했습니다."),
    MARKET_ORDER_ERROR("시장가 주문 중 오류가 발생했습니다."),
    ORDER_NOT_FOUND("지정된 주문을 찾을 수 없습니다."),
    ORDER_DELETE_FAILED("주문을 삭제하는 데 실패했습니다."),
    NOT_ENOUGH_BALANCE("주문 금액이 부족합니다."),
    NOT_ENOUGH_BTC("판매 BTC 수량이 부족합니다."),
    USER_NOT_FOUND("존재하지 않는 사용자입니다."),
    ACCOUNT_NOT_FOUND("존재하지 않는 원화 계좌입니다."),
    BTC_NOT_FOUND("존재하지 않는 BTC 계좌입니다."),
    NOT_EXISTS_CURRENT_PRICE("현재 가격을 가져오는데 실패했습니다."),
    INVALID_ORDER_TYPE("올바르지 않은 주문 종류입니다."),
    USER_ID_NOT_EQUALS_ORDER("해당 사용자 외에는 주문을 설정할 수 없습니다."),
    ALREADY_IN_POSITION("교차 포지션 진입은 불가능합니다.")
    ;

    private final String message;

    MockbitErrorCode(String message) {
        this.message = message;
    }
}
