package com.codewithmosh.store.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class )
    public ResponseEntity<Map<String,String>> handleValidationErrors(
            MethodArgumentNotValidException exception
    ){
        Map<String,String> storeErrors = new HashMap<>();
//        exception.getBindingResult().getFieldErrors()
//                .stream().map(exception1->storeErrors.put(exception1.getField(), exception1.getDefaultMessage()));
        exception.getBindingResult().getFieldErrors().forEach(fieldError ->
                storeErrors.put(fieldError.getField(), fieldError.getDefaultMessage()));


//            return ResponseEntity.badRequest().body(exception.getMessage());
        System.out.println(storeErrors);
        return ResponseEntity.badRequest().body(storeErrors);
        // now we would want to display errors in a suitable format.
    }
}
