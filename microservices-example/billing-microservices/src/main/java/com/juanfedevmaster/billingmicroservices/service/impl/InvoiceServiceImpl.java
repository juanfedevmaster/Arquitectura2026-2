package com.juanfedevmaster.billingmicroservices.service.impl;

import com.juanfedevmaster.billingmicroservices.domain.dto.InvoiceItemResponse;
import com.juanfedevmaster.billingmicroservices.domain.dto.InvoiceResponse;
import com.juanfedevmaster.billingmicroservices.domain.dto.SaleCreatedEvent;
import com.juanfedevmaster.billingmicroservices.domain.entity.Invoice;
import com.juanfedevmaster.billingmicroservices.domain.entity.InvoiceItem;
import com.juanfedevmaster.billingmicroservices.repository.InvoiceRepository;
import com.juanfedevmaster.billingmicroservices.service.InvoiceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

@Slf4j
@Service
@RequiredArgsConstructor
public class InvoiceServiceImpl implements InvoiceService {

    private final InvoiceRepository invoiceRepository;

    @Override
    @Transactional
    public InvoiceResponse processInvoice(SaleCreatedEvent event) {
        if (invoiceRepository.existsByEventId(event.getEventId())) {
            log.warn("Evento duplicado ignorado: eventId={}", event.getEventId());
            return getInvoiceBySaleId(event.getSaleId());
        }

        long count = invoiceRepository.count() + 1;
        String invoiceNumber = "INV-%06d".formatted(count);

        Invoice invoice = Invoice.builder()
                .invoiceNumber(invoiceNumber)
                .saleId(event.getSaleId())
                .customerId(event.getCustomerId())
                .eventId(event.getEventId())
                .totalAmount(event.getTotalAmount())
                .issuedAt(LocalDateTime.now())
                .status("ISSUED")
                .build();

        List<InvoiceItem> items = event.getItems().stream()
                .map(item -> InvoiceItem.builder()
                        .invoice(invoice)
                        .productId(item.getProductId())
                        .quantity(item.getQuantity())
                        .unitPrice(item.getUnitPrice())
                        .subtotal(item.getUnitPrice().multiply(
                                java.math.BigDecimal.valueOf(item.getQuantity())))
                        .build())
                .toList();

        invoice.setItems(items);
        Invoice saved = invoiceRepository.save(invoice);

        log.info("Factura emitida: {} para venta {}", saved.getInvoiceNumber(), saved.getSaleId());
        return toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public InvoiceResponse getInvoiceById(Long id) {
        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Factura no encontrada: " + id));
        return toResponse(invoice);
    }

    @Override
    @Transactional(readOnly = true)
    public InvoiceResponse getInvoiceBySaleId(String saleId) {
        Invoice invoice = invoiceRepository.findBySaleId(saleId)
                .orElseThrow(() -> new NoSuchElementException("Factura no encontrada para venta: " + saleId));
        return toResponse(invoice);
    }

    @Override
    @Transactional(readOnly = true)
    public List<InvoiceResponse> getAllInvoices() {
        return invoiceRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    private InvoiceResponse toResponse(Invoice invoice) {
        List<InvoiceItemResponse> itemResponses = invoice.getItems().stream()
                .map(item -> InvoiceItemResponse.builder()
                        .id(item.getId())
                        .productId(item.getProductId())
                        .quantity(item.getQuantity())
                        .unitPrice(item.getUnitPrice())
                        .subtotal(item.getSubtotal())
                        .build())
                .toList();

        return InvoiceResponse.builder()
                .id(invoice.getId())
                .invoiceNumber(invoice.getInvoiceNumber())
                .saleId(invoice.getSaleId())
                .customerId(invoice.getCustomerId())
                .totalAmount(invoice.getTotalAmount())
                .issuedAt(invoice.getIssuedAt())
                .status(invoice.getStatus())
                .items(itemResponses)
                .build();
    }
}
