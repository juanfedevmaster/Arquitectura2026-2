package com.juanfedevmaster.monolitemodular.modules.inventory.domain;

import com.juanfedevmaster.monolitemodular.modules.catalog.domain.Product;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "inventory_exits")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventoryExit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false)
    private Integer quantity;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ExitReason reason;

    @Column(length = 255)
    private String notes;

    @Column(name = "exit_date", nullable = false)
    private LocalDateTime exitDate;
}
