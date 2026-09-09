-- Esquema de referencia — Hibernate genera las tablas con ddl-auto=create

CREATE TABLE IF NOT EXISTS products (
    id          BIGSERIAL PRIMARY KEY,
    name        VARCHAR(150) NOT NULL,
    description VARCHAR(300),
    price       NUMERIC(12, 2) NOT NULL,
    category    VARCHAR(50)  NOT NULL DEFAULT 'COMESTIBLE',
    stock       INTEGER      NOT NULL DEFAULT 0
);

CREATE TABLE IF NOT EXISTS sales (
    id           BIGSERIAL PRIMARY KEY,
    sale_date    TIMESTAMP    NOT NULL DEFAULT NOW(),
    total_amount NUMERIC(12, 2) NOT NULL
);

CREATE TABLE IF NOT EXISTS sale_items (
    id         BIGSERIAL PRIMARY KEY,
    sale_id    BIGINT        NOT NULL REFERENCES sales(id),
    product_id BIGINT        NOT NULL REFERENCES products(id),
    quantity   INTEGER       NOT NULL,
    unit_price NUMERIC(12, 2) NOT NULL,
    subtotal   NUMERIC(12, 2) NOT NULL
);
