# Smart Shopping List

Minimal, fast, and pragmatic shopping list app.

## Architecture
- **Backend**: Java 21 · Spring Boot 3.5 · JPA/Hibernate · Flyway · PostgreSQL  
  Tests: JUnit 5 · Spring Test/MockMvc · Testcontainers (PostgreSQL)
- **Frontend (planned)**: React + TypeScript · Vite · TailwindCSS · shadcn/ui

---

## Project Structure
- `backend/` – Spring Boot API (Web, Validation, Data JPA, Flyway)  
  → See **[`backend/README.md`](backend/README.md)** for full setup & details.

---

## Quick Start (Backend)

```bash
cd backend
# (optional) copy example config and fill in your DB credentials
cp src/main/resources/application-example.properties src/main/resources/application.properties

# run the API
mvn spring-boot:run







