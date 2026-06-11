package com.kata.library.service;

import com.kata.library.domain.Book;
import com.kata.library.domain.Loan;
import com.kata.library.domain.User;
import com.kata.library.repository.BookRepository;
import com.kata.library.repository.LoanRepository;
import com.kata.library.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class LoanService {

    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    private final LoanRepository loanRepository;

    public LoanService(BookRepository bookRepository,
                       UserRepository userRepository,
                       LoanRepository loanRepository) {
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
        this.loanRepository = loanRepository;
    }

    /**
     * Borrow a book for a user.
     * Fails with {@link NoCopyAvailableException} if all copies are already loaned out.
     */
    public Loan borrowBook(Long userId, Long bookId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new IllegalArgumentException("Book not found: " + bookId));

        long activeLoans = loanRepository.countActiveLoansForBook(book);
        if (activeLoans >= book.getTotalCopies()) {
            throw new NoCopyAvailableException(book.getTitle());
        }

        Loan loan = new Loan(user, book);
        return loanRepository.save(loan);
    }

    /**
     * Return a previously borrowed book.
     */
    public Loan returnBook(Long loanId) {
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new IllegalArgumentException("Loan not found: " + loanId));
        loan.returnBook();
        return loanRepository.save(loan);
    }

    /**
     * How many copies of a book are currently available.
     */
    @Transactional(readOnly = true)
    public int availableCopies(Long bookId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new IllegalArgumentException("Book not found: " + bookId));
        long active = loanRepository.countActiveLoansForBook(book);
        return (int) (book.getTotalCopies() - active);
    }

    @Transactional(readOnly = true)
    public List<Book> listBooks() {
        return bookRepository.findAll();
    }
}
