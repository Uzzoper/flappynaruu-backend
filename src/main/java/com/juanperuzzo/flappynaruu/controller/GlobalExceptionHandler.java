package com.juanperuzzo.flappynaruu.controller;

import com.juanperuzzo.flappynaruu.exception.InvalidNicknameException;
import com.juanperuzzo.flappynaruu.exception.InvalidScoreException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private Map<String, Object> error(String code, String message) {
        return Map.of(
                "timestamp", Instant.now(),
                "error", code,
                "message", message
        );
    }

    @ExceptionHandler(InvalidNicknameException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidNickname(InvalidNicknameException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(error("INVALID_NICKNAME", ex.getMessage()));
    }

    @ExceptionHandler(InvalidScoreException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidScore(InvalidScoreException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(error("INVALID_SCORE", ex.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericError(Exception ex) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(error("INTERNAL_ERROR", ex.getMessage()));
    }
}
