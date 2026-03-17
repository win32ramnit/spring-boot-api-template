package com.demo.security;

import com.demo.config.AppProperties;
import com.demo.util.ApiResponseWriter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class ApiKeyAuthenticationFilter extends OncePerRequestFilter {

  private static final Logger log = LoggerFactory.getLogger(ApiKeyAuthenticationFilter.class);

  private final AppProperties properties;
  private final ApiResponseWriter responseWriter;

  @Override
  protected boolean shouldNotFilter(HttpServletRequest request) {
    if (!properties.getSecurity().isEnabled()
        || !properties.getSecurity().getApiKey().isEnabled()) {
      return true;
    }

    if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
      return true;
    }

    String path = request.getRequestURI();
    String basePath = properties.getApi().getBasePath();
    return path.startsWith("/actuator")
        || path.startsWith("/swagger-ui")
        || path.equals("/swagger-ui.html")
        || path.startsWith("/v3/api-docs")
        || path.equals("/favicon.ico")
        || path.equals(basePath + "/health")
        || path.equals(basePath + "/info");
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
      FilterChain filterChain) throws ServletException, IOException {
    String headerName = properties.getSecurity().getApiKey().getHeader();
    String providedKey = request.getHeader(headerName);
    String expectedKey = properties.getSecurity().getApiKey().getValue();

    if (!StringUtils.hasText(providedKey) || !providedKey.equals(expectedKey)) {
      log.warn("API key authentication failed for path={}", request.getRequestURI());
      responseWriter.write(request, response, HttpStatus.UNAUTHORIZED, "UNAUTHORIZED",
          "Invalid or missing API key", null, null);
      return;
    }

    SecurityContextHolder.getContext().setAuthentication(new ApiKeyAuthenticationToken(providedKey));
    filterChain.doFilter(request, response);
  }
}
