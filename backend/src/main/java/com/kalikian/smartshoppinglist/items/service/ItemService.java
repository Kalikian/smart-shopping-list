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
    public Item createItem(Integer listId, String name, String category) {
        // Basic input hygiene
        String cleanedName = (name == null) ? "" : name.trim();
        if (cleanedName.isEmpty()) {
            throw new IllegalArgumentException("Item name must not be blank.");
        }

        Item item = new Item(listId, cleanedName, trimOrNull(category));
        // done=false is default on entity
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
    public Item updateItem(Integer listId, Integer itemId, String newName, String newCategory, Boolean done) {
        Item item = repo.findByIdAndListId(itemId, listId)
                .orElseThrow(() -> notFound(itemId, listId));

        // Only update the fields that are provided (simple partial update)
        if (newName != null) {
            String cleaned = newName.trim();
            if (cleaned.isEmpty()) throw new IllegalArgumentException("Item name must not be blank.");
            item.setName(cleaned);
        }
        if (newCategory != null) {
            item.setCategory(trimOrNull(newCategory));
        }
        if (done != null) {
            item.setDone(done);
        }

        // Dirty checking will flush changes on commit; save() is optional, but explicit is fine
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

