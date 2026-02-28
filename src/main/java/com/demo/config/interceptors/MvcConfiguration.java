package com.demo.config.interceptors;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebMvc
@ComponentScan(basePackages = "com.demo")
public class MvcConfiguration implements WebMvcConfigurer {

  @Bean
  public GlobalInterceptor globalInterceptor() {
    return new GlobalInterceptor();
  }

  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    registry.addInterceptor(globalInterceptor()).addPathPatterns("/**");
  }

  @Override
  public void addResourceHandlers(ResourceHandlerRegistry registry) {
    // @formatter:off
    registry.addResourceHandler("/demoImg/**")
        .addResourceLocations("file:static-assets/demoImg/");
    // @formatter:on
  }
}
