package com.demo.config.interceptors;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.RequestBodyAdvice;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

@ControllerAdvice
public class RequestBodyInterceptor implements RequestBodyAdvice {

  Logger log = LoggerFactory.getLogger(RequestBodyInterceptor.class);

  private final HtmlSanitizationService htmlSanitizationService;
  private final ObjectMapper objectMapper;

  @Autowired
  public RequestBodyInterceptor(HtmlSanitizationService htmlSanitizationService) {
    this.htmlSanitizationService = htmlSanitizationService;
    this.objectMapper =
        new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
  }

  @Override
  public boolean supports(MethodParameter methodParameter, Type targetType,
      Class<? extends HttpMessageConverter<?>> converterType) {
    return true;
  }

  @Override
  public HttpInputMessage beforeBodyRead(HttpInputMessage inputMessage, MethodParameter parameter,
      Type targetType, Class<? extends HttpMessageConverter<?>> converterType) throws IOException {
    return performMessageSanitization(inputMessage);
  }

  private HttpInputMessage performMessageSanitization(HttpInputMessage inputMessage) {
    try {
      String bodyContent =
          new String(inputMessage.getBody().readAllBytes(), StandardCharsets.UTF_8);
      Map<String, Object> jsonMap = objectMapper.readValue(bodyContent, Map.class);
      sanitizeMap(jsonMap);

      byte[] sanitizedBytes = objectMapper.writeValueAsBytes(jsonMap);
      return new HttpInputMessage() {
        @Override
        public InputStream getBody() {
          return new ByteArrayInputStream(sanitizedBytes);
        }

        @Override
        public HttpHeaders getHeaders() {
          return inputMessage.getHeaders();
        }
      };
    } catch (IOException e) {
      log.error("Error during message sanitization: {}", e.getMessage(), e);
      throw new RuntimeException("Failed to sanitize request body", e);
    }
  }

  @SuppressWarnings("unchecked")
  private void sanitizeMap(Map<String, Object> jsonMap) {
    jsonMap.forEach((key, value) -> {
      if (value instanceof String) {
        jsonMap.put(key, htmlSanitizationService.sanitizeHtml((String) value));
      } else if (value instanceof Map) {
        sanitizeMap((Map<String, Object>) value);
      } else if (value instanceof List) {
        sanitizeList((List<Object>) value);
      }
    });
  }

  @SuppressWarnings("unchecked")
  private void sanitizeList(List<Object> list) {
    for (int i = 0; i < list.size(); i++) {
      Object value = list.get(i);
      if (value instanceof String) {
        list.set(i, htmlSanitizationService.sanitizeHtml((String) value));
      } else if (value instanceof Map) {
        sanitizeMap((Map<String, Object>) value);
      } else if (value instanceof List) {
        sanitizeList((List<Object>) value);
      }
    }
  }

  @Override
  public Object afterBodyRead(Object body, HttpInputMessage inputMessage, MethodParameter parameter,
      Type targetType, Class<? extends HttpMessageConverter<?>> converterType) {
    return body;
  }

  @Override
  public Object handleEmptyBody(Object body, HttpInputMessage inputMessage,
      MethodParameter parameter, Type targetType,
      Class<? extends HttpMessageConverter<?>> converterType) {
    return body;
  }
}
