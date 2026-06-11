-- V1: initial schema for the library kata

CREATE TABLE books (
    id            BIGINT IDENTITY(1,1) PRIMARY KEY,
    title         NVARCHAR(255) NOT NULL,
    author        NVARCHAR(255) NOT NULL,
    isbn          NVARCHAR(20)  NOT NULL UNIQUE,
    total_copies  INT           NOT NULL CHECK (total_copies >= 0)
);

CREATE TABLE users (
    id    BIGINT IDENTITY(1,1) PRIMARY KEY,
    name  NVARCHAR(255) NOT NULL,
    email NVARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE loans (
    id           BIGINT IDENTITY(1,1) PRIMARY KEY,
    user_id      BIGINT       NOT NULL REFERENCES users(id),
    book_id      BIGINT       NOT NULL REFERENCES books(id),
    borrowed_at  DATETIMEOFFSET NOT NULL,
    returned_at  DATETIMEOFFSET NULL
);
