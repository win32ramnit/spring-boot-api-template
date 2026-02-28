package com.demo.controller;

import com.demo.response.ServiceResponse;
import com.demo.constant.AppResponseStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Sample REST controller for health/status endpoints.
 * Replace or extend with your own API controllers.
 */
@RestController
@RequestMapping("/api/v1")
@Tag(name = "Health", description = "Health and status API")
public class HealthController {

  @GetMapping("/health")
  @Operation(summary = "Health check", description = "Returns application health status")
  public ResponseEntity<ServiceResponse> health() {
    return ResponseEntity.ok(
        new ServiceResponse(AppResponseStatus.OK, "Application is running"));
  }

  @GetMapping("/info")
  @Operation(summary = "Application info", description = "Returns basic application information")
  public ResponseEntity<ServiceResponse> info() {
    return ResponseEntity.ok(
        new ServiceResponse(AppResponseStatus.OK, "Spring Boot 4.0 Template - Ready for development"));
  }
}
