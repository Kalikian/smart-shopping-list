package com.kalikian.smartshoppinglist.items.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

/**
 * BE request DTO for creating a new Item.
 * Validates incoming JSON from the client.
 *
 * Notes:
 * - Use boxed types (Double/Boolean) for optional fields to allow null = "not provided".
 * - Keep this decoupled from the JPA entity.
 */
public class CreateItemRequest {

    @NotBlank(message = "Name must not be blank.")
    @Size(max = 120, message = "Name must be at most 120 characters.")
    private String name;

    @Positive(message = "Quantity must be greater than 0.")
    private Double quantity; // optional

    @Size(max = 24, message = "Unit must be at most 24 characters.")
    private String unit;     // optional

    // Optional initial done state; if null, service should default to false
    private Boolean done;

    // --- Getters & Setters ---
    public String getName() { return name; }

    public void setName(String name) {
        // Trim incoming whitespace; keep null as null to let @NotBlank handle empties
        this.name = (name == null) ? null : name.trim();
    }

    public Double getQuantity() { return quantity; }
    public void setQuantity(Double quantity) { this.quantity = quantity; }

    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = (unit == null) ? null : unit.trim(); }

    public Boolean getDone() { return done; }
    public void setDone(Boolean done) { this.done = done; }
}
