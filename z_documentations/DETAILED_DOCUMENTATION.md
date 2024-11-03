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
1. Customer:
   - `CustomerRepositoryIntegrationTest`: Setup-Klasse, die dafür zuständig tests für die JPA Entity Customer Model durchzuführen.
      <details>
       <summary style="color: blue"><strong>Klicke hier, um den Code anzuzeigen</strong></summary>
    
       ```
       @DataJpaTest
       class CustomerRepositoryIntegrationTest {

       @Autowired
       private CustomerRepository underTest;

       @Autowired
       private EmployeeRepository employeeRepository;

       @Autowired
       private NoteRepository noteRepository;  
    
       // Test Methods
       }
       ```
       </details>

   - `itShouldPerformAllCRUDOperations`: Ein umfassender Test, der die CRUD-Operationen für die `Customer`-Entität abdeckt.
       <details>
       <summary style="color: blue"><strong>Klicke hier, um den Code anzuzeigen</strong></summary>

       ```
       @Test
       public void itShouldPerformAllCRUDOperations() {
           // Given
           Employee employee = TestDataUtil.createEmployeeA();
           Employee savedEmployee = employeeRepository.save(employee);
           // Create a new customer and associate with saved employee
           Customer customer = TestDataUtil.createCustomerA(savedEmployee);
           // When
           Customer savedCustomer = underTest.save(customer);
           // Then
           Customer foundCustomer = underTest.findById(savedCustomer.getId()).orElse(null);
           assertNotNull(foundCustomer);
           assertEquals("John", foundCustomer.getFirstName());

           // Update the customer
           foundCustomer.setFirstName("Jane");
           underTest.save(foundCustomer);
           Customer updatedCustomer = underTest.findById(savedCustomer.getId()).orElse(null);
           assertNotNull(updatedCustomer);
           assertEquals("Jane", updatedCustomer.getFirstName());

           // Delete the customer
           underTest.delete(updatedCustomer);
           Customer deletedCustomer = underTest.findById(savedCustomer.getId()).orElse(null);
           assertNull(deletedCustomer);
       }
       ```
       </details>
    
   - `itShouldThrowWhenCreateCustomerWithoutEmployee`: Testet, ob ein Kunde ohne zugeordneten Mitarbeiter nicht gespeichert werden kann und eine `DataIntegrityViolationException` auslöst.
       <details>
       <summary style="color: blue"><strong>Klicke hier, um den Code anzuzeigen</strong></summary>

       ```
       @Test
       // Foreign key constraints
       public void itShouldThrowWhenCreateCustomerWithoutEmployee() {
           // Given
           // Create a new customer without an employee
           Customer customer = TestDataUtil.createCustomerB(null);
           // When
           // This should throw an exception because the employee is missing
           DataIntegrityViolationException exception = assertThrows(
                   DataIntegrityViolationException.class, () -> underTest.save(customer));
           // Then
           String expectedMessage = "not-null property references a null";
           assertTrue(exception.getMessage().contains(expectedMessage), expectedMessage);
       }
       ```
       </details>

   - `itShouldReturnCustomerByEmail`: `Custom-Query` Test die Suche nach ein Kunde per E-Mail.
       <details>
       <summary style="color: blue"><strong>Klicke hier, um den Code anzuzeigen</strong></summary>

       ```
       @Test
       public void itShouldReturnCustomerByEmail() {
           // Given
           String email = "jane.smith@example.com";
           Employee employee = TestDataUtil.createEmployeeC();
           Employee savedEmployee = employeeRepository.save(employee);
           Customer customer = TestDataUtil.createCustomerB(savedEmployee);
           underTest.save(customer);
           // When
           Customer foundCustomer = underTest.findByEmail(email).orElse(null);
           // Then
           assertNotNull(foundCustomer);
           assertEquals(email, foundCustomer.getEmail());
       }
       ```
       </details>
   - `itShouldNotReturnCustomerByNotExistingEmail`: Test die Suche einer nicht existierender Kunde per E-Mail.
       <details>
       <summary style="color: blue"><strong>Klicke hier, um den Code anzuzeigen</strong></summary>

       ```
       @Test
       public void itShouldNotReturnCustomerByNotExistingEmail() {
           // Given
           String notExistingEmail = "not.existing@example.com";
           // When
           Customer foundCustomer = underTest.findByEmail(notExistingEmail).orElse(null);
           // Then
           assertNull(foundCustomer);
       }
       ```
       </details>
   - `itShouldCreateNotesToCustomer`: Test die zuordnung von Notizen zur Kunde.
       <details>
       <summary style="color: blue"><strong>Klicke hier, um den Code anzuzeigen</strong></summary>

       ```
       @Test
       public void itShouldCreateNotesToCustomer() {
           // Given
           Employee employee = TestDataUtil.createEmployeeC();
           Employee savedEmployee = employeeRepository.save(employee);
           Customer customer = TestDataUtil.createCustomerC(savedEmployee);
           Customer savedCustomer = underTest.save(customer);

           // Create and associate notes
           List<Note> notes = new ArrayList<>();
           Note noteA = TestDataUtil.createNoteA(savedCustomer);
           Note noteB = TestDataUtil.createNoteB(savedCustomer);
           notes.add(noteA);
           notes.add(noteB);
           savedCustomer.setNotes(notes);

           // When
           underTest.save(savedCustomer);

           // Then
           Customer foundCustomer = underTest.findById(savedCustomer.getId()).orElse(null);
           assertNotNull(foundCustomer);
           assertEquals(2, foundCustomer.getNotes().size());
           assertEquals(notes.get(0).getContent(), foundCustomer.getNotes().get(0).getContent());
           assertEquals(notes.get(1).getContent(), foundCustomer.getNotes().get(1).getContent());
       }
       ```
       </details>
   - `itShouldDeleteCustomerNotesIfCustomerDeleted`: Beim Löschen eines Kunden, soll auch die dazugehörigen Notizen gelöscht werden.
       <details>
       <summary style="color: blue"><strong>Klicke hier, um den Code anzuzeigen</strong></summary>

       ```
       @Test
       @Transactional
       // Cascade test
       // Notizen sind keine eigenständigen Entitäten, sondern direkt mit dem Kunden verbunden.
       // Beim Löschen eines Kunden auch die dazugehörigen Notizen werden gelöscht.
       public void itShouldDeleteCustomerNotesIfCustomerDeleted() {
           // Given
           Employee employee = TestDataUtil.createEmployeeA();
           Employee savedEmployee = employeeRepository.save(employee);
           Customer customer = TestDataUtil.createCustomerB(savedEmployee);
           Customer savedCustomer = underTest.save(customer);

           // Create and associate notes
           List<Note> notes = new ArrayList<>();
           Note noteA = TestDataUtil.createNoteA(savedCustomer);
           Note noteB = TestDataUtil.createNoteB(savedCustomer);
           Note noteC = TestDataUtil.createNoteC(savedCustomer);
           notes.add(noteA);
           notes.add(noteB);
           notes.add(noteC);
           savedCustomer.setNotes(notes);
           underTest.save(savedCustomer);

           // When
           underTest.deleteById(savedCustomer.getId());

           // Then
           // Assert that the customer is deleted
           assertNull(underTest.findById(savedCustomer.getId()).orElse(null));
           // Assert that the notes are also deleted
           List<Note> notesList = noteRepository.findAll();
           assertTrue(notesList.isEmpty());
       }
       ```
       </details>
   -  **updateCustomerByExample**:
   > Die Methode updateCustomerByExample ermöglicht es, bestimmte Eigenschaften eines Kunden basierend auf einem Beispiel (CustomerDTO) zu aktualisieren.
   > Dies sorgt für eine gezielte Aktualisierung der Daten, ohne die gesamte Entität zu überschreiben.
   - `Pflichtfeld-Validierung`: Testet, ob das Feld "Phone" nicht `null` sein darf.
   - `Längen-Validierung`: Testet, ob das Feld "Phone" die richtige Länge hat.
   - `Initialisierung-Validierung`: Testet, ob die `notes`-Liste initialisiert ist.
