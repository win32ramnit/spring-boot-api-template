package com.demo.cache;

import com.demo.model.SystemProperty;
import com.demo.repository.SystemPropertyRepository;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Configuration
public class PropertiesCache extends Cache {

  Logger log = LoggerFactory.getLogger(PropertiesCache.class);

  private Map<String, Object> allProps = new HashMap<>();

  @Autowired
  private SystemPropertyRepository systemPropertyRepository;

  @PostConstruct
  @Override
  protected void constructCache() {
    load();
  }

  @Override
  public void load() {
    try {
      List<SystemProperty> systemProperties = systemPropertyRepository.findAll();
      allProps = systemProperties.stream().collect(
          Collectors.toMap(SystemProperty::getName, SystemProperty::getValue,
              (existing, replacement) -> existing)); // Handle duplicates
      log.info("Loaded {} properties into cache.", allProps.size());
    } catch (Exception e) {
      log.error("Failed to load properties from the database.", e);
    }
  }

  public String getProperty(String key) {
    return allProps.getOrDefault(key, key).toString();
  }
}
