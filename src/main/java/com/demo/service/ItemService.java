package com.demo.service;

import com.demo.dto.CreateItemRequest;
import com.demo.dto.UpdateItemRequest;
import com.demo.entity.Item;
import com.demo.exception.ResourceNotFoundException;
import com.demo.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ItemService {

  private final ItemRepository itemRepository;

  @Transactional
  public Item create(CreateItemRequest request) {
    Item item = Item.builder()
        .name(request.name())
        .description(request.description())
        .build();
    return itemRepository.save(item);
  }

  @Transactional(readOnly = true)
  public Item getById(Long id) {
    return itemRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Item not found: " + id));
  }

  @Transactional(readOnly = true)
  public Page<Item> list(Pageable pageable) {
    return itemRepository.findAll(pageable);
  }

  @Transactional
  public Item update(Long id, UpdateItemRequest request) {
    Item item = getById(id);
    item.setName(request.name());
    item.setDescription(request.description());
    item.setStatus(request.status());
    return itemRepository.save(item);
  }

  @Transactional
  public void delete(Long id) {
    Item item = getById(id);
    itemRepository.delete(item);
  }
}
