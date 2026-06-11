package com.kata.library.service;

public class NoCopyAvailableException extends RuntimeException {
    public NoCopyAvailableException(String bookTitle) {
        super("No copy available for book: " + bookTitle);
    }
}
