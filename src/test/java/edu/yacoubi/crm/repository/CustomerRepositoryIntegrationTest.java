package edu.yacoubi.crm.repository;

import edu.yacoubi.crm.TestDataUtil;
import edu.yacoubi.crm.dto.CustomerDTO;
import edu.yacoubi.crm.model.Customer;
import edu.yacoubi.crm.model.Employee;
import edu.yacoubi.crm.model.Note;
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class CustomerRepositoryIntegrationTest {

    @Autowired
    private CustomerRepository underTest;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private NoteRepository noteRepository;

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
        String expectedMessage = "NULL not allowed for column";
        assertTrue(exception.getMessage().contains(expectedMessage), expectedMessage);
    }

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

    @Test
    public void itShouldNotReturnCustomerByNotExistingEmail() {
        // Given
        String notExistingEmail = "not.existing@example.com";
        // When
        Customer foundCustomer = underTest.findByEmail(notExistingEmail).orElse(null);
        // Then
        assertNull(foundCustomer);
    }

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

    @Test
    @Transactional
    public void itShouldUpdateCustomerByExample() {
        // Given
        Employee employee = TestDataUtil.createEmployeeA();
        Employee savedEmployee = employeeRepository.save(employee);
        Customer customer = TestDataUtil.createCustomerA(savedEmployee);
        Customer savedCustomer = underTest.save(customer);

        // Example customer DTO with new address
        CustomerDTO customerExample = CustomerDTO.builder()
                .address("Neue Straße 123")
                .build();

        // When
        Customer updatedCustomer = underTest.updateCustomerByExample(customerExample, savedCustomer.getId());

        // Then
        assertNotNull(updatedCustomer);
        assertEquals("Neue Straße 123", updatedCustomer.getAddress());
    }

    @Test
    public void itShouldThrowValidationExceptionForInvalidCustomer() {
        // Given
        Employee employee = TestDataUtil.createEmployeeA();
        Employee savedEmployee = employeeRepository.save(employee);
        Customer invalidCustomer = new Customer(); // Invalid because of missing fields
        invalidCustomer.setEmployee(savedEmployee);

        // When & Then
        assertThrows(ConstraintViolationException.class, () -> {
            underTest.save(invalidCustomer);
        });
    }

    @Test
    public void itShouldThrowValidationExceptionForInvalidEmail() {
        // Given
        Employee employee = TestDataUtil.createEmployeeA();
        Employee savedEmployee = employeeRepository.save(employee);
        Customer invalidCustomer = TestDataUtil.createCustomerA(savedEmployee);
        invalidCustomer.setEmail("a.b.de"); // Invalid Email Value

        // When & Then
        assertThrows(ConstraintViolationException.class, () -> {
            underTest.save(invalidCustomer);
        });
    }

    @Test
    public void itShouldThrowValidationExceptionForShortString() {
        // Given
        Employee employee = TestDataUtil.createEmployeeA();
        Employee savedEmployee = employeeRepository.save(employee);
        Customer invalidCustomer = TestDataUtil.createCustomerA(savedEmployee);
        invalidCustomer.setPhone("21"); // Too short

        // When & Then
        assertThrows(ConstraintViolationException.class, () -> {
            underTest.save(invalidCustomer);
        });
    }

    // test notes list if initialized
    @Test
    @Transactional
    public void itShouldConfirmNotesListAlreadyInitialized() {
        // Given
        Employee employee = TestDataUtil.createEmployeeA();
        Employee savedEmployee = employeeRepository.save(employee);
        Customer customer = TestDataUtil.createCustomerA(savedEmployee);
        underTest.save(customer);

        // When
        Customer foundCustomer = underTest.findById(customer.getId()).orElse(null);

        // Then
        assertNotNull(foundCustomer.getNotes());
        assertTrue(foundCustomer.getNotes().isEmpty());
    }

    @Test
    public void itShouldThrowValidationExceptionForMissingPhoneNumber() {
        // Given
        Employee employee = TestDataUtil.createEmployeeA();
        Employee savedEmployee = employeeRepository.save(employee);
        Customer invalidCustomer = TestDataUtil.createCustomerA(savedEmployee);
        invalidCustomer.setPhone(null); // Missing phone number

        // When & Then
        assertThrows(ConstraintViolationException.class, () -> {
            underTest.save(invalidCustomer);
        });
    }

    @Test
    public void itShouldThrowValidationExceptionForTooShortPhoneNumber() {
        // Given
        Employee employee = TestDataUtil.createEmployeeA();
        Employee savedEmployee = employeeRepository.save(employee);
        Customer invalidCustomer = TestDataUtil.createCustomerA(savedEmployee);
        invalidCustomer.setPhone("21"); // Too short

        // When & Then
        ConstraintViolationException exception = assertThrows(ConstraintViolationException.class, () -> {
            underTest.save(invalidCustomer);
        });
        String expectedMessage = "Phone number must be between 10 and 15 characters";
        assertTrue(exception.getMessage().contains(expectedMessage), expectedMessage);
    }

    @Test
    public void itShouldThrowValidationExceptionForTooLongPhoneNumber() {
        // Given
        Employee employee = TestDataUtil.createEmployeeA();
        Employee savedEmployee = employeeRepository.save(employee);
        Customer invalidCustomer = TestDataUtil.createCustomerA(savedEmployee);
        invalidCustomer.setPhone("12345678901234567890"); // Too long

        // When & Then
        ConstraintViolationException exception = assertThrows(ConstraintViolationException.class, () -> {
            underTest.save(invalidCustomer);
        });
        String expectedMessage = "Phone number must be between 10 and 15 characters";
        assertTrue(exception.getMessage().contains(expectedMessage), expectedMessage);
    }

    @Test
    public void itShouldSaveCustomerWithValidPhoneNumber() {
        // Given
        Employee employee = TestDataUtil.createEmployeeA();
        Employee savedEmployee = employeeRepository.save(employee);
        Customer validCustomer = TestDataUtil.createCustomerA(savedEmployee);
        validCustomer.setPhone("1234567890"); // Valid phone number

        // When
        Customer savedCustomer = underTest.save(validCustomer);

        // Then
        Customer foundCustomer = underTest.findById(savedCustomer.getId()).orElse(null);
        assertNotNull(foundCustomer);
        assertEquals("1234567890", foundCustomer.getPhone());
    }
}