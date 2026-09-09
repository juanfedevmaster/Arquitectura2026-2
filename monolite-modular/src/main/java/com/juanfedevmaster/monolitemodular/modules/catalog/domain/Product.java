package com.juanfedevmaster.monolitemodular.modules.catalog.domain;

import com.juanfedevmaster.monolitemodular.shared.exception.BusinessException;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String sku;

    @Column(nullable = false, length = 150)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Enumerated(EnumType.STRING)
    @Column(name = "unit_measure", nullable = false, length = 20)
    private UnitMeasure unitMeasure;

    @Column(name = "purchase_price", nullable = false, precision = 12, scale = 2)
    private BigDecimal purchasePrice;

    @Column(name = "sale_price", nullable = false, precision = 12, scale = 2)
    private BigDecimal salePrice;

    @Column(name = "min_stock", nullable = false)
    private Integer minStock;

    @Column(name = "stock_quantity", nullable = false)
    @Builder.Default
    private Integer stockQuantity = 0;

    @Column(nullable = false)
    @Builder.Default
    private boolean active = true;

    @Version
    private Long version;

    public boolean isBelowMinStock() {
        return stockQuantity <= minStock;
    }

    public void addStock(int quantity) {
        this.stockQuantity += quantity;
    }

    public void deductStock(int quantity) {
        if (this.stockQuantity < quantity) {
            throw new BusinessException(
                "Insufficient stock for '" + name + "'. Available: " + stockQuantity + ", requested: " + quantity
            );
        }
        this.stockQuantity -= quantity;
    }
}
