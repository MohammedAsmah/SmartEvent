package com.SmartEvent.SmartEvent.Exception;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

// This class will "watch" all controllers and handle exceptions
@ControllerAdvice
public class GlobalExceptionHandler {

    // Handle specific exception
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgument(IllegalArgumentException ex) {
        return new ResponseEntity<>("Invalid argument: " + ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

//    @ExceptionHandler(ForbiddenException.class)
//    public ResponseEntity<String> handleResponseStatusException(ResponseStatusException ex) {
//        return ResponseEntity
//                .status(ex.getStatusCode())
//                .body("Error: " + ex.getReason());
//    }


    @ExceptionHandler({
            AuthenticationException.class,
            BadCredentialsException.class,
            ExpiredJwtException.class,
            JwtException.class,
            ForbiddenException.class
    })
    public ResponseEntity<String> handleUnauthorized(Exception ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ex.getMessage());
    }


    // Handle generic exceptions (fallback)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleException(Exception ex) {
        return new ResponseEntity<>("Error: " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
