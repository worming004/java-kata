# Library Kata — Java 21 · Spring Boot · SQL Server

A starter kata for practicing Java backend development around a **library borrowing** domain.

## Kata goal

Implement the rules for borrowing and returning books in a small library system.
The key invariant is:

> A user can borrow a book **only if at least one copy is currently available**.

---

## Tech stack

| Layer | Technology |
|---|---|
| Language | Java 21 |
| Build | Maven 3 |
| Framework | Spring Boot 3 |
| Persistence | Spring Data JPA + Hibernate |
| Database | Microsoft SQL Server 2022 |
| Migrations | Flyway |
| Tests | JUnit 5 + Testcontainers (SQL Server) |

---

## Domain model

```
Book           — title, author, ISBN, totalCopies
User           — name, email
Loan           — user, book, borrowedAt, returnedAt (null when active)
```

**Core rules:**
1. `availableCopies = book.totalCopies − activeLoans`
2. A borrow attempt fails when `availableCopies == 0`
3. Returning a loan sets `returnedAt` and frees the copy

---

## Project structure

```
src/
  main/
    java/com/kata/library/
      domain/          — Book, User, Loan entities
      repository/      — Spring Data JPA repositories
      service/         — LoanService (borrow / return / availability)
      web/             — REST controllers
    resources/
      db/migration/    — Flyway SQL scripts
      application.properties
  test/
    java/com/kata/library/
      AbstractIntegrationTest.java   — Testcontainers base class
      BorrowIntegrationTest.java     — integration tests
```

---

## Running the application locally

### 1 — Start SQL Server with Docker

```bash
docker run \
  -e ACCEPT_EULA=Y \
  -e MSSQL_SA_PASSWORD=Dev!Passw0rd \
  -p 1433:1433 \
  --name sqlserver-dev \
  -d mcr.microsoft.com/mssql/server:2022-latest
```

### 2 — Create the database

```bash
docker exec -it sqlserver-dev /opt/mssql-tools18/bin/sqlcmd \
  -S localhost -U sa -P 'Dev!Passw0rd' -No \
  -Q "CREATE DATABASE library"
```

### 3 — Build and run

```bash
JAVA_HOME=/usr/lib/jvm/temurin-21-jdk-amd64 mvn spring-boot:run
```

The app starts on **http://localhost:8080**.

---

## API endpoints

| Method | Path | Description |
|---|---|---|
| `GET` | `/books` | List all books |
| `GET` | `/books/{id}/availability` | Available copies for a book |
| `POST` | `/loans/borrow` | Borrow a book |
| `POST` | `/loans/{id}/return` | Return a loan |

### Example — borrow a book

```bash
curl -X POST http://localhost:8080/loans/borrow \
  -H 'Content-Type: application/json' \
  -d '{"userId": 1, "bookId": 1}'
```

---

## Running tests

Tests are self-contained — Testcontainers pulls a SQL Server Docker image and starts it automatically. **Docker must be running.**

```bash
JAVA_HOME=/usr/lib/jvm/temurin-21-jdk-amd64 mvn test
```

---

## Kata exercises

Start from the existing structure and extend it:

1. **Level 1** — Verify the borrow rules with unit tests on `LoanService` using mocks.
2. **Level 2** — Add a rule: a user cannot have more than 3 active loans at a time.
3. **Level 3** — Prevent a user from borrowing the same book twice simultaneously.
4. **Level 4** — Handle concurrent borrow attempts safely (optimistic locking, pessimistic lock, or retry).
5. **Level 5** — Add an overdue concept: loans not returned within 14 days are overdue.

---

## Contributing

Clone the repo, pick a level, write tests first, then implement.
Keep business rules inside `LoanService`, not in controllers or repositories.
