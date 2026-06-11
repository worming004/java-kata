package com.kata.library.domain;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "loans")
public class Loan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(optional = false)
    @JoinColumn(name = "book_id")
    private Book book;

    @Column(nullable = false)
    private Instant borrowedAt;

    private Instant returnedAt;

    protected Loan() {}

    public Loan(User user, Book book) {
        this.user = user;
        this.book = book;
        this.borrowedAt = Instant.now();
    }

    public boolean isActive() {
        return returnedAt == null;
    }

    public void returnBook() {
        if (!isActive()) {
            throw new IllegalStateException("This loan has already been returned.");
        }
        this.returnedAt = Instant.now();
    }

    public Long getId() { return id; }
    public User getUser() { return user; }
    public Book getBook() { return book; }
    public Instant getBorrowedAt() { return borrowedAt; }
    public Instant getReturnedAt() { return returnedAt; }
}
