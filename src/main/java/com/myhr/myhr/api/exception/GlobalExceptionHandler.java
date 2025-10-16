package com.myhr.myhr.api.exception;

import com.myhr.myhr.domain.exception.ApiException;
import com.myhr.myhr.domain.exception.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


import java.time.Instant;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {


    @ExceptionHandler(ApiException.class)
    public ResponseEntity<Map<String, Object>> handleApi(ApiException ex, HttpServletRequest req) {
        var code = ex.getCode();

        var msg = (ex.getMessage() != null && !ex.getMessage().isBlank())
                ? ex.getMessage()
                : code.message;

        log.warn("ApiException at {} [{}]: {}", safePath(req), code.name(), msg, ex);

        return ResponseEntity.status(code.status).body(Map.of(
                "timestamp", Instant.now().toString(),
                "error", code.name(),
                "message", msg,
                "path", safePath(req)
        ));
    }



    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleOther(Exception ex, HttpServletRequest req) {
        var code = ErrorCode.INTERNAL_ERROR;
        var root = rootCause(ex);
        var msg = root.getClass().getSimpleName() + ": " + String.valueOf(root.getMessage());


        log.error("Unhandled exception at {} -> {}", safePath(req), msg, ex);

        return ResponseEntity.status(code.status).body(Map.of(
                "timestamp", Instant.now().toString(),
                "error", code.name(),
                "message", msg,
                "path", safePath(req)
        ));
    }

    private Throwable rootCause(Throwable t) {
        Throwable r = t;
        while (r.getCause() != null) r = r.getCause();
        return r;
    }

    private String safePath(HttpServletRequest req) {
        return req != null ? req.getRequestURI() : "-";
    }
}
