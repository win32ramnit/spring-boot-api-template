package com.demo.config.security;

import com.demo.constant.AppResponseStatus;
import com.demo.constant.Constants;
import com.demo.response.ServiceErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Custom Authentication Entry Point that handles unauthorized access attempts.
 */
@Component("securityAuthenticationEntryPoint")
@RequiredArgsConstructor
public class SecurityAuthenticationEntryPoint implements AuthenticationEntryPoint {
  private final ObjectMapper objectMapper;

  @Override
  public void commence(HttpServletRequest request, HttpServletResponse response,
      AuthenticationException authException) throws IOException, ServletException {
    // @formatter:off
    // Build the error response
    ServiceErrorResponse errorResponse = ServiceErrorResponse.builder()
        .status(AppResponseStatus.INVALID)
        .description("Request validation failed")
        .errorCode("INVALID-CALL")
        .build();
    // @formatter:on
    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    response.setContentType(Constants.APPLICATION_JSON);
    try {
      objectMapper.writeValue(response.getOutputStream(), errorResponse);
    } catch (IOException e) {
      // Log and handle serialization exceptions
      response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
      response.getWriter().write("Error serializing error response");
    }
  }
}
