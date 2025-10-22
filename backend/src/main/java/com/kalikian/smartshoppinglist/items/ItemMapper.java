package com.kalikian.smartshoppinglist.items;

import com.kalikian.smartshoppinglist.items.domain.Item;
import com.kalikian.smartshoppinglist.items.dto.ItemResponse;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Maps between domain entities and API DTOs.
 * Static utility; no state.
 */
public final class ItemMapper {

    private ItemMapper() {
        // utility class; prevent instantiation
    }

    /**
     * Map a single Item entity to an ItemResponse DTO.
     */
    public static ItemResponse toResponse(Item item) {
        if (item == null) return null;

        ItemResponse dto = new ItemResponse();
        dto.setId(item.getId());
        dto.setListId(item.getListId());
        dto.setName(item.getName());
        dto.setCategory(item.getCategory());     // may be null or ""
        dto.setQuantity(item.getQuantity());     // may be null
        dto.setUnit(item.getUnit());             // may be null or ""
        dto.setDone(item.isDone());
        dto.setCreatedAt(item.getCreatedAt());
        dto.setUpdatedAt(item.getUpdatedAt());
        return dto;
    }

    /**
     * Map a list of Item entities to ItemResponse DTOs.
     * Null items are filtered out defensively.
     */
    public static List<ItemResponse> toResponseList(List<Item> items) {
        if (items == null) return List.of();
        return items.stream()
                .filter(Objects::nonNull)
                .map(ItemMapper::toResponse)
                .collect(Collectors.toList());
    }
}