2. Note:
    - `itShouldPerformAllCRUDOperations`: ....
    - `itShouldThrowWhenCreatingNoteWithoutCustomer`: ....
    - `itShouldReturnNotesByCustomerId`: ....
    - `itShouldReturnNoNotesWhenCustomerHasNoNotes`: ....
    - `itShouldThrowWhenContentIsNull`: ....
    - `itShouldThrowWhenDateIsNull`: ....
    - `itShouldThrowWhenInteractionTypeIsNull`: ....
    <details>
       <summary style="color: blue"><strong>Klicke hier, um den Code anzuzeigen</strong></summary>
    
   ```
   
    package edu.yacoubi.crm.repository;
    import edu.yacoubi.crm.TestDataUtil;
    import edu.yacoubi.crm.model.Customer;
    import edu.yacoubi.crm.model.Employee;
    import edu.yacoubi.crm.model.InteractionType;
    import edu.yacoubi.crm.model.Note;
    import org.junit.jupiter.api.Test;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
    import org.springframework.dao.DataIntegrityViolationException;
    import org.springframework.dao.InvalidDataAccessApiUsageException;
    
    import java.time.LocalDate;
    import java.util.List;
    
    import static org.junit.jupiter.api.Assertions.*;
    
    @DataJpaTest
    class NoteRepositoryTest {
    
        @Autowired
        EmployeeRepository employeeRepository;
        @Autowired
        CustomerRepository customerRepository;
        @Autowired
        private NoteRepository underTest;
    
        @Test
        public void itShouldPerformAllCRUDOperations() {
            // Given
            Employee employee = TestDataUtil.createEmployeeA();
            Employee savedEmployee = employeeRepository.save(employee);
            Customer customer = TestDataUtil.createCustomerA(savedEmployee);
            Customer savedCustomer = customerRepository.save(customer);
            // ...request
            Note note = TestDataUtil.createNoteA(savedCustomer);
    
            // When
            Note savedNote = underTest.save(note);
            Note foundNote = underTest.findById(savedNote.getId()).orElse(null);
            assertNotNull(foundNote);
            assertEquals("First interaction", foundNote.getContent());
            assertEquals(savedCustomer, foundNote.getCustomer());
    
            // Then
            foundNote.setContent("Updated Note");
            underTest.save(foundNote);
            Note updatedNote = underTest.findById(foundNote.getId()).orElse(null);
            assertNotNull(updatedNote);
            assertEquals("Updated Note", updatedNote.getContent());
            underTest.deleteById(foundNote.getId());
            Note deletedNote = underTest.findById(foundNote.getId()).orElse(null);
            assertNull(deletedNote);
        }
    
        @Test
        public void itShouldThrowWhenCreatingNoteWithoutCustomer() {
            // Given
            Note note = TestDataUtil.createNoteB(null);
    
            // When
            DataIntegrityViolationException exception = assertThrows(
                    DataIntegrityViolationException.class, () -> underTest.save(note));
    
            // Then
            String expectedMessage = "not-null property references a null";
            assertTrue(exception.getMessage().contains(expectedMessage), expectedMessage);
        }
    
        @Test
        public void itShouldReturnNotesByCustomerId() {
            // Given
            Employee employee = TestDataUtil.createEmployeeA();
            Employee savedEmployee = employeeRepository.save(employee);
            Customer customer = TestDataUtil.createCustomerA(savedEmployee);
            customer = customerRepository.save(customer);
            Note noteA = TestDataUtil.createNoteA(customer);
            Note noteB = TestDataUtil.createNoteB(customer);
            Note noteC = TestDataUtil.createNoteC(customer);
            underTest.save(noteA);
            underTest.save(noteB);
            underTest.save(noteC);
    
            // When
            List<Note> foundNotes = underTest.findAllByCustomerId(customer.getId());
    
            // Then
            assertEquals(3, foundNotes.size());
        }
    
        @Test
        public void itShouldReturnNoNotesWhenCustomerHasNoNotes() {
            // Given
            Employee employee = TestDataUtil.createEmployeeA();
            Employee savedEmployee = employeeRepository.save(employee);
            Customer customer = TestDataUtil.createCustomerA(savedEmployee);
            customer = customerRepository.save(customer);
    
            // When
            List<Note> foundNotes = underTest.findAllByCustomerId(customer.getId());
    
            // Then
            assertTrue(foundNotes.isEmpty());
        }
    
        @Test
        public void itShouldThrowWhenContentIsNull() {
            // Given
            Employee employee = TestDataUtil.createEmployeeA();
            Employee savedEmployee = employeeRepository.save(employee);
            Customer customer = TestDataUtil.createCustomerA(savedEmployee);
            Note note = Note.builder()
                    .content(null)
                    .date(LocalDate.now())
                    .interactionType(InteractionType.EMAIL)
                    .customer(customer)
                    .build();
            //underTest.save(note);
            // When
            InvalidDataAccessApiUsageException exception = assertThrows(
                    InvalidDataAccessApiUsageException.class, () -> underTest.save(note));
    
            // Then
            String expectedMessage = "Not-null property references a transient value";
            assertTrue(exception.getMessage().contains(expectedMessage), expectedMessage);
        }
    
        @Test
        public void itShouldThrowWhenDateIsNull() {
            // Given
            Employee employee = TestDataUtil.createEmployeeA();
            Employee savedEmployee = employeeRepository.save(employee);
            Customer customer = TestDataUtil.createCustomerA(savedEmployee);
            Customer savedCustomer = customerRepository.save(customer);
    
            Note note = TestDataUtil.createNoteA(savedCustomer);
            note.setDate(null);
            // When
            DataIntegrityViolationException exception = assertThrows(
                    DataIntegrityViolationException.class, () -> underTest.save(note));
    
            // Then
            String expectedMessage = "not-null property references a null or transient value";
            assertTrue(exception.getMessage().contains(expectedMessage), expectedMessage);
        }
    
        @Test
        public void itShouldThrowWhenInteractionTypeIsNull() {
            // Given
            Employee employee = TestDataUtil.createEmployeeA();
            Employee savedEmployee = employeeRepository.save(employee);
            Customer customer = TestDataUtil.createCustomerA(savedEmployee);
            Customer savedCustomer = customerRepository.save(customer);
    
            Note note = TestDataUtil.createNoteA(savedCustomer);
            note.setInteractionType(null);
            // When
            DataIntegrityViolationException exception = assertThrows(
                    DataIntegrityViolationException.class, () -> underTest.save(note));
    
            // Then
            String expectedMessage = "not-null property references a null or transient value";
            assertTrue(exception.getMessage().contains(expectedMessage), expectedMessage);
        }
    }
     ```
    </details>
      

