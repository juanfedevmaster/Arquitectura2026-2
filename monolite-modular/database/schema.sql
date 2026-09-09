-- ============================================================
-- Tienda Abarrotes — Database Schema
-- PostgreSQL
-- ============================================================

CREATE TABLE IF NOT EXISTS categories (
    id          BIGSERIAL    PRIMARY KEY,
    name        VARCHAR(100) NOT NULL UNIQUE,
    description VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS suppliers (
    id           BIGSERIAL    PRIMARY KEY,
    name         VARCHAR(150) NOT NULL,
    contact_name VARCHAR(100),
    phone        VARCHAR(20),
    email        VARCHAR(100),
    active       BOOLEAN      NOT NULL DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS products (
    id             BIGSERIAL      PRIMARY KEY,
    sku            VARCHAR(50)    NOT NULL UNIQUE,
    name           VARCHAR(150)   NOT NULL,
    category_id    BIGINT         NOT NULL REFERENCES categories(id),
    unit_measure   VARCHAR(20)    NOT NULL,
    purchase_price NUMERIC(12,2)  NOT NULL,
    sale_price     NUMERIC(12,2)  NOT NULL,
    min_stock      INTEGER        NOT NULL DEFAULT 0,
    stock_quantity INTEGER        NOT NULL DEFAULT 0,
    active         BOOLEAN        NOT NULL DEFAULT TRUE,
    version        BIGINT         NOT NULL DEFAULT 0
);

CREATE TABLE IF NOT EXISTS inventory_entries (
    id          BIGSERIAL     PRIMARY KEY,
    product_id  BIGINT        NOT NULL REFERENCES products(id),
    quantity    INTEGER       NOT NULL,
    cost        NUMERIC(12,2) NOT NULL,
    entry_date  TIMESTAMP     NOT NULL,
    supplier_id BIGINT        REFERENCES suppliers(id)
);

CREATE TABLE IF NOT EXISTS inventory_exits (
    id         BIGSERIAL    PRIMARY KEY,
    product_id BIGINT       NOT NULL REFERENCES products(id),
    quantity   INTEGER      NOT NULL,
    reason     VARCHAR(20)  NOT NULL,
    notes      VARCHAR(255),
    exit_date  TIMESTAMP    NOT NULL
);

CREATE TABLE IF NOT EXISTS sales (
    id             BIGSERIAL     PRIMARY KEY,
    sale_date      TIMESTAMP     NOT NULL,
    payment_method VARCHAR(20)   NOT NULL,
    total          NUMERIC(12,2) NOT NULL,
    status         VARCHAR(10)   NOT NULL DEFAULT 'ACTIVE'
);

CREATE TABLE IF NOT EXISTS sale_items (
    id         BIGSERIAL     PRIMARY KEY,
    sale_id    BIGINT        NOT NULL REFERENCES sales(id),
    product_id BIGINT        NOT NULL REFERENCES products(id),
    quantity   INTEGER       NOT NULL,
    unit_price NUMERIC(12,2) NOT NULL,
    subtotal   NUMERIC(12,2) NOT NULL
);

-- Indexes
CREATE INDEX IF NOT EXISTS idx_products_category        ON products(category_id);
CREATE INDEX IF NOT EXISTS idx_products_sku             ON products(sku);
CREATE INDEX IF NOT EXISTS idx_products_active          ON products(active);
CREATE INDEX IF NOT EXISTS idx_sales_date               ON sales(sale_date);
CREATE INDEX IF NOT EXISTS idx_sales_status             ON sales(status);
CREATE INDEX IF NOT EXISTS idx_sale_items_sale          ON sale_items(sale_id);
CREATE INDEX IF NOT EXISTS idx_sale_items_product       ON sale_items(product_id);
CREATE INDEX IF NOT EXISTS idx_inventory_entries_product ON inventory_entries(product_id);
CREATE INDEX IF NOT EXISTS idx_inventory_exits_product  ON inventory_exits(product_id);
