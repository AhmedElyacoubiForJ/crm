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
<summary>Klicke hier, um den Code anzuzeigen</summary>

```
java
@SpringBootTest
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
