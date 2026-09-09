# Enunciado de Proyecto — Sistema de Venta e Inventario para Tienda de Abarrotes

## Contexto

Doña Rosa tiene una tienda de abarrotes de barrio. Hasta ahora lleva las ventas y el inventario en cuadernos y en la memoria: no sabe con certeza cuánto stock le queda de cada producto, se le vencen productos sin darse cuenta, y calcular las ventas del día le toma horas. Necesita un sistema que le permita **vender** y **controlar el inventario** de forma simple, rápida y confiable.

## Objetivo general

Diseñar e implementar un sistema de información que permita gestionar el catálogo de productos, controlar el inventario (entradas y salidas de mercancía) y registrar las ventas de una tienda de abarrotes, generando la información necesaria para la toma de decisiones del negocio.

**Nota importante:** este sistema **no requiere autenticación** en esta etapa. Se asume un único punto de acceso (la caja) sin control de usuarios ni contraseñas. La autenticación podrá agregarse en una fase posterior del proyecto; por ahora el esfuerzo de diseño debe concentrarse en el dominio del negocio (productos, inventario, ventas), no en seguridad.

## Alcance funcional

### 1. Gestión de productos
- Registrar, editar y eliminar (dar de baja) productos.
- Cada producto tiene: código/SKU, nombre, categoría, unidad de medida (unidad, kg, litro, paquete), precio de compra, precio de venta y stock mínimo.
- Agrupar productos por categorías (aseo, granos, lácteos, bebidas, etc.).

### 2. Gestión de inventario
- Registrar entradas de mercancía (compras a proveedores): producto, cantidad, costo, fecha, proveedor.
- Descontar automáticamente el inventario cuando se realiza una venta.
- Registrar salidas manuales por pérdida, vencimiento o daño, con motivo.
- Consultar el stock actual de cualquier producto en tiempo real.
- Alertar (visual o en un listado) cuando un producto llega a su stock mínimo.

### 3. Registro de ventas (punto de venta)
- Armar una venta agregando productos por código o búsqueda por nombre, con cantidad.
- Calcular automáticamente el subtotal, y el total a pagar.
- Registrar el método de pago (efectivo, transferencia) — no se requiere integración real con pasarelas de pago, basta con el registro.
- Generar un comprobante/recibo simple de la venta (en pantalla o imprimible en PDF).
- Permitir anular una venta reciente, devolviendo el stock al inventario.

### 4. Reportes
- Ventas por período (día, semana, mes): total vendido y número de transacciones.
- Productos más vendidos.
- Productos con stock bajo o próximos a agotarse.
- Utilidad estimada (precio de venta − precio de compra) por período.

### 5. Proveedores (opcional / de valor agregado)
- Registrar proveedores y asociarlos a las entradas de inventario.

## Actores del sistema

- **Cajero/a**: registra ventas, consulta productos y stock.
- **Administrador/a (Doña Rosa)**: gestiona productos, categorías, proveedores, revisa reportes y controla entradas/salidas de inventario.

*(Estos son roles funcionales para efectos de diseño de casos de uso y procesos — no implican login/autenticación en esta fase.)*

## Requisitos no funcionales

- El sistema debe responder a las operaciones de venta en menos de 2 segundos (uso en caja, con clientes esperando).
- La información de inventario y ventas debe persistir en una base de datos relacional.
- La interfaz debe ser simple y usable por personal sin formación técnica.
- El sistema debe funcionar en un único punto de venta (no se exige multi-sucursal en esta fase).

## Restricciones tecnológicas sugeridas

- **Frontend:** React
- **Backend:** Spring Boot (API REST)
- **Base de datos:** relacional (PostgreSQL/MySQL)

## Entregables esperados

1. Documento de análisis: historias de usuario o casos de uso, diagrama de procesos (BPMN) del flujo de venta e inventario.
2. Diagrama de clases del dominio, aplicando principios SOLID.
3. Diagrama de despliegue y de componentes de la solución.
4. Repositorio en GitHub con el código fuente y un README de instalación/ejecución.
5. Aplicación funcional que cubra, como mínimo, gestión de productos, inventario y ventas (los reportes y proveedores pueden entregarse como incremento posterior).

## Criterio guía

Prioricen que el flujo de **venta descuente inventario correctamente** y que el **stock nunca quede inconsistente** (por ejemplo, vender más de lo que hay disponible) — ese es el núcleo del negocio. Todo lo demás (reportes, proveedores, comprobantes bonitos) es incremento sobre esa base.

---

## Arquitectura — Monolito Modular

Este sistema se implementa como un **monolito modular**: un único proceso desplegable (Spring Boot) cuyo código interno está organizado en módulos con fronteras explícitas, cada uno dueño de su dominio. Los módulos se comunican entre sí mediante llamadas a servicios Java dentro del mismo proceso, nunca accediendo directamente a las tablas de otro módulo.

> Para una explicación detallada del patrón, ver [MonolitoModular.md](./MonolitoModular.md).

---

## Diagramas C4

El modelo C4 describe la arquitectura en cuatro niveles de zoom. Se documentan aquí los tres primeros.

### Nivel 1 — Contexto del sistema

