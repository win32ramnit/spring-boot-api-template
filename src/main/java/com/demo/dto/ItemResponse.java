package com.demo.dto;

import com.demo.entity.ItemStatus;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class ItemResponse {
  private Long id;
  private String name;
  private String description;
  private ItemStatus status;
  private Instant createdAt;
  private Instant updatedAt;
}
