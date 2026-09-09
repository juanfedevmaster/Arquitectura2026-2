package com.juanfedevmaster.monolitemodular.modules.sales.application;

import com.juanfedevmaster.monolitemodular.modules.catalog.application.ProductService;
import com.juanfedevmaster.monolitemodular.modules.catalog.domain.Product;
import com.juanfedevmaster.monolitemodular.modules.catalog.infrastructure.ProductRepository;
import com.juanfedevmaster.monolitemodular.modules.sales.api.dto.*;
import com.juanfedevmaster.monolitemodular.modules.sales.domain.*;
import com.juanfedevmaster.monolitemodular.modules.sales.infrastructure.SaleRepository;
import com.juanfedevmaster.monolitemodular.shared.exception.BusinessException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SaleService {

    private final SaleRepository saleRepository;
    private final ProductService productService;
    private final ProductRepository productRepository;

    @Transactional
    public SaleResponse createSale(SaleRequest request) {
        Sale sale = Sale.builder()
            .saleDate(LocalDateTime.now())
            .paymentMethod(request.paymentMethod())
            .total(BigDecimal.ZERO)
            .status(SaleStatus.ACTIVE)
            .build();

        BigDecimal total = BigDecimal.ZERO;
        List<SaleItem> items = new ArrayList<>();

        for (SaleItemRequest itemRequest : request.items()) {
            Product product = productService.getOrThrow(itemRequest.productId());
            if (!product.isActive()) {
                throw new BusinessException("Product is inactive: " + product.getName());
            }
            product.deductStock(itemRequest.quantity());
            productRepository.save(product);

            BigDecimal subtotal = product.getSalePrice().multiply(BigDecimal.valueOf(itemRequest.quantity()));
            total = total.add(subtotal);

            SaleItem item = SaleItem.builder()
                .sale(sale)
                .product(product)
                .quantity(itemRequest.quantity())
                .unitPrice(product.getSalePrice())
                .subtotal(subtotal)
                .build();
            items.add(item);
        }

        sale.setTotal(total);
        sale.getItems().addAll(items);
        return toResponse(saleRepository.save(sale));
    }

    @Transactional(readOnly = true)
    public SaleResponse findById(Long id) {
        return toResponse(getOrThrow(id));
    }

    @Transactional
    public SaleResponse voidSale(Long id) {
        Sale sale = getOrThrow(id);
        if (sale.getStatus() == SaleStatus.VOID) {
            throw new BusinessException("Sale is already voided: " + id);
        }
        for (SaleItem item : sale.getItems()) {
            Product product = item.getProduct();
            product.addStock(item.getQuantity());
            productRepository.save(product);
        }
        sale.setStatus(SaleStatus.VOID);
        return toResponse(saleRepository.save(sale));
    }

    private Sale getOrThrow(Long id) {
        return saleRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Sale not found: " + id));
    }

    private SaleResponse toResponse(Sale sale) {
        List<SaleItemResponse> itemResponses = sale.getItems().stream()
            .map(item -> new SaleItemResponse(
                item.getProduct().getId(),
                item.getProduct().getName(),
                item.getProduct().getSku(),
                item.getQuantity(),
                item.getUnitPrice(),
                item.getSubtotal()
            )).toList();
        return new SaleResponse(
            sale.getId(), sale.getSaleDate(), sale.getPaymentMethod(),
            sale.getTotal(), sale.getStatus(), itemResponses
        );
    }
}
