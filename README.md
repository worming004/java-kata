# Kata Bibliothèque — Java 21 · Spring Boot · SQL Server

Un kata de démarrage pour pratiquer le développement backend en Java autour d’un domaine de **prêt de bibliothèque**.

## Objectif du kata

Implémenter les règles d’emprunt et de retour des livres dans un petit système de bibliothèque.
L’invariant principal est le suivant :

> Un utilisateur peut emprunter un livre **uniquement si au moins un exemplaire est actuellement disponible**.

---

## Stack technique

| Couche          | Technologie                           |
| --------------- | ------------------------------------- |
| Langage         | Java 21                               |
| Build           | Maven 3                               |
| Framework       | Spring Boot 3                         |
| Persistance     | Spring Data JPA + Hibernate           |
| Base de données | Microsoft SQL Server 2022             |
| Migrations      | Flyway                                |
| Tests           | JUnit 5 + Testcontainers (SQL Server) |

---

## Modèle de domaine

```text
Livre          — titre, auteur, ISBN, totalCopies
Utilisateur    — nom, email
Emprunt        — utilisateur, livre, borrowedAt, returnedAt (null lorsqu’il est actif)
```

**Règles principales :**

1. `availableCopies = book.totalCopies − activeLoans`
2. Une tentative d’emprunt échoue lorsque `availableCopies == 0`
3. Le retour d’un emprunt renseigne `returnedAt` et libère l’exemplaire

---

## Structure du projet

```text
src/
  main/
    java/com/kata/library/
      domain/          — entités Book, User, Loan
      repository/      — repositories Spring Data JPA
      service/         — LoanService (emprunt / retour / disponibilité)
      web/             — contrôleurs REST
    resources/
      db/migration/    — scripts SQL Flyway
      application.properties
  test/
    java/com/kata/library/
      AbstractIntegrationTest.java   — classe de base Testcontainers
      BorrowIntegrationTest.java     — tests d’intégration
```

---

## Exécuter l’application en local

### 1 — Démarrer SQL Server avec Docker

```bash
docker run \
  -e ACCEPT_EULA=Y \
  -e MSSQL_SA_PASSWORD=Dev\!Passw0rd \
  -p 1433:1433 \
  --name sqlserver-dev \
  -d mcr.microsoft.com/mssql/server:2022-latest
```

### 2 — Créer la base de données

```bash
docker exec -it sqlserver-dev /opt/mssql-tools18/bin/sqlcmd \
  -S localhost -U sa -P 'Dev!Passw0rd' -No \
  -Q "CREATE DATABASE library"
```

### 3 — Compiler et lancer

```bash
mvn spring-boot:run
```

L’application démarre sur **http://localhost:8080**.

---

## Endpoints API

| Méthode | Chemin                     | Description                      |
| ------- | -------------------------- | -------------------------------- |
| `GET`   | `/books`                   | Lister tous les livres           |
| `GET`   | `/books/{id}/availability` | Nombre d’exemplaires disponibles |
| `POST`  | `/loans/borrow`            | Emprunter un livre               |
| `POST`  | `/loans/{id}/return`       | Retourner un emprunt             |

### Exemple — emprunter un livre

```bash
curl -X POST http://localhost:8080/loans/borrow \
  -H 'Content-Type: application/json' \
  -d '{"userId": 1, "bookId": 1}'
```

---

## Exécuter les tests

Les tests sont autonomes — Testcontainers télécharge une image Docker SQL Server et la démarre automatiquement. **Docker doit être en cours d’exécution.**

```bash
mvn test
```

---

## Exercice du kata

Partez de la structure existante et faites-la évoluer. Créez un système de file d’attente : au lieu de refuser l’emprunt d’un livre, l’utilisateur est désormais enregistré dans une file FIFO afin de pouvoir emprunter un exemplaire d’un livre dès qu’un exemplaire redevient disponible.

