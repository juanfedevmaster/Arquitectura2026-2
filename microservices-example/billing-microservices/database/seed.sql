-- Facturas de ejemplo (generadas a partir del SaleCreatedEvent)
-- En producción estos registros los crea el RabbitMQSaleEventAdapter al consumir la cola.

INSERT INTO invoices (invoice_number, sale_id, customer_id, event_id, total_amount, issued_at, status)
VALUES
    ('INV-000001', 'S-00001', 'C-1001',
     'a1b2c3d4-0001-0001-0001-000000000001', 90000.00, '2026-09-01 10:05:00', 'ISSUED'),
    ('INV-000002', 'S-00002', 'C-1002',
     'a1b2c3d4-0002-0002-0002-000000000002', 45000.00, '2026-09-02 14:22:00', 'ISSUED'),
    ('INV-000003', 'S-00003', 'C-1003',
     'a1b2c3d4-0003-0003-0003-000000000003', 135000.00, '2026-09-03 09:10:00', 'CANCELLED');

INSERT INTO invoice_items (invoice_id, product_id, quantity, unit_price, subtotal)
VALUES
    (1, 'P-9001', 2, 45000.00, 90000.00),
    (2, 'P-9002', 1, 45000.00, 45000.00),
    (3, 'P-9001', 3, 45000.00, 135000.00);
