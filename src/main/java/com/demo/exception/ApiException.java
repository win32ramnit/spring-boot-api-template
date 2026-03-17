package com.demo.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Map;

@Getter
public class ApiException extends RuntimeException {

  private final HttpStatus status;
  private final String code;
  private final Map<String, String> fieldErrors;
  private final List<String> details;

  public ApiException(HttpStatus status, String code, String message) {
    super(message);
    this.status = status;
    this.code = code;
    this.fieldErrors = null;
    this.details = null;
  }

  public ApiException(HttpStatus status, String code, String message,
      Map<String, String> fieldErrors, List<String> details) {
    super(message);
    this.status = status;
    this.code = code;
    this.fieldErrors = fieldErrors;
    this.details = details;
  }
}
