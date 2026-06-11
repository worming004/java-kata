-- V2: seed sample data for the library kata

INSERT INTO books (title, author, isbn, total_copies) VALUES
    ('Clean Code', 'Robert C. Martin', '9780132350884', 3),
    ('The Pragmatic Programmer', 'David Thomas & Andrew Hunt', '9780135957059', 2),
    ('Domain-Driven Design', 'Eric Evans', '9780321125217', 1);

INSERT INTO users (name, email) VALUES
    ('Alice', 'alice@example.com'),
    ('Bob',   'bob@example.com');
