package com.juanfedevmaster.inventorymicroservice.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
    name = "stock_movements",
    uniqueConstraints = @UniqueConstraint(columnNames = {"event_id", "product_id"})
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockMovement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "product_id", nullable = false, length = 50)
    private String productId;

    @Column(name = "sale_id", nullable = false, length = 50)
    private String saleId;

    // Clave de idempotencia: un (eventId, productId) → un solo movimiento
    @Column(name = "event_id", nullable = false, length = 36)
    private String eventId;

    @Column(nullable = false)
    private Integer quantity;

    @Column(name = "moved_at", nullable = false)
    private LocalDateTime movedAt;

    @Column(name = "movement_type", nullable = false, length = 20)
    private String movementType;
}
