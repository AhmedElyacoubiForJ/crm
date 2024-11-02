## Dokumentation: Exception Workflow Dokumentation

## Inhaltsverzeichnis
1. [Übersicht](#übersicht)
2. [Workflow](#workflow)
3. [Vorteile des globalen Exception Handlers](#vorteile)


## Übersicht
> Diese Dokumentation beschreibt den Workflow für die Fehlerbehandlung in der Anwendung,
> insbesondere die Verwendung des globalen Exception Handlers.

## Workflow
1. **Fehlerquellen**
    - Im Code können verschiedene Fehler auftreten, z.B. bei Datenbankoperationen, ungültigen Eingaben oder unerwarteten Zuständen.
2. **Exception werfen**
   - Wenn ein Fehler auftritt, wird eine entsprechende Ausnahme (Exception) geworfen. Beispiel:
   <details>
   <summary style="color: blue"><strong>Klicke hier, um den Code anzuzeigen</strong></summary>
   
   ```java
   @Override
   public void deleteCustomer(Long id) {
       log.info("Deleting Customer with ID: {}", id);
       ensureCustomerExists(id);
       customerRepository.deleteById(id);
   }
   
   @Override
   public void ensureCustomerExists(Long id) {
       log.info("Ensuring customer with ID: {} exists", id);
       if (!customerRepository.existsById(id)) {
           log.warn("Customer not found with ID: {}", id);
           throw new ResourceNotFoundException("Customer not found with ID: " + id);
       }
   }
   
   ```
   </details>

3. **Globaler Exception Handler**
   - Der globale Exception Handler fängt alle definierten Ausnahmen ab und verarbeitet sie entsprechend. Er sorgt für eine zentrale und konsistente Fehlerbehandlung in der Anwendung.
   <details>
   <summary style="color: blue"><strong>Klicke hier, um den Code anzuzeigen</strong></summary>
   
   ```java
   package edu.yacoubi.crm.exception;
   
   import edu.yacoubi.crm.dto.ErrorResponse;
   import org.springframework.http.HttpStatus;
   import org.springframework.http.ResponseEntity;
   import org.springframework.web.bind.annotation.ControllerAdvice;
   import org.springframework.web.bind.annotation.ExceptionHandler;
   
   @ControllerAdvice
   public class GlobalExceptionHandler {
   
      @ExceptionHandler(ResourceNotFoundException.class)
      public ResponseEntity<ErrorResponse> handleResourceNotFoundException(ResourceNotFoundException ex) {
         ErrorResponse errorResponse = new ErrorResponse(HttpStatus.NOT_FOUND.value(), ex.getMessage());
         return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
      }
   
      @ExceptionHandler(Exception.class)
      public ResponseEntity<ErrorResponse> handleGeneralException(Exception ex) {
         ErrorResponse errorResponse = new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "An unexpected error occurred: " + ex.getMessage());
         return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
      }
   
      @ExceptionHandler(IllegalArgumentException.class)
      public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex) {
         ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST.value(), ex.getMessage());
         return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
      }
   }

   ```
   </details>
4. **Response an den Client**
   - Der globale Exception Handler erstellt eine konsistente Antwort (ErrorResponse) und gibt diese zusammen mit dem entsprechenden HTTP-Statuscode an den Client zurück.
   - Beispiel für eine `ErrorResponse` Klasse:
   <details>
   <summary style="color: blue"><strong>Klicke hier, um den Code anzuzeigen</strong></summary>
   
   ```java
   package edu.yacoubi.crm.dto;

   public class ErrorResponse {
      private int status;
      private String message;

      public ErrorResponse(int status, String message) {
         this.status = status;
         this.message = message;
      }

      // Getter und Setter
   }

   ```
   </details>

## Vorteile
- **`Zentrale Fehlerbehandlung`**: Alle Ausnahmen werden an einer Stelle behandelt, was den Code sauberer und einfacher wartbar macht.
- **`Konsistente API-Responses`**: Deine API wird konsistente Fehlermeldungen zurückgeben, was die Benutzerfreundlichkeit verbessert.
- **`Erweiterbarkeit`**: Du kannst den Handler einfach erweitern, um weitere spezifische Ausnahmen zu behandeln, falls notwendig.