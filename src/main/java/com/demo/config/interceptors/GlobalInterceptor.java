package com.demo.config.interceptors;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import java.time.LocalDateTime;

@Component
public class GlobalInterceptor implements HandlerInterceptor {

  Logger log = LoggerFactory.getLogger(GlobalInterceptor.class);

  public GlobalInterceptor() {
  }

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
      throws Exception {
    logRequestDetails(request);
    return true;
  }

  @Override
  public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
      ModelAndView modelAndView) throws Exception {
  }

  @Override
  public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
      Object handler, Exception ex) throws Exception {
    logCompletionDetails(request);
  }

  private void logRequestDetails(HttpServletRequest request) {
    log.info("Global interceptor => preHandle called. For URL: {} at {}", request.getRequestURI(),
        LocalDateTime.now());
    log.info("Request Method: {}", request.getMethod());
    log.info("Remote IP address: X-FORWARDED-FOR: {}, remote addr: {}",
        request.getHeader("X-FORWARDED-FOR"), request.getRemoteAddr());
  }

  private void logCompletionDetails(HttpServletRequest request) {
    log.info("Global interceptor => afterCompletion called for URL: {}", request.getServletPath());
    log.info("Completion time: {}", LocalDateTime.now());
  }
}
