package com.juanfedevmaster.monolitemodular.modules.inventory.infrastructure;

import com.juanfedevmaster.monolitemodular.modules.inventory.domain.InventoryEntry;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InventoryEntryRepository extends JpaRepository<InventoryEntry, Long> {
    List<InventoryEntry> findByProductId(Long productId);
}
