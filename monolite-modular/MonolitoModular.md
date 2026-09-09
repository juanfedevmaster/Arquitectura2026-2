# Monolito Modular — Qué es y por qué usarlo

## El problema de los extremos

En arquitectura de software existe una tensión clásica entre dos extremos:

| | Monolito tradicional | Microservicios |
|---|---|---|
| **Despliegue** | Un solo artefacto | Muchos servicios independientes |
| **Comunicación** | Llamadas en memoria | Red (HTTP, mensajería) |
| **Consistencia** | Transaccional (ACID) | Eventual, compleja |
| **Operación** | Simple | Costosa (K8s, service mesh, observabilidad distribuida) |
| **Límites de dominio** | Generalmente ausentes | Forzados por la red |
| **Problema principal** | Se convierte en un "big ball of mud" con el tiempo | Complejidad operacional altísima desde el día uno |

El monolito tradicional falla porque **no tiene estructura interna**: cualquier clase puede llamar a cualquier otra, los módulos se mezclan, y el código se vuelve imposible de razonar con el tiempo.

Los microservicios resuelven los límites con la red, pero introducen latencia, fallos parciales, transacciones distribuidas y una infraestructura que es innecesariamente cara para la mayoría de los sistemas.

---

## Qué es un Monolito Modular

Un **monolito modular** es un sistema que:

1. Se **despliega como un único proceso** (un solo artefacto `.jar`, `.war`, contenedor Docker, etc.)
2. Está **dividido internamente en módulos con fronteras explícitas**, cada uno dueño de su propio dominio
3. Los módulos se **comunican entre sí mediante interfaces públicas bien definidas** (llamadas Java en el mismo proceso), nunca accediendo directamente a las clases internas o tablas de otro módulo
4. Cada módulo tiene su propia capa: `domain` → `infrastructure` → `application` → `api`

La clave es que **la disciplina de límites se mantiene por convención y estructura de paquetes**, no por la red. Un módulo expone solo lo que necesita exponer (su `Service` público); el resto es privado al módulo.

```
modules/
├── catalog/
│   ├── domain/          ← entidades y reglas de negocio del catálogo
│   ├── infrastructure/  ← repositorios JPA
│   ├── application/     ← servicios: único punto de entrada al módulo
│   └── api/             ← controllers REST + DTOs
├── inventory/
│   └── ...              ← mismo patrón
├── sales/
│   └── ...
└── ...
```

Cuando el módulo `sales` necesita un producto, no accede a `ProductRepository` directamente: llama a `ProductService.getOrThrow()`, que es la API pública del módulo `catalog`. La base de datos es compartida, pero el acceso está mediado por los servicios.

---

## Diferencias con el monolito tradicional

| Aspecto | Monolito tradicional | Monolito modular |
|---|---|---|
| Acceso entre componentes | Cualquiera llama a cualquiera | Solo a través de la API pública del módulo |
| Propiedad de la BD | Tablas mezcladas sin dueño | Cada módulo es dueño de sus tablas |
| Evolución | Cambia una cosa, rompe diez | Cambios contenidos dentro del módulo |
| Legibilidad | Difícil encontrar dónde vive el negocio | El módulo define el contexto |
| Migración a microservicios | Costosa, requiere cirugía mayor | Natural: el módulo ya tiene sus límites; se extrae con mínima fricción |

---

## Cuándo usar un Monolito Modular

Es la elección correcta cuando:

- El equipo es pequeño (1–10 personas) o el proyecto está en fase inicial
- No hay certeza de qué partes del dominio necesitan escalar de forma independiente
- Se quiere la **disciplina de microservicios** (límites de dominio claros) sin el **costo operacional** de microservicios (red, orquestación, observabilidad distribuida)
- Se necesita **consistencia transaccional fuerte** (como en ventas e inventario: vender y descontar stock deben ocurrir en la misma transacción)

No es la elección correcta cuando distintas partes del sistema tienen requisitos de escala radicalmente diferentes y ya se conocen con certeza.

---

## Cómo está organizado este proyecto

El sistema **Tienda Abarrotes** implementa este patrón con cinco módulos:

