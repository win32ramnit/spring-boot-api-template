package com.demo.dto;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.domain.Page;

import java.util.List;

@Data
@Builder
public class PageResponse<T> {
  private List<T> items;
  private int page;
  private int size;
  private long totalElements;
  private int totalPages;

  public static <T> PageResponse<T> fromPage(Page<T> page) {
    return PageResponse.<T>builder()
        .items(page.getContent())
        .page(page.getNumber())
        .size(page.getSize())
        .totalElements(page.getTotalElements())
        .totalPages(page.getTotalPages())
        .build();
  }
}