### Service-Unit-Tests
- `itShouldCreateCustomer`: Testet die Erstellung eines neuen Kunden.
- `itShouldFindCustomerById`: Testet das Finden eines Kunden anhand der ID.
- `itShouldUpdateCustomer`: Testet die Aktualisierung eines bestehenden Kunden.
- `itShouldDeleteCustomer`: Testet das Löschen eines Kunden.
- `itShouldFindCustomerPerEmail`: Testet das Finden eines Kunden anhand der E-Mail.
- `itShouldThrowExceptionWhenCustomerDoesNotExist`: Testet das Werfen einer Ausnahme, wenn der Kunde nichts existiert.
- `itShouldNotThrowExceptionWhenCustomerExists`: Testet das Nicht-Werfen einer Ausnahme, wenn der kunde existiert.
<details>
<summary style="color: blue"><strong>Klicke hier, um den Code anzuzeigen</strong></summary>

```
package edu.yacoubi.crm.service.impl;

import edu.yacoubi.crm.TestDataUtil;
import edu.yacoubi.crm.model.Customer;
import edu.yacoubi.crm.repository.CustomerRepository;
import edu.yacoubi.crm.service.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;

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

    @Test
    public void itShouldFindCustomerPerEmail() {
        // Given
        String email = "test@example.com";
        Customer customer = TestDataUtil.createCustomerA(null);
        customer.setEmail(email);
        when(customerRepository.findByEmail(email)).thenReturn(Optional.of(customer));

        // When
        Optional<Customer> foundCustomer = underTest.getCustomerByEmail(email);

        // Then
        assertTrue(foundCustomer.isPresent());
        assertEquals(email, foundCustomer.get().getEmail());
        verify(customerRepository, times(1)).findByEmail(email);
    }

    @Test
    public void itShouldThrowExceptionWhenCustomerDoesNotExist() {
        // Given
        Long customerId = 1L;
        when(customerRepository.existsById(customerId)).thenReturn(false);

        // When & Then
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class, () -> underTest.getCustomerById(customerId));
        assertEquals("Customer not found with ID:", exception.getMessage());
    }

    @Test
    public void itShouldNotThrowExceptionWhenCustomerExists() {
        // Given
        Long customerId = 1L;
        Customer customer = TestDataUtil.createCustomerA(null);
        customer.setId(customerId);
        when(customerRepository.existsById(customerId)).thenReturn(true);
        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));

        // When
        Optional<Customer> foundCustomer = underTest.getCustomerById(customerId);

        // Then
        assertTrue(foundCustomer.isPresent());
        assertEquals(customer.getEmail(), foundCustomer.get().getEmail());
    }

}
```
</details>

