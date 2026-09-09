-- Productos comestibles de ejemplo
-- Ejecutado al arranque por DataSeeder.java (CommandLineRunner)

INSERT INTO products (name, description, price, category, stock) VALUES
    ('Arroz Diana 500g',       'Arroz blanco de grano largo',         3200.00, 'COMESTIBLE', 100),
    ('Aceite Girasol 1L',      'Aceite vegetal de girasol puro',      8500.00, 'COMESTIBLE',  80),
    ('Leche Alquería 1L',      'Leche entera pasteurizada',           4200.00, 'COMESTIBLE', 150),
    ('Azúcar Manuelita 1kg',   'Azúcar blanco refinado',              4800.00, 'COMESTIBLE',  60),
    ('Harina de trigo 1kg',    'Harina todo uso',                     3600.00, 'COMESTIBLE',  90),
    ('Chocolate Corona 500g',  'Chocolate de mesa oscuro',            7100.00, 'COMESTIBLE',  40),
    ('Atún Van Camps 170g',    'Atún en agua, lata',                  5300.00, 'COMESTIBLE', 200),
    ('Sal Refisal 500g',       'Sal refinada yodada',                 1800.00, 'COMESTIBLE', 120),
    ('Frijol bolo rojo 500g',  'Frijol rojo seleccionado',            4500.00, 'COMESTIBLE',  70),
    ('Avena Quaker 500g',      'Avena en hojuelas instantánea',       6200.00, 'COMESTIBLE',  55);

-- Venta de ejemplo
INSERT INTO sales (sale_date, total_amount) VALUES ('2026-09-01 10:00:00', 21900.00);

INSERT INTO sale_items (sale_id, product_id, quantity, unit_price, subtotal) VALUES
    (1, 1, 3, 3200.00,  9600.00),
    (1, 3, 2, 4200.00,  8400.00),
    (1, 8, 2, 1800.00,  3600.00),
    (1, 2, 0, 8500.00,     0.00);
