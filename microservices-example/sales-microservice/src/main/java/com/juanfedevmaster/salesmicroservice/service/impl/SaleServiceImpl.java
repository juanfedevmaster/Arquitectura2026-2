package com.juanfedevmaster.salesmicroservice.service.impl;

import com.juanfedevmaster.salesmicroservice.domain.dto.*;
import com.juanfedevmaster.salesmicroservice.domain.entity.Product;
import com.juanfedevmaster.salesmicroservice.domain.entity.Sale;
import com.juanfedevmaster.salesmicroservice.domain.entity.SaleItem;
import com.juanfedevmaster.salesmicroservice.repository.ProductRepository;
import com.juanfedevmaster.salesmicroservice.repository.SaleRepository;
import com.juanfedevmaster.salesmicroservice.service.SaleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class SaleServiceImpl implements SaleService {

    private final SaleRepository saleRepository;
    private final ProductRepository productRepository;

    @Override
    @Transactional
    public SaleResponse createSale(CreateSaleRequest request) {
        List<SaleItem> saleItems = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;

        Sale sale = Sale.builder()
                .saleDate(LocalDateTime.now())
                .totalAmount(BigDecimal.ZERO)
                .build();

        for (SaleItemRequest itemReq : request.getItems()) {
            Product product = productRepository.findById(itemReq.getProductId())
                    .orElseThrow(() -> new NoSuchElementException(
                            "Producto no encontrado: " + itemReq.getProductId()));

            if (product.getStock() < itemReq.getQuantity()) {
                throw new IllegalArgumentException(
                        "Stock insuficiente para: " + product.getName());
            }

            BigDecimal subtotal = product.getPrice()
                    .multiply(BigDecimal.valueOf(itemReq.getQuantity()));

            SaleItem item = SaleItem.builder()
                    .sale(sale)
                    .product(product)
                    .quantity(itemReq.getQuantity())
                    .unitPrice(product.getPrice())
                    .subtotal(subtotal)
                    .build();

            saleItems.add(item);
            total = total.add(subtotal);

            product.setStock(product.getStock() - itemReq.getQuantity());
            productRepository.save(product);
        }

        sale.setItems(saleItems);
        sale.setTotalAmount(total);
        Sale saved = saleRepository.save(sale);

        return toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public SaleResponse getSaleById(Long id) {
        Sale sale = saleRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Venta no encontrada: " + id));
        return toResponse(sale);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SaleResponse> getAllSales() {
        return saleRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponse> getAvailableProducts() {
        return productRepository.findByStockGreaterThan(0).stream()
                .map(this::toProductResponse)
                .toList();
    }

    private SaleResponse toResponse(Sale sale) {
        List<SaleItemResponse> itemResponses = sale.getItems().stream()
                .map(item -> SaleItemResponse.builder()
                        .id(item.getId())
                        .productId(item.getProduct().getId())
                        .productName(item.getProduct().getName())
                        .quantity(item.getQuantity())
                        .unitPrice(item.getUnitPrice())
                        .subtotal(item.getSubtotal())
                        .build())
                .toList();

        return SaleResponse.builder()
                .id(sale.getId())
                .saleDate(sale.getSaleDate())
                .totalAmount(sale.getTotalAmount())
                .items(itemResponses)
                .build();
    }

    private ProductResponse toProductResponse(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .category(product.getCategory())
                .stock(product.getStock())
                .build();
    }
}
