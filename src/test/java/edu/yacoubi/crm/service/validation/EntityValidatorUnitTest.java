package edu.yacoubi.crm.service.validation;

import ch.qos.logback.classic.Logger;
import edu.yacoubi.crm.util.TestAppender;
import edu.yacoubi.crm.exception.ResourceNotFoundException;
import edu.yacoubi.crm.repository.CustomerRepository;
import edu.yacoubi.crm.repository.EmployeeRepository;
import edu.yacoubi.crm.repository.NoteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EntityValidatorUnitTest {
    @Mock
    private EmployeeRepository employeeRepository;
    @Mock
    private CustomerRepository customerRepository;
    @Mock
    private NoteRepository noteRepository;
    @InjectMocks
    private EntityValidator underTest;

    private TestAppender testAppender;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        Logger logger = (Logger) LoggerFactory.getLogger(EntityValidator.class);
        testAppender = new TestAppender();
        testAppender.start();
        logger.addAppender(testAppender);
    }

    @Test
    public void itShouldValidateEmployeeExists() {
        // Given
        Long employeeId = 1L;
        when(employeeRepository.existsById(employeeId)).thenReturn(true);

        // When
        underTest.validateEmployeeExists(employeeId);

        // Then
        verify(employeeRepository, times(1)).existsById(employeeId);

        assertTrue(testAppender.contains(
                String.format("EntityValidator::validateEmployeeExists employeeId: %d", employeeId),
                "INFO"
        ));
        assertFalse(testAppender.contains(
                String.format("EntityValidator::validateEmployeeExists employeeId: %d not found", employeeId),
                "ERROR"
        ));
        assertTrue(testAppender.contains(
                String.format("EntityValidator::validateEmployeeExists employeeId: %d successfully validated", employeeId),
                "INFO"
        ));
    }

    @Test
    public void itShouldThrowWhenEmployeeDoesNotExit() {
        // Given
        Long employeeId = 1L;
        when(employeeRepository.existsById(employeeId)).thenReturn(false);

        // When
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class, () -> underTest.validateEmployeeExists(employeeId)
        );

        // Then
        verify(employeeRepository, times(1)).existsById(employeeId);

        assertEquals(String.format("Employee not found with ID: %d", employeeId), exception.getMessage());

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
        Long noteId = 1L;
        when(noteRepository.existsById(noteId)).thenReturn(true);

        // When
        underTest.validateNoteExists(noteId);

        // Then
        verify(noteRepository, times(1)).existsById(noteId);

        assertTrue(testAppender.contains(
                String.format("EntityValidator::validateNoteExists id: %d", noteId),
                "INFO"
        ));
        assertFalse(testAppender.contains(
                String.format("EntityValidator::validateNoteExists id: %d not found", noteId),
                "ERROR"
        ));
        assertTrue(testAppender.contains(
                String.format("EntityValidator::validateNoteExists id: %d successfully validated", noteId),
                "INFO"
        ));
    }

    @Test
    public void itShouldThrowWhenNoteDoesNotExit() {
        // Given
        Long noteId = 1L;
        when(noteRepository.existsById(noteId)).thenReturn(false);

        // When
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class, () -> underTest.validateNoteExists(noteId)
        );

        // Then
        verify(noteRepository, times(1)).existsById(noteId);

        assertEquals(String.format("Note not found with ID: %d", noteId), exception.getMessage());

        assertTrue(testAppender.contains(
                String.format("EntityValidator::validateNoteExists id: %d", noteId),
                "INFO"
        ));
        assertTrue(testAppender.contains(
                String.format("EntityValidator::validateNoteExists id: %d not found", noteId),
                "ERROR"
        ));
        assertTrue(testAppender.contains(
                String.format("EntityValidator::validateNoteExists id: %d not found", noteId),
                "ERROR"
        ));
    }

    @Test
    public void itShouldValidateCustomerExists() {
        // Given
        Long customerId = 1L;
        when(customerRepository.existsById(customerId)).thenReturn(true);

        // When
        underTest.validateCustomerExists(customerId);

        // Then
        verify(customerRepository, times(1)).existsById(customerId);

        assertTrue(testAppender.contains(
                String.format("EntityValidator::validateCustomerExists id: %d", customerId),
                "INFO"
        ));
        assertFalse(testAppender.contains(
                String.format("EntityValidator::validateCustomerExists id: %d not found", customerId),
                "ERROR"
        ));
        assertTrue(testAppender.contains(
                String.format("EntityValidator::validateCustomerExists id: %d successfully validated", customerId),
                "INFO"
        ));
    }

    @Test
    public void itShouldThrowWhenCustomerDoesNotExit() {
        // Given
        Long customerId = 1L;
        when(customerRepository.existsById(customerId)).thenReturn(false);

        // When
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class, () -> underTest.validateCustomerExists(customerId)
        );

        // Then
        verify(customerRepository, times(1)).existsById(customerId);

        assertEquals(String.format("Customer not found with ID: %d", customerId), exception.getMessage());

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