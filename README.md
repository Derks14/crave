# Crave

Crave is an experimental microservice implementation of a food ordering system built with Spring Boot. The repository currently contains three independently deployable services:

- `food` for menu/catalog management
- `order` for order creation and persistence
- `inventory` for stock lookup and availability checks

The codebase is intentionally small, but the architecture already reflects production-minded concerns: service isolation, database-per-service persistence choices, schema versioning with Flyway, and test support with Testcontainers.

## What This Project Demonstrates

This repository is designed to show how a simple food ordering domain can be split into focused services with clear responsibilities:

- Each service owns its own data model and runtime configuration.
- The catalog service uses MongoDB for document storage.
- The order and inventory services use MySQL with JPA and Flyway-managed schema migrations.
- Services are exposed through REST controllers and kept intentionally thin, with business logic concentrated in service classes.
- Test dependencies are already in place for container-backed integration testing.

This is not yet a full distributed system with orchestration, messaging, or service discovery. It is a clean foundation for that next step.

## Technology Stack

- Java 21
- Spring Boot 4.0.5
- Spring Web MVC
- Spring Data MongoDB
- Spring Data JPA
- Flyway
- MySQL 8
- MongoDB
- Lombok
- Testcontainers

## Service Overview

| Service | Responsibility | Storage | Default Port |
| --- | --- | --- | --- |
| `food` | Create and list food items | MongoDB | `8080` |
| `order` | Create orders | MySQL | `8081` |
| `inventory` | Check stock availability | MySQL | `8082` |

## Architecture at a Glance

The services are deliberately decoupled:

- `food` stores catalog data independently from order flow.
- `order` persists order requests as records in its own database.
- `inventory` answers stock availability using its own persistence layer.

That means each service can evolve its schema, deployment, and runtime settings without coupling to the others. In a larger system, this is the right starting point for introducing event-driven communication, asynchronous workflows, or API composition.

## `food` Service

The `food` service is the catalog boundary of the system. It accepts new menu items and returns the full list of available foods.

### Responsibilities

- Create food items
- Read all food items
- Persist data in MongoDB

### Implementation Notes

- REST controller: [`food/controllers/FoodController.java`](food/src/main/java/food/controllers/FoodController.java)
- Service layer: [`food/services/FoodService.java`](food/src/main/java/food/services/FoodService.java)
- Document model: [`food/model/Food.java`](food/src/main/java/food/model/Food.java)
- DTOs are used to keep request and response shapes stable

### API

#### `POST /api/food`

Creates a new food item.

Request body:

```json
{
  "name": "Big Mac",
  "description": "Signature beef burger with special sauce",
  "price": 9.99
}
```

Response:

```json
{
  "id": "662f3f8d5c3d4f6b8d5b0d11",
  "name": "Big Mac",
  "description": "Signature beef burger with special sauce",
  "price": "9.99"
}
```

#### `GET /api/food`

Returns all food items.

### Persistence Model

The `Food` document currently contains:

- `id`
- `name`
- `description`
- `price`

Price is represented internally as `BigDecimal` and returned as a string to preserve precision.

### Configuration

The service reads its MongoDB connection from:

- [`food/src/main/resources/application.properties`](food/src/main/resources/application.properties)

## `order` Service

The `order` service accepts order requests and persists them as first-class order records.

### Responsibilities

- Create orders
- Generate order numbers
- Persist orders in MySQL

### Implementation Notes

- REST controller: [`order/controllers/OrderController.java`](order/src/main/java/order/controllers/OrderController.java)
- Service layer: [`order/service/OrderService.java`](order/src/main/java/order/service/OrderService.java)
- Entity model: [`order/models/Order.java`](order/src/main/java/order/models/Order.java)
- Database access: [`order/repository/OrderRepository.java`](order/src/main/java/order/repository/OrderRepository.java)

### API

#### `POST /api/order`

Places a new order.

Request body:

```json
{
  "skuCode": "big_mac",
  "price": 9.99,
  "quantity": 2
}
```

Response:

```json
{
  "id": 1,
  "orderNumber": "3f19c2bc-30ab-43e0-bb72-8e7c1c947ea0",
  "skuCode": "big_mac",
  "price": 9.99,
  "quantity": 2
}
```

### Persistence Model

The `orders` table stores:

- `id`
- `orderNumber`
- `skuCode`
- `price`
- `quantity`

