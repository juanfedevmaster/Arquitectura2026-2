package com.juanfedevmaster.monolitemodular.modules.suppliers.application;

import com.juanfedevmaster.monolitemodular.modules.suppliers.api.dto.SupplierRequest;
import com.juanfedevmaster.monolitemodular.modules.suppliers.api.dto.SupplierResponse;
import com.juanfedevmaster.monolitemodular.modules.suppliers.domain.Supplier;
import com.juanfedevmaster.monolitemodular.modules.suppliers.infrastructure.SupplierRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SupplierService {

    private final SupplierRepository repository;

    @Transactional(readOnly = true)
    public List<SupplierResponse> findAll() {
        return repository.findByActiveTrue().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public SupplierResponse findById(Long id) {
        return toResponse(getOrThrow(id));
    }

    @Transactional
    public SupplierResponse create(SupplierRequest request) {
        Supplier supplier = Supplier.builder()
            .name(request.name())
            .contactName(request.contactName())
            .phone(request.phone())
            .email(request.email())
            .build();
        return toResponse(repository.save(supplier));
    }

    @Transactional
    public SupplierResponse update(Long id, SupplierRequest request) {
        Supplier supplier = getOrThrow(id);
        supplier.setName(request.name());
        supplier.setContactName(request.contactName());
        supplier.setPhone(request.phone());
        supplier.setEmail(request.email());
        return toResponse(repository.save(supplier));
    }

    @Transactional
    public void deactivate(Long id) {
        Supplier supplier = getOrThrow(id);
        supplier.setActive(false);
        repository.save(supplier);
    }

    private Supplier getOrThrow(Long id) {
        return repository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Supplier not found: " + id));
    }

    private SupplierResponse toResponse(Supplier s) {
        return new SupplierResponse(s.getId(), s.getName(), s.getContactName(), s.getPhone(), s.getEmail(), s.isActive());
    }
}
