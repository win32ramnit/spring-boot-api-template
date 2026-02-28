package com.demo.config.security;

import com.demo.config.JwtProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;

/**
 * Security configuration for the application.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

  @Autowired
  @Qualifier("securityAuthenticationEntryPoint")
  private AuthenticationEntryPoint authenticationEntryPoint;

  private final JwtProperties jwtProperties;
  private final AuthorizationManager<RequestAuthorizationContext> keyAuthManager;

  public SecurityConfig(JwtProperties jwtProperties, AuthorizationManager<RequestAuthorizationContext> keyAuthManager) {
    this.jwtProperties = jwtProperties;
    this.keyAuthManager = keyAuthManager;
  }

  /*@Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    // @formatter:off
    http.csrf(csrf -> csrf.disable())
        .authorizeHttpRequests(authorize -> authorize
            .requestMatchers("/**").permitAll()
            .requestMatchers("/xyz/trade-finance/**").hasAuthority("SCOPE_EMAIL")
            .anyRequest().authenticated()
        )
        .oauth2ResourceServer(oauth2 -> oauth2
            .jwt(jwt -> jwt.decoder(jwtDecoder())));
    // @formatter:on

    return http.build();
  }*/

//  @Bean
//  public JwtDecoder jwtDecoder() {
//    return NimbusJwtDecoder.withIssuerLocation(jwtProperties.getIssuerUri()).build();
//  }

  @Bean
  @Order(1)
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    // @formatter:off
    http.securityMatcher("/v1/**")

        .csrf(AbstractHttpConfigurer::disable)

        .exceptionHandling(exceptionHandling -> exceptionHandling
            .authenticationEntryPoint(authenticationEntryPoint))

        .sessionManagement(sessionManagement -> sessionManagement
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS))

        .authorizeHttpRequests(authorize -> authorize
            .requestMatchers("/v1/**").access(keyAuthManager));

    // @formatter:on
    return http.build();
  }

  @Bean
  public WebSecurityCustomizer webSecurityCustomizer() {
    return (web) -> web.ignoring().requestMatchers("/actuator/**", "/api/v1/health", "/api/v1/info",
        "/swagger-ui/**", "/v3/api-docs/**");
  }

}
