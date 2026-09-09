package com.juanfedevmaster.billingmicroservices.adapter;

import com.juanfedevmaster.billingmicroservices.domain.dto.SaleCreatedEvent;

/**
 * Puerto de entrada (driving port) para recibir eventos de venta.
 *
 * Define el contrato que cualquier adaptador de mensajería debe implementar
 * para notificar al dominio de facturación que ocurrió una venta.
 * Desacopla el dominio del mecanismo de transporte (RabbitMQ, Kafka, HTTP, etc.).
 */
public interface SaleEventPort {

    void onSaleCreated(SaleCreatedEvent event);
}
