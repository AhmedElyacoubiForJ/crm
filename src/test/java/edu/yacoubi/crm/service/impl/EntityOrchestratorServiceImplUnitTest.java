package edu.yacoubi.crm.service.impl;

import ch.qos.logback.classic.Logger;
import edu.yacoubi.crm.exception.ResourceNotFoundException;
import edu.yacoubi.crm.model.Customer;
import edu.yacoubi.crm.model.Employee;
import edu.yacoubi.crm.model.InactiveEmployee;
import edu.yacoubi.crm.service.ICustomerService;
import edu.yacoubi.crm.service.IEmployeeService;
import edu.yacoubi.crm.service.IInactiveEmployeeService;
import edu.yacoubi.crm.util.TestAppender;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static edu.yacoubi.crm.util.Constants.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

/**
 * Dieser Code enthält Unit-Tests für die Hauptmethoden meines EntityOrchestratorServiceImpl.
 * Er deckt die Erfolgsfälle sowie Fälle ab, in denen Ausnahmen geworfen werden, wenn die Validierung fehlschlägt.
 * Die Tests verwenden Mockito, um die Abhängigkeiten zu mocken und das Verhalten zu verifizieren.
 */
class EntityOrchestratorServiceImplUnitTest {
    private static TestAppender testAppender;

    @Mock
    private IEmployeeService employeeService;
    @Mock
    private ICustomerService customerService;
    @Mock
    private IInactiveEmployeeService inactiveEmployeeService;

