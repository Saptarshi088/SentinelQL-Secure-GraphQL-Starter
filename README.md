# SentinelQL – Secure GraphQL Starter

Production-minded Spring Boot GraphQL starter with JWT auth, role-based authorization, validation, observability, and CI/container scaffolding.

## What is now “industry-ready”
- JWT authentication with stateless Spring Security filter chain.
- Role-based authorization for GraphQL and REST write operations.
- Request validation for auth and GraphQL/REST mutation inputs.
- Centralized REST exception handling with predictable error payloads.
- GraphQL exception resolver for cleaner API errors.
- Entity constraints and indexes for better database integrity/performance.
- Environment-driven configuration (no hardcoded secrets).
- Production profile (`application-prod.properties`) for safer defaults.
- Dockerfile + docker-compose for local/prod-like runtime.
- GitHub Actions CI workflow (Maven test pipeline).

## Stack
- Java 21, Spring Boot 4, Spring GraphQL
- Spring Security + JWT (jjwt)
- Spring Data JPA + MySQL
- Bean Validation (Jakarta Validation)
- Micrometer/OTLP tracing

## Quick start
```bash
export SPRING_DATASOURCE_URL="jdbc:mysql://localhost:3306/sentinelql"
export SPRING_DATASOURCE_USERNAME="root"
export SPRING_DATASOURCE_PASSWORD="password"
export JWT_SECRET="replace-with-a-long-random-secret-at-least-32-characters"

./mvnw spring-boot:run
```

- GraphiQL: `http://localhost:8080/graphiql`
- GraphQL endpoint: `http://localhost:8080/graphql`

## Auth flow
1. Register user:
```bash
curl -X POST http://localhost:8080/auth/sign-in \
  -H "Content-Type: application/json" \
  -d '{"email":"demo@mail.com","password":"StrongPass123","role":"USER"}'
```

2. Login and capture token:
```bash
curl -X POST http://localhost:8080/auth/log-in \
  -H "Content-Type: application/json" \
  -d '{"email":"demo@mail.com","password":"StrongPass123"}'
```

Response:
```json
{
  "token": "...",
  "tokenType": "Bearer",
  "expiresInMinutes": 15
}
```

## Run with Docker Compose
```bash
docker compose up --build
```

## Testing
```bash
./mvnw test
```

## Production notes
- Use `SPRING_PROFILES_ACTIVE=prod`.
- Replace `JWT_SECRET` with a secure, rotated secret.
- Route logs and metrics to your observability stack.
- Add DB migrations (Flyway/Liquibase) for controlled schema evolution.
