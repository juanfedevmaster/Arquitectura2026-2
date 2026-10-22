# Tienda de Abarrotes — Mono Repo

3 WebAPIs independientes en Spring Boot, compartiendo la misma base de datos PostgreSQL (`tienda_db`), con una UI en HTML/CSS/Bootstrap/JS que consume los 3 servicios.

## Requisitos

- Java 17+
- Maven 3.8+
- PostgreSQL corriendo en `localhost:5432` con base de datos `tienda_db`

## Servicios

| Servicio | Puerto | Responsabilidad |
|---|---|---|
| auth-service | 8081 | Registro, login y refresh de JWT |
| catalog-service | 8082 | Productos y categorías |
| orders-service | 8083 | Carrito y pedidos |

## Comandos para correr cada servicio

Abrir **3 terminales** desde la raíz del proyecto, una por servicio:

### Terminal 1 — auth-service
```bash
cd auth-service
mvn spring-boot:run
```

### Terminal 2 — catalog-service
```bash
cd catalog-service
mvn spring-boot:run
```

### Terminal 3 — orders-service
```bash
cd orders-service
mvn spring-boot:run
```

## UI Web

Con los 3 servicios corriendo, abrir la UI en el navegador:

### macOS
```bash
open UI/index.html
```

### Linux
```bash
xdg-open UI/index.html
```

### Windows
```bash
start UI/index.html
```

O arrastrar el archivo `UI/index.html` directamente al navegador.

> **Nota:** la UI hace peticiones a `localhost`. No requiere servidor web, se puede abrir como archivo local (`file://`).

## Flujo de uso

1. Registrar usuario:
```
POST http://localhost:8081/auth/register
{ "username": "juan", "email": "juan@email.com", "password": "123456" }
```

2. Login:
```
POST http://localhost:8081/auth/login
{ "username": "juan", "password": "123456" }
```

3. Usar el `accessToken` retornado en el header de las siguientes peticiones:
```
Authorization: Bearer <accessToken>
```

4. Consultar productos (no requiere token):
```
GET http://localhost:8082/products
```

5. Agregar al carrito (requiere token):
```
POST http://localhost:8083/cart/items
{ "productId": "<uuid>", "quantity": 2, "unitPrice": 5.99 }
```

6. Confirmar pedido (requiere token):
```
POST http://localhost:8083/orders
```
