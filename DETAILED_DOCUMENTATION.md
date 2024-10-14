# Detaillierte Projektdokumentation

## Projektbeschreibung
Dieses Projekt ist eine Implementierung eines **Customer Relationship Management Systems (CRM)**, das es Mitarbeitern ermöglicht, Kunden zu verwalten, Notizen zu Interaktionen zu hinterlegen und Kunden nach bestimmten Kriterien zu durchsuchen. Kunden werden Mitarbeitern zugeordnet und jede Kundeninteraktion wird als Notiz dokumentiert.

## Implementierungsprozess
> Zuerst werden die Entities identifiziert, daraus JPA Entities spezifiziert und für jeweils eine Repository erstellt.
> Bei Bedarf werden custom Queries hinzugefügt. Danach werden Unit-Tests implementiert, um die Funktionalität und Integrität jeder Entität und ihrer Beziehungen zu überprüfen.
> Parallel dazu wird die Geschäftslogik in den Service-Schichten definiert und implementiert, wobei die Services ebenfalls umfassend getestet werden.
> Schließlich werden Integrationstests durchgeführt, um sicherzustellen, dass alle Systemkomponenten nahtlos zusammenarbeiten.


## Entitäten Beschreibung
### Customer
Die `Customer`-Entität repräsentiert einen Kunden und hat folgende Attribute:
- `id` (Long): Primärschlüssel
- `firstName` (String): Vorname
- `lastName` (String): Nachname
- `email` (String): E-Mail-Adresse
- `phone` (String): Telefonnummer
- `address` (String): Adresse
- `lastInteractionDate` (LocalDate): Datum der letzten Interaktion
- `employee` (Employee): Mitarbeiter, dem der Kunde zugeordnet ist
- `notes` (List<Note>): Liste von Notizen, die mit dem Kunden verbunden sind

### Employee
Die `Employee`-Entität repräsentiert einen Mitarbeiter und hat folgende Attribute:
- `id` (Long): Primärschlüssel
- `firstName` (String): Vorname
- `lastName` (String): Nachname
- `email` (String): E-Mail-Adresse
- `department` (String): Abteilung
- `customers` (List<Customer>): Liste von Kunden, die diesem Mitarbeiter zugeordnet sind

### Note
Die `Note`-Entität repräsentiert eine Notiz zu einer Kundeninteraktion und hat folgende Attribute:
- `id` (Long): Primärschlüssel
- `content` (String): Inhalt der Notiz
- `date` (LocalDate): Datum der Notiz
- `interactionType` (InteractionType): Typ der Interaktion (EMAIL, PHONE_CALL, MEETING)
- `customer` (Customer): Kunde, dem die Notiz zugeordnet ist

### InteractionType
Das `InteractionType`-Enum definiert die verschiedenen Arten von Interaktionen:
- `EMAIL`
- `PHONE_CALL`
- `MEETING`

## Funktionale Anforderungen
- **Kundenverwaltung**: Kunden erstellen, aktualisieren, löschen und anzeigen.
- **Notizenverwaltung**: Notizen zu Kunden erstellen, aktualisieren und löschen.
- **Suche**: Kunden nach Name, E-Mail oder Telefonnummer durchsuchen.
- **Interaktionen anzeigen**: Interaktionen eines Kunden nach Datum geordnet anzeigen.

## Tests
### Datenbankintegrität
- `testCreateCustomerWithoutEmployeeFailed`: Testet, ob ein Kunde ohne zugeordneten Mitarbeiter nicht gespeichert werden kann und eine `DataIntegrityViolationException` auslöst.
- `testCreateReadUpdateDelete`: Ein umfassender Test, der die CRUD-Operationen für die `Customer`-Entität abdeckt.

<details>
<summary style="color: blue"><strong>Klicke hier, um den Code anzuzeigen</strong></summary>

