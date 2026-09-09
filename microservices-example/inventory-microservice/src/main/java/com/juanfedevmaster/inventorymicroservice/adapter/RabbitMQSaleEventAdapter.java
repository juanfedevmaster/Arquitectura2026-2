package com.juanfedevmaster.inventorymicroservice.adapter;

import com.juanfedevmaster.inventorymicroservice.domain.dto.SaleCreatedEvent;
import com.juanfedevmaster.inventorymicroservice.service.StockService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * Adaptador de entrada que conecta RabbitMQ con el puerto SaleEventPort.
 *
 * Responsabilidades:
 *  1. Escuchar la cola inventory.sale-created.queue.
 *  2. Deserializar el mensaje JSON al contrato SaleCreatedEvent.
 *  3. Delegar al StockService sin exponer detalles de mensajería al dominio.
 *
 * Si el procesamiento lanza excepción, Spring AMQP reencola el mensaje
 * (at-least-once delivery). La idempotencia se garantiza en StockService
 * verificando que el par (eventId, productId) no haya sido procesado antes.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RabbitMQSaleEventAdapter implements SaleEventPort {

    private final StockService stockService;

    @RabbitListener(queues = "${rabbitmq.queue.inventory}")
    public void receive(SaleCreatedEvent event) {
        log.info("Evento recibido desde RabbitMQ: eventId={}, saleId={}",
                event.getEventId(), event.getSaleId());
        onSaleCreated(event);
    }

    @Override
    public void onSaleCreated(SaleCreatedEvent event) {
        stockService.processStockReduction(event);
    }
}