### Service-Integration-Tests
> Integrationstests verwenden tatsächliche Implementierungen, um sicherzustellen, dass alle Komponenten des Systems
> reibungslos zusammenarbeiten.
> Sie prüfen die Interaktionen zwischen verschiedenen Modulen und erkennen Probleme, die in isolierten Unit-Tests
> möglicherweise nicht auftreten.

## Customer-Integration-Tests
- `itShouldCreateCustomer`: Testet die Erstellung eines neuen Kunden.
- `itShouldFindCustomerById`: Testet das Finden eines Kunden anhand der ID.
- `itShouldUpdateCustomer`: Testet die Aktualisierung eines bestehenden Kunden.
- `itShouldDeleteCustomer`: Testet das Löschen eines Kunden.
- `itShouldFindCustomerPerEmail`: Testet das Finden eines Kunden anhand der E-Mail.
<details>
<summary style="color: blue"><strong>Klicke hier, um den Code anzuzeigen</strong></summary>

```
package edu.yacoubi.crm.service.impl;

import edu.yacoubi.crm.TestDataUtil;
import edu.yacoubi.crm.model.Customer;
import edu.yacoubi.crm.model.Employee;
import edu.yacoubi.crm.repository.EmployeeRepository;
import edu.yacoubi.crm.repository.CustomerRepository;
import edu.yacoubi.crm.service.exception.ResourceNotFoundException;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CustomerServiceImplIntegrationTest {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private CustomerServiceImpl underTest;

    @BeforeEach
    public void setUp() {
        customerRepository.deleteAll();
        employeeRepository.deleteAll();
    }

    @Test
    public void itShouldCreateCustomer() {
        // Given
        Employee employee = TestDataUtil.createEmployeeA();
        Employee savedEmployee = employeeRepository.save(employee);
        Customer customer = TestDataUtil.createCustomerA(savedEmployee);

        // When
        Customer savedCustomer = underTest.createCustomer(customer);

        // Then
        assertNotNull(savedCustomer);
        assertEquals(customer.getFirstName(), savedCustomer.getFirstName());
        assertEquals(customer.getEmail(), savedCustomer.getEmail());
        assertEquals(customer.getEmployee().getId(), savedCustomer.getEmployee().getId());
    }

    @Test
    public void itShouldFindCustomerById() {
        // Given
        Employee employee = TestDataUtil.createEmployeeA();
        Employee savedEmployee = employeeRepository.save(employee);
        Customer customer = TestDataUtil.createCustomerA(savedEmployee);
        Customer savedCustomer = underTest.createCustomer(customer);

        // When
        Customer foundCustomer = underTest.getCustomerById(savedCustomer.getId()).orElse(null);

        // Then
        assertNotNull(foundCustomer);
        assertEquals(savedCustomer.getId(), foundCustomer.getId());
    }

    @Test
    @Transactional
    public void itShouldUpdateCustomer() {
        // Given
        Employee employee = TestDataUtil.createEmployeeA();
        Employee savedEmployee = employeeRepository.save(employee);
        Customer customer = TestDataUtil.createCustomerA(savedEmployee);
        Customer savedCustomer = underTest.createCustomer(customer);
        savedCustomer.setFirstName("Updated Name");

        // When
        Customer updatedCustomer = underTest.updateCustomer(savedCustomer.getId(), savedCustomer);

        // Then
        assertNotNull(updatedCustomer);
        assertEquals("Updated Name", updatedCustomer.getFirstName());
    }

    @Test
    public void itShouldDeleteCustomer() {
        // Given
        Employee employee = TestDataUtil.createEmployeeA();
        Employee savedEmployee = employeeRepository.save(employee);
        Customer customer = TestDataUtil.createCustomerA(savedEmployee);
        Customer savedCustomer = underTest.createCustomer(customer);

        // When
        underTest.deleteCustomer(savedCustomer.getId());

        // Then
        assertThrows(ResourceNotFoundException.class, () -> {
            underTest.getCustomerById(savedCustomer.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Customer not found with ID:"));
        });
    }

    @Test
    public void itShouldGetCustomerByEmail() {
        // Given
        Employee employee = TestDataUtil.createEmployeeA();
        Employee savedEmployee = employeeRepository.save(employee);
        Customer customer = TestDataUtil.createCustomerA(savedEmployee);
        Customer savedCustomer = underTest.createCustomer(customer);

        // When
        Customer foundCustomer = underTest.getCustomerByEmail(savedCustomer.getEmail()).orElse(null);

        // Then
        assertNotNull(foundCustomer);
        assertEquals(savedCustomer.getId(), foundCustomer.getId());
    }
}
```
</details>
