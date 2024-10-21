package edu.yacoubi.crm.repository;

import edu.yacoubi.crm.TestDataUtil;
import edu.yacoubi.crm.dto.CustomerDTO;
import edu.yacoubi.crm.model.Customer;
import edu.yacoubi.crm.model.Employee;
import edu.yacoubi.crm.model.Note;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;

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
        String expectedMessage = "not-null property references a null";
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
}