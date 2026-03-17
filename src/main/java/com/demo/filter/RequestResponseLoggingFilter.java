package com.demo.filter;

import com.demo.config.AppProperties;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Component
@Order(org.springframework.core.Ordered.LOWEST_PRECEDENCE - 10)
@RequiredArgsConstructor
public class RequestResponseLoggingFilter extends OncePerRequestFilter {

  private static final Logger log = LoggerFactory.getLogger(RequestResponseLoggingFilter.class);

  private final AppProperties properties;

  @Override
  protected boolean shouldNotFilter(HttpServletRequest request) {
    return !properties.getLogging().getHttp().isEnabled();
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
      FilterChain filterChain) throws ServletException, IOException {
    int maxPayloadLength = Math.max(0, properties.getLogging().getHttp().getMaxPayloadLength());
    ContentCachingRequestWrapper wrappedRequest =
        new ContentCachingRequestWrapper(request, maxPayloadLength);
    ContentCachingResponseWrapper wrappedResponse = new ContentCachingResponseWrapper(response);

    long start = System.currentTimeMillis();
    try {
      filterChain.doFilter(wrappedRequest, wrappedResponse);
    } finally {
      long durationMs = System.currentTimeMillis() - start;
      logMessage(wrappedRequest, wrappedResponse, durationMs);
      wrappedResponse.copyBodyToResponse();
    }
  }

  private void logMessage(ContentCachingRequestWrapper request,
      ContentCachingResponseWrapper response, long durationMs) {
    String method = request.getMethod();
    String uri = request.getRequestURI();
    if (request.getQueryString() != null) {
      uri = uri + "?" + request.getQueryString();
    }
    int status = response.getStatus();

    String requestBody = readBody(request.getContentAsByteArray(), request.getContentType());
    String responseBody = readBody(response.getContentAsByteArray(), response.getContentType());

    log.debug("http_request method={} path={} status={} durationMs={} requestBody={} responseBody={}",
        method, uri, status, durationMs, requestBody, responseBody);
  }

  private String readBody(byte[] body, String contentType) {
    if (body == null || body.length == 0) {
      return null;
    }
    if (!isLoggable(contentType)) {
      return "[payload omitted]";
    }
    String payload = new String(body, StandardCharsets.UTF_8);
    int maxLength = properties.getLogging().getHttp().getMaxPayloadLength();
    if (maxLength > 0 && payload.length() > maxLength) {
      return payload.substring(0, maxLength) + "...(truncated)";
    }
    return payload;
  }

  private boolean isLoggable(String contentType) {
    if (!StringUtils.hasText(contentType)) {
      return true;
    }
    String lower = contentType.toLowerCase();
    return lower.contains("application/json")
        || lower.contains("application/xml")
        || lower.startsWith("text/");
  }
}
