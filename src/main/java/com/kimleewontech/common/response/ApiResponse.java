package com.kimleewontech.common.response;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.kimleewontech.common.exception.ErrorCode;

import lombok.Getter;

@JsonPropertyOrder({
	"code",
	"message",
	"status",
	"timestamp",
	"data",
	"validationErrors"
})
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

	private final String code;
	private final String message;
	private final int status;
	private final LocalDateTime timestamp;
	private final T data;

	//Bean Validation 검증용
	private final List<ValidationErrorResponse> validationErrors;

	private ApiResponse(String code, String message, int status, T data,
		List<ValidationErrorResponse> validationErrors) {
		this.code = code;
		this.message = message;
		this.status = status;
		this.timestamp = LocalDateTime.now();
		this.data = data;
		this.validationErrors = validationErrors;
	}

	public static <T> ApiResponse<T> ok(T data, HttpStatus status) {
		return new ApiResponse<>("SUCCESS", null, status.value(), data, null);
	}

	public static ApiResponse<Void> ok(HttpStatus status) {
		return new ApiResponse<>("SUCCESS", null, status.value(), null, null);
	}

	public static ApiResponse<Void> error(ErrorCode errorCode) {
		return new ApiResponse<>(
			errorCode.getCode(),
			errorCode.getMessage(),
			errorCode.getStatus().value(),
			null,
			null
		);
	}

	public static ApiResponse<Void> error(ErrorCode errorCode, List<ValidationErrorResponse> validationErrors) {
		return new ApiResponse<>(
			errorCode.getCode(),
			errorCode.getMessage(),
			errorCode.getStatus().value(),
			null,
			validationErrors
		);
	}
}
