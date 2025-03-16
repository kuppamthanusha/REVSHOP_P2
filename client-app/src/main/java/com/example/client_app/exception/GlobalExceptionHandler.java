package com.example.client_app.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {


    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<?> handleResourceNotFoundException(ResourceNotFoundException ex){

        String errorMessage = ex.getMessage();

        return new ResponseEntity<>(errorMessage, HttpStatus.NOT_FOUND);
    }

      @ExceptionHandler(SizeNotFoundException.class)
        public ResponseEntity<String> handleSizeNotFound(SizeNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }

        @ExceptionHandler(ProductNotFoundException.class)
        public ResponseEntity<String> handleProductNotFound(ProductNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }
}
