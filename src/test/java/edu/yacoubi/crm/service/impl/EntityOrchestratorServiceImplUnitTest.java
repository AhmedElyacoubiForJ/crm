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
    // exception msg
    // cause the service method
    private static final String ERROR_MSG_INVALID_IDS =
            "Employee IDs must not be null and must be a positive number";
    private static final String ERROR_MSG_SAME_IDS =
            "Old and new employee IDs must be different";
    private static final String ERROR_MSG_NO_CUSTOMERS =
            "No customers found for oldEmployee ID: %d";
    // cause the 3.th argument for assertEquals
    private static final String ERROR_INFO_MSG =
            "Error message should be %s: ";

    // logger msg's
    private static final String LOG_MSG_REASSIGN_CUSTOMERS_ENTRY_POINT =
            "EntityOrchestratorServiceImpl::reassignCustomers oldEmployeeId: %d, newEmployeeId: %d";
    private static final String LOG_MSG_REASSIGN_CUSTOMERS_EXIT_POINT =
            "Customers reassigned successfully: oldEmployeeId= %d, newEmployeeId= %d";
    private static final String LOG_WARN_MSG =
            "Warn message should be %s: ";
    // assert msg
    private static final String LOG_INFO_MSG_ENTRY_POINT =
            "Should indicate the entry point for reassigning customers";
    private static final String LOG_INFO_MSG_EXIT_POINT =
            "Should indicate the exit point for reassigning customers";

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
    }

    // tests for reassignCustomers(Long oldEmployeeId, Long newEmployeeId)
    @Test
    void itShouldThrowExceptionWhenOldEmployeeIdIsNullByCallingReassignCustomers() {
        // Given
        final Long oldEmployeeId = null;
        final Long newEmployeeId = 1L;

        // When
        final IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            underTest.reassignCustomers(oldEmployeeId, newEmployeeId);
        });

        // Then verify the exception message
        assertEquals(
                // Erwartete Wert
                ERROR_MSG_INVALID_IDS,
                // Tatsächlicher Wert
                exception.getMessage(),
                // Nachricht bei Fehlschlag
                String.format(ERROR_INFO_MSG, ERROR_MSG_INVALID_IDS)
        );
        // The Verify logger message
        assertTrue(
                testAppender.contains(
                        String.format(LOG_MSG_REASSIGN_CUSTOMERS_ENTRY_POINT, oldEmployeeId, newEmployeeId),
                        "INFO"
                ),
                LOG_INFO_MSG_ENTRY_POINT
        );
        assertFalse(
                testAppender.contains(
                        String.format(LOG_MSG_REASSIGN_CUSTOMERS_EXIT_POINT, oldEmployeeId, newEmployeeId),
                        "INFO"
                ),
                LOG_INFO_MSG_EXIT_POINT
        );
    }

    @Test
    void itShouldThrowExceptionWhenNewEmployeeIdIsNullByCallingReassignCustomers() {
        // Given
        final Long oldEmployeeId = 1L;
        final Long newEmployeeId = null; // set to null to test the precondition

        // When
        final IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            underTest.reassignCustomers(oldEmployeeId, newEmployeeId);
        });

        // Then
        // Verify the exception message
        assertEquals(
                // Erwartete Wert
                ERROR_MSG_INVALID_IDS,
                // Tatsächlicher Wert
                exception.getMessage(),
                // Nachricht bei Fehlschlag
                String.format(ERROR_INFO_MSG, ERROR_MSG_INVALID_IDS)
        );
        // Verify logger message
        assertTrue(
                testAppender.contains(
                        String.format(LOG_MSG_REASSIGN_CUSTOMERS_ENTRY_POINT, oldEmployeeId, newEmployeeId),
                        "INFO"
                ),
                LOG_INFO_MSG_ENTRY_POINT
        );
        assertFalse(testAppender.contains(
                        String.format(LOG_MSG_REASSIGN_CUSTOMERS_EXIT_POINT, oldEmployeeId, newEmployeeId),
                        "INFO"
                ),
                LOG_INFO_MSG_EXIT_POINT
        );
    }

    @Test
    void itShouldThrowExceptionWhenOldEmployeeIdIsNegativeByCallingReassignCustomers() {
        // Given
        final Long oldEmployeeId = -1L;
        final Long newEmployeeId = 1L;

        // When
        final IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            underTest.reassignCustomers(oldEmployeeId, newEmployeeId);
        });

        // Then
        // verify the exception message
        assertEquals(
                // Erwartete Wert
                ERROR_MSG_INVALID_IDS,
                // Tatsächlicher Wert
                exception.getMessage(),
                // Nachricht bei Fehlschlag
                String.format(ERROR_INFO_MSG, ERROR_MSG_INVALID_IDS)
        );
        // Verify logger message
        assertTrue(testAppender.contains(
                        String.format(LOG_MSG_REASSIGN_CUSTOMERS_ENTRY_POINT, oldEmployeeId, newEmployeeId),
                        "INFO"
                ),
                LOG_INFO_MSG_ENTRY_POINT
        );
        assertFalse(testAppender.contains(
                        String.format(LOG_MSG_REASSIGN_CUSTOMERS_EXIT_POINT, oldEmployeeId, newEmployeeId),
                        "INFO"
                ),
                LOG_INFO_MSG_EXIT_POINT
        );
    }

    @Test
    void itShouldThrowExceptionWhenNewEmployeeIdIsNegativeByCallingReassignCustomers() {
        // Given
        final Long oldEmployeeId = 1L;
        final Long newEmployeeId = -1L;

        // When
        final IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            underTest.reassignCustomers(oldEmployeeId, newEmployeeId);
        });

        // Then
        // Verify the exception message
        assertEquals(
                // Erwartete Wert
                ERROR_MSG_INVALID_IDS,
                // Tatsächlicher Wert
                exception.getMessage(),
                // Nachricht bei Fehlschlag
                String.format(ERROR_INFO_MSG, ERROR_MSG_INVALID_IDS)
        );
        // Verify logger message
        assertTrue(
                testAppender.contains(
                        String.format(LOG_MSG_REASSIGN_CUSTOMERS_ENTRY_POINT, oldEmployeeId, newEmployeeId),
                        "INFO"
                ),
                LOG_INFO_MSG_ENTRY_POINT
        );
        assertFalse(
                testAppender.contains(
                        String.format(LOG_MSG_REASSIGN_CUSTOMERS_EXIT_POINT, oldEmployeeId, newEmployeeId),
                        "INFO"
                ),
                LOG_INFO_MSG_EXIT_POINT
        );
    }

    @Test
    void itShouldThrowExceptionWhenOldEmployeeAndNewEmployeeIdsAreEqualsByCallingReassignCustomers() {
        // Given
        final Long oldEmployeeId = 1L;
        final Long newEmployeeId = 1L;

        // When
        final IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            underTest.reassignCustomers(oldEmployeeId, newEmployeeId);
        });

        // Then
        // Verify the exception message
        assertEquals(
                // Erwartete Wert
                ERROR_MSG_SAME_IDS,
                // Tatsächlicher Wert
                exception.getMessage(),
                // Nachricht bei Fehlschlag
                String.format(ERROR_INFO_MSG, ERROR_MSG_SAME_IDS)
        );
        // Verify logger message
        assertTrue(
                testAppender.contains(
                        String.format(LOG_MSG_REASSIGN_CUSTOMERS_ENTRY_POINT, oldEmployeeId, newEmployeeId),
                        "INFO"
                ),
                LOG_INFO_MSG_ENTRY_POINT
        );
        assertTrue(
                testAppender.contains(
                        String.format(ERROR_MSG_SAME_IDS),
                        "WARN"
                ),
                String.format(LOG_WARN_MSG, ERROR_MSG_SAME_IDS)
        );
        assertFalse(
                testAppender.contains(
                        String.format(LOG_MSG_REASSIGN_CUSTOMERS_EXIT_POINT, oldEmployeeId, newEmployeeId), "INFO"
                ),
                LOG_INFO_MSG_EXIT_POINT
        );
    }

    @Test
    void itShouldThrowExceptionWhenCustomersNotFoundByOldEmployeeIdByCallingReassignCustomers() {
        // Given
        final Long oldEmployeeId = 1L;
        final Long newEmployeeId = 2L;

        when(customerService.getCustomersByEmployeeId(oldEmployeeId))
                .thenReturn(new ArrayList<>());

        // When
        final IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            underTest.reassignCustomers(oldEmployeeId, newEmployeeId);
        });

        // Then
        // Verify the exception message
        assertEquals(
                String.format(ERROR_MSG_NO_CUSTOMERS, oldEmployeeId),
                exception.getMessage(),
                String.format(ERROR_INFO_MSG, String.format(ERROR_MSG_NO_CUSTOMERS, oldEmployeeId))
        );
        // Verify logger message
        assertTrue(
                testAppender.contains(
                        String.format(LOG_MSG_REASSIGN_CUSTOMERS_ENTRY_POINT, oldEmployeeId, newEmployeeId),
                        "INFO"
                ),
                LOG_INFO_MSG_ENTRY_POINT
        );
        assertTrue(
                testAppender.contains(
                        String.format(ERROR_MSG_NO_CUSTOMERS, oldEmployeeId),
                        "WARN"),
                String.format(
                        LOG_WARN_MSG,
                        String.format(ERROR_MSG_NO_CUSTOMERS, oldEmployeeId)
                )
        );
        assertFalse(
                testAppender.contains(
                        String.format(LOG_MSG_REASSIGN_CUSTOMERS_EXIT_POINT, oldEmployeeId, newEmployeeId),
                        "INFO"
                ),
                LOG_INFO_MSG_EXIT_POINT
        );
    }

    @Test
    void itShouldThrowExceptionWhenGetCustomersByEmployeeIdAndEmployeeDoesNotExistByCallingReassignCustomers() {
        // Given
        final Long oldEmployeeId = 999L;
        final Long newEmployeeId = 2L;
        final String errorMessage = "Employee not found with ID: " + oldEmployeeId;

        doThrow(new ResourceNotFoundException(errorMessage))
                .when(customerService).getCustomersByEmployeeId(oldEmployeeId);

        // When
        final ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            underTest.reassignCustomers(oldEmployeeId, newEmployeeId);
        });

        // Then
        // Verify exception message
        assertEquals(
                errorMessage,
                exception.getMessage(),
                String.format(ERROR_INFO_MSG, errorMessage)
        );
        // Verify logger message
        assertTrue(
                testAppender.contains(
                        String.format(LOG_MSG_REASSIGN_CUSTOMERS_ENTRY_POINT, oldEmployeeId, newEmployeeId),
                        "INFO"
                ),
                LOG_INFO_MSG_ENTRY_POINT
        );
        assertFalse(
                testAppender.contains(
                        String.format(LOG_MSG_REASSIGN_CUSTOMERS_EXIT_POINT, oldEmployeeId, newEmployeeId),
                        "INFO"
                ),
                LOG_INFO_MSG_EXIT_POINT
        );
    }

    @Test
    void itShouldThrowExceptionWhenGetCustomersByEmployeeIdAndEmployeeDoesNotHaveCustomersByCallingReassignCustomers() {
        // Given
        final Long oldEmployeeId = 1L;
        final Long newEmployeeId = 2L;
        final String errorMessage = "No customers found for oldEmployee ID: " + oldEmployeeId;

        when(customerService.getCustomersByEmployeeId(oldEmployeeId))
                .thenReturn(Collections.emptyList());

        // When
        final IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            underTest.reassignCustomers(oldEmployeeId, newEmployeeId);
        });

        // Then
        // Verify the exception message
        assertEquals(
                errorMessage,
                exception.getMessage(),
                String.format(ERROR_INFO_MSG, errorMessage)
        );
        // Verify logger message
        assertTrue(
                testAppender.contains(
                        String.format(LOG_MSG_REASSIGN_CUSTOMERS_ENTRY_POINT, oldEmployeeId, newEmployeeId),
                        "INFO"
                ),
                LOG_INFO_MSG_ENTRY_POINT
        );
        assertTrue(
                testAppender.contains(
                        String.format("EntityOrchestratorServiceImpl::reassignCustomers warn: %s", errorMessage),
                        "WARN"
                ),
                String.format(LOG_WARN_MSG, errorMessage)
        );
        assertFalse(
                testAppender.contains(
                        String.format(LOG_MSG_REASSIGN_CUSTOMERS_EXIT_POINT, oldEmployeeId, newEmployeeId),
                        "INFO"
                ),
                LOG_INFO_MSG_EXIT_POINT
        );
    }

    @Test
    void itShouldThrowExceptionWhenGetEmployeeByIdForNewEmployeeDoesNotExistByCallingReassignCustomers() {
        // Given
        final Long oldEmployeeId = 1L;
        final Long newEmployeeId = 999L;
        final String errorMessage = "Employee not found with ID: " + newEmployeeId;

        doThrow(new ResourceNotFoundException(errorMessage))
                .when(employeeService).getEmployeeById(anyLong());
        when(customerService.getCustomersByEmployeeId(oldEmployeeId))
                .thenReturn(List.of(new Customer()));

        // When
        final ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            underTest.reassignCustomers(oldEmployeeId, newEmployeeId);
        });

        // Then
        // Verify the exception message
        assertEquals(
                errorMessage,
                exception.getMessage(),
                String.format(ERROR_INFO_MSG, errorMessage)
        );
        // Verify that the info logs are not triggered
        assertTrue(
                testAppender.contains(
                        String.format(LOG_MSG_REASSIGN_CUSTOMERS_ENTRY_POINT, oldEmployeeId, newEmployeeId),
                        "INFO"
                ),
                LOG_INFO_MSG_ENTRY_POINT
        );
        assertFalse(
                testAppender.contains(
                        String.format(LOG_MSG_REASSIGN_CUSTOMERS_EXIT_POINT, oldEmployeeId, newEmployeeId),
                        "INFO"
                ),
                LOG_INFO_MSG_EXIT_POINT
        );
    }

    @Test
    void itShouldReassignCustomersFromOldEmployeeToNewEmployeeByCallingReassignCustomers() {
        // Given
        final Long oldEmployeeId = 1L;
        final Long newEmployeeId = 2L;
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

        // Then
        // Verify logger message
        assertTrue(
                testAppender.contains(
                        String.format(LOG_MSG_REASSIGN_CUSTOMERS_ENTRY_POINT, oldEmployeeId, newEmployeeId),
                        "INFO"
                ),
                LOG_INFO_MSG_ENTRY_POINT
        );
        customers.forEach(
                customer -> assertTrue(testAppender.contains(
                        String.format(
                                "Reassigning customer ID: %d to new employee ID: %d",
                                customer.getId(),
                                newEmployee.getId()
                        ),
                        "INFO"
                ))
        );
        assertFalse(
                testAppender.contains(
                        "Customers reassigned successfully",
                        "WARN"
                ),
                LOG_WARN_MSG
        );
        assertTrue(
                testAppender.contains(
                        String.format(LOG_MSG_REASSIGN_CUSTOMERS_EXIT_POINT, oldEmployeeId, newEmployeeId),
                        "INFO"
                ),
                LOG_INFO_MSG_EXIT_POINT
        );
    }

    // tests for reassignCustomerToEmployee(Long customerId, Long employeeId)
    @Test
    void itShouldThrowExceptionWhenCustomerIdIsNullByCallingReassignCustomerToEmployee() {
        // Given
        final Long customerId = null; // set to null to test the precondition
        final Long employeeId = 1L;
        final String errorMessage = "Customer or Employee IDs must not be null and must be a positive number";

        // When
        final IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            underTest.reassignCustomerToEmployee(customerId, employeeId);
        });

        // Then
        // Verify exception message
        assertEquals(
                errorMessage,
                exception.getMessage(),
                String.format(ERROR_INFO_MSG, errorMessage)
        );
        // Verify logger message
        assertTrue(testAppender.contains(
                String.format("EntityOrchestratorServiceImpl::reassignCustomerToEmployee customerId: %d, employeeId: %d", customerId, employeeId),
                "INFO"
        ));
        assertTrue(testAppender.contains(
                String.format("EntityOrchestratorServiceImpl parameter warn: %s", errorMessage),
                "WARN"
        ));
        assertFalse(testAppender.contains(
                String.format("Customer reassigned: customerId= %d, newEmployeeId= %d", customerId, employeeId),
                "INFO"
        ));
    }

    @Test
    void itShouldThrowExceptionWhenEmployeeIdIsNullByCallingReassignCustomerToEmployee() {
        // Given
        final Long customerId = 1L;
        final Long employeeId = null; // set to null to test the precondition
        final String errorMessage = "Customer or Employee IDs must not be null and must be a positive number";

        // When
        final IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            underTest.reassignCustomerToEmployee(customerId, employeeId);
        });

        // Then
        // Verify the exception message
        assertEquals(
                errorMessage,
                exception.getMessage(),
                String.format(ERROR_INFO_MSG, errorMessage)
        );
        // Verify the logger message
        assertTrue(testAppender.contains(
                String.format("EntityOrchestratorServiceImpl::reassignCustomerToEmployee customerId: %d, employeeId: %d", customerId, employeeId),
                "INFO"
        ));
        assertTrue(testAppender.contains(
                String.format("EntityOrchestratorServiceImpl parameter warn: %s", errorMessage),
                "WARN"
        ));
        assertFalse(testAppender.contains(
                String.format("Customer reassigned: customerId= %d, newEmployeeId= %d", customerId, employeeId),
                "INFO"
        ));
    }

    @Test
    void itShouldThrowExceptionWhenCustomerIdIsNegativeNumberByCallingReassignCustomerToEmployee() {
        // Given
        final Long customerId = -1L;
        final Long employeeId = 1L;
        final String errorMessage = "Customer or Employee IDs must not be null and must be a positive number";

        // When
        final IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            underTest.reassignCustomerToEmployee(customerId, employeeId);
        });

        // Then
        // Verify the exception message
        assertEquals(
                errorMessage,
                exception.getMessage(),
                String.format(ERROR_INFO_MSG, errorMessage)
        );
        // Verify the logger message
        assertTrue(testAppender.contains(
                String.format("EntityOrchestratorServiceImpl::reassignCustomerToEmployee customerId: %d, employeeId: %d", customerId, employeeId),
                "INFO"
        ));
        assertTrue(testAppender.contains(
                String.format("EntityOrchestratorServiceImpl parameter warn: %s", errorMessage),
                "WARN"
        ));
        assertFalse(testAppender.contains(
                String.format("Customer reassigned: customerId= %d, newEmployeeId= %d", customerId, employeeId),
                "INFO"
        ));
    }

    @Test
    void itShouldThrowExceptionWhenEmployeeIdIsNegativeNumberByCallingReassignCustomerToEmployee() {
        // Given
        final Long customerId = 1L;
        final Long employeeId = -1L;
        final String errorMessage = "Customer or Employee IDs must not be null and must be a positive number";

        // When
        final IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            underTest.reassignCustomerToEmployee(customerId, employeeId);
        });

        // Then
        // Verify the exception message
        assertEquals(
                errorMessage,
                exception.getMessage(),
                String.format(ERROR_INFO_MSG, errorMessage)
        );
        // Verify the logger message
        assertTrue(testAppender.contains(
                String.format("EntityOrchestratorServiceImpl::reassignCustomerToEmployee customerId: %d, employeeId: %d", customerId, employeeId),
                "INFO"
        ));
        assertTrue(testAppender.contains(
                String.format("EntityOrchestratorServiceImpl parameter warn: %s", errorMessage),
                "WARN"
        ));
        assertFalse(testAppender.contains(
                String.format("Customer reassigned: customerId= %d, newEmployeeId= %d", customerId, employeeId),
                "INFO"
        ));
    }

    @Test
    void itShouldThrowExceptionWhenCustomerDoesNotExistByCallingReassignCustomerToEmployee() {
        // Given
        final Long customerId = 999L;
        final Long employeeId = 2L;
        final String errorMessage = "Customer not found with ID: " + customerId;
        doThrow(new ResourceNotFoundException(errorMessage))
                .when(customerService).getCustomerById(anyLong());

        // When
        final ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            underTest.reassignCustomerToEmployee(customerId, employeeId);
        });

        // Then
        // Verify the exception message
        assertEquals(
                errorMessage,
                exception.getMessage(),
                String.format(ERROR_INFO_MSG, errorMessage)
        );
        // Verify logger message
        assertTrue(testAppender.contains(
                String.format("EntityOrchestratorServiceImpl::reassignCustomerToEmployee customerId: %d, employeeId: %d", customerId, employeeId),
                "INFO"
        ));
        assertFalse(testAppender.contains(
                String.format("Customer reassigned: customerId= %d, newEmployeeId= %d", customerId, employeeId),
                "INFO"
        ));
    }

    @Test
    void itShouldThrowExceptionWhenEmployeeDoesNotExistByCallingReassignCustomerToEmployee() {
        // Given
        final Long oldEmployeeId = 1L;
        final Long newEmployeeId = 999L; // set to a non-existing employee id to test the precondition
        final String errorMessage = "Employee not found with ID: " + newEmployeeId;
        when(customerService.getCustomerById(anyLong()))
                .thenReturn(Optional.of(new Customer()));
        doThrow(new ResourceNotFoundException(errorMessage))
                .when(employeeService).getEmployeeById(anyLong());

        // When
        final ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            underTest.reassignCustomerToEmployee(oldEmployeeId, newEmployeeId);
        });

        // Then
        // Verify the exception message
        assertEquals(
                errorMessage,
                exception.getMessage(),
                String.format(ERROR_INFO_MSG, errorMessage)
        );
        // Verify logger message
        assertTrue(testAppender.contains(
                String.format("EntityOrchestratorServiceImpl::reassignCustomerToEmployee customerId: %d, employeeId: %d", oldEmployeeId, newEmployeeId),
                "INFO"
        ));
        assertFalse(testAppender.contains(
                String.format("Customer reassigned: customerId= %d, newEmployeeId= %d", oldEmployeeId, newEmployeeId),
                "INFO"
        ));
    }

    @Test
    void itShouldAssignCustomerToEmployeeByCallingReassignCustomerToEmployee() {
        // Given
        final Long employeeId = 1L;
        final Long customerId = 101L;
        when(customerService.getCustomerById(anyLong())).thenReturn(Optional.of(new Customer()));
        when(employeeService.getEmployeeById(anyLong())).thenReturn(Optional.of(new Employee()));
        when(customerService.updateCustomer(anyLong(), any(Customer.class))).thenReturn(new Customer());

        // When
        underTest.reassignCustomerToEmployee(customerId, employeeId);

        // Then
        // Verify service calls
        verify(customerService).getCustomerById(customerId);
        verify(employeeService).getEmployeeById(employeeId);
        verify(customerService).updateCustomer(anyLong(), any(Customer.class));
        // Verify logger message
        assertTrue(testAppender.contains(
                String.format("EntityOrchestratorServiceImpl::reassignCustomerToEmployee customerId: %d, employeeId: %d", customerId, employeeId),
                "INFO"
        ));
        assertTrue(testAppender.contains(
                String.format("Customer reassigned: customerId= %d, newEmployeeId= %d", customerId, employeeId),
                "INFO"
        ));
    }

    @Test
    void itShouldReassignCustomersAndArchiveEmployeeByCallingDeleteEmployeeAndReassignCustomers() {
        final Long oldEmployeeId = 1L;
        final Long newEmployeeId = 2L;

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
                // Überschreiben der Methode ohne Inhalt, um die Ausführung zu verhindern
                // da die Methode schon getestet ist
            }
        };

        spyService.deleteEmployeeAndReassignCustomers(oldEmployeeId, newEmployeeId);

        // Then
        // Verify service calls
        verify(inactiveEmployeeService, times(1)).createInactiveEmployee(oldEmployee);
        verify(employeeService, times(1)).getEmployeeById(oldEmployee.getId());
        verify(employeeService, times(1)).deleteEmployee(anyLong());
    }
}
