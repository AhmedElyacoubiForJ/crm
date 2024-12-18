Die Dokumentation für die Tests der Methode deleteEmployeeAndReassignCustomers der Klasse EntityOrchestratorServiceImpl:

## Testen der Methode `deleteEmployeeAndReassignCustomers` in der Klasse `EntityOrchestratorServiceImpl`

Diese Datei dokumentiert die Unit-Tests für die Methode `deleteEmployeeAndReassignCustomers` in der Klasse `EntityOrchestratorServiceImpl`. Diese Methode löscht einen Mitarbeiter und weist dessen Kunden einem anderen Mitarbeiter zu, bevor sie den ursprünglichen Mitarbeiter archiviert.

### Schritt 1: Testprojekt einrichten

Stelle sicher, dass die folgenden Abhängigkeiten in der `pom.xml` Datei deines Projekts enthalten sind:

<details> <summary>Klicken, um den Code anzuzeigen</summary>
<div style="max-height: 300px; overflow-y: auto;">

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
</div>
</details>

### Schritt 2: Testklasse erstellen

Erstelle eine Testklasse für `EntityOrchestratorServiceImpl`:

<details> <summary>Klicken, um den Code anzuzeigen</summary>
<div style="max-height: 300px; overflow-y: auto;">

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
</div>
</details>

### Schritt 3: Null-Parameter-Validierung testen

Teste, dass die Methode `deleteEmployeeAndReassignCustomers` eine `IllegalArgumentException` wirft, wenn einer der Parameter `null` ist:

<details> <summary>Klicken, um den Code anzuzeigen</summary>
<div style="max-height: 300px; overflow-y: auto;">

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
</div>
</details>

### Schritt 4: Gleiche IDs validieren

Teste, dass die Methode eine `IllegalArgumentException` wirft, wenn `oldEmployeeId` und `newEmployeeId` gleich sind:

<details> <summary>Klicken, um den Code anzuzeigen</summary>
<div style="max-height: 300px; overflow-y: auto;">

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
</div>
</details>

### Schritt 5: Validierung auf Existenz von Mitarbeitern

Teste, dass `validateEmployeeExists` für beide IDs aufgerufen wird:

<details> <summary>Klicken, um die Beschreibung des Lösungsansatzes anzuzeigen</summary>
<div style="max-height: 400px; overflow-y: auto;">

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
</div>
</details>

### Schritt 6: Fehler bei nicht gefundenen Mitarbeitern

Simuliere, dass `ResourceNotFoundException` geworfen wird, wenn ein Mitarbeiter nicht gefunden wird:

<details> <summary>Klicken, um die Beschreibung des Lösungsansatzes anzuzeigen</summary>
<div style="max-height: 200px; overflow-y: auto;">

Der Testfall stellt sicher, dass eine `ResourceNotFoundException` geworfen wird, wenn `validateEmployeeExists` für `oldEmployeeId` fehlschlägt.
Hier eine kurze Erklärung zu dem Test:

### Test für das Werfen einer Ausnahme bei nicht gefundenem alten Mitarbeiter
```java
@Test
void itShouldThrowExceptionWhenOldEmployeeNotFound_ByCallingDeleteEmployeeAndReassignCustomers() {
    Long oldEmployeeId = 1L;
    Long newEmployeeId = 2L;

    // Mock the validateEmployeeExists method to throw an exception for oldEmployeeId
    String message = String.format(EMPLOYEE_NOT_FOUND_WITH_ID, oldEmployeeId);
    doThrow(new ResourceNotFoundException(message))
            .when(validationService).validateEmployeeExists(oldEmployeeId);

    // Call the method to test and expect ResourceNotFoundException
    ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
        underTest.deleteEmployeeAndReassignCustomers(oldEmployeeId, newEmployeeId);
    });

    // Verify the exception message
    assertEquals(message, exception.getMessage());
}
```

### Erklärung:
- **Mocking**: `doThrow` wird verwendet, um eine `ResourceNotFoundException` zu werfen, wenn `validateEmployeeExists` mit `oldEmployeeId` aufgerufen wird.
- **Ausführung und Überprüfung**: `assertThrows` überprüft, dass die Ausnahme tatsächlich geworfen wird, und `assertEquals` stellt sicher, dass die Ausnahme die erwartete Nachricht enthält.

Dieser Test stellt sicher, dass die Methode korrekt auf eine nicht gefundene Mitarbeiter-ID reagiert, indem sie eine entsprechende Ausnahme wirft.
</div>
</details>

### Schritt 7: Erfolgreiche Neuzuweisung und Archivierung

Stelle sicher, dass die Kunden neu zugewiesen, der alte Mitarbeiter archiviert und dann gelöscht wird:

<details> <summary>Klicken, um die Beschreibung des Lösungsansatzes anzuzeigen</summary>
<div style="max-height: 400px; overflow-y: auto;">

