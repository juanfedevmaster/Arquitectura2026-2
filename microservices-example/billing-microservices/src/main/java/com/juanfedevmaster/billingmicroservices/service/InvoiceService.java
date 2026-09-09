package com.juanfedevmaster.billingmicroservices.service;

import com.juanfedevmaster.billingmicroservices.domain.dto.InvoiceResponse;
import com.juanfedevmaster.billingmicroservices.domain.dto.SaleCreatedEvent;

import java.util.List;

public interface InvoiceService {

    InvoiceResponse processInvoice(SaleCreatedEvent event);

    InvoiceResponse getInvoiceById(Long id);

    InvoiceResponse getInvoiceBySaleId(String saleId);

    List<InvoiceResponse> getAllInvoices();
}
