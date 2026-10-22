package com.tienda.orders.controller;

import com.tienda.orders.dto.CartItemRequest;
import com.tienda.orders.dto.CartResponse;
import com.tienda.orders.service.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @GetMapping
    public ResponseEntity<CartResponse> getCart(Authentication auth) {
        return ResponseEntity.ok(cartService.getCart(UUID.fromString(auth.getName())));
    }

    @PostMapping("/items")
    public ResponseEntity<CartResponse> addItem(Authentication auth,
                                                 @Valid @RequestBody CartItemRequest request) {
        return ResponseEntity.ok(cartService.addItem(UUID.fromString(auth.getName()), request));
    }

    @DeleteMapping("/items/{itemId}")
    public ResponseEntity<Void> removeItem(Authentication auth, @PathVariable UUID itemId) {
        cartService.removeItem(UUID.fromString(auth.getName()), itemId);
        return ResponseEntity.noContent().build();
    }
}
