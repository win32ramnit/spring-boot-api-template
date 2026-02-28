package com.demo.response;

import com.demo.constant.AppResponseStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.util.Map;

/**
 * Represents an error response returned by the service layer.
 *
 * @param status            the response status (must not be null)
 * @param description       human-readable description of the error
 * @param moreInfo          additional details about the error
 * @param errorCode         application-specific error code
 * @param validationFailed  map of field names to validation error messages
 */
@Builder
public record ServiceErrorResponse(
    @NotNull AppResponseStatus status,
    String description,
    String moreInfo,
    String errorCode,
    Map<String, String> validationFailed
) {
  public ServiceErrorResponse {
    validationFailed = validationFailed == null
        ? Map.of()
        : Map.copyOf(validationFailed);
  }
}
