package com.demo.config.security;

import com.demo.config.ApplicationProperties;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.regex.Pattern;

@Component
public class SimpleCORSFilter extends OncePerRequestFilter {

  Logger log = LoggerFactory.getLogger(SimpleCORSFilter.class);

  @Autowired
  private ApplicationProperties applicationProperties;

  public SimpleCORSFilter() {
    log.info("SimpleCORSFilter init");
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
      FilterChain filterChain) throws ServletException, IOException {

    boolean isErrorFound = false;
    boolean maliciousFound = checkHeadersInfo(request);
    if (maliciousFound) {
      log.error("Header Having unexpected chars");
      response.setStatus(HttpServletResponse.SC_NOT_ACCEPTABLE);
      response.getWriter().write("NotAcceptable");
      isErrorFound = true;
    }
    response.setHeader("Access-Control-Allow-Credentials", "true");
    response.setHeader("Access-Control-Allow-Origin", applicationProperties.getAllowedOrigins());
    response.setHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
    response.setHeader("Access-Control-Max-Age", "3600");
    response.setHeader("Access-Control-Allow-Headers",
        "Content-Type, Accept," + "X-Request-With, remember-me, XUuid, Authorization, requestType, observ, a, b, c, v, j, k ");
    response.setHeader("Access-Control-Expose-Headers",
        "Content-Disposition, a, b, c, v, j, k, X-Captcha");
    response.setHeader("Pragma", "no-cache");
    response.setDateHeader("Expires", 0);

    if (!StringUtils.hasText(response.getHeader("X-Forwarded-Proto")))
      response.setHeader("X-Forwarded-Proto", "https");
    if (!StringUtils.hasText(response.getHeader("X-Forwarded-For")))
      response.setHeader("X-Forwarded-For", "127.0.0.1");
    if (!StringUtils.hasText(response.getHeader("X-Forwarded-Port")))
      response.setHeader("X-Forwarded-Port", "8443");
    if (!StringUtils.hasText(response.getHeader("X-FRAME-OPTIONS")))
      response.setHeader("X-FRAME-OPTIONS", "SAMEORIGIN");
    if (!StringUtils.hasText(response.getHeader("X-XSS-Protection")))
      response.setHeader("X-XSS-Protection", "1; mode=block");
    if (!StringUtils.hasText(response.getHeader("x-content-type-options")))
      response.setHeader("x-content-type-options", "nosniff");
    if (!StringUtils.hasText(response.getHeader("strict-transport-security")))
      response.setHeader("strict-transport-security",
          "max-age-15552000; includeSubDomains; preload");
    if (!StringUtils.hasText(response.getHeader("Content-Security-Policy")) && StringUtils.hasText(
        applicationProperties.getContentSecurityPolicy()))
      response.setHeader("Content-Security-Policy",
          applicationProperties.getContentSecurityPolicy());
    if (!StringUtils.hasText(
        response.getHeader("X-Content-Security-Policy")) && StringUtils.hasText(
        applicationProperties.getContentSecurityPolicy()))
      response.setHeader("X-Content-Security-Policy",
          applicationProperties.getContentSecurityPolicy());
    if (!StringUtils.hasText(response.getHeader("Referrer-Policy")))
      response.setHeader("Referrer-Policy", "origin");
    if (!StringUtils.hasText(response.getHeader("Permissions-Policy")))
      response.setHeader("Permissions-Policy",
          "fullscreen=(self), sync-xhr=(self), clipboard-read=(self), clipboard-read=(self)");

    response.addHeader("Cache-Control",
        "no-cache, no-store, must-revalidate, post-check=0, pre-check=0, max-age=0, s-maxage=0;");

    if (request.getHeader("Origin") != null
        && StringUtils.hasText(response.getHeader("Access-Control-Allow-Origin"))
        && !response.getHeader("Access-Control-Allow-Origin").equals("*")
        && !isOriginAllowed(request.getHeader("Origin"),
            response.getHeader("Access-Control-Allow-Origin"))) {
      log.error("Origin error {} {}", request.getHeader("Origin"),
          response.getHeader("Access-Control-Allow-Origin"));
      response.setStatus(HttpServletResponse.SC_NOT_ACCEPTABLE);
      response.getWriter().write("NotAcceptable");
      isErrorFound = true;
    }
    if (StringUtils.hasText(request.getHeader("X-Forwarded-For"))
        && StringUtils.hasText(response.getHeader("X-Forwarded-For"))
        && !request.getHeader("X-Forwarded-For").matches("[0-9.]+")) {
      log.error("X-Forwarded-For error {} {}", request.getHeader("X-Forwarded-For"),
          response.getHeader("X-Forwarded-For"));
      response.setStatus(HttpServletResponse.SC_NOT_ACCEPTABLE);
      response.getWriter().write("NotAcceptable");
      isErrorFound = true;
    }
    if (response.getHeader("Access-Control-Allow-Methods") != null
        && !Arrays.asList(response.getHeader("Access-Control-Allow-Methods").replaceAll("\\s", "").split(",", -1))
        .contains(request.getMethod())) {
      log.error("Access-Control-Allow-Methods error {} {}", request.getMethod(),
          response.getHeader("Access-Control-Allow-Methods"));
      response.setStatus(HttpServletResponse.SC_NOT_ACCEPTABLE);
      response.getWriter().write("NotAcceptable");
      isErrorFound = true;
    }
    if (isErrorFound) {
      return;
    }

    filterChain.doFilter(request, response);
  }

  private boolean isOriginAllowed(String origin, String allowedOrigins) {
    String[] allowed = allowedOrigins.replaceAll("\\s", "").split(",", -1);
    for (String allowedOrigin : allowed) {
      if (allowedOrigin.equals(origin)) {
        return true;
      }
      if (allowedOrigin.endsWith(":*") && origin.startsWith(allowedOrigin.substring(0, allowedOrigin.length() - 1))) {
        return true;
      }
    }
    return false;
  }

  private boolean checkHeadersInfo(HttpServletRequest request) {
    String MALICIOUS_REGEX = "(<|>)";
    Enumeration<String> headerNames = request.getHeaderNames();
    while (headerNames.hasMoreElements()) {
      String key = headerNames.nextElement();
      String value = request.getHeader(key);
      if (Pattern.compile(MALICIOUS_REGEX).matcher(value).find()) {
        return true;
      }
    }
    return false;
  }
}
