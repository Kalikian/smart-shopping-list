package com.kalikian.smartshoppinglist.items.dto;

import jakarta.validation.constraints.*;

public class CreateItemRequest {

    @NotNull(message = "listId is required.")
    private Integer listId; // keep Integer to match Entity/Service

    @NotBlank(message = "Name must not be blank.")
    @Size(max = 120, message = "Name must be at most 120 characters.")
    private String name;

    @Size(max = 64, message = "Category must be at most 64 characters.")
    private String category;

    // Optional; if provided, must be >= 0
    @PositiveOrZero(message = "Quantity must be >= 0.")
    private Double quantity;

    @Size(max = 24, message = "Unit must be at most 24 characters.")
    private String unit;

    // Optional; default to false in service if null
    private Boolean done;

    // --- Getters / Setters ---
    public Integer getListId() { return listId; }
    public void setListId(Integer listId) { this.listId = listId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = (name == null) ? null : name.trim(); }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = (category == null) ? null : category.trim(); }

    public Double getQuantity() { return quantity; }
    public void setQuantity(Double quantity) { this.quantity = quantity; }

    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = (unit == null) ? null : unit.trim(); }

    public Boolean getDone() { return done; }
    public void setDone(Boolean done) { this.done = done; }
}
