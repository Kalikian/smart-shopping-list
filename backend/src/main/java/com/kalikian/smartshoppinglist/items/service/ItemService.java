package com.kalikian.smartshoppinglist.items.service;

import com.kalikian.smartshoppinglist.items.domain.Item;
import com.kalikian.smartshoppinglist.items.repository.ItemRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class ItemService {

    private final ItemRepository repo;

    public ItemService(ItemRepository repo) {
        this.repo = repo;
    }

    // ---------- Create ----------

    @Transactional
    public Item createItem(Integer listId, String name, String category, Double quantity, String unit) {
        // Normalize and validate required fields
        final String cleanedName = trimOrNull(name);
        if (cleanedName == null) {
            throw new IllegalArgumentException("Item name must not be blank.");
        }

        // Normalize optional strings
        final String cleanedCategory = trimOrNull(category);
        final String cleanedUnit = trimOrNull(unit);

        // Use required-fields constructor to enforce invariants
        Item item = new Item(listId, cleanedName, cleanedCategory);

        // Set optional fields only if provided
        if (quantity != null) {
            item.setQuantity(quantity);
        }
        if (cleanedUnit != null) {
            item.setUnit(cleanedUnit);
        }

        // Make default explicit (even though entity default is false)
        item.setDone(false);

        return repo.save(item);
    }

    // ---------- Read ----------

    @Transactional(readOnly = true)
    public List<Item> getItemsByList(Integer listId) {
        return repo.findByListIdOrderByCreatedAtDesc(listId);
    }

    @Transactional(readOnly = true)
    public Item getItem(Integer listId, Integer itemId) {
        return repo.findByIdAndListId(itemId, listId)
                .orElseThrow(() -> notFound(itemId, listId));
    }

    // ---------- Update ----------

    @Transactional
    public Item updateItem(
            Integer listId,
            Integer itemId,
            String newName,
            String newCategory,
            Boolean done,
            Double newQuantity,
            String newUnit
    ) {
        Item item = repo.findByIdAndListId(itemId, listId)
                .orElseThrow(() -> notFound(itemId, listId));

        // Update only provided fields (partial update)
        if (newName != null) {
            String cleaned = trimOrNull(newName);
            if (cleaned == null) throw new IllegalArgumentException("Item name must not be blank.");
            item.setName(cleaned);
        }

        if (newCategory != null) {
            // apply even if "", meaning: clear the category
            item.setCategory(newCategory);
        }

        if (done != null) {
            item.setDone(done);
        }

        // NEW: quantity (nullable) – if provided, set it; allow zero or positive
        if (newQuantity != null) {
            if (newQuantity < 0) {
                throw new IllegalArgumentException("Quantity must be >= 0.");
            }
            item.setQuantity(newQuantity);
        }

        if (newUnit != null) {
            // apply even if "", meaning: clear the unit
            item.setUnit(newUnit);
        }

        // Dirty checking will flush on commit; save() explicit for clarity
        return repo.save(item);
    }

    @Transactional
    public Item toggleDone(Integer listId, Integer itemId) {
        Item item = repo.findByIdAndListId(itemId, listId)
                .orElseThrow(() -> notFound(itemId, listId));
        item.setDone(!item.isDone());
        return repo.save(item);
    }

    // ---------- Delete ----------

    @Transactional
    public void deleteItem(Integer listId, Integer itemId) {
        long deleted = repo.deleteByIdAndListId(itemId, listId);
        if (deleted == 0) {
            throw notFound(itemId, listId);
        }
    }

    // ---------- Helpers ----------

    private static String trimOrNull(String s) {
        if (s == null) return null;
        String t = s.trim();
        return t.isEmpty() ? null : t;
    }

    private static NoSuchElementException notFound(Integer itemId, Integer listId) {
        return new NoSuchElementException("Item " + itemId + " not found in list " + listId);
    }
}

