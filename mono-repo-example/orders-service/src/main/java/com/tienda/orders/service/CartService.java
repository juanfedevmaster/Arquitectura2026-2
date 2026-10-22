package com.tienda.orders.service;

import com.tienda.orders.dto.CartItemRequest;
import com.tienda.orders.dto.CartResponse;
import com.tienda.orders.model.Cart;
import com.tienda.orders.model.CartItem;
import com.tienda.orders.repository.CartItemRepository;
import com.tienda.orders.repository.CartRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;

    public CartResponse getCart(UUID userId) {
        Cart cart = getOrCreateCart(userId);
        return new CartResponse(cart);
    }

    @Transactional
    public CartResponse addItem(UUID userId, CartItemRequest request) {
        Cart cart = getOrCreateCart(userId);
        CartItem item = new CartItem();
        item.setCart(cart);
        item.setProductId(request.getProductId());
        item.setQuantity(request.getQuantity());
        item.setUnitPrice(request.getUnitPrice());
        cart.getItems().add(item);
        return new CartResponse(cartRepository.save(cart));
    }

    @Transactional
    public void removeItem(UUID userId, UUID itemId) {
        Cart cart = getOrCreateCart(userId);
        cart.getItems().removeIf(item -> item.getId().equals(itemId));
        cartRepository.save(cart);
    }

    @Transactional
    public void clearCart(UUID userId) {
        Cart cart = getOrCreateCart(userId);
        cart.getItems().clear();
        cartRepository.save(cart);
    }

    private Cart getOrCreateCart(UUID userId) {
        return cartRepository.findByUserId(userId)
                .orElseGet(() -> {
                    Cart cart = new Cart();
                    cart.setUserId(userId);
                    return cartRepository.save(cart);
                });
    }
}
