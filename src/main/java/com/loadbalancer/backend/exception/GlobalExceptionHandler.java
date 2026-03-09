package com.loadbalancer.backend.exception;



import com.loadbalancer.backend.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.time.LocalDateTime;




@RestControllerAdvice
public class GlobalExceptionHandler {


     // Catches Malformed JSON requests (e.g., wrong data types, missing commas)

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleMalformedJsonException(HttpMessageNotReadableException ex) {
        ErrorResponse response = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                "Malformed JSON Request",
                "The request body contains invalid data types or syntax errors. Please verify your JSON payload."
        );
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }


     // Catches NullPointerExceptions, Database connection drops, or any other unhandled crash

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        // In a real production app, you would log 'ex.getMessage()' to your server logs here

        ErrorResponse response = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Internal Server Error",
                "An unexpected system error occurred while processing the dispatch plan."
        );
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}