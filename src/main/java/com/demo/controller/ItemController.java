package com.demo.controller;

import com.demo.dto.ApiResponse;
import com.demo.dto.CreateItemRequest;
import com.demo.dto.ItemResponse;
import com.demo.dto.PageResponse;
import com.demo.dto.UpdateItemRequest;
import com.demo.entity.Item;
import com.demo.service.ItemService;
import com.demo.util.ApiResponseUtil;
import com.demo.util.ItemMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;
import java.net.URI;

@RestController
@RequestMapping("${app.api.base-path:/api/v1}/items")
@Tag(name = "Items", description = "Sample CRUD endpoints")
@SecurityRequirement(name = "ApiKeyAuth")
@SecurityRequirement(name = "BearerAuth")
@RequiredArgsConstructor
@Validated
public class ItemController {

  private final ItemService itemService;

  @PostMapping
  @Operation(summary = "Create a new item")
  public ResponseEntity<ApiResponse<ItemResponse>> create(@Valid @RequestBody CreateItemRequest body,
      HttpServletRequest request) {
    Item item = itemService.create(body);
    ApiResponse<ItemResponse> response = ApiResponseUtil.success(ItemMapper.toResponse(item),
        request, HttpStatus.CREATED);
    URI location = ServletUriComponentsBuilder.fromCurrentRequest()
        .path("/{id}")
        .buildAndExpand(item.getId())
        .toUri();
    return ResponseEntity.created(location).body(response);
  }

  @GetMapping("/{id}")
  @Operation(summary = "Get item by id")
  public ResponseEntity<ApiResponse<ItemResponse>> getById(@PathVariable @Min(1) Long id,
      HttpServletRequest request) {
    Item item = itemService.getById(id);
    return ResponseEntity.ok(ApiResponseUtil.success(ItemMapper.toResponse(item), request,
        HttpStatus.OK));
  }

  @GetMapping
  @Operation(summary = "List items")
  public ResponseEntity<ApiResponse<PageResponse<ItemResponse>>> list(
      @ParameterObject Pageable pageable, HttpServletRequest request) {
    Page<ItemResponse> page = itemService.list(pageable).map(ItemMapper::toResponse);
    PageResponse<ItemResponse> data = PageResponse.fromPage(page);
    return ResponseEntity.ok(ApiResponseUtil.success(data, request, HttpStatus.OK));
  }

  @PutMapping("/{id}")
  @Operation(summary = "Update an item")
  public ResponseEntity<ApiResponse<ItemResponse>> update(@PathVariable @Min(1) Long id,
      @Valid @RequestBody UpdateItemRequest body, HttpServletRequest request) {
    Item item = itemService.update(id, body);
    return ResponseEntity.ok(ApiResponseUtil.success(ItemMapper.toResponse(item), request,
        HttpStatus.OK));
  }

  @DeleteMapping("/{id}")
  @Operation(summary = "Delete an item")
  public ResponseEntity<ApiResponse<List<String>>> delete(@PathVariable @Min(1) Long id,
      HttpServletRequest request) {
    itemService.delete(id);
    return ResponseEntity.ok(ApiResponseUtil.success(List.of("deleted"), request, HttpStatus.OK));
  }
}
