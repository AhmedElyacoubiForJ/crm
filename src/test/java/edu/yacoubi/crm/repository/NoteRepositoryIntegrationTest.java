package edu.yacoubi.crm.repository;

import edu.yacoubi.crm.util.TestDataUtil;
import edu.yacoubi.crm.model.Customer;
import edu.yacoubi.crm.model.Employee;
import edu.yacoubi.crm.model.InteractionType;
import edu.yacoubi.crm.model.Note;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.InvalidDataAccessApiUsageException;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class NoteRepositoryIntegrationTest {

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
        String expectedMessage = "NULL not allowed for column \"CUSTOMER_ID\"; SQL statement:";
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
        ConstraintViolationException exception = assertThrows(
                ConstraintViolationException.class, () -> underTest.save(note));

        // Then
        String expectedMessage = "Date is mandatory";
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
        ConstraintViolationException exception = assertThrows(
                ConstraintViolationException.class, () -> underTest.save(note));

        // Then
        String expectedMessage = "Interaction type is mandatory";
        assertTrue(exception.getMessage().contains(expectedMessage), expectedMessage);
    }
}