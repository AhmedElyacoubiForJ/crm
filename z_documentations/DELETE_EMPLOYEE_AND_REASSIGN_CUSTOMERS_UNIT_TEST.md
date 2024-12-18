Die Dokumentation für die Tests der Methode deleteEmployeeAndReassignCustomers der Klasse EntityOrchestratorServiceImpl:


## Testen der Methode `deleteEmployeeAndReassignCustomers` in der Klasse `EntityOrchestratorServiceImpl`

Diese Datei dokumentiert die Unit-Tests für die Methode `deleteEmployeeAndReassignCustomers` in der Klasse `EntityOrchestratorServiceImpl`. Diese Methode löscht einen Mitarbeiter und weist dessen Kunden einem anderen Mitarbeiter zu, bevor sie den ursprünglichen Mitarbeiter archiviert.

### Schritt 1: Testprojekt einrichten

Stelle sicher, dass die folgenden Abhängigkeiten in der `pom.xml` Datei deines Projekts enthalten sind:

<details> <summary>Klicken, um den Code anzuzeigen</summary>

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-test</artifactId>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>org.mockito</groupId>
    <artifactId>mockito-core</artifactId>
    <scope>test</scope>
</dependency>
```
</details>

### Schritt 2: Testklasse erstellen

Erstelle eine Testklasse für `EntityOrchestratorServiceImpl`:

<details> <summary>Klicken, um den Code anzuzeigen</summary>

```java
package edu.yacoubi.crm.service.impl;

import edu.yacoubi.crm.exception.ResourceNotFoundException;
import edu.yacoubi.crm.model.Customer;
import edu.yacoubi.crm.model.Employee;
import edu.yacoubi.crm.repository.CustomerRepository;
import edu.yacoubi.crm.repository.EmployeeRepository;
import edu.yacoubi.crm.service.ICustomerService;
import edu.yacoubi.crm.service.IInactiveEmployeeService;
import edu.yacoubi.crm.service.ValidationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.*;

class EntityOrchestratorServiceImplUnitTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private ICustomerService customerService;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private IInactiveEmployeeService inactiveEmployeeService;

    @Mock
    private ValidationService validationService;

    @InjectMocks
    private EntityOrchestratorServiceImpl entityOrchestratorService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }
}
```
</details>

### Schritt 3: Null-Parameter-Validierung testen

Teste, dass die Methode `deleteEmployeeAndReassignCustomers` eine `IllegalArgumentException` wirft, wenn einer der Parameter `null` ist:

<details> <summary>Klicken, um den Code anzuzeigen</summary>

```java
@Test
void itShouldThrowExceptionWhenOldEmployeeIdIsNull_ByCallingDeleteEmployeeAndReassignCustomers() {
    // Given
    Long oldEmployeeId = null; // set to null to test the precondition
    Long newEmployeeId = 2L;

    // When
    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
        entityOrchestratorService.deleteEmployeeAndReassignCustomers(oldEmployeeId, newEmployeeId);
    });

    // Then verify the exception message
    assertEquals("Old employee ID must not be null", exception.getMessage());
}

