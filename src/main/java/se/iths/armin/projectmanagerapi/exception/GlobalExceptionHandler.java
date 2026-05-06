package se.iths.armin.projectmanagerapi.exception;

import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {


    @ExceptionHandler(DuplicateFoundException.class)
    public String handleDuplicateFoundException(DuplicateFoundException ex) {
        return ex.getMessage();
    }
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public String handleValidationException(MethodArgumentNotValidException exception) {
        FieldError firstError = exception.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .orElse(null);
        String message = firstError != null
                ? firstError.getDefaultMessage()
                : exception.getMessage();

        return message;
    }
}
