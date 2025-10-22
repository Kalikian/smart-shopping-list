package com.kalikian.smartshoppinglist.items.domain;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.OffsetDateTime;

@Entity
@Table(name = "items")
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // Foreign key to the owning list (we’ll add a real relation later)
    @Column(name = "list_id", nullable = false)
    private Integer listId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private boolean done = false;

    @Column
    private String category;

    // NEW: quantity and unit (both optional)
    // quantity maps to DOUBLE PRECISION (nullable)
    @Column
    private Double quantity;

    // unit maps to TEXT (nullable)
    @Column
    private String unit;

    // Set once at creation time (Hibernate Annotation)
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    // Updated whenever a mutable field changes (Hibernate Annotation)
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    public Item() {}

    // Convenience constructor for creating a new item
    public Item(Integer listId, String name, String category) {
        this.listId = listId;
        this.name = name;
        this.category = category;
    }

    // Convenience constructor for required + optional fields
    public Item(Integer listId, String name, String category, Double quantity, String unit) {
        this(listId, name, category);
        this.quantity = quantity;                           // may be null
        this.unit = (unit == null) ? null : unit.trim();    // may be null
    }

    // --- Getters & Setters ---

    public Integer getId() { return id; }

    public Integer getListId() { return listId; }
    public void setListId(Integer listId) { this.listId = listId; }

    public String getName() { return name; }
    public void setName(String name) {
        // basic input hygiene (trim while keeping null as null)
        this.name = (name == null) ? null : name.trim();
    }

    public boolean isDone() { return done; }
    public void setDone(boolean done) { this.done = done; }

    public String getCategory() { return category; }
    public void setCategory(String category) {
        this.category = (category == null) ? null : category.trim();
    }

    public Double getQuantity() { return quantity; }
    public void setQuantity(Double quantity) { this.quantity = quantity; }

    public String getUnit() { return unit; }
    public void setUnit(String unit) {
        this.unit = (unit == null) ? null : unit.trim();
    }

    public OffsetDateTime getCreatedAt() { return createdAt; }
    public OffsetDateTime getUpdatedAt() { return updatedAt; }
}