### `catalog` — Catálogo de productos
Dueño de `categories` y `products`. Gestiona el ciclo de vida de productos (CRUD, soft delete, búsqueda). Expone `ProductService.getOrThrow()` para que otros módulos accedan a productos sin tocar el repositorio.

### `inventory` — Control de inventario
Dueño de `inventory_entries` e `inventory_exits`. Registra entradas de mercancía (compras a proveedores) y salidas manuales (pérdida, vencimiento, daño). Usa `ProductService` para validar productos y actualizar el stock en la entidad `Product`.

### `sales` — Punto de venta
Dueño de `sales` y `sale_items`. Implementa la transacción de venta completa: valida stock disponible, descuenta inventario y registra la venta en una sola transacción `@Transactional`. También gestiona la anulación de ventas (restaura el stock). Es el módulo más crítico del negocio.

### `reports` — Reportes
No tiene tablas propias. Lee datos de los repositorios de `sales` y `catalog` para generar: ventas por período, productos más vendidos, stock bajo y utilidad estimada. Es un módulo de solo lectura.

### `suppliers` — Proveedores
Dueño de `suppliers`. Gestiona el registro de proveedores que se asocian a las entradas de inventario.

### `shared` — Infraestructura transversal
No es un módulo de dominio. Contiene `GlobalExceptionHandler` (manejo de errores RFC 9457 ProblemDetail) y `OpenApiConfig` (Swagger). Todo lo que necesitan todos los módulos pero que no pertenece a ninguno.

---

## La regla de consistencia de stock

El criterio más importante del sistema es que **el stock nunca puede quedar inconsistente**. Se garantiza así:

1. `Product.deductStock(int qty)` — método en la entidad de dominio que lanza `BusinessException` si `stockQuantity < qty`. No hay forma de descontar sin pasar por este método.
2. `@Transactional` en `SaleService.createSale()` — si cualquier producto falla (sin stock, inactivo), toda la transacción hace rollback. O se venden todos los ítems o no se vende ninguno.
3. `@Version` en `Product` — optimistic locking de Hibernate. Si dos transacciones concurrentes intentan descontar el mismo producto simultáneamente, una falla con `OptimisticLockException` antes de comprometer datos incorrectos.

```
POST /api/sales
  └── @Transactional
        ├── producto A: deductStock(3) ← falla si stock < 3 → rollback completo
        ├── producto B: deductStock(1)
        └── Sale + SaleItems guardados ← solo si todo el bloque anterior tuvo éxito
```

---

## Flujo de una venta (flujo principal del negocio)

```
Cliente en caja
    │
    ▼
POST /api/sales  {paymentMethod, items:[{productId, qty},...]}
    │
    ▼
SaleService.createSale()  @Transactional
    │
    ├── Por cada ítem:
    │     ├── ProductService.getOrThrow(productId)   → 404 si no existe
    │     ├── product.isActive()?                    → 400 si dado de baja
    │     └── product.deductStock(qty)               → 400 si stock insuficiente
    │
    ├── Calcular total = Σ(precio × cantidad)
    │
    └── Guardar Sale + SaleItems
          │
          ├── éxito → 201 Created + SaleResponse (comprobante)
          └── fallo → rollback total, stock intacto
```

---

## Comparación rápida de arquitecturas para este proyecto

| Criterio | Monolito tradicional | **Monolito Modular** | Microservicios |
|---|---|---|---|
| Consistencia stock | Posible pero sin disciplina | **Garantizada por @Transactional** | Requiere saga/2PC |
| Complejidad operacional | Baja | **Baja** | Alta |
| Límites de dominio | Ausentes | **Explícitos por módulo** | Forzados por red |
| Equipo requerido | 1+ | **1–10** | 5+ (DevOps incluido) |
| Escalabilidad | Todo junto | **Todo junto** | Por servicio |
| Migración futura | Muy costosa | **Natural** | N/A |

Para una tienda de abarrotes de barrio, operada por una persona, con un equipo de desarrollo pequeño, el monolito modular es la arquitectura correcta: tiene la disciplina que previene el "big ball of mud" y la simplicidad operacional que permite enfocarse en el negocio.
