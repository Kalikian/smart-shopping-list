package com.kalikian.smartshoppinglist.items.controller;

import com.kalikian.smartshoppinglist.items.domain.Item;
import com.kalikian.smartshoppinglist.items.dto.CreateItemRequest;
import com.kalikian.smartshoppinglist.items.dto.ItemResponse;
import com.kalikian.smartshoppinglist.items.dto.UpdateItemRequest;
import com.kalikian.smartshoppinglist.items.service.ItemService;
import com.kalikian.smartshoppinglist.items.mapper.ItemMapper;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

/**
 * REST controller for Items.
 * Uses DTOs for request/response and delegates business logic to the service.
 */
@RestController
@RequestMapping("/api")
public class ItemController {

    private final ItemService service;

    public ItemController(ItemService service) {
        this.service = service;
    }

    // ---------------- Create ----------------

    /**
     * Create a new item. We accept listId in the body (matches the CreateItemRequest).
     * If you prefer a nested route (/lists/{listId}/items), we can easily add it later.
     */
    @PostMapping("/items")
    public ResponseEntity<ItemResponse> create(@Valid @RequestBody CreateItemRequest req) {
        // Service constructs and persists the entity
        Item created = service.createItem(
                req.getListId(),
                req.getName(),
                req.getCategory(),
                req.getQuantity(),
                req.getUnit()
        );
        ItemResponse body = ItemMapper.toResponse(created);
        // Location header is useful for REST semantics
        return ResponseEntity
                .created(URI.create("/api/lists/" + body.getListId() + "/items/" + body.getId()))
                .body(body);
    }

    // ---------------- Read ----------------

    /**
     * Get all items for a given list, newest first.
     */
    @GetMapping("/lists/{listId}/items")
    public ResponseEntity<List<ItemResponse>> listByList(@PathVariable Integer listId) {
        List<Item> items = service.getItemsByList(listId);
        return ResponseEntity.ok(ItemMapper.toResponseList(items));
    }

    /**
     * Get a single item by list and id.
     */
    @GetMapping("/lists/{listId}/items/{itemId}")
    public ResponseEntity<ItemResponse> getOne(@PathVariable Integer listId, @PathVariable Integer itemId) {
        Item item = service.getItem(listId, itemId);
        return ResponseEntity.ok(ItemMapper.toResponse(item));
    }

    // ---------------- Update (partial) ----------------

    /**
     * Partially update an item. Only provided fields are changed.
     * - name: if provided, must not be blank (DTO enforces it)
     * - category/unit: empty string "" will clear the field
     * - quantity: must be >= 0 if provided
     */
    @PatchMapping("/lists/{listId}/items/{itemId}")
    public ResponseEntity<ItemResponse> update(
            @PathVariable Integer listId,
            @PathVariable Integer itemId,
            @Valid @RequestBody UpdateItemRequest req
    ) {
        Item updated = service.updateItem(
                listId,
                itemId,
                req.getName(),
                req.getCategory(),
                req.getDone(),
                req.getQuantity(),
                req.getUnit()
        );
        return ResponseEntity.ok(ItemMapper.toResponse(updated));
    }

    /**
     * Toggle the done flag of an item.
     */
    @PatchMapping("/lists/{listId}/items/{itemId}/toggle")
    public ResponseEntity<ItemResponse> toggleDone(
            @PathVariable Integer listId,
            @PathVariable Integer itemId
    ) {
        Item toggled = service.toggleDone(listId, itemId);
        return ResponseEntity.ok(ItemMapper.toResponse(toggled));
    }

    // ---------------- Delete ----------------

    /**
     * Delete an item.
     */
    @DeleteMapping("/lists/{listId}/items/{itemId}")
    public ResponseEntity<Void> delete(
            @PathVariable Integer listId,
            @PathVariable Integer itemId
    ) {
        service.deleteItem(listId, itemId);
        return ResponseEntity.noContent().build();
    }
}
