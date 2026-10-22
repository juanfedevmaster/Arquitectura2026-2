package com.tienda.orders.controller;

import com.tienda.orders.dto.OrderResponse;
import com.tienda.orders.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<OrderResponse> confirmOrder(Authentication auth) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(orderService.confirmOrder(UUID.fromString(auth.getName())));
    }

    @GetMapping
    public ResponseEntity<List<OrderResponse>> myOrders(Authentication auth) {
        return ResponseEntity.ok(orderService.getMyOrders(UUID.fromString(auth.getName())));
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponse> getOrder(Authentication auth,
                                                    @PathVariable UUID orderId) {
        return ResponseEntity.ok(orderService.getOrder(orderId, UUID.fromString(auth.getName())));
    }
}
