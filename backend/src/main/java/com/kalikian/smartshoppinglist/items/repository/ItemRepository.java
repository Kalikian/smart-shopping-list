package com.kalikian.smartshoppinglist.items.repository;

import com.kalikian.smartshoppinglist.items.domain.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ItemRepository extends JpaRepository<Item, Integer> {

    // Find all items for a given list, newest first
    List<Item> findByListIdOrderByCreatedAtDesc(Integer listId);

    // Find all "open" (not done) items for a list
    List<Item> findAllByListIdAndDoneFalse(Integer listId);

    // Safety check: ensure an item belongs to a list (useful for multi-tenant/ownership checks)
    Optional<Item> findByIdAndListId(Integer id, Integer listId);

    // Existence checks
    boolean existsByIdAndListId(Integer id, Integer listId);

    // Scoped delete: delete only if the item belongs to that list
    long deleteByIdAndListId(Integer id, Integer listId);

    // Simple search by name within a list (case-insensitive contains)
    List<Item> findByListIdAndNameIgnoreCaseContainingOrderByCreatedAtDesc(Integer listId, String namePart);
}
