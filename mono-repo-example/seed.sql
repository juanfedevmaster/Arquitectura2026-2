-- Tablas
CREATE TABLE IF NOT EXISTS users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL DEFAULT 'CUSTOMER',
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    is_active BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS categories (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) UNIQUE NOT NULL,
    description TEXT
);

CREATE TABLE IF NOT EXISTS products (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    category_id INT REFERENCES categories(id),
    name VARCHAR(150) NOT NULL,
    description TEXT,
    price NUMERIC(10,2) NOT NULL,
    stock INT NOT NULL DEFAULT 0,
    image_url VARCHAR(255),
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS carts (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID UNIQUE NOT NULL,
    updated_at TIMESTAMP DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS cart_items (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    cart_id UUID NOT NULL REFERENCES carts(id),
    product_id UUID NOT NULL,
    quantity INT NOT NULL,
    unit_price NUMERIC(10,2) NOT NULL
);

CREATE TABLE IF NOT EXISTS orders (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    total NUMERIC(10,2) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS order_items (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    order_id UUID NOT NULL REFERENCES orders(id),
    product_id UUID NOT NULL,
    quantity INT NOT NULL,
    unit_price NUMERIC(10,2) NOT NULL
);

-- Usuarios
-- password: Admin123! (bcrypt)
INSERT INTO users (username, email, password_hash, role) VALUES
    ('admin', 'admin@tienda.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'ADMIN'),
    ('juan', 'juan@tienda.com',   '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'CUSTOMER'),
    ('maria', 'maria@tienda.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'CUSTOMER')
ON CONFLICT (username) DO NOTHING;

-- Categorias
INSERT INTO categories (name, description) VALUES
    ('Lácteos',      'Leche, queso, yogurt y derivados'),
    ('Carnes',       'Res, cerdo, pollo y embutidos'),
    ('Frutas',       'Frutas frescas de temporada'),
    ('Verduras',     'Verduras y hortalizas frescas'),
    ('Panadería',    'Pan, galletas y productos de horno'),
    ('Bebidas',      'Jugos, gaseosas y agua'),
    ('Granos',       'Arroz, fríjol, lenteja y pasta'),
    ('Aseo',         'Productos de limpieza del hogar')
ON CONFLICT (name) DO NOTHING;

-- Productos
INSERT INTO products (category_id, name, description, price, stock) VALUES
    ((SELECT id FROM categories WHERE name='Lácteos'),   'Leche entera 1L',         'Leche entera pasteurizada',          3200,  80),
    ((SELECT id FROM categories WHERE name='Lácteos'),   'Queso campesino 500g',    'Queso fresco campesino',             8500,  40),
    ((SELECT id FROM categories WHERE name='Lácteos'),   'Yogurt natural 200g',     'Yogurt sin azúcar',                  2800,  60),
    ((SELECT id FROM categories WHERE name='Carnes'),    'Pechuga de pollo 1kg',    'Pollo fresco refrigerado',           12000, 30),
    ((SELECT id FROM categories WHERE name='Carnes'),    'Carne molida 500g',       'Carne de res molida especial',       9500,  25),
    ((SELECT id FROM categories WHERE name='Frutas'),    'Banano x6',               'Bananos maduros',                    2500,  100),
    ((SELECT id FROM categories WHERE name='Frutas'),    'Manzana roja x4',         'Manzanas importadas',                4800,  70),
    ((SELECT id FROM categories WHERE name='Verduras'),  'Tomate chonto 500g',      'Tomates frescos',                    2200,  90),
    ((SELECT id FROM categories WHERE name='Verduras'),  'Cebolla cabezona 1kg',    'Cebolla blanca',                     3000,  85),
    ((SELECT id FROM categories WHERE name='Panadería'), 'Pan tajado integral',     'Pan integral en tajadas',            4500,  50),
    ((SELECT id FROM categories WHERE name='Bebidas'),   'Agua 600ml',              'Agua mineral sin gas',               1500,  200),
    ((SELECT id FROM categories WHERE name='Bebidas'),   'Jugo de naranja 1L',      'Jugo natural de naranja',            5500,  45),
    ((SELECT id FROM categories WHERE name='Granos'),    'Arroz Diana 1kg',         'Arroz blanco de primera',            3800,  120),
    ((SELECT id FROM categories WHERE name='Granos'),    'Fríjol bola roja 500g',   'Fríjol seco seleccionado',           4200,  75),
    ((SELECT id FROM categories WHERE name='Aseo'),      'Detergente Ariel 1kg',    'Detergente en polvo multiacción',    12500, 35),
    ((SELECT id FROM categories WHERE name='Aseo'),      'Jabón de baño x3',        'Jabón antibacterial en barra',       7800,  60);
