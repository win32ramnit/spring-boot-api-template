package com.demo.dto;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class ApiResponse<T> {
  private Instant timestamp;
  private String path;
  private String requestId;
  private int status;
  private T data;
  private ApiError error;
}
