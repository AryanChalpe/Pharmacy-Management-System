package com.pharmacy.management.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void handleMedicineNotFound_ShouldReturnNotFound() {
        MedicineNotFoundException ex = new MedicineNotFoundException("Not found");
        ResponseEntity<?> response = handler.handleMedicineNotFound(ex);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void handleInsufficientStock_ShouldReturnBadRequest() {
        InsufficientStockException ex = new InsufficientStockException("Insufficient");
        ResponseEntity<?> response = handler.handleInsufficientStock(ex);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void handleIllegalArgument_ShouldReturnBadRequest() {
        IllegalArgumentException ex = new IllegalArgumentException("Invalid");
        ResponseEntity<?> response = handler.handleIllegalArgument(ex);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void handleRuntime_ShouldReturnInternalServerError() {
        RuntimeException ex = new RuntimeException("Runtime error");
        ResponseEntity<?> response = handler.handleRuntime(ex);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }
}
