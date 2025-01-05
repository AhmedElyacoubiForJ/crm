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
    public static final String ERROR_MESSAGE_INVALID_IDS = "Employee IDs must not be null and must be a positive number";
    public static final String ERROR_MESSAGE_SAME_IDS = "Old and new employee IDs must be different";
    public static final String ERROR_MESSAGE_NO_CUSTOMERS = "No customers found for oldEmployee ID: %d";

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
        assertEquals(ERROR_MESSAGE_INVALID_IDS, exception.getMessage());
        // Verify that the info logs are not triggered
        assertTrue(testAppender.contains(
                String.format("EntityOrchestratorServiceImpl::reassignCustomers oldEmployeeId: %d, newEmployeeId: %d", oldEmployeeId, newEmployeeId),
                "INFO"
        ));
        assertFalse(testAppender.contains(
                String.format("Customers reassigned successfully: oldEmployeeId= %d, newEmployeeId= %d", oldEmployeeId, newEmployeeId),
                "INFO"
        ));
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

        // Then verify the exception message
        assertEquals(ERROR_MESSAGE_INVALID_IDS, exception.getMessage());
        // Verify that the info logs are not triggered
        assertTrue(testAppender.contains(
                String.format("EntityOrchestratorServiceImpl::reassignCustomers oldEmployeeId: %d, newEmployeeId: %d", oldEmployeeId, newEmployeeId),
                "INFO"
        ));
        assertFalse(testAppender.contains(
                String.format("Customers reassigned successfully: oldEmployeeId= %d, newEmployeeId= %d", oldEmployeeId, newEmployeeId),
                "INFO"
        ));
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

        // Then verify the exception message
        assertEquals(ERROR_MESSAGE_INVALID_IDS, exception.getMessage());
        // Verify that the info logs are not triggered
        assertTrue(testAppender.contains(
                String.format("EntityOrchestratorServiceImpl::reassignCustomers oldEmployeeId: %d, newEmployeeId: %d", oldEmployeeId, newEmployeeId),
                "INFO"
        ));
        assertFalse(testAppender.contains(
                String.format("Customers reassigned successfully: oldEmployeeId= %d, newEmployeeId= %d", oldEmployeeId, newEmployeeId),
                "INFO"
        ));
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

        // Then verify the exception message
        assertEquals(ERROR_MESSAGE_INVALID_IDS, exception.getMessage());
        // Verify that the info logs are not triggered
        assertTrue(testAppender.contains(
                String.format("EntityOrchestratorServiceImpl::reassignCustomers oldEmployeeId: %d, newEmployeeId: %d", oldEmployeeId, newEmployeeId),
                "INFO"
        ));
        assertFalse(testAppender.contains(
                String.format("Customers reassigned successfully: oldEmployeeId= %d, newEmployeeId= %d", oldEmployeeId, newEmployeeId),
                "INFO"
        ));
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

        // Then verify the exception message
        assertEquals(ERROR_MESSAGE_SAME_IDS, exception.getMessage());
        // Verify that the info logs are not triggered
        assertTrue(testAppender.contains(
                String.format("EntityOrchestratorServiceImpl::reassignCustomers oldEmployeeId: %d, newEmployeeId: %d", oldEmployeeId, newEmployeeId),
                "INFO"
        ));
        assertTrue(testAppender.contains(
                String.format(ERROR_MESSAGE_SAME_IDS), "WARN"
        ));
        assertFalse(testAppender.contains(
                String.format("Customers reassigned successfully: oldEmployeeId= %d, newEmployeeId= %d", oldEmployeeId, newEmployeeId), "INFO"
        ));
    }

    @Test
    void itShouldThrowExceptionWhenCustomersNotFoundByOldEmployeeIsByCallingReassignCustomers() {
        // Given
        final Long oldEmployeeId = 1L;
        final Long newEmployeeId = 2L;

        when(customerService.getCustomersByEmployeeId(oldEmployeeId))
                .thenReturn(new ArrayList<>());

        // When
        final IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            underTest.reassignCustomers(oldEmployeeId, newEmployeeId);
        });

        // Then verify the exception message
        assertEquals(String.format(ERROR_MESSAGE_NO_CUSTOMERS, oldEmployeeId), exception.getMessage());
        // Verify that the info logs are not triggered
        assertTrue(testAppender.contains(
                String.format("EntityOrchestratorServiceImpl::reassignCustomers oldEmployeeId: %d, newEmployeeId: %d", oldEmployeeId, newEmployeeId),
                "INFO"
        ));
        assertTrue(testAppender.contains(
                String.format(ERROR_MESSAGE_NO_CUSTOMERS, oldEmployeeId), "WARN"
        ));
        assertFalse(testAppender.contains(
                String.format("Customers reassigned successfully: oldEmployeeId= %d, newEmployeeId= %d", oldEmployeeId, newEmployeeId), "INFO"
        ));
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

        // Then verify the exception message
        assertEquals(errorMessage, exception.getMessage());
        // Verify that the info logs are not triggered
        assertTrue(testAppender.contains(
                String.format("EntityOrchestratorServiceImpl::reassignCustomers oldEmployeeId: %d, newEmployeeId: %d", oldEmployeeId, newEmployeeId),
                "INFO"
        ));
        assertFalse(testAppender.contains(
                String.format("Customers reassigned successfully: oldEmployeeId= %d, newEmployeeId= %d", oldEmployeeId, newEmployeeId),
                "INFO"
        ));
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

        // Then verify the exception message
        assertEquals(errorMessage, exception.getMessage());
        // Verify that the info logs are not triggered
        assertTrue(testAppender.contains(
                String.format("EntityOrchestratorServiceImpl::reassignCustomers oldEmployeeId: %d, newEmployeeId: %d", oldEmployeeId, newEmployeeId),
                "INFO"
        ));
        assertTrue(testAppender.contains(
                String.format("EntityOrchestratorServiceImpl::reassignCustomers warn: %s", errorMessage),
                "WARN"
        ));
        assertFalse(testAppender.contains(
                String.format("Customers reassigned successfully: oldEmployeeId= %d, newEmployeeId= %d", oldEmployeeId, newEmployeeId),
                "INFO"
        ));
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

        // Then verify the exception message
        assertEquals(errorMessage, exception.getMessage());
        // Verify that the info logs are not triggered
        assertTrue(testAppender.contains(
                String.format("EntityOrchestratorServiceImpl::reassignCustomers oldEmployeeId: %d, newEmployeeId: %d", oldEmployeeId, newEmployeeId),
                "INFO"
        ));
        assertFalse(testAppender.contains(
                String.format("Customers reassigned successfully: oldEmployeeId= %d, newEmployeeId= %d", oldEmployeeId, newEmployeeId),
                "INFO"
        ));
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

        // Then verify that the info logs are triggered
        assertTrue(testAppender.contains(
                String.format("EntityOrchestratorServiceImpl::reassignCustomers oldEmployeeId: %d, newEmployeeId: %d", oldEmployeeId, newEmployeeId),
                "INFO"
        ));

