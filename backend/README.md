# Smart Shopping List

Minimal, fast, and pragmatic shopping list app.

## Architecture

- **Backend**: Java 21 · Spring Boot 3.5 · JPA/Hibernate · Flyway · PostgreSQL  
  Tests: JUnit 5 · Spring Test/MockMvc · Testcontainers (Postgres)
- **Frontend (planned)**: React + TypeScript · Vite · TailwindCSS · shadcn/ui

---

## Tech Stack (Backend)

- Spring Boot (Web, Validation, Data JPA)
- PostgreSQL JDBC driver
- Flyway (DB migrations)
- Testing: JUnit 5, Spring Test + MockMvc, **Testcontainers** (postgres:16-alpine)

---

## Run Backend locally

> The repo contains `backend/src/main/resources/application-example.properties`.  
> Copy it to `application.properties` and fill in **your** local DB credentials.

```bash
cd backend

# 1) create your local config from the template
cp src/main/resources/application-example.properties \
   src/main/resources/application.properties

# 2) edit the copied file and set at least:
# spring.datasource.url=jdbc:postgresql://localhost:5432/smartshoppinglist
# spring.datasource.username=YOUR_USER
# spring.datasource.password=YOUR_PASSWORD
# spring.jpa.hibernate.ddl-auto=validate   # schema managed by Flyway
# spring.flyway.enabled=true

# 3) start the app
mvn spring-boot:run

