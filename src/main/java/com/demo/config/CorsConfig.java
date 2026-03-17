package com.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
public class CorsConfig {

  @Bean
  public CorsConfigurationSource corsConfigurationSource(AppProperties properties) {
    CorsConfiguration config = new CorsConfiguration();
    AppProperties.Cors cors = properties.getCors();

    config.setAllowCredentials(cors.isAllowCredentials());
    config.setAllowedMethods(cors.getAllowedMethods());
    config.setAllowedHeaders(cors.getAllowedHeaders());
    config.setExposedHeaders(cors.getExposedHeaders());
    config.setMaxAge(cors.getMaxAgeSeconds());

    List<String> origins = cors.getAllowedOrigins();
    if (origins.stream().anyMatch(origin -> origin.contains("*"))) {
      config.setAllowedOriginPatterns(origins);
    } else {
      config.setAllowedOrigins(origins);
    }

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", config);
    return source;
  }
}
