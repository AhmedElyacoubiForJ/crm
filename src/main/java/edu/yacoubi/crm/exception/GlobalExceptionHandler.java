package edu.yacoubi.crm.exception;

import edu.yacoubi.crm.dto.APIResponse;
import edu.yacoubi.crm.dto.ValidationError;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.List;
import java.util.stream.Collectors;

import static edu.yacoubi.crm.util.ApiResponseHelper.getDTOAPIResponse;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<APIResponse<Object>> handleResourceNotFoundException(
            ResourceNotFoundException ex) {
        List<ValidationError> errors = List.of(new ValidationError("Resource", ex.getMessage()));

        APIResponse<Object> response = getDTOAPIResponse("Resource not found", "error",
                HttpStatus.NOT_FOUND, errors);

        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<APIResponse<Object>> handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        List<ValidationError> errors = ex.getBindingResult().getFieldErrors().stream()
                .map(
                        error -> new ValidationError(error.getField(), error.getDefaultMessage())
                ).collect(Collectors.toList());

        APIResponse<Object> response = getDTOAPIResponse("Validation error", "error",
                HttpStatus.BAD_REQUEST, errors);

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<APIResponse<Object>> handleGeneralException(Exception ex) {
        List<ValidationError> errors = List.of(
                new ValidationError("Exception", "An unexpected error occurred: " + ex.getMessage())
        );

        APIResponse<Object> response = getDTOAPIResponse("Internal server error", "error",
                        HttpStatus.INTERNAL_SERVER_ERROR, errors);

        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<APIResponse<Object>> handleIllegalArgumentException(IllegalArgumentException ex) {
        List<ValidationError> errors = List.of(new ValidationError("Argument", ex.getMessage()));

        APIResponse<Object> response = getDTOAPIResponse("Bad request", "error",
                        HttpStatus.BAD_REQUEST, errors);

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
}
