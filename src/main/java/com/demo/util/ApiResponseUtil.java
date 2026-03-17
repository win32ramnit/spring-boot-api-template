package com.demo.util;

import com.demo.dto.ApiError;
import com.demo.dto.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;

import java.time.Instant;
import java.util.List;
import java.util.Map;

public final class ApiResponseUtil {

  private ApiResponseUtil() {
  }

  public static <T> ApiResponse<T> success(T data, HttpServletRequest request, HttpStatus status) {
    return ApiResponse.<T>builder()
        .timestamp(Instant.now())
        .path(request.getRequestURI())
        .requestId(MDC.get("requestId"))
        .status(status.value())
        .data(data)
        .build();
  }

  public static ApiResponse<Void> error(HttpServletRequest request, HttpStatus status, String code,
      String message, Map<String, String> fieldErrors, List<String> details) {
    ApiError error = ApiError.builder()
        .code(code)
        .message(message)
        .fieldErrors(fieldErrors == null || fieldErrors.isEmpty() ? null : fieldErrors)
        .details(details == null || details.isEmpty() ? null : details)
        .build();

    return ApiResponse.<Void>builder()
        .timestamp(Instant.now())
        .path(request.getRequestURI())
        .requestId(MDC.get("requestId"))
        .status(status.value())
        .error(error)
        .build();
  }
}
