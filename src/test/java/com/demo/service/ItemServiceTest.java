package com.demo.service;

import com.demo.dto.CreateItemRequest;
import com.demo.entity.Item;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class ItemServiceTest {

  @Autowired
  private ItemService itemService;

  @Test
  void createAndFetch() {
    Item created = itemService.create(new CreateItemRequest("Test Item", "Sample description"));
    Item fetched = itemService.getById(created.getId());

    assertThat(fetched.getId()).isNotNull();
    assertThat(fetched.getName()).isEqualTo("Test Item");
  }
}
