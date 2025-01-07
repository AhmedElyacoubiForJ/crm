package edu.yacoubi.crm.service.validation;

import ch.qos.logback.classic.Logger;
import edu.yacoubi.crm.exception.ResourceNotFoundException;
import edu.yacoubi.crm.repository.CustomerRepository;
import edu.yacoubi.crm.repository.EmployeeRepository;
import edu.yacoubi.crm.repository.InactiveEmployeeRepository;
import edu.yacoubi.crm.repository.NoteRepository;
import edu.yacoubi.crm.util.TestAppender;
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
    @Mock
    private InactiveEmployeeRepository inactiveEmployeeRepository;
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
    void itShouldValidateEmployeeExists() {
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
        assertTrue(testAppender.contains(
                String.format("EntityValidator::validateEmployeeExists employeeId: %d successfully validated", employeeId),
                "INFO"
        ));
    }

    @Test
    void itShouldThrowWhenEmployeeDoesNotExit() {
        // Given
        Long employeeId = 1L;
        String errorMessage = "Employee not found with ID: " + employeeId;
        when(employeeRepository.existsById(employeeId)).thenReturn(false);

        // When
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class, () -> underTest.validateEmployeeExists(employeeId)
        );

        // Then
        verify(employeeRepository, times(1)).existsById(employeeId);
        assertEquals(errorMessage, exception.getMessage());
        assertTrue(testAppender.contains(
                String.format("EntityValidator::validateEmployeeExists employeeId: %d", employeeId),
                "INFO"
        ));
        assertTrue(testAppender.contains(
                String.format("EntityValidator::validateEmployeeExists error: %s", errorMessage),
                "ERROR"
        ));
        assertFalse(testAppender.contains(
                String.format("EntityValidator::validateEmployeeExists employeeId: %d successfully validated", employeeId),
                "INFO"
        ));
    }

    @Test
    void itShouldValidateNoteExists() {
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
    void itShouldThrowWhenNoteDoesNotExit() {
        // Given
        Long noteId = 1L;
        String errorMessage = "Note not found with ID: " + noteId;
        when(noteRepository.existsById(noteId)).thenReturn(false);

        // When
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class, () -> underTest.validateNoteExists(noteId)
        );

        // Then
        verify(noteRepository, times(1)).existsById(noteId);

        assertEquals(errorMessage, exception.getMessage());
        assertTrue(testAppender.contains(
                String.format("EntityValidator::validateNoteExists id: %d", noteId),
                "INFO"
        ));
        assertTrue(testAppender.contains(
                String.format("EntityValidator::validateNoteExists error: %s", errorMessage),
                "ERROR"
        ));
        assertFalse(testAppender.contains(
                String.format("EntityValidator::validateNoteExists id: %d successfully validated", noteId),
                "INFO"
        ));
    }

    @Test
    void itShouldValidateCustomerExists() {
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
    void itShouldThrowWhenCustomerDoesNotExit() {
        // Given
        Long customerId = 1L;
        String errorMessage = "Customer not found with ID: " + customerId;
        when(customerRepository.existsById(customerId)).thenReturn(false);

        // When
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class, () -> underTest.validateCustomerExists(customerId)
        );

        // Then
        verify(customerRepository, times(1)).existsById(customerId);

        assertEquals(errorMessage, exception.getMessage());
        assertTrue(testAppender.contains(
                String.format("EntityValidator::validateCustomerExists id: %d", customerId),
                "INFO"
        ));
        assertTrue(testAppender.contains(
                String.format("EntityValidator::validateCustomerExists error: %s", errorMessage),
                "ERROR"
        ));
        assertFalse(testAppender.contains(
                String.format("EntityValidator::validateCustomerExists id: %d successfully validated", customerId),
                "INFO"
        ));
    }

    @Test
    void itShouldValidateInactiveEmployeeExits() {
        // Given
        final Long originalEmployeeId = 1L;
        when(inactiveEmployeeRepository.existsByOriginalEmployeeId(originalEmployeeId)).thenReturn(true);

        // Then
        underTest.validateInactiveEmployeeExists(originalEmployeeId);

        // Then
        // Verify repo. call
        verify(inactiveEmployeeRepository, times(1)).existsByOriginalEmployeeId(originalEmployeeId);

        // verify logs
        assertTrue(
                testAppender.contains(
                        String.format("EntityValidator::validateInactiveEmployeeExists originalEmployeeId: %d", originalEmployeeId),
                        "INFO"
                ),
                "Should indicate the entry point for inactiveEmployee exists");
        assertTrue(testAppender.contains(
                        String.format("EntityValidator::validateInactiveEmployeeExists originalEmployeeId: %d successfully validated", originalEmployeeId),
                        "INFO"
                ),
                "Should indicate the exit point for inactiveEmployee successfully validated");
    }

    @Test
    void itShouldThrowWhenInactiveEmployeeDoesNotExit() {
        // Given
        Long originalEmployeeId = 1L;
        String errorMessage = "Inactive employee not found with ID: " + originalEmployeeId;
        when(inactiveEmployeeRepository.existsByOriginalEmployeeId(originalEmployeeId)).thenReturn(false);

        // When
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class, () -> underTest.validateInactiveEmployeeExists(originalEmployeeId)
        );

        // Then
        verify(inactiveEmployeeRepository, times(1)).existsByOriginalEmployeeId(originalEmployeeId);

        assertEquals(errorMessage, exception.getMessage());
        assertTrue(testAppender.contains(
                String.format("EntityValidator::validateInactiveEmployeeExists originalEmployeeId: %d", originalEmployeeId),
                "INFO"
        ));
        assertTrue(testAppender.contains(
                String.format("EntityValidator::validateInactiveEmployeeExists error: %s", errorMessage),
                "ERROR"
        ));
    }

    @Test
    void itShouldReturnFalseWhenInactiveEmployeeHasNoCustomers() {
        // Given
        final Long employeeId = 1L;
        final boolean hasCustomers = false;
        when(employeeRepository.hasCustomers(employeeId)).thenReturn(hasCustomers);

        // When
        final boolean result = underTest.hasCustomers(employeeId);

        // Then
        verify(employeeRepository, times(1)).hasCustomers(employeeId);
        assertFalse(result);
        assertTrue(
                testAppender.contains(
                        String.format("EntityValidator::hasCustomers employeeId: %d", employeeId),
                        "INFO"
                ),
                "Should indicate the entry point for hasCustomers"
        );
        assertTrue(
                testAppender.contains(
                        String.format("EntityValidator::hasCustomers employeeId: %d hasCustomers: %s", employeeId, hasCustomers),
                        "INFO"
                ),
                "Should indicate the exit point for hasCustomers"
        );
    }

    @Test
    void itShouldReturnTrueWhenInactiveEmployeeHasCustomers() {
        // Given
        final Long employeeId = 1L;
        final boolean hasCustomers = true;
        when(employeeRepository.hasCustomers(employeeId)).thenReturn(hasCustomers);

        // When
        final boolean result = underTest.hasCustomers(employeeId);

        // Then
        verify(employeeRepository, times(1)).hasCustomers(employeeId);
        assertTrue(result);
        assertTrue(
                testAppender.contains(
                        String.format("EntityValidator::hasCustomers employeeId: %d", employeeId),
                        "INFO"
                ),
                "Should indicate the entry point for hasCustomers"
        );
        assertTrue(
                testAppender.contains(
                        String.format("EntityValidator::hasCustomers employeeId: %d hasCustomers: %s", employeeId, hasCustomers),
                        "INFO"
                ),
                "Should indicate the exit point for hasCustomers"
        );
    }
}