@Test
void itShouldThrowExceptionWhenNewEmployeeIdIsNull_ByCallingDeleteEmployeeAndReassignCustomers() {
    // Given
    Long oldEmployeeId = 1L;
    Long newEmployeeId = null; // set to null to test the precondition

    // When
    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
        entityOrchestratorService.deleteEmployeeAndReassignCustomers(oldEmployeeId, newEmployeeId);
    });

    // Then verify the exception message
    assertEquals("New employee ID must not be null", exception.getMessage());
}
```
</details>

### Schritt 4: Gleiche IDs validieren

Teste, dass die Methode eine `IllegalArgumentException` wirft, wenn `oldEmployeeId` und `newEmployeeId` gleich sind:

<details> <summary>Klicken, um den Code anzuzeigen</summary>

```java
@Test
void itShouldThrowExceptionWhenIdsAreEquals_ByCallingDeleteEmployeeAndReassignCustomers() {
    // Given
    Long oldEmployeeId = 1L;
    Long newEmployeeId = 1L;

    // When
    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
        entityOrchestratorService.deleteEmployeeAndReassignCustomers(oldEmployeeId, newEmployeeId);
    });

    // Then verify the exception message
    assertEquals("Old and new employee IDs must be different", exception.getMessage());
}
```
</details>

### Schritt 5: Validierung auf Existenz von Mitarbeitern

Teste, dass `validateEmployeeExists` für beide IDs aufgerufen wird:

<details> <summary>Klicken, um die Beschreibung des Lösungsansatzes anzuzeigen</summary>

### Lösungsansatz zur Überprüfung von `validateEmployeeExists`

#### Ziel:
Wir möchten sicherstellen, dass die Methode `validateEmployeeExists` für beide IDs (`oldEmployeeId` und `newEmployeeId`) aufgerufen wird, ohne dass nachfolgende Methoden wie `reassignCustomers` ausgeführt werden und Assertions-Fehler verursachen.

#### Vorgehensweise:
1. **Erstellung einer anonymen Unterklasse**:
    - Da die ursprüngliche Service-Klasse (`EntityOrchestratorServiceImpl`) keinen No-Argument-Konstruktor hat, erstellen wir eine anonyme Unterklasse, die `reassignCustomers` überschreibt, um sicherzustellen, dass diese Methode keine Aktionen ausführt.

   2. **Setzen der Abhängigkeiten**:
       - Wir setzen die notwendigen Abhängigkeiten manuell im Konstruktor der anonymen Unterklasse, um sicherzustellen, dass der Test korrekt ausgeführt wird.

   3. **Mocking der `validateEmployeeExists` Methode**:
       - Wir verwenden `doNothing().when(validationService).validateEmployeeExists(anyLong())`, um sicherzustellen, dass die Methode `validateEmployeeExists` aufgerufen wird, aber keine Aktion ausführt.

   4. **Mocking der abhängigen Methoden**:
       - Methoden wie `findById` werden gemockt, um sicherzustellen, dass sie keine Seiteneffekte haben und den Test nicht beeinflussen.

   5. **Verifizierung der Methodenaufrufe**:
       - Schließlich verifizieren wir, dass die Methode `validateEmployeeExists` für beide IDs genau einmal aufgerufen wird.

#### Beispielcode:
```java
@Test
void itShouldValidateEmployeesExist_ByCallingDeleteEmployeeAndReassignCustomers() {
    // Given
    Long oldEmployeeId = 1L;
    Long newEmployeeId = 2L;

    // Erstelle eine anonyme Unterklasse, um reassignCustomers zu überschreiben
    EntityOrchestratorServiceImpl spyService = new EntityOrchestratorServiceImpl(
            employeeRepository,
            customerService,
            customerRepository,
            inactiveEmployeeService,
            validationService
    ) {
        @Override
        public void reassignCustomers(Long oldId, Long newId) {
            // Überschreibe die Methode ohne Inhalt, um die Ausführung zu verhindern
        }
    };

    // Mock the validateEmployeeExists method
    doNothing().when(validationService).validateEmployeeExists(anyLong());

    // Mock the findById method für die Validierungs- und Repository-Aufrufe
    when(employeeRepository.findById(oldEmployeeId)).thenReturn(Optional.of(new Employee()));
    when(employeeRepository.findById(newEmployeeId)).thenReturn(Optional.of(new Employee()));

    // Call the method to test
    spyService.deleteEmployeeAndReassignCustomers(oldEmployeeId, newEmployeeId);

    // Verify interactions
    verify(validationService, times(1)).validateEmployeeExists(oldEmployeeId);
    verify(validationService, times(1)).validateEmployeeExists(newEmployeeId);
}
```

#### Erklärung:
- **Anonyme Unterklasse**: Erstellen einer Unterklasse von `EntityOrchestratorServiceImpl` und Überschreiben der Methode `reassignCustomers`, um sicherzustellen, dass sie keine Aktionen ausführt.
  - **Manuelle Setzung der Abhängigkeiten**: Setzen der notwendigen Abhängigkeiten im Konstruktor der anonymen Unterklasse.
  - **Mocking**: Verwenden von `doNothing` für `validateEmployeeExists` und Mocking der Methode `findById`, um sicherzustellen, dass sie keine Seiteneffekte haben.
  - **Verifizierung**: Überprüfen, dass `validateEmployeeExists` für beide IDs aufgerufen wird.

Dieser Ansatz stellt sicher, dass nur die Methodenaufrufe von `validateEmployeeExists` überprüft werden, ohne dass nachfolgende Anweisungen den Test beeinflussen.

</details>

### Schritt 6: Fehler bei nicht gefundenen Mitarbeitern

Simuliere, dass `ResourceNotFoundException` geworfen wird, wenn ein Mitarbeiter nicht gefunden wird:

<details> <summary>Klicken, um den Code anzuzeigen</summary>

```java
@Test
void deleteEmployeeAndReassignCustomers_whenOldEmployeeNotFound_shouldThrowException() {
    Long oldEmployeeId = 1L;
    Long newEmployeeId = 2L;

    doThrow(new ResourceNotFoundException("Employee not found"))
            .when(validationService).validateEmployeeExists(oldEmployeeId);

    ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
        entityOrchestratorService.deleteEmployeeAndReassignCustomers(oldEmployeeId, newEmployeeId);
    });
    assertEquals("Employee not found", exception.getMessage());
}
```
</details>

### Schritt 7: Erfolgreiche Neuzuweisung und Archivierung

Stelle sicher, dass die Kunden neu zugewiesen, der alte Mitarbeiter archiviert und dann gelöscht wird:

<details> <summary>Klicken, um den Code anzuzeigen</summary>

```java
@Test
void deleteEmployeeAndReassignCustomers_shouldReassignCustomersAndArchiveEmployee() {
    Long oldEmployeeId = 1L;
    Long newEmployeeId = 2L;

    Employee oldEmployee = new Employee();
    oldEmployee.setId(oldEmployeeId);
    Employee newEmployee = new Employee();
    newEmployee.setId(newEmployeeId);

    when(employeeRepository.findById(oldEmployeeId)).thenReturn(Optional.of(oldEmployee));
    when(employeeRepository.findById(newEmployeeId)).thenReturn(Optional.of(newEmployee));
    List<Customer> customers = List.of(new Customer(), new Customer());
    when(customerService.getCustomersByEmployeeId(oldEmployeeId)).thenReturn(customers);

    entityOrchestratorService.deleteEmployeeAndReassignCustomers(oldEmployeeId, newEmployeeId);

    verify(validationService).validateEmployeeExists(oldEmployeeId);
    verify(validationService).validateEmployeeExists(newEmployeeId);
    verify(employeeRepository).findById(oldEmployeeId);
    verify(employeeRepository).findById(newEmployeeId);
    verify(customerRepository).saveAll(anyList());
    verify(inactiveEmployeeService).createInactiveEmployee(oldEmployee);
    verify(employeeRepository).delete(oldEmployee);
}
```
</details>

> Diese Dokumentation umfasst die Tests für die Methode `deleteEmployeeAndReassignCustomers`
> und bietet eine gründliche Überprüfung aller wichtigen Aspekte.
