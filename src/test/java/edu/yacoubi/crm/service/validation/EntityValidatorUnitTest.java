package edu.yacoubi.crm.service.validation;

import ch.qos.logback.classic.Logger;
import edu.yacoubi.crm.exception.ResourceNotFoundException;
import edu.yacoubi.crm.repository.CustomerRepository;
import edu.yacoubi.crm.repository.EmployeeRepository;
import edu.yacoubi.crm.repository.InactiveEmployeeRepository;
import edu.yacoubi.crm.repository.NoteRepository;
import edu.yacoubi.crm.util.TestAppender;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EntityValidatorUnitTest {
    // Assert supplied failure message
    private static final String ERROR_SUPPLIED_MSG = "Error message should be: %s";
    private static final String INFO_SUPPLIED_MSG  = "Info message should be: %s";

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
        final Logger logger = (Logger) LoggerFactory.getLogger(EntityValidator.class);
        testAppender = new TestAppender();
        testAppender.start();
        logger.addAppender(testAppender);
    }

    @AfterEach
    void tearDown() {
        final Logger logger = (Logger) LoggerFactory.getLogger(EntityValidator.class);
        logger.detachAppender(testAppender);
        testAppender.stop();
    }

    @Test
    void itShouldValidateEmployeeExists() {
        // Given
        final Long employeeId = 1L;
        final String expectedEntryLogMsg = String.format("::validateEmployeeExists started with: employeeId %d",
                employeeId);
        final String expectedExitLogMsg = "::validateEmployeeExists completed successfully";

        when(employeeRepository.existsById(employeeId)).thenReturn(true);

        // When
        underTest.validateEmployeeExists(employeeId);

        // Then
        verify(employeeRepository, times(1)).existsById(employeeId);
        assertTrue(
                testAppender.contains(expectedEntryLogMsg, "INFO"),
                String.format(INFO_SUPPLIED_MSG, expectedEntryLogMsg)
        );
        assertTrue(
                testAppender.contains(expectedExitLogMsg, "INFO"),
                String.format(INFO_SUPPLIED_MSG, expectedExitLogMsg)
        );
    }

    @Test
    void itShouldThrowWhenEmployeeDoesNotExit() {
        // Given
        final Long employeeId = 1L;
        final String errorMessage = "Employee not found with ID: " + employeeId;
        final String expectedErrorLogMsg = String.format("::validateEmployeeExists error: %s",
                errorMessage);

        final String expectedEntryLogMsg = String.format("::validateEmployeeExists started with: employeeId %d",
                employeeId);
        final String expectedExitLogMsg = "::validateEmployeeExists completed successfully";

        when(employeeRepository.existsById(employeeId)).thenReturn(false);

        // When
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> underTest.validateEmployeeExists(employeeId)
        );

        // Then
        verify(employeeRepository, times(1)).existsById(employeeId);
        assertEquals(errorMessage, exception.getMessage());
        assertTrue(
                testAppender.contains(expectedErrorLogMsg, "ERROR"),
                String.format(ERROR_SUPPLIED_MSG, expectedErrorLogMsg)
        );
        assertTrue(
                testAppender.contains(expectedEntryLogMsg, "INFO"),
                String.format(INFO_SUPPLIED_MSG, expectedEntryLogMsg)
        );
        assertFalse(
                testAppender.contains(expectedExitLogMsg, "INFO"),
                String.format(INFO_SUPPLIED_MSG, expectedExitLogMsg)
        );
    }

    @Test
    void itShouldValidateNoteExists() {
        // Given
        Long noteId = 1L;
        final String expectedEntryLogMsg = String.format("::validateNoteExists started with: noteId %d",
                noteId);
        final String expectedExitLogMsg = "::validateNoteExists completed successfully";

        when(noteRepository.existsById(noteId)).thenReturn(true);

        // When
        underTest.validateNoteExists(noteId);

        // Then
        verify(noteRepository, times(1)).existsById(noteId);
        assertTrue(
                testAppender.contains(expectedEntryLogMsg, "INFO"),
                String.format(INFO_SUPPLIED_MSG, expectedEntryLogMsg)
        );
        assertTrue(
                testAppender.contains(expectedExitLogMsg, "INFO"),
                String.format(INFO_SUPPLIED_MSG, expectedExitLogMsg)
        );
    }

    @Test
    void itShouldThrowWhenNoteDoesNotExit() {
        // Given
        final Long noteId = 1L;
        final String errorMessage = "Note not found with ID: " + noteId;
        final String expectedErrorLogMsg = String.format(
                "::validateNoteExists error: %s",
                errorMessage
        );
        final String expectedEntryLogMsg = String.format("::validateNoteExists started with: noteId %d",
                noteId);
        final String expectedExitLogMsg = "::validateNoteExists completed successfully";

        when(noteRepository.existsById(noteId)).thenReturn(false);

        // When
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> underTest.validateNoteExists(noteId)
        );

        // Then
        verify(noteRepository, times(1)).existsById(noteId);
        assertEquals(errorMessage, exception.getMessage());
        assertTrue(
                testAppender.contains(expectedErrorLogMsg, "ERROR"),
                String.format(ERROR_SUPPLIED_MSG, expectedErrorLogMsg)
        );
        assertTrue(
                testAppender.contains(expectedEntryLogMsg, "INFO"),
                String.format(INFO_SUPPLIED_MSG, expectedEntryLogMsg)
        );
        assertFalse(
                testAppender.contains(expectedExitLogMsg, "INFO"),
                String.format(INFO_SUPPLIED_MSG, expectedExitLogMsg)
        );
    }

    @Test
    void itShouldValidateCustomerExists() {
        // Given
        final Long customerId = 1L;
        final String expectedEntryLogMsg = String.format("::validateCustomerExists started with: customerId %d",
                customerId);
        final String expectedExitLogMsg = "::validateCustomerExists completed successfully";

        when(customerRepository.existsById(customerId)).thenReturn(true);

        // When
        underTest.validateCustomerExists(customerId);

        // Then
        verify(customerRepository, times(1)).existsById(customerId);
        assertTrue(
                testAppender.contains(expectedEntryLogMsg, "INFO"),
                String.format(INFO_SUPPLIED_MSG, expectedEntryLogMsg)
        );
        assertTrue(
                testAppender.contains(expectedExitLogMsg, "INFO"),
                String.format(INFO_SUPPLIED_MSG, expectedExitLogMsg)
        );
    }

    @Test
    void itShouldThrowWhenCustomerDoesNotExit() {
        // Given
        final Long customerId = 1L;
        final String errorMessage = "Customer not found with ID: " + customerId;
        final String expectedErrorLogMsg = String.format(
                "::validateCustomerExists error: %s",
                errorMessage
        );
        final String expectedEntryLogMsg = String.format(
                "::validateCustomerExists started with: customerId %d",
                customerId
        );
        final String expectedExitLogMsg = "::validateCustomerExists completed successfully";
        when(customerRepository.existsById(customerId)).thenReturn(false);

        // When
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> underTest.validateCustomerExists(customerId)
        );

        // Then
        verify(customerRepository, times(1)).existsById(customerId);
        assertEquals(errorMessage, exception.getMessage());
        assertTrue(
                testAppender.contains(expectedErrorLogMsg, "ERROR"),
                String.format(ERROR_SUPPLIED_MSG, expectedErrorLogMsg)
        );
        assertTrue(
                testAppender.contains(expectedEntryLogMsg, "INFO"),
                String.format(INFO_SUPPLIED_MSG, expectedEntryLogMsg)
        );
        assertFalse(
                testAppender.contains(expectedExitLogMsg, "INFO"),
                String.format(INFO_SUPPLIED_MSG, expectedExitLogMsg)
        );
    }

    @Test
    void itShouldValidateInactiveEmployeeExits() {
        // Given
        final Long originalEmployeeId = 1L;
        final String expectedEntryLogMsg = String.format(
                "::validateInactiveEmployeeExists started with: originalEmployeeId: %d",
                originalEmployeeId
        );
        final String expectedExitLogMsg = "::validateInactiveEmployeeExists completed successfully";
        when(inactiveEmployeeRepository.existsByOriginalEmployeeId(originalEmployeeId))
                .thenReturn(true);

        // Then
        underTest.validateInactiveEmployeeExists(originalEmployeeId);

        // Then
        verify(inactiveEmployeeRepository, times(1)).existsByOriginalEmployeeId(originalEmployeeId);
        assertTrue(
                testAppender.contains(expectedEntryLogMsg, "INFO"),
                String.format(INFO_SUPPLIED_MSG, expectedEntryLogMsg)
        );
        assertTrue(testAppender.contains(expectedExitLogMsg, "INFO"),
                String.format(INFO_SUPPLIED_MSG, expectedExitLogMsg)
        );
    }

    @Test
    void itShouldThrowWhenInactiveEmployeeDoesNotExit() {
        // Given
        final Long originalEmployeeId = 1L;
        final String errorMessage = "Inactive employee not found with ID: " + originalEmployeeId;
        final String expectedErrorLogMsg = String.format(
                "::validateInactiveEmployeeExists error: %s",
                errorMessage
        );
        final String expectedEntryLogMsg = String.format(
                "::validateInactiveEmployeeExists started with: originalEmployeeId: %d",
                originalEmployeeId
        );
        final String expectedExitLogMsg = "::validateInactiveEmployeeExists completed successfully";
        when(inactiveEmployeeRepository.existsByOriginalEmployeeId(originalEmployeeId)).thenReturn(false);

        // When
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> underTest.validateInactiveEmployeeExists(originalEmployeeId)
        );

        // Then
        verify(inactiveEmployeeRepository, times(1)).existsByOriginalEmployeeId(originalEmployeeId);
        assertEquals(errorMessage, exception.getMessage());
        assertTrue(
                testAppender.contains(expectedErrorLogMsg, "ERROR"),
                String.format(ERROR_SUPPLIED_MSG, expectedErrorLogMsg)
        );
        assertTrue(
                testAppender.contains(expectedEntryLogMsg, "INFO"),
                String.format(INFO_SUPPLIED_MSG, expectedEntryLogMsg)
        );
        assertFalse(
                testAppender.contains(expectedExitLogMsg, "INFO"),
                String.format(INFO_SUPPLIED_MSG, expectedExitLogMsg)
        );
    }

    @Test
    void itShouldReturnFalseWhenEmployeeHasNoCustomers() {
        // Given
        final Long employeeId = 1L;
        final String expectedEntryLogMsg = String.format(
                "::validateEmployeeHasCustomers started with: employeeId: %d",
                employeeId
        );
        final boolean hasCustomers = false;
        final String expectedExitLogMsg = String.format(
                "::validateEmployeeHasCustomers completed successfully: employeeId: %d hasCustomers: %s",
                employeeId,
                hasCustomers
        );
        when(employeeRepository.hasCustomers(employeeId)).thenReturn(hasCustomers);

        // When
        final boolean result = underTest.validateEmployeeHasCustomers(employeeId);

        // Then
        verify(employeeRepository, times(1)).hasCustomers(employeeId);
        assertFalse(result);
        assertTrue(
                testAppender.contains(expectedEntryLogMsg, "INFO"),
                String.format(INFO_SUPPLIED_MSG, expectedEntryLogMsg)
        );
        assertTrue(
                testAppender.contains(expectedExitLogMsg, "INFO"),
                String.format(INFO_SUPPLIED_MSG, expectedExitLogMsg)
        );
    }

    @Test
    void itShouldReturnTrueWhenEmployeeValidateEmployeeHasCustomers() {
        // Given
        final Long employeeId = 1L;
        final String expectedEntryLogMsg = String.format(
                "::validateEmployeeHasCustomers started with: employeeId: %d",
                employeeId
        );
        final boolean hasCustomers = true;
        final String expectedExitLogMsg = String.format(
                "::validateEmployeeHasCustomers completed successfully: employeeId: %d hasCustomers: %s",
                employeeId,
                hasCustomers
        );
        when(employeeRepository.hasCustomers(employeeId)).thenReturn(hasCustomers);

        // When
        final boolean result = underTest.validateEmployeeHasCustomers(employeeId);

        // Then
        verify(employeeRepository, times(1)).hasCustomers(employeeId);
        assertTrue(result);
        assertTrue(
                testAppender.contains(expectedEntryLogMsg, "INFO"),
                String.format(INFO_SUPPLIED_MSG, expectedEntryLogMsg)
        );
        assertTrue(
                testAppender.contains(expectedExitLogMsg, "INFO"),
                String.format(INFO_SUPPLIED_MSG, expectedExitLogMsg)
        );
    }
}