    @InjectMocks
    private EntityOrchestratorServiceImpl underTest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        final Logger logger = (Logger) LoggerFactory.getLogger(EntityOrchestratorServiceImpl.class);
        testAppender = new TestAppender();
        testAppender.start();
        logger.addAppender(testAppender);
    }

    @AfterEach
    void tearDown() {
        final Logger logger = (Logger) LoggerFactory.getLogger(EntityOrchestratorServiceImpl.class);
        logger.detachAppender(testAppender);
        testAppender.stop();
    }

    @Test
    void itShouldThrowExceptionWhenOldEmployeeIdIsNullByCallingReassignCustomers() {
        // Given
        final Long oldEmployeeId = null;
        final Long newEmployeeId = 1L;
        final String expectedEntryLogMsg = String.format(INFO_LOG_REASSIGN_CUSTOMERS_ENTRY_POINT,
                oldEmployeeId, newEmployeeId);
        final String expectedExitLogMsg = String.format(INFO_LOG_REASSIGN_CUSTOMERS_EXIT_POINT,
                oldEmployeeId, newEmployeeId);

        // When
        final IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            underTest.reassignCustomers(oldEmployeeId, newEmployeeId);
        });

        // Then / Verify
        // Error exception message
        assertEquals(
                // Erwarteter Wert
                ERROR_INVALID_IDS_MSG,
                // Tatsächlicher Wert
                exception.getMessage(),
                // Nachricht bei Fehlschlag
                String.format(ERROR_SUPPLIED_MSG, ERROR_INVALID_IDS_MSG)
        );
        // Logger entry & exit messages
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
    void itShouldThrowExceptionWhenNewEmployeeIdIsNullByCallingReassignCustomers() {
        // Given
        final Long oldEmployeeId = 1L;
        final Long newEmployeeId = null; // set to null to test the precondition
        final String expectedEntryLogMsg = String.format(INFO_LOG_REASSIGN_CUSTOMERS_ENTRY_POINT,
                oldEmployeeId, newEmployeeId);
        final String expectedExitLogMsg = String.format(INFO_LOG_REASSIGN_CUSTOMERS_EXIT_POINT,
                oldEmployeeId, newEmployeeId);

        // When
        final IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            underTest.reassignCustomers(oldEmployeeId, newEmployeeId);
        });

        // Then / Verify
        // Error exception message
        assertEquals(
                // Erwartete Wert
                ERROR_INVALID_IDS_MSG,
                // Tatsächlicher Wert
                exception.getMessage(),
                // Nachricht bei Fehlschlag
                String.format(ERROR_SUPPLIED_MSG, ERROR_INVALID_IDS_MSG)
        );
        // Logger entry & exit messages
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
    void itShouldThrowExceptionWhenOldEmployeeIdIsNegativeByCallingReassignCustomers() {
        // Given
        final Long oldEmployeeId = -1L;
        final Long newEmployeeId = 1L;
        final String expectedEntryLogMsg = String.format(INFO_LOG_REASSIGN_CUSTOMERS_ENTRY_POINT,
                oldEmployeeId, newEmployeeId);
        final String expectedExitLogMsg = String.format(INFO_LOG_REASSIGN_CUSTOMERS_EXIT_POINT,
                oldEmployeeId, newEmployeeId);

        // When
        final IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            underTest.reassignCustomers(oldEmployeeId, newEmployeeId);
        });

        // Then / Verify
        // Error exception message
        assertEquals(
                // Erwartete Wert
                ERROR_INVALID_IDS_MSG,
                // Tatsächlicher Wert
                exception.getMessage(),
                // Nachricht bei Fehlschlag
                String.format(ERROR_SUPPLIED_MSG, ERROR_INVALID_IDS_MSG)
        );
        // Logger entry & exit messages
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
    void itShouldThrowExceptionWhenNewEmployeeIdIsNegativeByCallingReassignCustomers() {
        // Given
        final Long oldEmployeeId = 1L;
        final Long newEmployeeId = -1L;
        final String expectedEntryLogMsg = String.format(INFO_LOG_REASSIGN_CUSTOMERS_ENTRY_POINT,
                oldEmployeeId, newEmployeeId);
        final String expectedExitLogMsg = String.format(INFO_LOG_REASSIGN_CUSTOMERS_EXIT_POINT,
                oldEmployeeId, newEmployeeId);

        // When
        final IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            underTest.reassignCustomers(oldEmployeeId, newEmployeeId);
        });

        // Then / Verify
        // Error exception message
        assertEquals(
                // Erwartete Wert
                ERROR_INVALID_IDS_MSG,
                // Tatsächlicher Wert
                exception.getMessage(),
                // Nachricht bei Fehlschlag
                String.format(ERROR_SUPPLIED_MSG, ERROR_INVALID_IDS_MSG)
        );
        // Logger entry & exit messages
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
    void itShouldThrowExceptionWhenOldEmployeeAndNewEmployeeIdsAreEqualsByCallingReassignCustomers() {
        // Given
        final Long oldEmployeeId = 1L;
        final Long newEmployeeId = 1L;
        final String expectedEntryLogMsg = String.format(INFO_LOG_REASSIGN_CUSTOMERS_ENTRY_POINT,
                oldEmployeeId, newEmployeeId);
        final String expectedExitLogMsg = String.format(INFO_LOG_REASSIGN_CUSTOMERS_EXIT_POINT,
                oldEmployeeId, newEmployeeId);

        // When
        final IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            underTest.reassignCustomers(oldEmployeeId, newEmployeeId);
        });

        // Then / Verify
        // Error exception message & Logger
        assertEquals(
                // Erwartete Wert
                ERROR_SAME_IDS_MSG,
                // Tatsächlicher Wert
                exception.getMessage(),
                // Nachricht bei Fehlschlag
                String.format(ERROR_SUPPLIED_MSG, ERROR_SAME_IDS_MSG)
        );
        assertTrue(
                testAppender.contains(ERROR_SAME_IDS_MSG, "WARN"),
                String.format(WARN_SUPPLIED_MSG, ERROR_SAME_IDS_MSG)
        );

        // Logger entry & exit messages
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
    void itShouldThrowExceptionWhenCustomersNotFoundByOldEmployeeIdByCallingReassignCustomers() {
        // Given
        final Long oldEmployeeId = 1L;
        final Long newEmployeeId = 2L;
        final String expectedEntryLogMsg = String.format(INFO_LOG_REASSIGN_CUSTOMERS_ENTRY_POINT,
                oldEmployeeId, newEmployeeId);
        final String expectedExitLogMsg = String.format(INFO_LOG_REASSIGN_CUSTOMERS_EXIT_POINT,
                oldEmployeeId, newEmployeeId);

        when(customerService.getCustomersByEmployeeId(oldEmployeeId))
                .thenReturn(new ArrayList<>());

        // When
        final ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            underTest.reassignCustomers(oldEmployeeId, newEmployeeId);
        });

        // Then / Verify
        // Error exception message & logger
        final String expectedErrorMsg = String.format(ERROR_MSG_NO_CUSTOMERS, oldEmployeeId);
        assertEquals(
                expectedErrorMsg,
                exception.getMessage(),
                String.format(ERROR_SUPPLIED_MSG, expectedErrorMsg)
        );
        assertTrue(
                testAppender.contains(expectedErrorMsg, "WARN"),
                String.format(WARN_SUPPLIED_MSG, expectedErrorMsg)
        );
        // Logger entry & exit messages
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
    void itShouldThrowExceptionWhenGetCustomersByEmployeeIdAndEmployeeDoesNotExistByCallingReassignCustomers() {
        // Given
        final Long oldEmployeeId = 999L;
        final Long newEmployeeId = 2L;
        final String errorMessage = "Employee not found with ID: " + oldEmployeeId;
        final String expectedEntryLogMsg = String.format(INFO_LOG_REASSIGN_CUSTOMERS_ENTRY_POINT,
                oldEmployeeId, newEmployeeId);
        final String expectedExitLogMsg = String.format(INFO_LOG_REASSIGN_CUSTOMERS_EXIT_POINT,
                oldEmployeeId, newEmployeeId);

        doThrow(new ResourceNotFoundException(errorMessage))
                .when(customerService).getCustomersByEmployeeId(oldEmployeeId);

        // When
        final ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            underTest.reassignCustomers(oldEmployeeId, newEmployeeId);
        });

        // Then / Verify
        // Error exception message
        assertEquals(
                errorMessage,
                exception.getMessage(),
                String.format(ERROR_SUPPLIED_MSG, errorMessage)
        );
        // Logger entry & exit message
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
    void itShouldThrowExceptionWhenGetCustomersByEmployeeIdAndEmployeeDoesNotHaveCustomersByCallingReassignCustomers() {
        // Given
        final Long oldEmployeeId = 1L;
        final Long newEmployeeId = 2L;
        final String errorMessage = "No customers found for oldEmployee ID: " + oldEmployeeId;
        final String expectedEntryLogMsg = String.format(INFO_LOG_REASSIGN_CUSTOMERS_ENTRY_POINT,
                oldEmployeeId, newEmployeeId);
        final String expectedExitLogMsg = String.format(INFO_LOG_REASSIGN_CUSTOMERS_EXIT_POINT,
                oldEmployeeId, newEmployeeId);

        when(customerService.getCustomersByEmployeeId(oldEmployeeId))
                .thenReturn(Collections.emptyList());

        // When
        final ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            underTest.reassignCustomers(oldEmployeeId, newEmployeeId);
        });

        // Then / Verify
        // Exception error & logger message
        assertEquals(
                errorMessage,
                exception.getMessage(),
                String.format(ERROR_SUPPLIED_MSG, errorMessage)
        );
        assertTrue(
                testAppender.contains(
                        String.format(WARN_ENTITY_LOG_REASSIGN_CUS_2_EMP, errorMessage),
                        "WARN"
                ),
                String.format(WARN_SUPPLIED_MSG, errorMessage)
        );
        // Logger info entry & exit messages
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
    void itShouldThrowExceptionWhenGetEmployeeByIdForNewEmployeeDoesNotExistByCallingReassignCustomers() {
        // Given
        final Long oldEmployeeId = 1L;
        final Long newEmployeeId = 999L;
        final String errorMessage = "Employee not found with ID: " + newEmployeeId;
        final String expectedEntryLogMsg = String.format(INFO_LOG_REASSIGN_CUSTOMERS_ENTRY_POINT,
                oldEmployeeId, newEmployeeId);
        final String expectedExitLogMsg = String.format(INFO_LOG_REASSIGN_CUSTOMERS_EXIT_POINT,
                oldEmployeeId, newEmployeeId);

        doThrow(new ResourceNotFoundException(errorMessage))
                .when(employeeService).getEmployeeById(anyLong());
        when(customerService.getCustomersByEmployeeId(oldEmployeeId))
                .thenReturn(List.of(new Customer()));

        // When
        final ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            underTest.reassignCustomers(oldEmployeeId, newEmployeeId);
        });

        // Then / Verify
        // Error exception message
        assertEquals(
                errorMessage,
                exception.getMessage(),
                String.format(ERROR_SUPPLIED_MSG, errorMessage)
        );
        // Logger info entry & exit messages
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
    void itShouldReassignCustomersFromOldEmployeeToNewEmployeeByCallingReassignCustomers() {
        // Given
        final Long oldEmployeeId = 1L;
        final Long newEmployeeId = 2L;
        final String expectedEntryLogMsg = String.format(INFO_LOG_REASSIGN_CUSTOMERS_ENTRY_POINT,
                oldEmployeeId, newEmployeeId);
        final String expectedExitLogMsg = String.format(INFO_LOG_REASSIGN_CUSTOMERS_EXIT_POINT,
                oldEmployeeId, newEmployeeId);

        final List<Customer> customers = List.of(
                Customer.builder().id(111L).build(),
                Customer.builder().id(222L).build()
        );

        when(customerService.getCustomersByEmployeeId(oldEmployeeId))
                .thenReturn(customers);
        final Employee newEmployee = Employee.builder().id(newEmployeeId).build();
        when(employeeService.getEmployeeById(newEmployeeId))
                .thenReturn(Optional.of(newEmployee));
        doNothing().when(customerService).updateCustomers(anyList());

        // When
        underTest.reassignCustomers(oldEmployeeId, newEmployeeId);

        // Then / Verify
        // Logger info entry & exit & assignment messages
        final String expectedReassigningMsg = "Reassigning customer ID: %d to new employee ID: %d";
        customers.forEach(
                customer -> assertTrue(
                        testAppender.contains(
                                String.format(expectedReassigningMsg, customer.getId(), newEmployeeId),
                                "INFO"
                        ),
                        String.format(
                                INFO_SUPPLIED_MSG,
                                String.format(expectedReassigningMsg, customer.getId(), newEmployeeId)
                        ))
        );
        assertTrue(
                testAppender.contains(expectedEntryLogMsg, "INFO"),
                String.format(INFO_SUPPLIED_MSG, expectedEntryLogMsg)
        );
        assertTrue(
                testAppender.contains(expectedExitLogMsg, "INFO"),
                String.format(INFO_SUPPLIED_MSG, expectedExitLogMsg)
        );
    }

    // tests for reassignCustomerToEmployee(Long customerId, Long employeeId)
    @Test
    void itShouldThrowExceptionWhenCustomerIdIsNullByCallingReassignCustomerToEmployee() {
        // Given
        final Long customerId = null; // set to null to test the precondition
        final Long employeeId = 1L;
        final String errorMessage = "Customer or Employee IDs must not be null and must be a positive number";
        final String expectedEntryLogMsg = String.format(INFO_LOG_REASSIGN_CUS_2_EMP_ENTRY_POINT,
                customerId, employeeId);
        final String expectedExitLogMsg = String.format(INFO_LOG_REASSIGN_CUS_2_EMP_EXIT_POINT,
                customerId, employeeId);

        // When
        final IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            underTest.reassignCustomerToEmployee(customerId, employeeId);
        });

        // Then / Verify
        // Error exception message
        assertEquals(
                errorMessage,
                exception.getMessage(),
                String.format(ERROR_SUPPLIED_MSG, errorMessage)
        );
        // Logger warn message
        assertTrue(
                testAppender.contains(
                        String.format(WARN_PARAM_LOG_REASSIGN_CUS_2_EMP, errorMessage),
                        "WARN"
                ),
                String.format(WARN_SUPPLIED_MSG, String.format(WARN_PARAM_LOG_REASSIGN_CUS_2_EMP, errorMessage))
        );
        // Logger info entry & exit message
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
    void itShouldThrowExceptionWhenEmployeeIdIsNullByCallingReassignCustomerToEmployee() {
        // Given
        final Long customerId = 1L;
        final Long employeeId = null; // set to null to test the precondition
        final String errorMessage = "Customer or Employee IDs must not be null and must be a positive number";
        final String expectedEntryLogMsg = String.format(INFO_LOG_REASSIGN_CUS_2_EMP_ENTRY_POINT,
                customerId, employeeId);
        final String expectedExitLogMsg = String.format(INFO_LOG_REASSIGN_CUS_2_EMP_EXIT_POINT,
                customerId, employeeId);

        // When
        final IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            underTest.reassignCustomerToEmployee(customerId, employeeId);
        });

        // Then / Verify
        // Error exception msg
        assertEquals(
                errorMessage,
                exception.getMessage(),
                String.format(ERROR_SUPPLIED_MSG, errorMessage)
        );
        // Logger error msg
        assertTrue(testAppender.contains(
                String.format(WARN_PARAM_LOG_REASSIGN_CUS_2_EMP, errorMessage),
                "WARN"
        ));
        // Logger info entry msg
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
    void itShouldThrowExceptionWhenCustomerIdIsNegativeNumberByCallingReassignCustomerToEmployee() {
        // Given
        final Long customerId = -1L;
        final Long employeeId = 1L;
        final String errorMessage = "Customer or Employee IDs must not be null and must be a positive number";
        final String expectedEntryLogMsg = String.format(INFO_LOG_REASSIGN_CUS_2_EMP_ENTRY_POINT,
                customerId, employeeId);
        final String expectedExitLogMsg = String.format(INFO_LOG_REASSIGN_CUS_2_EMP_EXIT_POINT,
                customerId, employeeId);

        // When
        final IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            underTest.reassignCustomerToEmployee(customerId, employeeId);
        });

        // Then / Verify
        // Warn exception message & logger warn message
        assertEquals(
                errorMessage,
                exception.getMessage(),
                String.format(ERROR_SUPPLIED_MSG, errorMessage)
        );
        assertTrue(testAppender.contains(
                String.format(WARN_PARAM_LOG_REASSIGN_CUS_2_EMP, errorMessage),
                "WARN"
        ));
        // Logger info entry & exit messages
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
    void itShouldThrowExceptionWhenEmployeeIdIsNegativeNumberByCallingReassignCustomerToEmployee() {
        // Given
        final Long customerId = 1L;
        final Long employeeId = -1L;
        final String errorMessage = "Customer or Employee IDs must not be null and must be a positive number";
        final String expectedEntryLogMsg = String.format(INFO_LOG_REASSIGN_CUS_2_EMP_ENTRY_POINT,
                customerId, employeeId);
        final String expectedExitLogMsg = String.format(INFO_LOG_REASSIGN_CUS_2_EMP_EXIT_POINT,
                customerId, employeeId);

        // When
        final IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            underTest.reassignCustomerToEmployee(customerId, employeeId);
        });

        // Then / Verify
        // Error exception message & logger warn
        assertEquals(
                errorMessage,
                exception.getMessage(),
                String.format(ERROR_SUPPLIED_MSG, errorMessage)
        );
        assertTrue(
                testAppender.contains(
                        String.format(WARN_PARAM_LOG_REASSIGN_CUS_2_EMP, errorMessage),
                        "WARN"
                ),
                String.format(WARN_SUPPLIED_MSG,
                        String.format(WARN_PARAM_LOG_REASSIGN_CUS_2_EMP, errorMessage))
        );
        // Logger info entry & exit messages
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
    void itShouldThrowExceptionWhenCustomerDoesNotExistByCallingReassignCustomerToEmployee() {
        // Given
        final Long customerId = 999L;
        final Long employeeId = 2L;
        final String errorMessage = "Customer not found with ID: " + customerId;
        final String expectedEntryLogMsg = String.format(INFO_LOG_REASSIGN_CUS_2_EMP_ENTRY_POINT,
                customerId, employeeId);
        final String expectedExitLogMsg = String.format(INFO_LOG_REASSIGN_CUS_2_EMP_EXIT_POINT,
                customerId, employeeId);

        doThrow(new ResourceNotFoundException(errorMessage))
                .when(customerService).getCustomerById(anyLong());

        // When
        final ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            underTest.reassignCustomerToEmployee(customerId, employeeId);
        });

        // Then / Verify
        // Error exception message
        assertEquals(
                errorMessage,
                exception.getMessage(),
                String.format(ERROR_SUPPLIED_MSG, errorMessage)
        );
        // Logger info messages
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
    void itShouldThrowExceptionWhenEmployeeDoesNotExistByCallingReassignCustomerToEmployee() {
        // Given
        final Long customerId = 2L;
        final Long employeeId = 999L; // set to a non-existing employee id to test the precondition
        final String errorMessage = "Employee not found with ID: " + employeeId;
        final String expectedEntryLogMsg = String.format(INFO_LOG_REASSIGN_CUS_2_EMP_ENTRY_POINT,
                customerId, employeeId);
        final String expectedExitLogMsg = String.format(INFO_LOG_REASSIGN_CUS_2_EMP_EXIT_POINT,
                customerId, employeeId);

        when(customerService.getCustomerById(anyLong()))
                .thenReturn(Optional.of(new Customer()));
        doThrow(new ResourceNotFoundException(errorMessage))
                .when(employeeService).getEmployeeById(anyLong());

        // When
        final ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            underTest.reassignCustomerToEmployee(customerId, employeeId);
        });

        // Then / Verify
        // Error exception message
        assertEquals(
                errorMessage,
                exception.getMessage(),
                String.format(ERROR_SUPPLIED_MSG, errorMessage)
        );

        // Logger info entry & exit messages
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
    void itShouldAssignCustomerToEmployeeByCallingReassignCustomerToEmployee() {
        // Given
        final Long employeeId = 1L;
        final Long customerId = 101L;
        final String expectedEntryLogMsg = String.format(INFO_LOG_REASSIGN_CUS_2_EMP_ENTRY_POINT,
                customerId, employeeId);
        final String expectedExitLogMsg = String.format(INFO_LOG_REASSIGN_CUS_2_EMP_EXIT_POINT,
                customerId, employeeId);

        when(customerService.getCustomerById(anyLong())).thenReturn(Optional.of(new Customer()));
        when(employeeService.getEmployeeById(anyLong())).thenReturn(Optional.of(new Employee()));
        when(customerService.updateCustomer(anyLong(), any(Customer.class))).thenReturn(new Customer());

        // When
        underTest.reassignCustomerToEmployee(customerId, employeeId);

        // Then / Verify
        // Service calls
        verify(customerService).getCustomerById(customerId);
        verify(employeeService).getEmployeeById(employeeId);
        verify(customerService).updateCustomer(anyLong(), any(Customer.class));

        // Logger info entry & exit messages
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
    void itShouldReassignCustomersAndArchiveEmployeeByCallingDeleteEmployeeAndReassignCustomers() {
        final Long oldEmployeeId = 1L;
        final Long newEmployeeId = 2L;
        final String expectedEntryLogMsg = String.format(INFO_LOG_DEL_EMP_AND_REASSIGN_CUSTOMERS_ENTRY_POINT,
                oldEmployeeId, newEmployeeId);
        final String expectedExitLogMsg = INFO_LOG_DEL_EMP_AND_REASSIGN_CUSTOMERS_EXIT_POINT;

        final Employee oldEmployee = new Employee();
        oldEmployee.setId(oldEmployeeId);
        final Employee newEmployee = new Employee();
        newEmployee.setId(newEmployeeId);

        when(employeeService.getEmployeeById(oldEmployeeId))
                .thenReturn(Optional.of(oldEmployee));
        when(inactiveEmployeeService.createInactiveEmployee(oldEmployee))
                .thenReturn(new InactiveEmployee());
        doNothing().when(employeeService).deleteEmployee(anyLong());

        // Erstelle eine anonyme Unterklasse, um reassignCustomers zu überschreiben
        final EntityOrchestratorServiceImpl spyService = new EntityOrchestratorServiceImpl(
                employeeService,
                customerService,
                inactiveEmployeeService
        ) {
            @Override
            public void reassignCustomers(Long oldId, Long newId) {
                // Überschreiben der Methode ohne Inhalt, um die Ausführung zu verhindern,
                // da die Methode schon getestet ist
            }
        };

        spyService.deleteEmployeeAndReassignCustomers(oldEmployeeId, newEmployeeId);

        // Then
        // Verify service calls
        verify(inactiveEmployeeService, times(1)).createInactiveEmployee(oldEmployee);
        verify(employeeService, times(1)).getEmployeeById(oldEmployee.getId());
        verify(employeeService, times(1)).deleteEmployee(anyLong());

        // Verify logger entry message
        assertTrue(
                testAppender.contains(expectedEntryLogMsg, "INFO"),
                String.format(INFO_SUPPLIED_MSG, expectedEntryLogMsg)
        );
        // Verify logger exit message
        assertTrue(
                testAppender.contains(expectedExitLogMsg, "INFO"),
                String.format(INFO_SUPPLIED_MSG, expectedExitLogMsg)
        );
    }

    // TODO
    // test für createCustomerForEmployee(Customer customer, Long employeeId)
    @Test
    void itShouldCreateCustomerForEmployee() {
    }
}