Order numbers are generated with `UUID.randomUUID()` at the service layer.

### Database Migrations

The schema is managed with Flyway.

- [`order/src/main/resources/db/migration/V1__init.sql`](order/src/main/resources/db/migration/V1__init.sql)

### Configuration

The service is configured for MySQL on port `8081`:

- [`order/src/main/resources/application.properties`](order/src/main/resources/application.properties)

## `inventory` Service

The `inventory` service provides a stock availability lookup by SKU.

### Responsibilities

- Check whether a SKU is in stock
- Persist inventory records in MySQL

### Implementation Notes

- REST controller: [`inventory/controllers/controllers/InventoryController.java`](inventory/src/main/java/inventory/controllers/controllers/InventoryController.java)
- Service layer: [`inventory/services/InventoryService.java`](inventory/src/main/java/inventory/services/InventoryService.java)
- Entity model: [`inventory/models/Inventory.java`](inventory/src/main/java/inventory/models/Inventory.java)
- Database access: [`inventory/repository/InventoryRepository.java`](inventory/src/main/java/inventory/repository/InventoryRepository.java)

### API

#### `GET /api/inventory?skuCode=big_mac`

Checks whether an item exists in stock with quantity greater than or equal to `1`.

Response:

```json
true
```

### Persistence Model

The `inventory` table stores:

- `id`
- `skuCode`
- `quantity`

The repository exposes a derived query method that resolves stock availability without manual SQL.

### Database Migrations

Flyway migrations for inventory are located under:

- [`inventory/src/main/resources/db/migration/V1__init.sql`](inventory/src/main/resources/db/migration/V1__init.sql)
- [`inventory/src/main/resources/db/migration/v3__add_mc_inventory.sql`](inventory/src/main/resources/db/migration/v3__add_mc_inventory.sql)

The seed migration loads example inventory data for menu items such as `big_mac`, `quarter_pounder`, and `mcchicken`.

### Configuration

The service is configured for MySQL on port `8082`:

- [`inventory/src/main/resources/application.properties`](inventory/src/main/resources/application.properties)

## Local Development

Each service is a standalone Spring Boot application. Run them from their respective directories.

### Prerequisites

- Java 21
- Maven or the included Maven wrapper
- MongoDB for `food`
- MySQL for `order` and `inventory`

### Run the Services

From the root of each service directory:

```bash
./mvnw spring-boot:run
```

Recommended startup order:

1. Start MongoDB
2. Start MySQL for `order`
3. Start MySQL for `inventory`
4. Run `food`
5. Run `order`
6. Run `inventory`

### Notes on Local Infrastructure

The repository includes Docker Compose files inside each service directory for local database support. Use the configuration in each service’s `application.properties` as the source of truth for the application runtime settings.

Because `order` and `inventory` both default to MySQL on host port `3306`, they cannot both bind to the same local MySQL port at the same time unless you adjust one of the containers or app URLs.

## Example Requests

### Create a Food Item

```bash
curl -X POST http://localhost:8080/api/food \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Big Mac",
    "description": "Signature beef burger with special sauce",
    "price": 9.99
  }'
```

### Place an Order

```bash
curl -X POST http://localhost:8081/api/order \
  -H "Content-Type: application/json" \
  -d '{
    "skuCode": "big_mac",
    "price": 9.99,
    "quantity": 2
  }'
```

### Check Inventory

```bash
curl "http://localhost:8082/api/inventory?skuCode=big_mac"
```

## Testing

The build files already include Testcontainers support for integration testing:

- MongoDB Testcontainers for `food`
- MySQL Testcontainers for `order` and `inventory`

This is a strong foundation for verifying repository behavior against real databases instead of mocks.

## Project Status

Current scope:

- Food catalog CRUD baseline
- Order creation and persistence
- Inventory availability lookup

Likely next steps:

- Add order validation against food and inventory
- Introduce service-to-service communication
- Add distributed tracing and centralized logging
- Add container orchestration for the full stack
- Expand test coverage with repository and API integration tests

## Repository Layout

```text
crave/
├── food/
├── order/
└── inventory/
```

Each folder contains its own Spring Boot application, resources, tests, and build configuration.

## Summary

Crave is a pragmatic microservice prototype that demonstrates:

- clear service boundaries
- independent data stores
- REST-first APIs
- schema migration discipline
- test-ready infrastructure

It is intentionally small, but it already shows the structure and judgment expected in a real microservice codebase.
