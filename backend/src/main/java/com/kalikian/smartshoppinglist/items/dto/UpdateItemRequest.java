package com.kalikian.smartshoppinglist.items.dto;

import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

/**
 * BE request DTO for partially updating an existing Item.
 * All fields are optional; only provided values will be applied.
 */
public class UpdateItemRequest {

    @Size(max = 120, message = "Name must be at most 120 characters.")
    private String name;        // optional

    @Size(max = 64, message = "Category must be at most 64 characters.")
    private String category;    // optional

    @PositiveOrZero(message = "Quantity must be >= 0.")
    private Double quantity;    // optional

    @Size(max = 24, message = "Unit must be at most 24 characters.")
    private String unit;        // optional

    private Boolean done;       // optional

    // --- Getters & Setters (with light input hygiene) ---

    public String getName() { return name; }
    public void setName(String name) {
        this.name = trimOrNull(name);
    }

    public String getCategory() { return category; }
    public void setCategory(String category) {
        this.category = trimOrNull(category);
    }

    public Double getQuantity() { return quantity; }
    public void setQuantity(Double quantity) {
        this.quantity = quantity; // keep as-is; null means "not provided"
    }

    public String getUnit() { return unit; }
    public void setUnit(String unit) {
        this.unit = trimOrNull(unit);
    }

    public Boolean getDone() { return done; }
    public void setDone(Boolean done) {
        this.done = done; // null means "not provided"
    }

    // Normalize: trim whitespace; treat empty string as null
    private static String trimOrNull(String s) {
        if (s == null) return null;
        String t = s.trim();
        return t.isEmpty() ? null : t;
    }
}
