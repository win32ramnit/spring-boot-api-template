package com.demo.exception;

import com.demo.dto.ApiResponse;
import com.demo.util.ApiResponseUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

  private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

  @ExceptionHandler(ApiException.class)
  public ResponseEntity<ApiResponse<Void>> handleApiException(ApiException ex,
      HttpServletRequest request) {
    ApiResponse<Void> response = ApiResponseUtil.error(request, ex.getStatus(), ex.getCode(),
        ex.getMessage(), ex.getFieldErrors(), ex.getDetails());
    return ResponseEntity.status(ex.getStatus()).body(response);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ApiResponse<Void>> handleValidation(MethodArgumentNotValidException ex,
      HttpServletRequest request) {
    Map<String, String> fieldErrors = new HashMap<>();
    for (FieldError error : ex.getBindingResult().getFieldErrors()) {
      fieldErrors.put(error.getField(), error.getDefaultMessage());
    }
    ApiResponse<Void> response = ApiResponseUtil.error(request, HttpStatus.BAD_REQUEST,
        "VALIDATION_FAILED", "Request validation failed", fieldErrors, null);
    return ResponseEntity.badRequest().body(response);
  }

  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<ApiResponse<Void>> handleConstraintViolation(ConstraintViolationException ex,
      HttpServletRequest request) {
    Map<String, String> fieldErrors = ex.getConstraintViolations().stream()
        .collect(Collectors.toMap(
            violation -> violation.getPropertyPath().toString(),
            ConstraintViolation::getMessage,
            (existing, replacement) -> existing));
    ApiResponse<Void> response = ApiResponseUtil.error(request, HttpStatus.BAD_REQUEST,
        "VALIDATION_FAILED", "Request validation failed", fieldErrors, null);
    return ResponseEntity.badRequest().body(response);
  }

  @ExceptionHandler(MissingServletRequestParameterException.class)
  public ResponseEntity<ApiResponse<Void>> handleMissingParameter(
      MissingServletRequestParameterException ex, HttpServletRequest request) {
    ApiResponse<Void> response = ApiResponseUtil.error(request, HttpStatus.BAD_REQUEST,
        "MISSING_PARAMETER", ex.getMessage(), null, null);
    return ResponseEntity.badRequest().body(response);
  }

  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<ApiResponse<Void>> handleInvalidPayload(HttpMessageNotReadableException ex,
      HttpServletRequest request) {
    ApiResponse<Void> response = ApiResponseUtil.error(request, HttpStatus.BAD_REQUEST,
        "INVALID_PAYLOAD", "Request body is malformed or unreadable", null, null);
    return ResponseEntity.badRequest().body(response);
  }

  @ExceptionHandler(DataIntegrityViolationException.class)
  public ResponseEntity<ApiResponse<Void>> handleDataIntegrity(DataIntegrityViolationException ex,
      HttpServletRequest request) {
    ApiResponse<Void> response = ApiResponseUtil.error(request, HttpStatus.CONFLICT,
        "DATA_INTEGRITY_VIOLATION", "Database constraint violated", null, null);
    return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ApiResponse<Void>> handleException(Exception ex,
      HttpServletRequest request) {
    log.error("Unhandled exception", ex);
    ApiResponse<Void> response = ApiResponseUtil.error(request, HttpStatus.INTERNAL_SERVER_ERROR,
        "INTERNAL_ERROR", "Unexpected error occurred", null,
        List.of(ex.getClass().getSimpleName()));
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
  }
}
