# Dokumentation: Struktur der APIResponse-Klasse
## Klassen-Definition

<details>
<summary style="color: blue"><strong>Klicke hier, um den Code anzuzeigen</strong></summary>

```java

package edu.yacoubi.crm.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public class APIResponse<T> {
    private String status;
    private int statusCode;
    private String message;
    private List<ValidationError> errors;
    private T data;
}

```
</details>

## Felder
- `status`: Ein String, der den Status der Antwort anzeigt (z.B. "success", "error").
- `statusCode`: Ein numerischer HTTP-Statuscode (z.B. 200 für OK, 404 für Not Found).
- `message`: Eine optionale Nachricht, die zusätzliche Informationen zur Antwort enthält.
- `erros`: Eine Liste von ErrorDTO-Objekten, die spezifische Fehlerdetails enthalten.
- `data`: Der generische Datentyp T, der die eigentlichen Daten der Antwort enthält.

## Verwendung in Controllern
- **Beispiel: Erfolgreiche Antwort**
<details>
<summary style="color: blue"><strong>Klicke hier, um den Code anzuzeigen</strong></summary>

```java
@PostMapping
public ResponseEntity<APIResponse<EmployeeResponseDTO>> createEmployee(@Valid @RequestBody EmployeeRequestDTO employeeRequestDTO) {
    Employee employee = convertToEntity(employeeRequestDTO);
    Employee savedEmployee = employeeService.createEmployee(employee);
    EmployeeResponseDTO employeeResponseDTO = convertToResponseDTO(savedEmployee);

    APIResponse<EmployeeResponseDTO> response = APIResponse.<EmployeeResponseDTO>builder()
            .status("success")
            .statusCode(HttpStatus.CREATED.value())
            .data(employeeResponseDTO)
            .build();

    return ResponseEntity.status(HttpStatus.CREATED).body(response);
}

```
</details>

- **Beispiel: Fehlerhafte Antwort**
<details>
<summary style="color: blue"><strong>Klicke hier, um den Code anzuzeigen</strong></summary>

```java
@ExceptionHandler(MethodArgumentNotValidException.class)
public ResponseEntity<APIResponse<Object>> handleValidationExceptions(MethodArgumentNotValidException ex) {
    List<ErrorDTO> errors = ex.getBindingResult().getFieldErrors().stream()
            .map(error -> new ErrorDTO(error.getField(), error.getDefaultMessage()))
            .collect(Collectors.toList());
    APIResponse<Object> response = APIResponse.<Object>builder()
            .status("error")
            .statusCode(HttpStatus.BAD_REQUEST.value())
            .errors(errors)
            .build();
    return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
}

```
</details>

- **ErrorDTO-Klasse**
<details>
<summary style="color: blue"><strong>Klicke hier, um den Code anzuzeigen</strong></summary>

```java
package edu.yacoubi.crm.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ErrorDTO {
    private String field;
    private String errorMessage;
}

```
</details>

> Die ErrorDTO-Klasse wird verwendet, um spezifische Fehlerdetails zu übermitteln. Sie enthält zwei Felder:
- `field`: Der Name des Feldes, das den Fehler verursacht hat.
- `errorMessage`: Eine Nachricht, die den Fehler beschreibt.