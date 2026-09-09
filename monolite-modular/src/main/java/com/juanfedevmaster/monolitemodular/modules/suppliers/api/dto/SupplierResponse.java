package com.juanfedevmaster.monolitemodular.modules.suppliers.api.dto;

public record SupplierResponse(
    Long id,
    String name,
    String contactName,
    String phone,
    String email,
    boolean active
) {}
