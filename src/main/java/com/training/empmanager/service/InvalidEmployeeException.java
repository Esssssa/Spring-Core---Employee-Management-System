package com.training.empmanager.service;

// Unchecked (extends RuntimeException), so callers are not forced to catch it.
public class InvalidEmployeeException extends RuntimeException {
    public InvalidEmployeeException(String message) {
       super(message);
    }
}
