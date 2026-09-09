-- Esquema de referencia — Hibernate genera las tablas con ddl-auto=create

CREATE TABLE IF NOT EXISTS invoices (
    id             BIGSERIAL PRIMARY KEY,
    invoice_number VARCHAR(20)    NOT NULL UNIQUE,
    sale_id        VARCHAR(50)    NOT NULL,
    customer_id    VARCHAR(50)    NOT NULL,
    event_id       VARCHAR(36)    NOT NULL UNIQUE,  -- idempotencia: un evento → una factura
    total_amount   NUMERIC(12, 2) NOT NULL,
    issued_at      TIMESTAMP      NOT NULL DEFAULT NOW(),
    status         VARCHAR(20)    NOT NULL DEFAULT 'ISSUED'
);

CREATE TABLE IF NOT EXISTS invoice_items (
    id         BIGSERIAL PRIMARY KEY,
    invoice_id BIGINT         NOT NULL REFERENCES invoices(id),
    product_id VARCHAR(50)    NOT NULL,
    quantity   INTEGER        NOT NULL,
    unit_price NUMERIC(12, 2) NOT NULL,
    subtotal   NUMERIC(12, 2) NOT NULL
);
