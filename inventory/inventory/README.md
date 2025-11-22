# Inventory Service (Dealers & Vehicles) – Task 1

Multi-tenant Inventory microservice that manages dealers and their vehicles.

## Tech Stack

- Java 21
- Spring Boot 3
- Spring Data JPA (PostgreSQL)
- Spring Security (JWT)
- Flyway (database migrations)

---

## Domain Model

### Dealer

- `id: UUID`
- `tenantId: String`
- `name: String`
- `email: String`
- `subscriptionType: BASIC | PREMIUM`

### Vehicle

- `id: UUID`
- `tenantId: String`
- `dealerId: UUID`
- `model: String`
- `price: BigDecimal`
- `status: AVAILABLE | SOLD`

All data is **tenant-scoped** via `tenantId`.

---

## Security & Tenancy

- JWT auth (`/api/v1/auth/**` for register/login).
- All business endpoints require:
  - `Authorization: Bearer <JWT>`
  - `X-Tenant-Id: <tenant-id>`

Behavior:

- Missing `X-Tenant-Id` → `400 Bad Request` (header required).
- Repositories always filter by `tenantId`, so cross-tenant access is blocked (another tenant’s data is not visible).

---

## REST Endpoints

### Dealers

- `POST /dealers`  
  Create a dealer for the current tenant.

- `GET /dealers/{id}`  
  Get a dealer by id (tenant-scoped).

- `GET /dealers`  
  List dealers (tenant-scoped). Supports:
  - Pagination: `page`, `size`
  - Sorting: `sort=field,asc|desc`

- `PATCH /dealers/{id}`  
  Partial update.

- `DELETE /dealers/{id}`  
  Delete dealer within tenant.

### Vehicles

- `POST /vehicles`  
  Create a vehicle for a dealer within the current tenant.

- `GET /vehicles/{id}`  
  Get a vehicle by id (tenant-scoped).

- `GET /vehicles`  
  List vehicles (tenant-scoped) with filters:
  - `model`
  - `status`
  - `priceMin`
  - `priceMax`
  And pagination/sort via `page`, `size`, `sort`.

- `PATCH /vehicles/{id}`  
  Partial update.

- `DELETE /vehicles/{id}`  
  Delete vehicle within tenant.

### Query: Premium Vehicles

- `GET /vehicles?subscription=PREMIUM`  
  Returns vehicles where the **dealer** has `subscriptionType = PREMIUM`, still **within the caller’s tenant**.

### Admin (GLOBAL_ADMIN only)

- `GET /admin/dealers/countBySubscription`  

Returns JSON like:

```json
{
  "BASIC": 10,
  "PREMIUM": 5
}
```

> Note: Depending on implementation, counts may be per tenant (respect `X-Tenant-Id`) or global; this can be clarified during the review.

---

## How to Run

1. Configure PostgreSQL in `src/main/resources/application.properties`:

   ```properties
   spring.datasource.url=jdbc:postgresql://localhost:5432/inventory_db
   spring.datasource.username=inventory_user
   spring.datasource.password=inventory_pass

   spring.jpa.hibernate.ddl-auto=validate
   spring.flyway.enabled=true
   ```

2. Build & run:

   ```bash
   mvn clean spring-boot:run
   ```

3. Service runs on (example):

   - `http://localhost:8080`

---

## Quick Test Flow

1. **Register & login** via `/api/v1/auth/register` and `/api/v1/auth/login` to get a JWT.
2. Use headers on all protected endpoints:
   - `Authorization: Bearer <token>`
   - `X-Tenant-Id: tenant-a`
3. Create a `Dealer` (e.g. one BASIC, one PREMIUM).
4. Create `Vehicle`s linked to those dealers.
5. Call:
   - `GET /vehicles?subscription=PREMIUM` → only vehicles of PREMIUM dealers in `tenant-a`.
   - `GET /admin/dealers/countBySubscription` using a `GLOBAL_ADMIN` token.
