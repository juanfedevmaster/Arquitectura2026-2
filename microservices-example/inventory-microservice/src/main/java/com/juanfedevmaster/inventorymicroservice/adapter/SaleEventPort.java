package com.juanfedevmaster.inventorymicroservice.adapter;

import com.juanfedevmaster.inventorymicroservice.domain.dto.SaleCreatedEvent;

/**
 * Puerto de entrada (driving port) para recibir eventos de venta.
 *
 * Desacopla el dominio de inventario del mecanismo de transporte (RabbitMQ, Kafka, HTTP, etc.).
 * Cualquier adaptador de mensajería que quiera notificar una venta debe implementar este contrato.
 */
public interface SaleEventPort {

    void onSaleCreated(SaleCreatedEvent event);
}
