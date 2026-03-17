package com.demo.config;

import jakarta.validation.Valid;
import jakarta.validation.constraints.AssertTrue;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;
import org.springframework.util.StringUtils;

import java.util.List;

@Getter
@Setter
@Validated
@ConfigurationProperties(prefix = "app")
public class AppProperties {

  @Valid
  private final Api api = new Api();

  @Valid
  private final Security security = new Security();

  @Valid
  private final Cors cors = new Cors();

  @Valid
  private final Logging logging = new Logging();

  @Getter
  @Setter
  public static class Api {
    private String basePath = "/api/v1";
  }

  @Getter
  @Setter
  public static class Security {
    private boolean enabled = false;
    private final ApiKey apiKey = new ApiKey();
    private final Jwt jwt = new Jwt();

    @AssertTrue(message = "When app.security.enabled=true, enable exactly one of api-key or jwt and provide its required values.")
    public boolean isSecurityValid() {
      if (!enabled) {
        return true;
      }
      if (apiKey.enabled == jwt.enabled) {
        return false;
      }
      if (apiKey.enabled) {
        return StringUtils.hasText(apiKey.header) && StringUtils.hasText(apiKey.value);
      }
      int modeCount = 0;
      if (StringUtils.hasText(jwt.issuerUri)) {
        modeCount++;
      }
      if (StringUtils.hasText(jwt.jwkSetUri)) {
        modeCount++;
      }
      if (StringUtils.hasText(jwt.hmacSecret)) {
        modeCount++;
      }
      if (modeCount != 1) {
        return false;
      }
      return !StringUtils.hasText(jwt.hmacSecret) || jwt.hmacSecret.length() >= 32;
    }
  }

  @Getter
  @Setter
  public static class ApiKey {
    private boolean enabled = false;
    private String header = "X-API-KEY";
    private String value;
  }

  @Getter
  @Setter
  public static class Jwt {
    private boolean enabled = false;
    private String issuerUri;
    private String jwkSetUri;
    private String hmacSecret;
  }

  @Getter
  @Setter
  public static class Cors {
    private List<String> allowedOrigins = List.of();
    private List<String> allowedMethods = List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS");
    private List<String> allowedHeaders = List.of("Authorization", "Content-Type", "X-Requested-With", "Accept", "Origin");
    private List<String> exposedHeaders = List.of("Content-Disposition", "X-Request-Id");
    private boolean allowCredentials = true;
    private long maxAgeSeconds = 600;
  }

  @Getter
  @Setter
  public static class Logging {
    private final Http http = new Http();
  }

  @Getter
  @Setter
  public static class Http {
    private boolean enabled = false;
    private int maxPayloadLength = 2048;
  }
}
