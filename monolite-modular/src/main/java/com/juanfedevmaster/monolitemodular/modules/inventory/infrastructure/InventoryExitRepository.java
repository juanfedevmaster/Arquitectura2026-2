package com.juanfedevmaster.monolitemodular.modules.inventory.infrastructure;

import com.juanfedevmaster.monolitemodular.modules.inventory.domain.InventoryExit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InventoryExitRepository extends JpaRepository<InventoryExit, Long> {
    List<InventoryExit> findByProductId(Long productId);
}
