package edu.yacoubi.crm.service.validation;

import ch.qos.logback.classic.Logger;
import edu.yacoubi.crm.TestAppender;
import edu.yacoubi.crm.TestDataUtil;
import edu.yacoubi.crm.exception.ResourceNotFoundException;
import edu.yacoubi.crm.model.Customer;
import edu.yacoubi.crm.model.Employee;
import edu.yacoubi.crm.model.Note;
import edu.yacoubi.crm.repository.CustomerRepository;
import edu.yacoubi.crm.repository.EmployeeRepository;
import edu.yacoubi.crm.repository.NoteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class EntityValidatorIntegrationTest {
    @Autowired
    private EmployeeRepository employeeRepository;
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private NoteRepository noteRepository;

    @Autowired
    private EntityValidator underTest;

    private TestAppender testAppender;

    private Employee employeeA;

    @BeforeEach
    public void setUp() {
        Logger logger = (Logger) LoggerFactory.getLogger(EntityValidator.class);
        testAppender = new TestAppender();
        testAppender.start();
        logger.addAppender(testAppender);
        employeeA = TestDataUtil.createEmployeeA();
    }

    @Test
    public void itShouldValidateEmployeeExists() {
        // Given
        Employee savedEmployee = employeeRepository.save(employeeA);

        // When
        underTest.validateEmployeeExists(savedEmployee.getId());

        // Then
        assertTrue(testAppender.contains(
                String.format("EntityValidator::validateEmployeeExists employeeId: %d", savedEmployee.getId()),
                "INFO"
        ));
        assertFalse(testAppender.contains(
                String.format("EntityValidator::validateEmployeeExists employeeId: %d not found", savedEmployee.getId()),
                "ERROR"
        ));
        assertTrue(testAppender.contains(
                String.format("EntityValidator::validateEmployeeExists employeeId: %d successfully validated", savedEmployee.getId()),
                "INFO"
        ));
    }

    @Test
    public void itShouldThrowWhenEmployeeDoesNotExist() {
        // Given
        Long employeeId = 999L;

        // When & Then
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class, () -> underTest.validateEmployeeExists(employeeId)
        );

        assertEquals("Employee not found with ID: " + employeeId, exception.getMessage());

        assertTrue(testAppender.contains(
                String.format("EntityValidator::validateEmployeeExists employeeId: %d", employeeId),
                "INFO"
        ));
        assertTrue(testAppender.contains(
                String.format("EntityValidator::validateEmployeeExists employeeId: %d not found", employeeId),
                "ERROR"
        ));
        assertFalse(testAppender.contains(
                String.format("EntityValidator::validateEmployeeExists employeeId: %d successfully validated", employeeId),
                "INFO"
        ));
    }

    @Test
    public void itShouldValidateNoteExists() {
        // Given
        Note savedNote = noteRepository
                .save(TestDataUtil.createNoteA(customerRepository.save(TestDataUtil.createCustomerB(employeeRepository.save(employeeA)))));

        // When
        underTest.validateNoteExists(savedNote.getId());

        // Then
        assertTrue(testAppender.contains(
                String.format("EntityValidator::validateNoteExists id: %d", savedNote.getId()),
                "INFO"
        ));
        assertFalse(testAppender.contains(
                String.format("EntityValidator::validateNoteExists id: %d not found", savedNote.getId()),
                "ERROR"
        ));
        assertTrue(testAppender.contains(
                String.format("EntityValidator::validateNoteExists id: %d successfully validated", savedNote.getId()),
                "INFO"
        ));
    }

    @Test
    public void itShouldThrowWhenNoteDoesNotExist() {
        // Given
        Long noteId = 999L;

        // When & Then
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class, () -> underTest.validateNoteExists(noteId)
        );

        assertEquals("Note not found with ID: " + noteId, exception.getMessage());

        assertTrue(testAppender.contains(
                String.format("EntityValidator::validateNoteExists id: %d", noteId),
                "INFO"
        ));
        assertTrue(testAppender.contains(
                String.format("EntityValidator::validateNoteExists id: %d not found", noteId),
                "ERROR"
        ));
        assertFalse(testAppender.contains(
                String.format("EntityValidator::validateNoteExists id: %d successfully validated", noteId),
                "INFO"
        ));
    }

    @Test
    public void itShouldValidateCustomerExists() {
        // Given
        Customer savedCustomer = customerRepository
                .save(TestDataUtil.createCustomerA(employeeRepository.save(employeeA)));

        // When
        underTest.validateCustomerExists(savedCustomer.getId());

        // Then
        assertTrue(testAppender.contains(
                String.format("EntityValidator::validateCustomerExists id: %d", savedCustomer.getId()),
                "INFO"
        ));
        assertFalse(testAppender.contains(
                String.format("EntityValidator::validateCustomerExists id: %d not found", savedCustomer.getId()),
                "ERROR"
        ));
        assertTrue(testAppender.contains(
                String.format("EntityValidator::validateCustomerExists id: %d successfully validated", savedCustomer.getId()),
                "INFO"
        ));
    }

    @Test
    public void itShouldThrowWhenCustomerDoesNotExist() {
        // Given
        Long customerId = 999L;

        // When & Then
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class, () -> underTest.validateCustomerExists(customerId)
        );

        assertEquals("Customer not found with ID: " + customerId, exception.getMessage());

        assertTrue(testAppender.contains(
                String.format("EntityValidator::validateCustomerExists id: %d", customerId),
                "INFO"
        ));
        assertTrue(testAppender.contains(
                String.format("EntityValidator::validateCustomerExists id: %d not found", customerId),
                "ERROR"
        ));
        assertFalse(testAppender.contains(
                String.format("EntityValidator::validateCustomerExists id: %d successfully validated", customerId),
                "INFO"
        ));
    }
}
