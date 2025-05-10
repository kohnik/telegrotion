package com.fizzly.backend.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;

@ControllerAdvice
public class FizzlyExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler({FizzlyGlobalException.class, RuntimeException.class})
    public ResponseEntity<ExceptionResponse> handleGlobalAppException(FizzlyGlobalException ex, HttpServletRequest request) {
        return ResponseEntity.internalServerError().body(buildException(ex, request));
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ExceptionResponse> handleEntityNotFoundException(FizzlyGlobalException ex, HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(buildException(ex, request));
    }

    private ExceptionResponse buildException(RuntimeException ex, HttpServletRequest request) {
        ExceptionResponse response = new ExceptionResponse();
        response.setMessage(ex.getMessage());
        response.setTimestamp(LocalDateTime.now());
        response.setPath(request.getServletPath());

        return response;
    }
}
