package com.juanfedevmaster.billingmicroservices.repository;

import com.juanfedevmaster.billingmicroservices.domain.entity.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface InvoiceRepository extends JpaRepository<Invoice, Long> {

    Optional<Invoice> findBySaleId(String saleId);

    boolean existsByEventId(String eventId);

    List<Invoice> findByCustomerId(String customerId);

    List<Invoice> findByStatus(String status);
}
