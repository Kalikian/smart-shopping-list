package com.kalikian.smartshoppinglist.items.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "items")
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // Foreign key to the owning list (we’ll add a real relation later)
    @Column(nullable = false)
    private Integer listId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private boolean done = false;

    private String category;

    // Set once at creation time
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    // Updated whenever a mutable field changes
    @Column(nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();

    public Item() {}

    // Convenience constructor for creating a new item
    public Item(Integer listId, String name, String category) {
        this.listId = listId;
        this.name = name;
        this.category = category;
    }

    // Getters & setters
    public Integer getId() { return id; }
    public Integer getListId() { return listId; }
    public void setListId(Integer listId) { this.listId = listId; }

    public String getName() { return name; }
    public void setName(String name) {
        this.name = name;
        this.updatedAt = LocalDateTime.now();
    }

    public boolean isDone() { return done; }
    public void setDone(boolean done) {
        this.done = done;
        this.updatedAt = LocalDateTime.now();
    }

    public String getCategory() { return category; }
    public void setCategory(String category) {
        this.category = category;
        this.updatedAt = LocalDateTime.now();
    }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}
