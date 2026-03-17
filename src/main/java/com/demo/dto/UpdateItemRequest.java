package com.demo.dto;

import com.demo.entity.ItemStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UpdateItemRequest(
    @NotBlank @Size(max = 120) String name,
    @Size(max = 1000) String description,
    @NotNull ItemStatus status
) {
}
