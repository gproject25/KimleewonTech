package com.kimleewontech.common.exception;

import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import com.kimleewontech.common.response.ApiResponse;
import com.kimleewontech.common.response.ValidationErrorResponse;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

	// HTTP 요청 형식 예외 처리
	@ExceptionHandler({
		HttpMessageNotReadableException.class,
		MethodArgumentTypeMismatchException.class,
		MissingServletRequestParameterException.class
	})
	public ResponseEntity<ApiResponse<Void>> handleHttpFormatException(HttpServletRequest request, Exception e) {
		printErrorLog(request, e.getMessage());
		return buildResponseEntity(ErrorCode.REQUEST_FORMAT_INVALID);
	}

	// Bean Validation 예외 처리
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ApiResponse<Void>> handleValidationException(HttpServletRequest request,
		MethodArgumentNotValidException e) {
		printErrorLog(request, e.getMessage());
		List<ValidationErrorResponse> validationErrors = e.getBindingResult().getFieldErrors().stream().map(error ->
			new ValidationErrorResponse(
				error.getField(),
				error.getDefaultMessage()
			)).toList();
		return buildValidationResponseEntity(ErrorCode.INVALID_INPUT, validationErrors);
	}

	// Controller 메서드 파라미터 검증 예외 처리
	@ExceptionHandler(HandlerMethodValidationException.class)
	public ResponseEntity<ApiResponse<Void>> handleHandlerMethodValidationException(
		HttpServletRequest request,
		HandlerMethodValidationException e
	) {
		printErrorLog(request, e.getMessage());

		List<ValidationErrorResponse> validationErrors = e.getParameterValidationResults().stream()
			.flatMap(result -> result.getResolvableErrors().stream()
				.map(error -> new ValidationErrorResponse(
					result.getMethodParameter().getParameterName(),
					error.getDefaultMessage()
				))
			)
			.toList();

		return buildValidationResponseEntity(ErrorCode.INVALID_INPUT, validationErrors);
	}

	// 잘못된 URL 요청을 처리
	@ExceptionHandler(NoResourceFoundException.class)
	public ResponseEntity<ApiResponse<Void>> handleNoResourceFoundException(HttpServletRequest request) {
		log.error("[API - ERROR] {} {}", request.getMethod(), request.getRequestURI());
		return buildResponseEntity(ErrorCode.REQUEST_FORMAT_INVALID);
	}

	// 요청 파라미터 제약 조건 위반 처리
	@ExceptionHandler(ConstraintViolationException.class)
	public ResponseEntity<ApiResponse<Void>> handleConstraintViolationException(HttpServletRequest request,
		ConstraintViolationException e) {
		printErrorLog(request, e.getMessage());

		List<ValidationErrorResponse> validationErrors = e.getConstraintViolations()
			.stream()
			.map(violation -> new ValidationErrorResponse(
				violation.getPropertyPath().toString(),
				violation.getMessage()
			))
			.toList();

		return buildValidationResponseEntity(ErrorCode.INVALID_INPUT, validationErrors);
	}

	// 커스텀 비즈니스 예외 처리
	@ExceptionHandler(BusinessException.class)
	public ResponseEntity<ApiResponse<Void>> handleBusinessException(
		HttpServletRequest request,
		BusinessException e
	) {
		printErrorLog(request, e.getMessage());
		return buildResponseEntity(e.getErrorCode());
	}

	// DB 무결성 위반 예외 처리
	@ExceptionHandler(DataIntegrityViolationException.class)
	public ResponseEntity<ApiResponse<Void>> handleDataIntegrityViolationException(
		HttpServletRequest request,
		DataIntegrityViolationException e
	) {
		printErrorLog(request, e.getMessage());
		return buildResponseEntity(ErrorCode.DATA_INTEGRITY_VIOLATION);
	}

	// 잘못된 HTTP 메서드 요청 처리
	@ExceptionHandler(HttpRequestMethodNotSupportedException.class)
	public ResponseEntity<ApiResponse<Void>> handleHttpRequestMethodNotSupportedException(
		HttpServletRequest request,
		HttpRequestMethodNotSupportedException e
	) {
		printErrorLog(request, e.getMessage());
		return buildResponseEntity(ErrorCode.METHOD_NOT_ALLOWED);
	}

	// Default 예외 처리
	@ExceptionHandler(Exception.class)
	public ResponseEntity<ApiResponse<Void>> handleException(
		HttpServletRequest request,
		Exception e
	) {
		printErrorLog(request, e.getMessage());
		return buildResponseEntity(ErrorCode.INTERNAL_ERROR);
	}

	// ============================== Response ===============================

	private ResponseEntity<ApiResponse<Void>> buildResponseEntity(ErrorCode errorCode) {
		return ResponseEntity.status(errorCode.getStatus()).body(ApiResponse.error(errorCode));
	}

	private ResponseEntity<ApiResponse<Void>> buildValidationResponseEntity(ErrorCode errorCode,
		List<ValidationErrorResponse> validationErrors) {
		return ResponseEntity.status(errorCode.getStatus()).body(ApiResponse.error(errorCode, validationErrors));
	}

	private void printErrorLog(HttpServletRequest request, String message) {
		log.error("[API - ERROR] {} {} - failed: {}",
			request.getMethod(),
			request.getRequestURI(),
			message
		);
	}
}
