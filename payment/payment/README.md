# Payment Gateway Service – Task 2

Multi-tenant payment gateway simulation, with async completion, idempotency, and JWT + tenant-based security.

## Tech Stack

- Java 21
- Spring Boot 3
- Spring Data JPA (PostgreSQL)
- Spring Security (JWT)
- Flyway (database migrations)

---

## Domain Model

### PaymentTransaction

- `id: UUID`
- `tenantId: String`
- `dealerId: UUID`
- `amount: BigDecimal`
- `method: UPI | CARD | NET_BANKING`
- `status: PENDING | SUCCESS | FAILED`
- `requestId: String` — idempotency key (unique per tenant)
- `createdAt: LocalDateTime`
- `updatedAt: LocalDateTime`

All operations are tenant-scoped via `tenantId`.

---

## Security & Tenancy

- JWT auth (`/api/v1/auth/**` for register/login).
- Payment endpoints require:
  - `Authorization: Bearer <JWT>`
  - `X-Tenant-Id: <tenant-id>`

Behavior:

- Missing `X-Tenant-Id` → `400 Bad Request` (header required).
- Repositories always query by `tenantId`, so cross-tenant access to transactions is blocked.

---

## REST Endpoints & Behavior

### Initiate Payment

`POST /api/payment/initiate`

Headers:

- `Authorization: Bearer <token>`
- `X-Tenant-Id: <tenant-id>`
- `Idempotency-Key: <optional-string>`

Body:

```json
{
  "dealerId": "11111111-1111-1111-1111-111111111111",
  "amount": 100.50,
  "method": "CARD"
}
```

Behavior:

- Creates a new `PaymentTransaction` with:
  - `status = PENDING`
  - `requestId = Idempotency-Key` (if provided) or a generated UUID.
- Starts asynchronous processing to complete the payment.

**Idempotency**

- If `Idempotency-Key` is provided and a transaction with the same `(requestId, tenantId)` exists:
  - Returns that **existing** transaction instead of creating a new one.
- This ensures clients can safely retry `POST /api/payment/initiate` with the same key.

### Get Payment Status

`GET /api/payment/{id}`

Headers:

- `Authorization: Bearer <token>`
- `X-Tenant-Id: <tenant-id>`

Returns the `PaymentTransaction` for the given `id` and `tenantId`.

---

## Async Completion (~5 seconds)

- Implemented via an `@Async` method in the service:
  - Waits ~5 seconds.
  - Then updates the `status`:
    - `PENDING → SUCCESS` if processing succeeds.
    - `PENDING → FAILED` on exception.

- Optionally, a scheduled job (e.g. `@Scheduled(fixedDelay = 60000)`) scans and updates any remaining `PENDING` transactions every 60 seconds as a safety net.

This satisfies the requirement that `PENDING` transitions to `SUCCESS` within ~5 seconds for normal cases.

---

## Error Handling

Global exception handling returns consistent error JSON.

Notable cases:

- **Invalid payment method enum**

  If `method` is not one of `UPI`, `CARD`, `NET_BANKING`, the request fails with a clear error, for example:

  ```json
  {
    "status": 400,
    "error": "Invalid Payment Method",
    "message": "Invalid payment method 'CAsRD'. Allowed values are: UPI, CARD, NET_BANKING"
  }
  ```

- **Missing tenant header**

  ```json
  {
    "status": 400,
    "error": "Bad Request",
    "message": "Required header 'X-Tenant-Id' is not present"
  }
  ```

(Exact text may differ depending on Spring binding and your `GlobalExceptionHandler`.)

---

## How to Run

1. Configure PostgreSQL in `src/main/resources/application.properties`:

   ```properties
   spring.datasource.url=jdbc:postgresql://localhost:5432/payment_db
   spring.datasource.username=payment_user
   spring.datasource.password=payment_pass

   spring.jpa.hibernate.ddl-auto=validate
   spring.flyway.enabled=true
   ```

2. Build & run:

   ```bash
   mvn clean spring-boot:run
   ```

3. Service runs on (example):

   - `http://localhost:8081`

---

## Quick Test Flow

1. **Register & login** to get a JWT token using `/api/v1/auth/register` and `/api/v1/auth/login`.
2. Call `POST /api/payment/initiate`:

   ```bash
   curl --location 'http://localhost:8081/api/payment/initiate' \
     --header 'Authorization: Bearer <token>' \
     --header 'X-Tenant-Id: default-tenant' \
     --header 'Content-Type: application/json' \
     --data '{
       "dealerId": "11111111-1111-1111-1111-111111111111",
       "amount": 100.50,
       "method": "CARD"
     }'
   ```

   - Save the returned `id`.

3. Immediately call `GET /api/payment/{id}` → should be `PENDING`.

4. After ~5 seconds, call `GET /api/payment/{id}` again → should be `SUCCESS`.

5. Test idempotency:

   - Call `POST /api/payment/initiate` twice with the same `Idempotency-Key` and same body.
   - Both responses should contain the same `id` and data.

---

## Project Notes

- The legacy `Payment` entity/API is not exposed as endpoints; only Task 2 gateway endpoints are active (`/api/payment/**`), matching the assignment.