//        testAppender.events.forEach(
//                event -> System.out.println(event.getFormattedMessage()
//                )
//        );

        customers.forEach(
                customer -> assertTrue(testAppender.contains(
                        String.format("Reassigning customer ID: %d to new employee ID: %d", customer.getId(), newEmployee.getId()),
                        "INFO"
                ))
        );
        assertFalse(testAppender.contains(
                "Customers reassigned successfully",
                "WARN"
        ));
        assertTrue(testAppender.contains(
                String.format("Customers reassigned successfully: oldEmployeeId= %d, newEmployeeId= %d", oldEmployeeId, newEmployeeId),
                "INFO"
        ));
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
        assertEquals(errorMessage, exception.getMessage());
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
        assertEquals(errorMessage, exception.getMessage());
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
        assertEquals(errorMessage, exception.getMessage());
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
        assertEquals(errorMessage, exception.getMessage());
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
        assertEquals(errorMessage, exception.getMessage());
        assertTrue(testAppender.contains(
                String.format("EntityOrchestratorServiceImpl::reassignCustomerToEmployee customerId: %d, employeeId: %d", customerId, employeeId),
                "INFO"
        ));
//        testAppender.events.forEach(
//                event -> System.out.println(event.getFormattedMessage()
//                )
//        );
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
        assertEquals(errorMessage, exception.getMessage());
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
        verify(customerService).getCustomerById(customerId);
        verify(employeeService).getEmployeeById(employeeId);
        verify(customerService).updateCustomer(anyLong(), any(Customer.class));
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

//        Customer customer1 = new Customer();
//        customer1.setId(101L);
//        Customer customer2 = new Customer();
//        customer2.setId(102L);
        //List<Customer> customers = List.of(customer1, customer2);
        when(employeeService.getEmployeeById(oldEmployeeId)).thenReturn(Optional.of(oldEmployee));
        when(inactiveEmployeeService.createInactiveEmployee(oldEmployee)).thenReturn(any(InactiveEmployee.class));

        // Erstelle eine anonyme Unterklasse, um reassignCustomers zu überschreiben
        final EntityOrchestratorServiceImpl spyService = new EntityOrchestratorServiceImpl(
                employeeService,
                customerService,
                inactiveEmployeeService
        ) {
            @Override
            public void reassignCustomers(Long oldId, Long newId) {
                // Überschreiben der Methode ohne Inhalt, um die Ausführung zu verhindern
            }
        };

        spyService.deleteEmployeeAndReassignCustomers(oldEmployeeId, newEmployeeId);

        verify(inactiveEmployeeService, times(1)).createInactiveEmployee(oldEmployee);
        verify(employeeService, times(1)).getEmployeeById(oldEmployee.getId());
    }
}
