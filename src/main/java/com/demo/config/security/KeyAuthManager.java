package com.demo.config.security;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;

@Configuration
public class KeyAuthManager {

  Logger log = LoggerFactory.getLogger(KeyAuthManager.class);

  @Bean
  public AuthorizationManager<RequestAuthorizationContext> myAuthenticationManager(
      KeyAuthFilter keyAuthFilter) {
    return (authentication, context) -> {
      HttpServletRequest request = context.getRequest();
      try {
        boolean granted = keyAuthFilter.validateRequest(request);
        log.info("Authorization granted for request to {}", request.getRequestURI());
        return new AuthorizationDecision(granted);
      } catch (AuthenticationServiceException e) {
        log.error("Authorization failed for request to {}: {}", request.getRequestURI(),
            e.getMessage());
        return new AuthorizationDecision(false); // Deny access
      }
    };
  }
}
