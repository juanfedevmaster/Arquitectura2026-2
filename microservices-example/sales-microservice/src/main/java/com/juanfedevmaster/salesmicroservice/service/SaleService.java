package com.juanfedevmaster.salesmicroservice.service;

import com.juanfedevmaster.salesmicroservice.domain.dto.CreateSaleRequest;
import com.juanfedevmaster.salesmicroservice.domain.dto.ProductResponse;
import com.juanfedevmaster.salesmicroservice.domain.dto.SaleResponse;

import java.util.List;

public interface SaleService {

    SaleResponse createSale(CreateSaleRequest request);

    SaleResponse getSaleById(Long id);

    List<SaleResponse> getAllSales();

    List<ProductResponse> getAvailableProducts();
}
