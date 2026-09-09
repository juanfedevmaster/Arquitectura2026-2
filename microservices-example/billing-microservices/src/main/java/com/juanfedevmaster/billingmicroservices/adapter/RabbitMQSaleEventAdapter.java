package com.juanfedevmaster.billingmicroservices.adapter;

import com.juanfedevmaster.billingmicroservices.domain.dto.SaleCreatedEvent;
import com.juanfedevmaster.billingmicroservices.service.InvoiceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * Adaptador de entrada que conecta RabbitMQ con el puerto SaleEventPort.
 *
 * Responsabilidades:
 *  1. Escuchar la cola billing.sale-created.queue.
 *  2. Deserializar el mensaje JSON al contrato SaleCreatedEvent.
 *  3. Delegar al InvoiceService sin exponer detalles de mensajería al dominio.
 *
 * Si el procesamiento lanza excepción, Spring AMQP reencola el mensaje
 * (at-least-once delivery). La idempotencia se garantiza en InvoiceService
 * verificando que el eventId no haya sido procesado antes.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RabbitMQSaleEventAdapter implements SaleEventPort {

    private final InvoiceService invoiceService;

    @RabbitListener(queues = "${rabbitmq.queue.billing}")
    public void receive(SaleCreatedEvent event) {
        log.info("Evento recibido desde RabbitMQ: eventId={}, saleId={}",
                event.getEventId(), event.getSaleId());
        onSaleCreated(event);
    }

    @Override
    public void onSaleCreated(SaleCreatedEvent event) {
        invoiceService.processInvoice(event);
    }
}
