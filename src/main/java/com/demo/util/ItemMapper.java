package com.demo.util;

import com.demo.dto.ItemResponse;
import com.demo.entity.Item;

public final class ItemMapper {

  private ItemMapper() {
  }

  public static ItemResponse toResponse(Item item) {
    return ItemResponse.builder()
        .id(item.getId())
        .name(item.getName())
        .description(item.getDescription())
        .status(item.getStatus())
        .createdAt(item.getCreatedAt())
        .updatedAt(item.getUpdatedAt())
        .build();
  }
}
