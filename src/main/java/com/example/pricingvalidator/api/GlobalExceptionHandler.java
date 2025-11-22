package com.example.pricingvalidator.api;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiError> handleBadRequest(IllegalArgumentException ex, WebRequest req) {
        log.warn("Bad request: {}", ex.getMessage(), ex);
        ApiError e = new ApiError(Instant.now(), HttpStatus.BAD_REQUEST.value(),
                "Bad Request", ex.getMessage(), req.getDescription(false), null);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException ex, WebRequest req) {
        List<String> details = ex.getBindingResult().getFieldErrors()
                .stream().map(f -> f.getField() + ": " + f.getDefaultMessage()).collect(Collectors.toList());
        ApiError e = new ApiError(Instant.now(), HttpStatus.BAD_REQUEST.value(),
                "Validation Failed", "Request validation failed", req.getDescription(false), details);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e);
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ApiError> handleFileTooLarge(MaxUploadSizeExceededException ex, WebRequest req) {
        log.warn("File too large: {}", ex.getMessage());
        ApiError e = new ApiError(Instant.now(), HttpStatus.PAYLOAD_TOO_LARGE.value(),
                "Payload Too Large", "Uploaded file is too large", req.getDescription(false), null);
        return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE).body(e);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleAll(Exception ex, WebRequest req) {
        log.error("Unhandled error: {}", ex.getMessage(), ex);
        ApiError e = new ApiError(Instant.now(), HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Internal Server Error", "An unexpected error occurred", req.getDescription(false), null);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e);
    }
}
