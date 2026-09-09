-- Stock inicial de productos comestibles (cada ID referencia al producto en sales_db)
-- En producción este catálogo se sincroniza al dar de alta productos en sales-microservices.

INSERT INTO product_stock (product_id, product_name, current_stock) VALUES
    ('1',  'Arroz Diana 500g',       100),
    ('2',  'Aceite Girasol 1L',       80),
    ('3',  'Leche Alquería 1L',      150),
    ('4',  'Azúcar Manuelita 1kg',    60),
    ('5',  'Harina de trigo 1kg',     90),
    ('6',  'Chocolate Corona 500g',   40),
    ('7',  'Atún Van Camps 170g',    200),
    ('8',  'Sal Refisal 500g',       120),
    ('9',  'Frijol bolo rojo 500g',   70),
    ('10', 'Avena Quaker 500g',       55);

-- Movimiento de ejemplo (una venta procesada)
INSERT INTO stock_movements (product_id, sale_id, event_id, quantity, moved_at, movement_type) VALUES
    ('1', 'S-00001', 'a1b2c3d4-0001-0001-0001-000000000001', 3, '2026-09-01 10:05:00', 'SALE_REDUCTION'),
    ('3', 'S-00001', 'a1b2c3d4-0001-0001-0001-000000000001', 2, '2026-09-01 10:05:00', 'SALE_REDUCTION'),
    ('8', 'S-00001', 'a1b2c3d4-0001-0001-0001-000000000001', 2, '2026-09-01 10:05:00', 'SALE_REDUCTION');
