package com.kalikian.smartshoppinglist.items.dto;

import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Pattern;

/**
 * BE request DTO for partially updating an existing Item.
 * All fields are optional; only provided values will be applied.
 * Rule for name: if provided (non-null), it must not be blank.
 */
public class UpdateItemRequest {

    @Size(max = 120, message = "Name must be at most 120 characters.")
    @Pattern(regexp = ".*\\S.*", message = "Name must not be blank when provided.")
    private String name;        // optional; null = not provided; "" or whitespace => invalid

    @Size(max = 64, message = "Category must be at most 64 characters.")
    private String category;    // optional (null allowed)

    @PositiveOrZero(message = "Quantity must be >= 0.")
    private Double quantity;    // optional

    @Size(max = 24, message = "Unit must be at most 24 characters.")
    private String unit;        // optional

    private Boolean done;       // optional

    // --- Getters & Setters ---

    public String getName() { return name; }
    public void setName(String name) {
        // Trim but DO NOT convert empty to null; we want @Pattern to catch blanks.
        this.name = (name == null) ? null : name.trim();
    }

    public String getCategory() { return category; }
    public void setCategory(String category) {
        // Trim, but keep empty string "" if client wants to clear the field
        this.category = (category == null) ? null : category.trim();
    }

    public Double getQuantity() { return quantity; }
    public void setQuantity(Double quantity) { this.quantity = quantity; }

    public String getUnit() { return unit; }
    public void setUnit(String unit) {
        // Trim, but keep empty string "" to allow explicit clearing
        this.unit = (unit == null) ? null : unit.trim();
    }

    public Boolean getDone() { return done; }
    public void setDone(Boolean done) { this.done = done; }
}
