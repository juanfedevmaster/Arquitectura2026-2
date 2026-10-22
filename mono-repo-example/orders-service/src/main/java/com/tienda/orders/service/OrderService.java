package com.tienda.orders.service;

import com.tienda.orders.dto.OrderResponse;
import com.tienda.orders.model.Cart;
import com.tienda.orders.model.Order;
import com.tienda.orders.model.OrderItem;
import com.tienda.orders.repository.CartRepository;
import com.tienda.orders.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final CartService cartService;

    @Transactional
    public OrderResponse confirmOrder(UUID userId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Carrito vacío"));

        if (cart.getItems().isEmpty()) {
            throw new RuntimeException("El carrito no tiene productos");
        }

        Order order = new Order();
        order.setUserId(userId);

        BigDecimal total = BigDecimal.ZERO;
        for (var cartItem : cart.getItems()) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProductId(cartItem.getProductId());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setUnitPrice(cartItem.getUnitPrice());
            order.getItems().add(orderItem);
            total = total.add(cartItem.getUnitPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity())));
        }

        order.setTotal(total);
        Order saved = orderRepository.save(order);

        cartService.clearCart(userId);

        return new OrderResponse(saved);
    }

    public OrderResponse getOrder(UUID orderId, UUID userId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));
        if (!order.getUserId().equals(userId)) {
            throw new RuntimeException("Acceso denegado");
        }
        return new OrderResponse(order);
    }

    public List<OrderResponse> getMyOrders(UUID userId) {
        return orderRepository.findByUserId(userId)
                .stream().map(OrderResponse::new).toList();
    }
}
