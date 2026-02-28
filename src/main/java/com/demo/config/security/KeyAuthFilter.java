package com.demo.config.security;

import com.demo.config.ApplicationProperties;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

@Component("keyAuthFilter")
public class KeyAuthFilter {

  Logger log = LoggerFactory.getLogger(KeyAuthFilter.class);

  private final ApplicationProperties applicationProperties;

  public KeyAuthFilter(ApplicationProperties applicationProperties) {
    this.applicationProperties = applicationProperties;
  }

  public boolean validateRequest(HttpServletRequest request) throws AuthenticationException {
    String apiKeyHeader = applicationProperties.getSecurityHeaderName();
    String apiKey = request.getHeader(apiKeyHeader);

    if (apiKey == null) {
      log.error("Missing API key header: {}", apiKeyHeader);
      throw new AuthenticationServiceException("Missing API key");
    }

    if (!applicationProperties.getApiKey().equals(apiKey)) {
      log.error("Invalid API key found: {}", apiKey);
      throw new AuthenticationServiceException("Invalid API key");
    }

    log.info("API key validated successfully");
    return true; // Request is valid
  }
}
