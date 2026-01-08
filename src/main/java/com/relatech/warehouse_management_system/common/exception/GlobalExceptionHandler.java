package com.relatech.warehouse_management_system.common.exception;

import com.relatech.warehouse_management_system.goodsIn.exception.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    private ApiError buildError(HttpStatus status, Exception ex, HttpServletRequest req) {
        return new ApiError(LocalDateTime.now(), status.value(), status.getReasonPhrase(), ex.getMessage(),
                req.getRequestURI());
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiError> handleNotFound(ResourceNotFoundException ex, HttpServletRequest req) {
        log.error("ResourceNotFoundException at {} {} -> {}", req.getMethod(), req.getRequestURI(), ex.getMessage(),
                ex);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(buildError(HttpStatus.NOT_FOUND, ex, req));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiError> handleDuplicate(DataIntegrityViolationException ex, HttpServletRequest req) {
        log.error("DataIntegrityViolationException at {} {} -> {}", req.getMethod(), req.getRequestURI(),
                ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.CONFLICT).body(buildError(HttpStatus.CONFLICT, ex, req));
    }

    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ApiError> handleDuplicate(DuplicateResourceException ex, HttpServletRequest req) {
        log.error("DuplicateResourceException at {} {} -> {}", req.getMethod(), req.getRequestURI(), ex.getMessage(),
                ex);
        return ResponseEntity.status(HttpStatus.CONFLICT).body(buildError(HttpStatus.CONFLICT, ex, req));
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ApiError> handleConflict(ConflictException ex, HttpServletRequest req) {
        log.error("ConflictException at {} {} -> {}", req.getMethod(), req.getRequestURI(), ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.CONFLICT).body(buildError(HttpStatus.CONFLICT, ex, req));
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ApiError> handleValidation(ValidationException ex, HttpServletRequest req) {
        log.error("ValidationException at {} {} -> {}", req.getMethod(), req.getRequestURI(), ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body(buildError(HttpStatus.UNPROCESSABLE_ENTITY, ex, req));
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ApiError> handleBadRequest(BadRequestException ex, HttpServletRequest req) {
        log.error("BadRequestException at {} {} -> {}", req.getMethod(), req.getRequestURI(), ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(buildError(HttpStatus.BAD_REQUEST, ex, req));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleSpringValidation(MethodArgumentNotValidException ex, HttpServletRequest req) {
        String firstError = ex.getBindingResult().getFieldErrors().isEmpty()
                ? "Validation error"
                : ex.getBindingResult().getFieldErrors().getFirst().getDefaultMessage();

        log.error("MethodArgumentNotValidException at {} {} -> {}", req.getMethod(), req.getRequestURI(), firstError,
                ex);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiError(LocalDateTime.now(),
                        HttpStatus.BAD_REQUEST.value(),
                        HttpStatus.BAD_REQUEST.getReasonPhrase(),
                        firstError,
                        req.getRequestURI()));
    }

    @ExceptionHandler(InternalServerException.class)
    public ResponseEntity<ApiError> handleInternal(InternalServerException ex, HttpServletRequest req) {
        log.error("InternalServerException at {} {} -> {}", req.getMethod(), req.getRequestURI(), ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(buildError(HttpStatus.INTERNAL_SERVER_ERROR, ex, req));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiError> handleIllegalArgument(IllegalArgumentException ex, HttpServletRequest req) {
        log.error("IllegalArgumentException at {} {} -> {}", req.getMethod(), req.getRequestURI(), ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(buildError(HttpStatus.BAD_REQUEST, ex, req));
    }

    @ExceptionHandler(CannotAssignItemToGrnClosedException.class)
    public ResponseEntity<ApiError> handleCannotAssignItemToGrnClosedException(CannotAssignItemToGrnClosedException ex,
            HttpServletRequest req) {
        log.error("CannotAssignItemToGrnClosedException at {} {} -> {}", req.getMethod(), req.getRequestURI(),
                ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(buildError(HttpStatus.CONFLICT, ex, req));
    }

    @ExceptionHandler(CannotAssignCIToGrnItemInClosedOrPutawayStateException.class)
    public ResponseEntity<ApiError> handleCannotAssignCIToGrnItemInClosedOrPutawayStateException(
            CannotAssignCIToGrnItemInClosedOrPutawayStateException ex, HttpServletRequest req) {
        log.error("CannotAssignCIToGrnItemInClosedOrPutawayStateException at {} {} -> {}", req.getMethod(),
                req.getRequestURI(), ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(buildError(HttpStatus.CONFLICT, ex, req));
    }

    @ExceptionHandler(UpdateEntityException.class)
    public ResponseEntity<ApiError> handleUpdateEntityException(UpdateEntityException ex, HttpServletRequest req) {
        log.error("UpdateEntityException at {} {} -> {}", req.getMethod(), req.getRequestURI(), ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(buildError(HttpStatus.CONFLICT, ex, req));
    }

    @ExceptionHandler(GrnNotFoundException.class)
    public ResponseEntity<ApiError> handleGrnNotFoundException(GrnNotFoundException ex, HttpServletRequest req) {
        log.error("GrnNotFoundException at {} {} -> {}", req.getMethod(), req.getRequestURI(), ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(buildError(HttpStatus.NOT_FOUND, ex, req));
    }

    @ExceptionHandler(GrnItemNotFoundException.class)
    public ResponseEntity<ApiError> handleGrnItemNotFoundException(GrnItemNotFoundException ex,
            HttpServletRequest req) {
        log.error("GrnItemNotFoundException at {} {} -> {}", req.getMethod(), req.getRequestURI(), ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(buildError(HttpStatus.NOT_FOUND, ex, req));
    }

    @ExceptionHandler(GrnWithItemsException.class)
    public ResponseEntity<ApiError> handleGrnWithItemsException(GrnWithItemsException ex, HttpServletRequest req) {
        log.error("GrnWithItemsException at {} {} -> {}", req.getMethod(), req.getRequestURI(), ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(buildError(HttpStatus.CONFLICT, ex, req));
    }

    @ExceptionHandler(InvalidQuantityException.class)
    public ResponseEntity<ApiError> handleInvalidQuantityException(InvalidQuantityException ex,
            HttpServletRequest req) {
        log.error("InvalidQuantityException at {} {} -> {}", req.getMethod(), req.getRequestURI(), ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(buildError(HttpStatus.CONFLICT, ex, req));
    }

    @ExceptionHandler(InvalidStateTransitionException.class)
    public ResponseEntity<ApiError> handleInvalidStateTransitionException(InvalidStateTransitionException ex,
            HttpServletRequest req) {
        log.error("InvalidStateTransitionException at {} {} -> {}", req.getMethod(), req.getRequestURI(),
                ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(buildError(HttpStatus.CONFLICT, ex, req));
    }

    @ExceptionHandler(QuantityMismatchException.class)
    public ResponseEntity<ApiError> handleQuantityMismatchException(QuantityMismatchException ex,
            HttpServletRequest req) {
        log.error("QuantityMismatchException at {} {} -> {}", req.getMethod(), req.getRequestURI(), ex.getMessage(),
                ex);
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(buildError(HttpStatus.CONFLICT, ex, req));
    }

    @ExceptionHandler(QuantityNotAvailableException.class)
    public ResponseEntity<ApiError> handleQuantityNotAvailableException(QuantityNotAvailableException ex,
            HttpServletRequest req) {
        log.error("QuantityNotAvailableException at {} {} -> {}", req.getMethod(), req.getRequestURI(), ex.getMessage(),
                ex);
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(buildError(HttpStatus.CONFLICT, ex, req));
    }

    @ExceptionHandler(OverReceivedQuantityException.class)
    public ResponseEntity<ApiError> handleOverReceivedQuantityException(OverReceivedQuantityException ex,
            HttpServletRequest req) {
        log.error("OverReceivedQuantityException at {} {} -> {}", req.getMethod(), req.getRequestURI(), ex.getMessage(),
                ex);
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(buildError(HttpStatus.CONFLICT, ex, req));
    }

    @ExceptionHandler(MatchingDifferentCategoryException.class)
    public ResponseEntity<ApiError> MatchingDifferentCategoryException(MatchingDifferentCategoryException ex,
            HttpServletRequest req) {
        log.error("MatchingDifferentCategoryException at {} {} -> {}", req.getMethod(), req.getRequestURI(),
                ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(buildError(HttpStatus.CONFLICT, ex, req));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGeneric(Exception ex, HttpServletRequest req) {
        log.error("Unhandled exception at {} {} -> {}", req.getMethod(), req.getRequestURI(), ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(buildError(HttpStatus.INTERNAL_SERVER_ERROR, ex, req));
    }
}
