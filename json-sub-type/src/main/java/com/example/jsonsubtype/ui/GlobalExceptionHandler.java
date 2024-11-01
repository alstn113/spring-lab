package com.example.jsonsubtype.ui;

import com.fasterxml.jackson.databind.exc.InvalidTypeIdException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(InvalidTypeIdException.class)
    public ResponseEntity<String> handleInvalidTypeIdException(InvalidTypeIdException ex) {
        String errorMessage = "유효하지 않은 질문 타입입니다: " + ex.getTypeId();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage);
    }
}
