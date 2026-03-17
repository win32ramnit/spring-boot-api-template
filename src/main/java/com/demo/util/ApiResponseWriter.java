package com.demo.util;

import com.demo.dto.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class ApiResponseWriter {

  private final List<HttpMessageConverter<?>> messageConverters;

  public void write(HttpServletRequest request, HttpServletResponse response, HttpStatus status,
      String code, String message, Map<String, String> fieldErrors, List<String> details)
      throws IOException {
    ApiResponse<Void> body = ApiResponseUtil.error(request, status, code, message, fieldErrors,
        details);
    response.setStatus(status.value());
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    response.setCharacterEncoding(StandardCharsets.UTF_8.name());

    @SuppressWarnings("unchecked")
    HttpMessageConverter<Object> converter = messageConverters.stream()
        .filter(c -> c.canWrite(body.getClass(), MediaType.APPLICATION_JSON))
        .map(c -> (HttpMessageConverter<Object>) c)
        .findFirst()
        .orElse(null);

    if (converter != null) {
      converter.write(body, MediaType.APPLICATION_JSON, new ServletServerHttpResponse(response));
    } else {
      // Fallback to a minimal JSON payload if no converter is available.
      String fallback = "{\"status\":" + status.value() + ",\"error\":{\"code\":\"" + code
          + "\",\"message\":\"" + message + "\"}}";
      response.getOutputStream().write(fallback.getBytes(StandardCharsets.UTF_8));
    }
  }
}
