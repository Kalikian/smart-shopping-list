# Smart Shopping List — Backend (Spring Boot)

Minimal, fast, and pragmatic shopping list API.

## Architecture

- **Backend**: Java 21 · Spring Boot 3.5 · JPA/Hibernate · Flyway · PostgreSQL  
  Tests: JUnit 5 · Spring Test/MockMvc · **Testcontainers** (PostgreSQL)

---

## Tech Stack

- Spring Boot (Web, Validation, Data JPA)
- PostgreSQL JDBC driver
- Flyway (DB migrations)
- Testing: JUnit 5, Spring Test + MockMvc, Testcontainers (postgres:16-alpine)
- Build: Maven (Surefire + Surefire Report)

---

## Configuration

The repo contains `src/main/resources/application-example.properties`.  
Copy it to `application.properties` and fill in **your** local DB credentials.

```bash
cp src/main/resources/application-example.properties src/main/resources/application.properties
```
Now edit `src/main/resources/application.properties`:

```properties
# PostgreSQL connection
spring.datasource.url=jdbc:postgresql://localhost:5432/smartshoppinglist
spring.datasource.username=YOUR_USER
spring.datasource.password=YOUR_PASSWORD

# JPA/Hibernate
spring.jpa.hibernate.ddl-auto=validate   # schema is managed by Flyway

# Flyway
spring.flyway.enabled=true
spring.flyway.locations=classpath:db/migration

# (optional) SQL logging
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true

spring.jpa.open-in-view=false
```
## Run & Test
Requires: local PostgreSQL (per config) and Docker running (for Testcontainers).
```bash
# from backend/

# 1) Run the API locally (requires a local PostgreSQL per above config)
mvn spring-boot:run
# App starts on http://localhost:8080

# 2) Run tests (requires Docker Desktop/Engine running for Testcontainers)
mvn -q test

# 3) Generate HTML test report (Surefire) into target/reports
mvn -q surefire-report:report -DoutputDirectory=target/reports

# 4) On WSL: open the HTML report in your Windows browser
wslview target/reports/surefire.html
```
