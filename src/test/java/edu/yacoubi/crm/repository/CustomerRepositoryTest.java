package edu.yacoubi.crm.repository;

import edu.yacoubi.crm.model.Customer;
import edu.yacoubi.crm.model.Employee;
import edu.yacoubi.crm.model.InteractionType;
import edu.yacoubi.crm.model.Note;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class CustomerRepositoryTest {

    @Autowired
    private CustomerRepository underTest;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private NoteRepository noteRepository;

    @Test
    public void testCreateReadUpdateDelete() {
        // Given
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
        Customer savedCustomer = underTest.save(customer);

        // Read the customer
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

    @Test
    public void testCreateCustomerWithoutEmployeeFailed() {
        // Given
        // Create a new customer without an employee
        Customer customer = Customer.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .phone("1234567890")
                .address("123 Main St")
                .lastInteractionDate(LocalDate.now())
                .build();

        // When
        // This should throw an exception because the employee is missing
        DataIntegrityViolationException exception = assertThrows(DataIntegrityViolationException.class, () -> {
            underTest.save(customer);
        });

        // Then
        String expectedMessage = "not-null property references a null";
        assertTrue(exception.getMessage().contains(expectedMessage), expectedMessage);
    }

    @Test
    public void testFindCustomerByEmail() {
        // Given
        // Create a new employee
        Employee employee = Employee.builder()
                .firstName("Jane")
                .lastName("Doe")
                .email("jane.doe@example.com")
                .department("Sales")
                .build();

        // Save the employee
        Employee savedEmployee = employeeRepository.save(employee);
        // Create and save a new customer
        Customer customer = Customer.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .phone("1234567890")
                .address("123 Main St")
                .lastInteractionDate(LocalDate.now())
                .employee(savedEmployee)
                .build();
        underTest.save(customer);

        // Find the customer by email
        Customer foundCustomer = underTest.findByEmail("john.doe@example.com").orElse(null);
        assertNotNull(foundCustomer);
        assertEquals("john.doe@example.com", foundCustomer.getEmail());
    }

    @Test
    public void testFindCustomerByEmailNotFound() {
        // Given
        // Create a new customer
        Customer customer = Customer.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .phone("1234567890")
                .address("123 Main St")
                .lastInteractionDate(LocalDate.now())
                .build();

        // When
        // This should not throw an exception because the customer does not exist
        Customer foundCustomer = underTest.findByEmail("not.found@example.com").orElse(null);

        // Then
        assertNull(foundCustomer);
    }

    @Test
    public void testCreateCustomerWithNotes() {
        // Given
        Employee employee = Employee.builder()
                .firstName("Jane")
                .lastName("Doe")
                .email("jane.doe@example.com")
                .department("Sales")
                .build();
        Employee savedEmployee = employeeRepository.save(employee);

        Customer customer = Customer.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .phone("1234567890")
                .address("123 Main St")
                .lastInteractionDate(LocalDate.now())
                .employee(savedEmployee)
                .build();
        Customer savedCustomer = underTest.save(customer);

        // Create notes
        Note note1 = Note.builder()
                .content("First interaction")
                .date(LocalDate.now())
                .interactionType(InteractionType.EMAIL)
                .customer(savedCustomer) // Associate note with saved customer
                .build();

        Note note2 = Note.builder()
                .content("Follow-up call")
                .date(LocalDate.now().plusDays(1))
                .interactionType(InteractionType.PHONE_CALL)
                .customer(savedCustomer) // Associate note with saved customer
                .build();

        // Add notes to customer
        List<Note> notes = new ArrayList<>();
        notes.add(note1);
        notes.add(note2);
        savedCustomer.setNotes(notes);

        // Save the customer again with notes
        underTest.save(savedCustomer);

        // When
        Customer foundCustomer = underTest.findById(savedCustomer.getId()).orElse(null);

        // Then
        assertNotNull(foundCustomer);
        assertEquals(2, foundCustomer.getNotes().size());
    }

    @Test
    @Transactional
    public void testCascadeDeleteCustomerWithNotes() {
        // Given
        Employee employee = Employee.builder()
                .firstName("Jane")
                .lastName("Doe")
                .email("jane.doe@example.com")
                .department("Sales")
                .build();
        Employee savedEmployee = employeeRepository.save(employee);

        Customer customer = Customer.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .phone("1234567890")
                .address("123 Main St")
                .lastInteractionDate(LocalDate.now())
                .employee(savedEmployee)
                .build();
        Customer savedCustomer = underTest.save(customer);

        // Create notes
        Note note1 = Note.builder()
                .content("First interaction")
                .date(LocalDate.now())
                .interactionType(InteractionType.EMAIL)
                .customer(savedCustomer)
                .build();

        Note note2 = Note.builder()
                .content("Follow-up call")
                .date(LocalDate.now().plusDays(1))
                .interactionType(InteractionType.PHONE_CALL)
                .customer(savedCustomer)
                .build();

        // Create a mutable list and add notes
        List<Note> notes = new ArrayList<>();
        notes.add(note1);
        notes.add(note2);
        savedCustomer.setNotes(notes);

        // Save the customer again with notes
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
}
