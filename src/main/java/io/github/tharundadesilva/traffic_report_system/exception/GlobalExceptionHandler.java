package io.github.tharundadesilva.traffic_report_system.exception;

import io.github.tharundadesilva.traffic_report_system.model.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.apache.coyote.BadRequestException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Object>> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest req) {
        Map<String, String> fieldErrors = new HashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(err -> fieldErrors.put(err.getField(), err.getDefaultMessage()));
        Map<String, Object> error = Map.of(
                "code", "VALIDATION_ERROR",
                "message", "Validation failed",
                "fields", fieldErrors
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(error, req.getRequestURI()));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse<Object>> handleConstraintViolation(ConstraintViolationException ex, HttpServletRequest req) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(Map.of("code", "VALIDATION_ERROR", "message", ex.getMessage()), req.getRequestURI()));
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiResponse<Object>> handleBadCredentials(BadCredentialsException ex, HttpServletRequest req) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.error(Map.of("code", "UNAUTHORIZED", "message", ex.getMessage()), req.getRequestURI()));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<Object>> handleAccessDenied(AccessDeniedException ex, HttpServletRequest req) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ApiResponse.error(Map.of("code", "FORBIDDEN", "message", ex.getMessage()), req.getRequestURI()));
    }

    @ExceptionHandler(ChangeSetPersister.NotFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleNotFound(ChangeSetPersister.NotFoundException ex, HttpServletRequest req) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(Map.of("code", "NOT_FOUND", "message", ex.getMessage()), req.getRequestURI()));
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ApiResponse<Object>> handleBadRequest(BadRequestException ex, HttpServletRequest req) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(Map.of("code", "BAD_REQUEST", "message", ex.getMessage()), req.getRequestURI()));
    }

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ApiResponse<Object>> handleApi(ApiException ex, HttpServletRequest req) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ApiResponse.error(Map.of("code", "CONFLICT", "message", ex.getMessage()), req.getRequestURI()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleOther(Exception ex, HttpServletRequest req) {
        String message = ex.getMessage() != null ? ex.getMessage() : "An unexpected error occurred";
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(Map.of("code", "INTERNAL_ERROR", "message", message), req.getRequestURI()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Object>> handleIllegalArgument(IllegalArgumentException ex, HttpServletRequest req) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(Map.of("code", "BAD_REQUEST", "message", ex.getMessage()), req.getRequestURI()));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiResponse<Object>> handleDataIntegrityViolation(
            DataIntegrityViolationException ex, HttpServletRequest req) {

        String code = "DATA_ERROR";
        String message = "A database error occurred.";

        String dbMsg = ex.getMostSpecificCause().getMessage();

        if (dbMsg != null) {
            if (dbMsg.contains("Duplicate entry") && dbMsg.contains("users.email")) {
                code = "DUPLICATE_EMAIL";
                message = "An account with this email already exists. Please use a different email.";
            } else if (dbMsg.contains("Duplicate entry") && dbMsg.contains("users.username")) {
                code = "DUPLICATE_USERNAME";
                message = "This username is already taken. Please choose another.";
            } else if (dbMsg.contains("foreign key constraint fails")) {
                code = "CONSTRAINT_VIOLATION";
                message = "Cannot delete or update this record because it is referenced by other records.";
            }
        }

        String detail = dbMsg != null ? dbMsg : "No additional details available";
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(Map.of("code", code, "message", message, "detail", detail), req.getRequestURI()));
    }



}