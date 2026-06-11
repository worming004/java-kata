package com.kata.library.repository;

import com.kata.library.domain.Book;
import com.kata.library.domain.Loan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface LoanRepository extends JpaRepository<Loan, Long> {

    @Query("SELECT COUNT(l) FROM Loan l WHERE l.book = :book AND l.returnedAt IS NULL")
    long countActiveLoansForBook(@Param("book") Book book);
}
