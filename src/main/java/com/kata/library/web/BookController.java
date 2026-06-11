package com.kata.library.web;

import com.kata.library.domain.Book;
import com.kata.library.service.LoanService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/books")
public class BookController {

    private final LoanService loanService;

    public BookController(LoanService loanService) {
        this.loanService = loanService;
    }

    @GetMapping
    public List<Book> listBooks() {
        return loanService.listBooks();
    }

    @GetMapping("/{bookId}/availability")
    public AvailabilityResponse availability(@PathVariable Long bookId) {
        int copies = loanService.availableCopies(bookId);
        return new AvailabilityResponse(bookId, copies);
    }

    public record AvailabilityResponse(Long bookId, int availableCopies) {}
}