Muestra quién usa el sistema y con qué sistemas externos interactúa.

```mermaid
C4Context
    title Nivel 1 · Contexto del Sistema — Tienda Abarrotes

    Person(cajero, "Cajero/a", "Registra ventas y consulta stock desde la caja")
    Person(admin, "Administrador/a (Doña Rosa)", "Gestiona productos, inventario, proveedores y revisa reportes")

    System(tienda, "Sistema Tienda Abarrotes", "Permite vender, controlar inventario y generar reportes del negocio")

    Rel(cajero, tienda, "Registra ventas, consulta productos y stock", "HTTP/REST")
    Rel(admin, tienda, "Administra catálogo, inventario, proveedores y reportes", "HTTP/REST")
```

---

### Nivel 2 — Contenedores

Muestra los bloques tecnológicos de alto nivel que componen el sistema.

```mermaid
C4Container
    title Nivel 2 · Contenedores — Tienda Abarrotes

    Person(cajero, "Cajero/a")
    Person(admin, "Administrador/a")

    System_Boundary(tienda, "Sistema Tienda Abarrotes") {
        Container(frontend, "Frontend Web", "React (SPA)", "Interfaz de usuario para caja y administración. Se ejecuta en el navegador.")
        Container(backend, "API Backend", "Spring Boot · Java 21", "Monolito modular. Expone la API REST. Contiene toda la lógica de negocio organizada en módulos.")
        ContainerDb(db, "Base de Datos", "PostgreSQL 16", "Persiste productos, inventario, ventas, proveedores y reportes en tablas relacionales.")
    }

    Rel(cajero, frontend, "Usa", "HTTPS")
    Rel(admin, frontend, "Usa", "HTTPS")
    Rel(frontend, backend, "Consulta y envía datos", "JSON / HTTP REST")
    Rel(backend, db, "Lee y escribe", "JDBC / JPA")
```

---

### Nivel 3 — Componentes

Muestra los módulos internos del contenedor **API Backend** y cómo se relacionan entre sí.

```mermaid
C4Component
    title Nivel 3 · Componentes — API Backend (Monolito Modular)

    Container_Ext(frontend, "Frontend React", "Llama a los endpoints REST")
    ContainerDb_Ext(db, "PostgreSQL", "Base de datos relacional")

    Container_Boundary(backend, "API Backend · Spring Boot") {

        Component(shared, "Shared", "GlobalExceptionHandler · OpenApiConfig", "Manejo centralizado de errores (ProblemDetail) y configuración de Swagger/OpenAPI")

        Component(catalog, "Módulo Catalog", "CategoryController · ProductController\nCategoryService · ProductService\nCategory · Product · UnitMeasure", "Gestión del catálogo: categorías y productos (CRUD, búsqueda por nombre, soft delete, stock mínimo)")

        Component(inventory, "Módulo Inventory", "InventoryController\nInventoryService\nInventoryEntry · InventoryExit · ExitReason", "Entradas de mercancía desde proveedores, salidas manuales (pérdida/vencimiento/daño), consulta y alerta de stock bajo")

        Component(sales, "Módulo Sales", "SaleController\nSaleService\nSale · SaleItem · PaymentMethod · SaleStatus", "Punto de venta: armar venta, calcular total, registrar método de pago, anular venta (devuelve stock)")

        Component(reports, "Módulo Reports", "ReportController\nReportService", "Reportes: ventas por período, productos más vendidos, stock bajo, utilidad estimada")

        Component(suppliers, "Módulo Suppliers", "SupplierController\nSupplierService\nSupplier", "Gestión de proveedores asociados a entradas de inventario")
    }

    Rel(frontend, catalog, "GET/POST/PUT/DELETE /api/products\nGET/POST/PUT/DELETE /api/categories", "JSON/HTTP")
    Rel(frontend, inventory, "POST /api/inventory/entries\nPOST /api/inventory/exits\nGET /api/inventory/stock", "JSON/HTTP")
    Rel(frontend, sales, "POST /api/sales\nGET /api/sales/{id}\nPOST /api/sales/{id}/void", "JSON/HTTP")
    Rel(frontend, reports, "GET /api/reports/sales\nGET /api/reports/top-products\nGET /api/reports/profit\nGET /api/reports/low-stock", "JSON/HTTP")
    Rel(frontend, suppliers, "GET/POST/PUT/DELETE /api/suppliers", "JSON/HTTP")

    Rel(sales, catalog, "getOrThrow(productId)\ndeductStock / addStock", "Llamada interna Java")
    Rel(inventory, catalog, "getOrThrow(productId)\naddStock / deductStock", "Llamada interna Java")
    Rel(inventory, suppliers, "findById(supplierId)", "Llamada interna Java")
    Rel(reports, catalog, "findLowStockProducts()", "Llamada interna Java")
    Rel(reports, sales, "sumTotal / countByPeriod\ntopProducts / profit", "Llamada interna Java")

    Rel(catalog, db, "categories · products", "JPA")
    Rel(inventory, db, "inventory_entries · inventory_exits", "JPA")
    Rel(sales, db, "sales · sale_items", "JPA")
    Rel(suppliers, db, "suppliers", "JPA")
```