package com.demo.security;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;

public class ApiKeyAuthenticationToken extends AbstractAuthenticationToken {

  private final String apiKey;

  public ApiKeyAuthenticationToken(String apiKey) {
    super(List.of(new SimpleGrantedAuthority("ROLE_API_KEY")));
    this.apiKey = apiKey;
    setAuthenticated(true);
  }

  @Override
  public Object getCredentials() {
    return apiKey;
  }

  @Override
  public Object getPrincipal() {
    return "api-key";
  }
}
