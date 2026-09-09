package com.juanfedevmaster.monolitemodular.modules.suppliers.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SupplierRequest(
    @NotBlank @Size(max = 150) String name,
    @Size(max = 100) String contactName,
    @Size(max = 20) String phone,
    @Email @Size(max = 100) String email
) {}
