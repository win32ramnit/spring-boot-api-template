package com.demo.exception;

import com.demo.constant.AppResponseStatus;
import com.demo.response.ServiceErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

  private static final Logger LOG = LoggerFactory.getLogger(GlobalExceptionHandler.class);

  @ExceptionHandler(AppException.class)
  @ResponseBody
  public ServiceErrorResponse handleAppException(HttpServletRequest request,
      HttpServletResponse response, AppException ex) {
    LOG.error("App Exception Occurred:: URL= {}", request.getRequestURI());
    LOG.error(" Exception Message Code:: {}", ex.getMessageCode());
    LOG.error(" Exception Message Desc:: {}", ex.getMessageDesc());

    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);

    // @formatter:off
    return ServiceErrorResponse.builder()
        .status(AppResponseStatus.EXCEPTION)
        .errorCode(ex.getMessageCode())
        .description(ex.getMessageDesc())
        .moreInfo(ex.getMoreInfo())
        .build();
    // @formatter:on
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  @ResponseBody
  public ServiceErrorResponse handleValidationException(HttpServletRequest request,
      HttpServletResponse response, MethodArgumentNotValidException ex) {
    Map<String, String> errors = new HashMap<>();
    for (FieldError error : ex.getBindingResult().getFieldErrors()) {
      errors.put(error.getField(), error.getDefaultMessage());
    }

    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);

    // @formatter:off
    return ServiceErrorResponse.builder()
        .status(AppResponseStatus.BAD_REQUEST)
        .errorCode("VALIDATION-FAILED")
        .description("Request validation failed")
        .validationFailed(errors)
        .build();
    // @formatter:on
  }

  @ExceptionHandler(Exception.class)
  @ResponseBody
  public ServiceErrorResponse handleException(HttpServletRequest request,
      HttpServletResponse response, Exception ex) {
    LOG.info("#################### UNCHECKED EXCEPTION START ####################");
    LOG.error("Unhandled Exception Occurred:: URL {}", request.getRequestURI(), ex);

    LOG.info("#################### UNCHECKED EXCEPTION START ####################");

    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

    // @formatter:off
    return ServiceErrorResponse.builder()
        .status(AppResponseStatus.INTERNAL_SERVER_ERROR)
        .errorCode(ExceptionUtils.getRootCauseMessage(ex))
        .description(ExceptionUtils.getMessage(ex))
//        .moreInfo(ExceptionUtils.getStackTrace(ex))
        .moreInfo(ex.getStackTrace() != null ?
            String.join("\n", Arrays.stream(ex.getStackTrace()).map(StackTraceElement::toString).toArray(String[]::new))
            : "N/A")
        .build();
    // @formatter:on
  }

}
