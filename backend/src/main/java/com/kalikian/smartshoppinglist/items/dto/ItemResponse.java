package com.kalikian.smartshoppinglist.items.dto;

import java.time.OffsetDateTime;

/**
 * API response DTO for an Item.
 * Keeps the HTTP response shape decoupled from the JPA entity.
 */
public class ItemResponse {

    private Integer id;
    private Integer listId;
    private String name;
    private String category;      // can be null or ""
    private Double quantity;      // can be null
    private String unit;          // can be null or ""
    private boolean done;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    // --- Getters & Setters ---

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getListId() { return listId; }
    public void setListId(Integer listId) { this.listId = listId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public Double getQuantity() { return quantity; }
    public void setQuantity(Double quantity) { this.quantity = quantity; }

    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }

    public boolean isDone() { return done; }
    public void setDone(boolean done) { this.done = done; }

    public OffsetDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }

    public OffsetDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(OffsetDateTime updatedAt) { this.updatedAt = updatedAt; }
}
