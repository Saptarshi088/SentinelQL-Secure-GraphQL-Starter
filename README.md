# SentinelQL – Secure GraphQL Starter

Secure-by-default, beginner-friendly starter that shows how to protect a Spring Boot GraphQL API with JWT. It ships with a Book/Author schema, REST auth bridge, and method-level authorization so newcomers can see how to lock down mutations and expose only the operations they intend.

## Why use SentinelQL?
- **JWT-guarded GraphQL**: Stateless security via `Authorization: Bearer <token>` plus `@PreAuthorize` on sensitive mutations.
- **REST ↔ GraphQL handoff**: Sign-up/login endpoints issue tokens that gate GraphQL mutations.
- **Database-ready**: JPA entities for users, authors, and books; works with MySQL/TiDB out of the box.
- **Observability hooks**: Micrometer + OpenTelemetry already wired for tracing.
- **Playground included**: GraphiQL enabled at `/graphiql` for quick exploration.

## Tech stack
- Spring Boot 4.0.2, Spring GraphQL
- Spring Security with JWT (jjwt 0.13.x)
- Spring Data JPA + MySQL/TiDB
- Micrometer Tracing + OTLP exporter

## Quick start
1. **Prerequisites**: JDK 17+ (project property set to 25), Maven 3.9+, MySQL 8+/TiDB. 
2. **Configure secrets via environment variables** (recommended instead of committing them):
   ```bash
   export SPRING_DATASOURCE_URL="jdbc:mysql://localhost:3306/sentinelql"
   export SPRING_DATASOURCE_USERNAME="root"
   export SPRING_DATASOURCE_PASSWORD="changeme"
   export JWT_SECRET="generate-a-64-byte-random-string"
   export JWT_TOKEN_EXPIRATION=15   # minutes
   ``` 
3. **Prepare the database**: create the schema referenced in `SPRING_DATASOURCE_URL`; JPA is set to `ddl-auto=update` for local convenience.
4. **Run the app**: `./mvnw spring-boot:run` (or `mvn spring-boot:run`).
5. **Explore GraphQL**: open `http://localhost:8080/graphiql` and point it to `http://localhost:8080/graphql`.

## Security model
- Stateless JWTs are issued by `/auth/log-in`; expiration defaults to 15 minutes.
- `JwtFilter` processes the `Authorization` header and injects the user into the `SecurityContext`.
- Method security (`@EnableMethodSecurity`) protects:
  - `addBook`, `updateBook`, `deleteBook`, and `getAllAuthors` (GraphQL) require authentication.
  - REST endpoints under `/books` are currently open for demo purposes; lock them down before production.
- Secrets in `src/main/resources/application.properties` are placeholders—override them with env vars before publishing.

## API map
**Auth (REST)**
- `POST /auth/sign-in` — register a user `{ "email": "user@mail.com", "password": "pass", "role": "USER" }`
- `POST /auth/log-in` — returns JWT for `{ "email": "user@mail.com", "password": "pass" }`

**Books (REST)**
- `GET /books/getAllBooks` — list books (no auth)
- `POST /books/addBook` — create a book
- `POST /books/byExample` — search by example payload

**GraphQL**
- Endpoint: `POST /graphql` (GraphiQL UI at `/graphiql`)
- Query: `getAllBooks` (public), `getAllAuthors` (auth)
- Mutations: `addBook`, `updateBook`, `deleteBook` (auth); `addAuthor`, `deleteAuthor` are open in the starter—secure them as needed.

## Usage examples
**1) Register and log in**
```bash
curl -X POST http://localhost:8080/auth/sign-in \
  -H "Content-Type: application/json" \
  -d '{"email":"demo@mail.com","password":"password","role":"USER"}'

TOKEN=$(curl -s -X POST http://localhost:8080/auth/log-in \
  -H "Content-Type: application/json" \
  -d '{"email":"demo@mail.com","password":"password"}')
```

**2) Run a secured GraphQL mutation**
```bash
curl -X POST http://localhost:8080/graphql \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{"query":"mutation { addBook(input:{title:\"DDD\", pageCount:560, authorId:1}) { title pageCount author { firstName lastName } } }"}'
```

**3) Public query (no token)**
```graphql
query {
  getAllBooks {
    title
    pageCount
    author { firstName lastName }
  }
}
```

## Configuration notes
- Override any `application.properties` key with an environment variable using Spring Boot's relaxed binding (e.g., `jwt.secret` → `JWT_SECRET`).
- Tracing is enabled and points to `http://localhost:4318` by default; adjust `management.otlp.tracing.endpoint` as needed.
- CORS is enabled on controllers for easy local testing.

## Project layout
- `src/main/java/com/saptarshi/DemoInterview/controller` — REST + GraphQL entry points
- `src/main/java/com/saptarshi/DemoInterview/security` — `SecurityConfig`, `JwtFilter`, and `CustomUserDetails`
- `src/main/java/com/saptarshi/DemoInterview/jwt/JwtService.java` — token generation/validation
- `src/main/resources/schema/schema.graphqls` — GraphQL schema (queries, mutations, inputs)

## Hardening checklist
- Protect currently open mutations (`addAuthor`, `deleteAuthor`, REST book endpoints) with role checks.
- Move secrets to a secrets manager or environment variables; never commit production keys.
- Change `ddl-auto=update` to `validate` or migrations for real deployments.
- Add input validation (e.g., Bean Validation) and error handling to avoid generic runtime exceptions.

## Running tests
```bash
./mvnw test
```

## License
Add your preferred license (MIT/Apache-2.0) before publishing.