```
java
@DataJpaTest
public class CustomerRepositoryTests {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Test
    public void testCreateCustomerWithoutEmployeeFailed() {
        // Given
        Customer customer = Customer.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .phone("1234567890")
                .address("123 Main St")
                .lastInteractionDate(LocalDate.now())
                .build();

        // When
        DataIntegrityViolationException exception = assertThrows(DataIntegrityViolationException.class, () -> {
            customerRepository.save(customer);
        });

        // Then
        String expectedMessage = "not-null property references a null";
        assertTrue(exception.getMessage().contains(expectedMessage), expectedMessage);
    }

    @Test
    public void testCreateReadUpdateDelete() {
        // Create a new employee
        Employee employee = Employee.builder()
                .firstName("Jane")
                .lastName("Doe")
                .email("jane.doe@example.com")
                .department("Sales")
                .build();

        // Save the employee
        Employee savedEmployee = employeeRepository.save(employee);

        // Create a new customer and associate with saved employee
        Customer customer = Customer.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .phone("1234567890")
                .address("123 Main St")
                .lastInteractionDate(LocalDate.now())
                .employee(savedEmployee) // associate customer with employee
                .build();

        // Save the customer
        Customer savedCustomer = customerRepository.save(customer);

        // Read the customer
        Customer foundCustomer = customerRepository.findById(savedCustomer.getId()).orElse(null);
        assertNotNull(foundCustomer);
        assertEquals("John", foundCustomer.getFirstName());

        // Update the customer
        foundCustomer.setFirstName("Jane");
        customerRepository.save(foundCustomer);
        Customer updatedCustomer = customerRepository.findById(savedCustomer.getId()).orElse(null);
        assertNotNull(updatedCustomer);
        assertEquals("Jane", updatedCustomer.getFirstName());

        // Delete the customer
        customerRepository.delete(updatedCustomer);
        Customer deletedCustomer = customerRepository.findById(savedCustomer.getId()).orElse(null);
        assertNull(deletedCustomer);
    }
}
```
</details>

### Service-Tests
- `itShouldCreateCustomer`: Testet die Erstellung eines neuen Kunden.
- `itShouldFindCustomerById`: Testet das Finden eines Kunden nach ID.
- `itShouldUpdateCustomer`: Testet die Aktualisierung eines bestehenden Kunden.
- `itShouldDeleteCustomer`: Testet das Löschen eines Kunden.

### Service-Tests
- `itShouldCreateCustomer`: Testet die Erstellung eines neuen Kunden.
- `itShouldFindCustomerById`: Testet das Finden eines Kunden nach ID.
- `itShouldUpdateCustomer`: Testet die Aktualisierung eines bestehenden Kunden.
- `itShouldDeleteCustomer`: Testet das Löschen eines Kunden.

<details>
<summary style="color: blue"><strong>Klicke hier, um den Code anzuzeigen</strong></summary>

```
java
import edu.yacoubi.crm.TestDataUtil;
import edu.yacoubi.crm.model.Customer;
import edu.yacoubi.crm.repository.CustomerRepository;
import edu.yacoubi.crm.service.impl.CustomerServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CustomerServiceImplUnitTest {

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private CustomerServiceImpl underTest;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void itShouldCreateCustomer() {
        // Given
        Customer customer = TestDataUtil.createCustomerA(null);
        when(customerRepository.save(any(Customer.class))).thenReturn(customer);

        // When
        Customer savedCustomer = underTest.createCustomer(customer);

        // Then
        assertNotNull(savedCustomer);
        verify(customerRepository, times(1)).save(customer);
    }

    @Test
    public void itShouldFindCustomerById() {
        // Given
        Long customerId = 1L;  // Setze eine spezifische ID
        Customer customer = TestDataUtil.createCustomerA(null);
        customer.setId(customerId);  // Setze die ID im Mock-Objekt
        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer)); // Verwende eine spezifische ID
        when(customerRepository.existsById(customerId)).thenReturn(true); // Mock für existierende ID hinzufügen

        // When
        Optional<Customer> foundCustomer = underTest.getCustomerById(customerId);

        // Logging for debugging
        System.out.println("Found Customer: " + foundCustomer);

        // Then
        assertTrue(foundCustomer.isPresent());
        assertEquals(customer.getEmail(), foundCustomer.get().getEmail());
        verify(customerRepository, times(1)).findById(customerId);
        verify(customerRepository, times(1)).existsById(customerId); // Überprüfung der Mock-Interaktionen
    }

    @Test
    public void itShouldUpdateCustomer() {
        // Given
        Long customerId = 1L;
        Customer customer = TestDataUtil.createCustomerA(null);
        customer.setId(customerId);
        when(customerRepository.existsById(customerId)).thenReturn(true);
        when(customerRepository.save(any(Customer.class))).thenReturn(customer);

        // When
        Customer updatedCustomer = underTest.updateCustomer(customerId, customer);

        // Then
        assertNotNull(updatedCustomer);
        verify(customerRepository, times(1)).save(customer);
    }

    @Test
    public void itShouldDeleteCustomer() {
        // Given
        Long customerId = 1L;
        Customer customer = TestDataUtil.createCustomerA(null);
        customer.setId(customerId);
        when(customerRepository.existsById(customerId)).thenReturn(true);

        // When
        underTest.deleteCustomer(customerId);

        // Then
        verify(customerRepository, times(1)).deleteById(customerId);
    }
}
```
</details>
