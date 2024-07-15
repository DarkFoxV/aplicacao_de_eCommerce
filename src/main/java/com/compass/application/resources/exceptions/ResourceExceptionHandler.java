package com.compass.application.resources.exceptions;

import com.compass.application.services.exceptions.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@ControllerAdvice
@RestControllerAdvice
public class ResourceExceptionHandler {

    @ExceptionHandler(ObjectNotFoundException.class)
    public ResponseEntity<StandardError> handleObjectNotFoundException(ObjectNotFoundException e, HttpServletRequest request) {
        return handleException("Object not found", HttpStatus.NOT_FOUND, e, request);
    }

    @ExceptionHandler(ProductNotAvailableException.class)
    public ResponseEntity<StandardError> handleProductNotAvailableException(ProductNotAvailableException e, HttpServletRequest request) {
        return handleException("Product not available", HttpStatus.CONFLICT, e, request);
    }

    @ExceptionHandler(ProductInSaleException.class)
    public ResponseEntity<StandardError> handleProductInSaleException(ProductInSaleException e, HttpServletRequest request) {
        return handleException("Cannot delete product because it is associated with existing sale", HttpStatus.CONFLICT, e, request);
    }

    @ExceptionHandler(InsufficientStockException.class)
    public ResponseEntity<StandardError> handleInsufficientStockException(InsufficientStockException e, HttpServletRequest request) {
        return handleException("Insufficient stock", HttpStatus.CONFLICT, e, request);
    }

    @ExceptionHandler(ObjectAlreadyExistsException.class)
    public ResponseEntity<StandardError> handleProductAlreadyExistsException(ObjectAlreadyExistsException e, HttpServletRequest request) {
        return handleException("Product Already Exists", HttpStatus.CONFLICT, e, request);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<StandardError> handleMethodNotValidException(MethodArgumentNotValidException e, HttpServletRequest request) {
        List<String> errors = e.getBindingResult().getAllErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.toList());
        String errorMessage = String.join(", ", errors);
        return handleException("Argument Not Valid", HttpStatus.BAD_REQUEST, new IllegalArgumentException(errorMessage), request);
    }

    @ExceptionHandler(InvalidDataAccessApiUsageException.class)
    public ResponseEntity<StandardError> handleInvalidDataAccessApiUsageException(InvalidDataAccessApiUsageException e, HttpServletRequest request) {
        return handleException("Argument Not Valid", HttpStatus.BAD_REQUEST, e, request);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<StandardError> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e, HttpServletRequest request) {
        return handleException("Argument Not Valid", HttpStatus.BAD_REQUEST, e, request);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<StandardError> handleHttpMessageNotReadableException(HttpMessageNotReadableException e, HttpServletRequest request) {
        return handleException("Argument Not Valid", HttpStatus.BAD_REQUEST, e, request);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<StandardError> handleNoResourceFoundException(NoResourceFoundException e, HttpServletRequest request) {
        return handleException("Invalid Url", HttpStatus.BAD_REQUEST, e, request);
    }

    private ResponseEntity<StandardError> handleException(String error, HttpStatus status, Exception e, HttpServletRequest request) {
        StandardError err = new StandardError(Instant.now(), status.value(), error, e.getMessage(), request.getRequestURI());
        return ResponseEntity.status(status).body(err);
    }

}
