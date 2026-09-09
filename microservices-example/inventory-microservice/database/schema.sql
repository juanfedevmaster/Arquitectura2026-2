-- Esquema de referencia — Hibernate genera las tablas con ddl-auto=create

-- Stock actual por producto (contexto separado de sales_db: productId es referencia externa)
CREATE TABLE IF NOT EXISTS product_stock (
    id          BIGSERIAL PRIMARY KEY,
    product_id  VARCHAR(50) NOT NULL UNIQUE,  -- referencia al ID en sales_db
    product_name VARCHAR(150),
    current_stock INTEGER NOT NULL DEFAULT 0
);

-- Auditoría de movimientos de stock
CREATE TABLE IF NOT EXISTS stock_movements (
    id           BIGSERIAL PRIMARY KEY,
    product_id   VARCHAR(50)  NOT NULL,
    sale_id      VARCHAR(50)  NOT NULL,
    event_id     VARCHAR(36)  NOT NULL,        -- idempotencia: un eventId → un movimiento
    quantity     INTEGER      NOT NULL,
    moved_at     TIMESTAMP    NOT NULL DEFAULT NOW(),
    movement_type VARCHAR(20) NOT NULL DEFAULT 'SALE_REDUCTION'
);

CREATE UNIQUE INDEX IF NOT EXISTS uq_stock_movements_event_product
    ON stock_movements (event_id, product_id);
