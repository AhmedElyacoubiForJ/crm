# Dokumentation: ValueMapper und JUnit-Tests

## Inhaltsverzeichnis
1. [Übersicht](#übersicht)
2. [ValueMapper-Klasse](#valuemapper-klasse)
3. [JavaTimeModule Registrierung](#javatimemodule-registrierung)
4. [JUnit-Tests](#junit-tests)


## Übersicht
> Diese Dokumentation beschreibt die Implementierung und Tests für die ValueMapper-Klasse, die Konvertierungen 
> zwischen Entitäten und DTOs durchführt und JSON-Strings erstellt.
> Besonderes Augenmerk liegt auf der Registrierung des JavaTimeModule für die Unterstützung von Java 8 Date/Time-Typen.

## ValueMapper-Klasse
> Die ValueMapper-Klasse ist eine Utility-Klasse, die Methoden zur Konvertierung von Entitäten zu DTOs und umgekehrt bietet.
> Zusätzlich wird eine Methode zum Konvertieren von Objekten in JSON-Strings bereitgestellt.

## JavaTimeModule Registrierung
> Das JavaTimeModule wird registriert, um die Unterstützung für Java 8 Date/Time-Typen zu gewährleisten.
<details>
<summary style="color: blue"><strong>Klicke hier, um den Code anzuzeigen</strong></summary>

    ```java
    import com.fasterxml.jackson.core.JsonProcessingException;
    import com.fasterxml.jackson.databind.ObjectMapper;
    import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
    
    public class ValueMapper {
        private static final ObjectMapper objectMapper = new ObjectMapper();
    
        static {
            objectMapper.registerModule(new JavaTimeModule());
        }
    
        private ValueMapper() {}
    
        public static NoteResponseDTO convertToResponseDTO(Note note) {
            // Methode zur Konvertierung einer Note in NoteResponseDTO
        }
    
        public static Note convertToEntity(NoteRequestDTO noteRequestDTO) {
            // Methode zur Konvertierung von NoteRequestDTO in Note
        }
    
        public static EmployeeResponseDTO convertToResponseDTO(Employee employee) {
            // Methode zur Konvertierung eines Employee in EmployeeResponseDTO
        }
    
        public static Employee convertToEntity(EmployeeRequestDTO employeeRequestDTO) {
            // Methode zur Konvertierung von EmployeeRequestDTO in Employee
        }
    
        public static Customer convertToEntity(CustomerRequestDTO customerRequestDTO) {
            // Methode zur Konvertierung von CustomerRequestDTO in Customer
        }
    
        public static CustomerResponseDTO convertToResponseDTO(Customer customer) {
            // Methode zur Konvertierung eines Customers in CustomerResponseDTO
        }
    
        public static String jsonAsString(Object obj) {
            try {
                return objectMapper.writeValueAsString(obj);
            } catch (JsonProcessingException e) {
                throw new RuntimeException("Error converting object to JSON string", e);
            }
        }
    }
    
    ```
</details>

## JUnit-Tests
> Die Tests überprüfen die Korrektheit der Konvertierungen und der JSON-String-Erstellung.
- `Tests für Konvertierungen`
<details>
<summary style="color: blue"><strong>Klicke hier, um den Code anzuzeigen</strong></summary>

```java
@Test
void itShouldConvertToResponseDTO() {
    // Test für Konvertierung von Employee zu EmployeeResponseDTO

    // Test für Konvertierung von Customer zu CustomerResponseDTO

    // Test für Konvertierung von Note zu NoteResponseDTO
}

@Test
void itShouldConvertToEntity() {
    // Test für Konvertierung von EmployeeRequestDTO zu Employee

    // Test für Konvertierung von CustomerRequestDTO zu Customer

    // Test für Konvertierung von NoteRequestDTO zu Note
}

```
</details>

- `Test für JSON-String-Erstellung`
<details>
<summary style="color: blue"><strong>Klicke hier, um den Code anzuzeigen</strong></summary>

```java
@Test
void itShouldConvertObjectToJsonString() {
    // Test für JSON-String-Erstellung von EmployeeRequestDTO

    // Test für JSON-String-Erstellung von CustomerRequestDTO

    // Test für JSON-String-Erstellung von NoteRequestDTO
}

```
</details>

- `Test für JSON-String-Erstellung Fehlerfall`
<details>
<summary style="color: blue"><strong>Klicke hier, um den Code anzuzeigen</strong></summary>

```java
@Test
void itShouldThrowExceptionWhenObjectCannotBeConverted() {
    // Test für Fehlerfall bei der JSON-String-Erstellung
}

```
</details>