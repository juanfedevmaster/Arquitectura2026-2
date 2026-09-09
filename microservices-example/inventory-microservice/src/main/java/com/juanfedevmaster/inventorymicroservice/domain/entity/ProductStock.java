package com.juanfedevmaster.inventorymicroservice.domain.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "product_stock")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductStock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Referencia externa al ID del producto en sales_db (contextos de dominio separados)
    @Column(name = "product_id", nullable = false, unique = true, length = 50)
    private String productId;

    @Column(name = "product_name", length = 150)
    private String productName;

    @Column(name = "current_stock", nullable = false)
    private Integer currentStock;
}
