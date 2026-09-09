package com.juanfedevmaster.monolitemodular.modules.suppliers.infrastructure;

import com.juanfedevmaster.monolitemodular.modules.suppliers.domain.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SupplierRepository extends JpaRepository<Supplier, Long> {
    List<Supplier> findByActiveTrue();
}
