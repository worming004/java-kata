package com.kata.library.web;

import com.kata.library.domain.Loan;
import com.kata.library.service.LoanService;
import com.kata.library.service.NoCopyAvailableException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/loans")
public class LoanController {

    private final LoanService loanService;

    public LoanController(LoanService loanService) {
        this.loanService = loanService;
    }

    @PostMapping("/borrow")
    public ResponseEntity<?> borrow(@RequestBody BorrowRequest request) {
        try {
            Loan loan = loanService.borrowBook(request.userId(), request.bookId());
            return ResponseEntity.ok(new LoanResponse(loan));
        } catch (NoCopyAvailableException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    @PostMapping("/{loanId}/return")
    public ResponseEntity<?> returnBook(@PathVariable Long loanId) {
        try {
            Loan loan = loanService.returnBook(loanId);
            return ResponseEntity.ok(new LoanResponse(loan));
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    public record BorrowRequest(Long userId, Long bookId) {}

    public record LoanResponse(Long loanId, Long userId, Long bookId, String borrowedAt, String returnedAt) {
        public LoanResponse(Loan loan) {
            this(
                loan.getId(),
                loan.getUser().getId(),
                loan.getBook().getId(),
                loan.getBorrowedAt().toString(),
                loan.getReturnedAt() != null ? loan.getReturnedAt().toString() : null
            );
        }
    }
}
