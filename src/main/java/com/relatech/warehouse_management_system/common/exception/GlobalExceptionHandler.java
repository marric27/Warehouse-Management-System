package com.relatech.warehouse_management_system.common.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    private ApiError buildError(HttpStatus status, Exception ex, HttpServletRequest req) {
        return new ApiError(LocalDateTime.now(), status.value(), status.getReasonPhrase(), ex.getMessage(), req.getRequestURI());
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiError> handleNotFound(ResourceNotFoundException ex, HttpServletRequest req) {
        log.error("ResourceNotFoundException at {} {} -> {}", req.getMethod(), req.getRequestURI(), ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(buildError(HttpStatus.NOT_FOUND, ex, req));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiError> handleDuplicate(DataIntegrityViolationException ex, HttpServletRequest req) {
        log.error("DataIntegrityViolationException at {} {} -> {}", req.getMethod(), req.getRequestURI(), ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.CONFLICT).body(buildError(HttpStatus.CONFLICT, ex, req));
    }

    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ApiError> handleDuplicate(DuplicateResourceException ex, HttpServletRequest req) {
        log.error("DuplicateResourceException at {} {} -> {}", req.getMethod(), req.getRequestURI(), ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.CONFLICT).body(buildError(HttpStatus.CONFLICT, ex, req));
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ApiError> handleConflict(ConflictException ex, HttpServletRequest req) {
        log.error("ConflictException at {} {} -> {}", req.getMethod(), req.getRequestURI(), ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.CONFLICT).body(buildError(HttpStatus.CONFLICT, ex, req));
    }

    @ExceptionHandler(GrnWithItemsException.class)
    public ResponseEntity<ApiError> handleGrnWithItems(GrnWithItemsException ex, HttpServletRequest req) {
        log.error("GrnWithItemsException at {} {} -> {}", req.getMethod(), req.getRequestURI(), ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.CONFLICT).body(buildError(HttpStatus.CONFLICT, ex, req));
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ApiError> handleValidation(ValidationException ex, HttpServletRequest req) {
        log.error("ValidationException at {} {} -> {}", req.getMethod(), req.getRequestURI(), ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(buildError(HttpStatus.UNPROCESSABLE_ENTITY, ex, req));
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
                : ex.getBindingResult().getFieldErrors().get(0).getDefaultMessage();

        log.error("MethodArgumentNotValidException at {} {} -> {}", req.getMethod(), req.getRequestURI(), firstError, ex);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiError(LocalDateTime.now(),
                        HttpStatus.BAD_REQUEST.value(),
                        HttpStatus.BAD_REQUEST.getReasonPhrase(),
                        firstError,
                        req.getRequestURI()));
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ApiError> handleUnauthorized(UnauthorizedException ex, HttpServletRequest req) {
        log.error("UnauthorizedException at {} {} -> {}", req.getMethod(), req.getRequestURI(), ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(buildError(HttpStatus.UNAUTHORIZED, ex, req));
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ApiError> handleForbidden(ForbiddenException ex, HttpServletRequest req) {
        log.error("ForbiddenException at {} {} -> {}", req.getMethod(), req.getRequestURI(), ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(buildError(HttpStatus.FORBIDDEN, ex, req));
    }

    @ExceptionHandler(InternalServerException.class)
    public ResponseEntity<ApiError> handleInternal(InternalServerException ex, HttpServletRequest req) {
        log.error("InternalServerException at {} {} -> {}", req.getMethod(), req.getRequestURI(), ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(buildError(HttpStatus.INTERNAL_SERVER_ERROR, ex, req));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiError> handleIllegalArgument(IllegalArgumentException ex, HttpServletRequest req) {
        log.error("IllegalArgumentException at {} {} -> {}", req.getMethod(), req.getRequestURI(), ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(buildError(HttpStatus.BAD_REQUEST, ex, req));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGeneric(Exception ex, HttpServletRequest req) {
        log.error("Unhandled exception at {} {} -> {}", req.getMethod(), req.getRequestURI(), ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(buildError(HttpStatus.INTERNAL_SERVER_ERROR, ex, req));
    }
}
