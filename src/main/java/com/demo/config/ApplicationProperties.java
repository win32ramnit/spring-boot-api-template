package com.demo.config;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "demo")
public class ApplicationProperties {
  @NotBlank
  private String contentSecurityPolicy;

  @NotBlank
  private String allowedOrigins;

  @NotBlank
  private String excludedUrls;

  @NotBlank
  private String issuerUri;

  @NotBlank
  private String securityHeaderName;

  @NotBlank
  private String apiKey;
}
