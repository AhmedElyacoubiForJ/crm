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

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<APIResponse<Object>> handleResourceNotFoundException(ResourceNotFoundException ex) {
        List<ValidationError> errors = List.of(new ValidationError("Resource", ex.getMessage()));

        APIResponse<Object> response = APIResponse.<Object>builder()
                .status("error")
                .statusCode(HttpStatus.NOT_FOUND.value())
                .errors(errors)
                .build();

        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<APIResponse<Object>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        List<ValidationError> errors = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> new ValidationError(error.getField(), error.getDefaultMessage()))
                .collect(Collectors.toList());

        APIResponse<Object> response = APIResponse.<Object>builder()
                .status("error")
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .message("Validierungsfehler")
                .errors(errors)
                .build();

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<APIResponse<Object>> handleGeneralException(Exception ex) {
        List<ValidationError> errors = List.of(new ValidationError("Exception", "An unexpected error occurred: " + ex.getMessage()));

        APIResponse<Object> response = APIResponse.<Object>builder()
                .status("error")
                .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .errors(errors)
                .build();

        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<APIResponse<Object>> handleIllegalArgumentException(IllegalArgumentException ex) {
        List<ValidationError> errors = List.of(new ValidationError("Argument", ex.getMessage()));

        APIResponse<Object> response = APIResponse.<Object>builder()
                .status("error")
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .errors(errors)
                .build();

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
}
