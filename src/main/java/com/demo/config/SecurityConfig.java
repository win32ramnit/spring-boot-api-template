package com.demo.config;

import com.demo.security.ApiKeyAuthenticationFilter;
import com.demo.util.ApiResponseWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.util.StringUtils;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

  private final AppProperties properties;
  private final ApiKeyAuthenticationFilter apiKeyAuthenticationFilter;
  private final ApiResponseWriter responseWriter;

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    String basePath = properties.getApi().getBasePath();

    http.csrf(csrf -> csrf.disable())
        .cors(Customizer.withDefaults())
        .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .httpBasic(basic -> basic.disable())
        .formLogin(form -> form.disable())
        .exceptionHandling(ex -> ex
            .authenticationEntryPoint((request, response, authException) ->
                responseWriter.write(request, response, HttpStatus.UNAUTHORIZED, "UNAUTHORIZED",
                    "Authentication required", null, null))
            .accessDeniedHandler((request, response, accessDeniedException) ->
                responseWriter.write(request, response, HttpStatus.FORBIDDEN, "FORBIDDEN",
                    "Access denied", null, null)));

    http.authorizeHttpRequests(auth -> {
      auth.requestMatchers("/actuator/**", "/swagger-ui/**", "/swagger-ui.html", "/v3/api-docs/**",
              "/favicon.ico").permitAll()
          .requestMatchers(basePath + "/health", basePath + "/info").permitAll();

      if (properties.getSecurity().isEnabled()) {
        auth.anyRequest().authenticated();
      } else {
        auth.anyRequest().permitAll();
      }
    });

    if (properties.getSecurity().isEnabled()) {
      if (properties.getSecurity().getJwt().isEnabled()) {
        http.oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()));
      } else if (properties.getSecurity().getApiKey().isEnabled()) {
        http.addFilterBefore(apiKeyAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
      }
    }

    return http.build();
  }

  @Bean
  @ConditionalOnProperty(prefix = "app.security.jwt", name = "enabled", havingValue = "true")
  public JwtDecoder jwtDecoder() {
    AppProperties.Jwt jwt = properties.getSecurity().getJwt();
    if (StringUtils.hasText(jwt.getJwkSetUri())) {
      return NimbusJwtDecoder.withJwkSetUri(jwt.getJwkSetUri()).build();
    }
    if (StringUtils.hasText(jwt.getIssuerUri())) {
      NimbusJwtDecoder decoder = NimbusJwtDecoder.withIssuerLocation(jwt.getIssuerUri()).build();
      decoder.setJwtValidator(JwtValidators.createDefaultWithIssuer(jwt.getIssuerUri()));
      return decoder;
    }
    if (StringUtils.hasText(jwt.getHmacSecret())) {
      SecretKey key = new SecretKeySpec(jwt.getHmacSecret().getBytes(StandardCharsets.UTF_8),
          "HmacSHA256");
      NimbusJwtDecoder decoder = NimbusJwtDecoder.withSecretKey(key).build();
      decoder.setJwtValidator(JwtValidators.createDefault());
      return decoder;
    }
    throw new IllegalStateException(
        "JWT is enabled but no issuer-uri, jwk-set-uri, or hmac-secret is configured");
  }
}
