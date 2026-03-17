package com.demo.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@Builder
public class ApiError {
  private String code;
  private String message;
  private Map<String, String> fieldErrors;
  private List<String> details;
}
