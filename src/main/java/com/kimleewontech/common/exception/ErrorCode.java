package com.kimleewontech.common.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
	// 공통
	REQUEST_FORMAT_INVALID(HttpStatus.BAD_REQUEST, "COMMON_001", "잘못된 요청 형식입니다."),
	INVALID_INPUT(HttpStatus.BAD_REQUEST, "COMMON_002", "입력값이 올바르지 않습니다."),
	INTERNAL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON_003", "서버 내부 오류가 발생했습니다."),
	REQUEST_URL_INVALID(HttpStatus.BAD_REQUEST, "COMMON_004", "URL 경로가 잘못되었습니다."),
	METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "COMMON_005", "지원하지 않는 HTTP 메서드입니다."),
	DATA_INTEGRITY_VIOLATION(HttpStatus.CONFLICT, "COMMON_006", "데이터 무결성 위반이 발생했습니다."),

	// 인증

	// 사용자
	DUPLICATE_EMAIL(HttpStatus.CONFLICT, "USER_001", "이미 가입한 이메일입니다.")

	// 상품

	// 카트

	//주문

	//결제

	;

	private final HttpStatus status;
	private final String code;
	private final String message;
}
