// File: src/main/java/com/portfolio/blog/web/RestExceptionHandler.java
package com.portfolio.blog.web;

import org.springframework.http.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;

import java.time.OffsetDateTime;
import java.util.*;

@RestControllerAdvice
public class RestExceptionHandler {

    record ApiError(String code, String message, String path, OffsetDateTime timestamp, Map<String, Object> details) {}

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiError> handleIllegalArg(IllegalArgumentException ex, WebRequest req) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ApiError("NOT_FOUND", ex.getMessage(), path(req), OffsetDateTime.now(), Map.of()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException ex, WebRequest req) {
        var errs = new LinkedHashMap<String, String>();
        ex.getBindingResult().getFieldErrors()
                .forEach(fe -> errs.put(fe.getField(), fe.getDefaultMessage()));
        return ResponseEntity.badRequest()
                .body(new ApiError("VALIDATION_ERROR", "Invalid input", path(req), OffsetDateTime.now(), Map.of("errors", errs)));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleOther(Exception ex, WebRequest req) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiError("INTERNAL_ERROR", "Unexpected error", path(req), OffsetDateTime.now(), Map.of()));
    }

    private String path(WebRequest req) {
        return Optional.ofNullable(req.getDescription(false)).orElse("").replace("uri=", "");
    }
}
