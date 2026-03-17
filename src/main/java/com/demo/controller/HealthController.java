package com.demo.controller;

import com.demo.dto.ApiResponse;
import com.demo.util.ApiResponseUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.info.BuildProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("${app.api.base-path:/api/v1}")
@Tag(name = "Health", description = "Health and info endpoints")
@RequiredArgsConstructor
public class HealthController {

  private final Optional<BuildProperties> buildProperties;

  @GetMapping("/health")
  @Operation(summary = "Health check")
  public ResponseEntity<ApiResponse<Map<String, Object>>> health(HttpServletRequest request) {
    Map<String, Object> data = new HashMap<>();
    data.put("status", "UP");
    return ResponseEntity.ok(ApiResponseUtil.success(data, request, HttpStatus.OK));
  }

  @GetMapping("/info")
  @Operation(summary = "Application info")
  public ResponseEntity<ApiResponse<Map<String, Object>>> info(HttpServletRequest request) {
    Map<String, Object> data = new HashMap<>();
    buildProperties.ifPresent(props -> {
      data.put("name", props.getName());
      data.put("version", props.getVersion());
      data.put("time", props.getTime());
    });
    return ResponseEntity.ok(ApiResponseUtil.success(data, request, HttpStatus.OK));
  }
}
