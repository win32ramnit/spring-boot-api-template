package com.demo.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

  @Bean
  public OpenAPI templateOpenAPI(
      @Value("${spring.application.name}") String appName,
      @Value("${app.api.base-path:/api/v1}") String basePath) {
    return new OpenAPI()
        .info(new Info()
            .title(appName + " API")
            .version("v1")
            .description("Starter template API. Base path: " + basePath))
        .components(new Components()
            .addSecuritySchemes("ApiKeyAuth",
                new SecurityScheme()
                    .type(SecurityScheme.Type.APIKEY)
                    .in(SecurityScheme.In.HEADER)
                    .name("X-API-KEY"))
            .addSecuritySchemes("BearerAuth",
                new SecurityScheme()
                    .type(SecurityScheme.Type.HTTP)
                    .scheme("bearer")
                    .bearerFormat("JWT")));
  }
}