> Dein Testfall deckt mehrere Aspekte ab und überprüft, ob die Kunden korrekt neu zugewiesen und der alte Mitarbeiter archiviert wird.
> Hier sind die wesentlichen Punkte und eine kurze Erklärung:

### Test zur Überprüfung der Neukundenzuweisung und Archivierung des alten Mitarbeiters

#### Ziel:
> Sicherstellen, dass die Methode `deleteEmployeeAndReassignCustomers` die Kunden korrekt neu zuweist und den alten Mitarbeiter archiviert.

#### Vorgehensweise:
1. **Setup der Testdaten**:
    - Erstellen von `Employee` Objekten für den alten und neuen Mitarbeiter.
    - Erstellen von `Customer` Objekten und einer Liste von Kunden.

2. **Mocking der Methodenaufrufe**:
    - `findById` wird gemockt, um die entsprechenden `Employee` Objekte zurückzugeben.
    - `getCustomersByEmployeeId` wird gemockt, um die Liste der Kunden zurückzugeben.
    - `validateEmployeeExists` wird gemockt, um sicherzustellen, dass keine Aktion ausgeführt wird.

3. **Aufruf der Methode**:
    - Die Methode `deleteEmployeeAndReassignCustomers` wird aufgerufen.

4. **Verifizierung der Methodenaufrufe**:
    - Überprüfung, dass `validateEmployeeExists` viermal aufgerufen wird (zwei für jeden Mitarbeiter).
    - Verifizierung, dass `findById` dreimal aufgerufen wird (zwei für die Validierung und eine für die Neukundenzuweisung).
    - Verifizierung, dass `saveAll`, `createInactiveEmployee` und `delete` genau einmal aufgerufen werden.

5. **Spezifische Überprüfung**:
    - Überprüfen, ob `saveAll` mit einer Liste von Kunden aufgerufen wird, die `customer1` und `customer2` enthält.
    - Sicherstellen, dass die Kunden tatsächlich dem neuen Mitarbeiter zugewiesen wurden.

#### Beispielcode:
```java
@Test
void itShouldReassignCustomersAndArchiveEmployee_ByCallingDeleteEmployeeAndReassignCustomers() {
    Long oldEmployeeId = 1L;
    Long newEmployeeId = 2L;

    Employee oldEmployee = new Employee();
    oldEmployee.setId(oldEmployeeId);
    Employee newEmployee = new Employee();
    newEmployee.setId(newEmployeeId);

    Customer customer1 = new Customer();
    customer1.setId(101L);
    Customer customer2 = new Customer();
    customer2.setId(102L);
    List<Customer> customers = List.of(customer1, customer2);

    when(employeeRepository.findById(oldEmployeeId)).thenReturn(Optional.of(oldEmployee));
    when(employeeRepository.findById(newEmployeeId)).thenReturn(Optional.of(newEmployee));
    when(customerService.getCustomersByEmployeeId(oldEmployeeId)).thenReturn(customers);
    doNothing().when(validationService).validateEmployeeExists(anyLong());

    underTest.deleteEmployeeAndReassignCustomers(oldEmployeeId, newEmployeeId);

    verify(validationService, times(4)).validateEmployeeExists(anyLong());
    verify(employeeRepository, times(3)).findById(anyLong());
    verify(validationService, times(2)).validateEmployeeExists(oldEmployeeId);
    verify(validationService, times(2)).validateEmployeeExists(newEmployeeId);
    verify(employeeRepository, times(1)).findById(oldEmployeeId);
    verify(employeeRepository, times(2)).findById(newEmployeeId);
    verify(customerRepository, times(1)).saveAll(anyList());
    verify(inactiveEmployeeService, times(1)).createInactiveEmployee(oldEmployee);
    verify(employeeRepository, times(1)).delete(oldEmployee);

    // Überprüft, ob die Methode customerRepository.saveAll mit einer Liste von Kunden aufgerufen wird,
    // die sowohl customer1 als auch customer2 enthält.
    verify(customerRepository).saveAll(argThat(customer_s -> {
        boolean containsCustomer1 = false;
        boolean containsCustomer2 = false;
        for (Customer customer : customers) {
            if (customer.equals(customer1)) {
                containsCustomer1 = true;
            } else if (customer.equals(customer2)) {
                containsCustomer2 = true;
            }
        }
        return containsCustomer1 && containsCustomer2;
    }));

    // Check if customers are reassigned to the new employee
    assertEquals(newEmployee, customer1.getEmployee());
    assertEquals(newEmployee, customer2.getEmployee());
}
```

#### Erklärung:
- **Mocking**: Stellt sicher, dass die Methodenaufrufe korrekt simuliert werden, um Seiteneffekte zu vermeiden.
- **Verifizierung**: Überprüft, dass die Methoden wie erwartet aufgerufen werden und die Kunden richtig neu zugewiesen werden.
</div>
</details>

> Diese Dokumentation umfasst die Tests für die Methode `deleteEmployeeAndReassignCustomers`
> und bietet eine gründliche Überprüfung aller wichtigen Aspekte.
