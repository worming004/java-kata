package com.kata.library;

import com.kata.library.domain.Book;
import com.kata.library.domain.Loan;
import com.kata.library.domain.User;
import com.kata.library.repository.BookRepository;
import com.kata.library.repository.UserRepository;
import com.kata.library.service.LoanService;
import com.kata.library.service.NoCopyAvailableException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.*;

/**
 * Integration tests for the borrow flow.
 *
 * These tests run against a real SQL Server instance provisioned by Testcontainers.
 */
@Transactional
class BorrowIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private LoanService loanService;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private UserRepository userRepository;

    private Book bookWithTwoCopies;
    private Book bookWithOneCopy;
    private User alice;
    private User bob;

    @BeforeEach
    void setUp() {
        bookWithTwoCopies = bookRepository.save(new Book("Effective Java", "Joshua Bloch", "9780134685991", 2));
        bookWithOneCopy   = bookRepository.save(new Book("Refactoring", "Martin Fowler", "9780201485677", 1));
        alice = userRepository.save(new User("Alice", "alice-test@example.com"));
        bob   = userRepository.save(new User("Bob",   "bob-test@example.com"));
    }

    @Test
    void borrowBook_succeeds_whenCopyIsAvailable() {
        Loan loan = loanService.borrowBook(alice.getId(), bookWithOneCopy.getId());

        assertThat(loan.getId()).isNotNull();
        assertThat(loan.getUser().getId()).isEqualTo(alice.getId());
        assertThat(loan.getBook().getId()).isEqualTo(bookWithOneCopy.getId());
        assertThat(loan.getBorrowedAt()).isNotNull();
        assertThat(loan.getReturnedAt()).isNull();
    }

    @Test
    void borrowBook_fails_whenNoCopyAvailable() {
        // Both copies of bookWithTwoCopies are borrowed
        loanService.borrowBook(alice.getId(), bookWithTwoCopies.getId());
        loanService.borrowBook(bob.getId(),   bookWithTwoCopies.getId());

        User carol = userRepository.save(new User("Carol", "carol-test@example.com"));

        assertThatThrownBy(() -> loanService.borrowBook(carol.getId(), bookWithTwoCopies.getId()))
                .isInstanceOf(NoCopyAvailableException.class)
                .hasMessageContaining("Effective Java");
    }

    @Test
    void availableCopies_decreasesAfterBorrow() {
        assertThat(loanService.availableCopies(bookWithTwoCopies.getId())).isEqualTo(2);

        loanService.borrowBook(alice.getId(), bookWithTwoCopies.getId());
        assertThat(loanService.availableCopies(bookWithTwoCopies.getId())).isEqualTo(1);

        loanService.borrowBook(bob.getId(), bookWithTwoCopies.getId());
        assertThat(loanService.availableCopies(bookWithTwoCopies.getId())).isEqualTo(0);
    }

    @Test
    void availableCopies_increasesAfterReturn() {
        Loan loan = loanService.borrowBook(alice.getId(), bookWithOneCopy.getId());
        assertThat(loanService.availableCopies(bookWithOneCopy.getId())).isEqualTo(0);

        loanService.returnBook(loan.getId());
        assertThat(loanService.availableCopies(bookWithOneCopy.getId())).isEqualTo(1);
    }

    @Test
    void returnedLoan_isNoLongerActive() {
        Loan loan = loanService.borrowBook(alice.getId(), bookWithOneCopy.getId());

        Loan returned = loanService.returnBook(loan.getId());

        assertThat(returned.isActive()).isFalse();
        assertThat(returned.getReturnedAt()).isNotNull();
    }
}
