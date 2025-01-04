package edu.yacoubi.crm.service.impl;

import ch.qos.logback.classic.Logger;
import edu.yacoubi.crm.exception.ResourceNotFoundException;
import edu.yacoubi.crm.model.Customer;
import edu.yacoubi.crm.model.Employee;
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
        Logger logger = (Logger) LoggerFactory.getLogger(EntityOrchestratorServiceImpl.class);
        testAppender = new TestAppender();
        testAppender.start();
        logger.addAppender(testAppender);
    }

    // tests for reassignCustomers(Long oldEmployeeId, Long newEmployeeId)
    @Test
    void itShouldThrowExceptionWhenOldEmployeeIdIsNull_ByCallingReassignCustomers() {
        // Given
        Long oldEmployeeId = null;
        Long newEmployeeId = 1L;

        // When
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            underTest.reassignCustomers(oldEmployeeId, newEmployeeId);
        });

        // Then verify the exception message
        assertEquals("Employee IDs must not be null and must be a positive number", exception.getMessage());
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
    void itShouldThrowExceptionWhenNewEmployeeIdIsNull_ByCallingReassignCustomers() {
        // Given
        Long oldEmployeeId = 1L;
        Long newEmployeeId = null; // set to null to test the precondition

        // When
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            underTest.reassignCustomers(oldEmployeeId, newEmployeeId);
        });

        // Then verify the exception message
        assertEquals("Employee IDs must not be null and must be a positive number", exception.getMessage());
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
    void itShouldThrowExceptionWhenOldEmployeeIdIsNegative_ByCallingReassignCustomers() {
        // Given
        Long oldEmployeeId = -1L;
        Long newEmployeeId = 1L;

        // When
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            underTest.reassignCustomers(oldEmployeeId, newEmployeeId);
        });

        // Then verify the exception message
        assertEquals("Employee IDs must not be null and must be a positive number", exception.getMessage());
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
    void itShouldThrowExceptionWhenNewEmployeeIdIsNegative_ByCallingReassignCustomers() {
        // Given
        Long oldEmployeeId = 1L;
        Long newEmployeeId = -1L;

        // When
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            underTest.reassignCustomers(oldEmployeeId, newEmployeeId);
        });

        // Then verify the exception message
        assertEquals("Employee IDs must not be null and must be a positive number", exception.getMessage());
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
    void itShouldThrowExceptionWhenOldEmployeeAndNewEmployeeIdsAreEquals_ByCallingReassignCustomers() {
        // Given
        Long oldEmployeeId = 1L;
        Long newEmployeeId = 1L;

        // When
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            underTest.reassignCustomers(oldEmployeeId, newEmployeeId);
        });

        // Then verify the exception message
        assertEquals("Old and new employee IDs must be different", exception.getMessage());
        // Verify that the info logs are not triggered
        assertTrue(testAppender.contains(
                String.format("EntityOrchestratorServiceImpl::reassignCustomers oldEmployeeId: %d, newEmployeeId: %d", oldEmployeeId, newEmployeeId),
                "INFO"
        ));
        assertTrue(testAppender.contains(
                String.format("Old and new employee IDs must be different"), "WARN"
        ));
        assertFalse(testAppender.contains(
                String.format("Customers reassigned successfully: oldEmployeeId= %d, newEmployeeId= %d", oldEmployeeId, newEmployeeId), "INFO"
        ));
    }

    @Test
    void itShouldThrowExceptionWhenCustomersNotFoundByOldEmployeeIs_ByCallingReassignCustomers() {
        // Given
        Long oldEmployeeId = 1L;
        Long newEmployeeId = 2L;

        when(customerService.getCustomersByEmployeeId(oldEmployeeId))
                .thenReturn(new ArrayList<>());

        // When
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            underTest.reassignCustomers(oldEmployeeId, newEmployeeId);
        });

        // Then verify the exception message
        assertEquals(String.format("No customers found for oldEmployee ID: %d", oldEmployeeId), exception.getMessage());
        // Verify that the info logs are not triggered
        assertTrue(testAppender.contains(
                String.format("EntityOrchestratorServiceImpl::reassignCustomers oldEmployeeId: %d, newEmployeeId: %d", oldEmployeeId, newEmployeeId),
                "INFO"
        ));
        assertTrue(testAppender.contains(
                String.format("No customers found for oldEmployee ID: %d", oldEmployeeId), "WARN"
        ));
        assertFalse(testAppender.contains(
                String.format("Customers reassigned successfully: oldEmployeeId= %d, newEmployeeId= %d", oldEmployeeId, newEmployeeId), "INFO"
        ));
    }

    @Test
    public void itShouldThrowExceptionWhenGetCustomersByEmployeeIdAndEmployeeDoesNotExist_ByCallingReassignCustomers() {
        // Given
        Long oldEmployeeId = 999L;
        Long newEmployeeId = 2L;
        String errorMessage = "Employee not found with ID: " + oldEmployeeId;

        doThrow(new ResourceNotFoundException(errorMessage))
                .when(customerService).getCustomersByEmployeeId(oldEmployeeId);

        // When
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
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
    public void itShouldThrowExceptionWhenGetCustomersByEmployeeIdAndEmployeeDoesNotHaveCustomers_ByCallingReassignCustomers() {
        // Given
        Long oldEmployeeId = 1L;
        Long newEmployeeId = 2L;
        String errorMessage = "No customers found for oldEmployee ID: " + oldEmployeeId;

        when(customerService.getCustomersByEmployeeId(oldEmployeeId))
                .thenReturn(Collections.emptyList());

        // When
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
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
    public void itShouldThrowExceptionWhenGetEmployeeByIdForNewEmployeeDoesNotExist_ByCallingReassignCustomers() {
        // Given
        Long oldEmployeeId = 1L;
        Long newEmployeeId = 999L;
        String errorMessage = "Employee not found with ID: " + newEmployeeId;

        doThrow(new ResourceNotFoundException(errorMessage))
                .when(employeeService).getEmployeeById(anyLong());
        when(customerService.getCustomersByEmployeeId(oldEmployeeId))
                .thenReturn(List.of(new Customer()));

        // When
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
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
    void itShouldReassignCustomersFromOldEmployeeToNewEmployee_ByCallingReassignCustomers() {
        // Given
        Long oldEmployeeId = 1L;
        Long newEmployeeId = 2L;
        List<Customer> customers = List.of(
                Customer.builder().id(111L).build(),
                Customer.builder().id(222L).build()
        );

        when(customerService.getCustomersByEmployeeId(oldEmployeeId))
                .thenReturn(customers);
        Employee newEmployee = Employee.builder().id(newEmployeeId).build();
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

    @Test
    void itShouldThrowExceptionWhenCustomerIdIsNull_ByCallingReassignCustomerToEmployee() {
        // Given
        Long customerId = null; // set to null to test the precondition
        Long employeeId = 1L;

        // When
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            underTest.reassignCustomerToEmployee(customerId, employeeId);
        });

        // Then verify the exception message
        assertEquals("Customer or Employee IDs must not be null and must be a positive number", exception.getMessage());
        // Verify that the info logs are not triggered
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
    void itShouldThrowExceptionWhenEmployeeIdIsNull_ByCallingReassignCustomerToEmployee() {
        // Given
        Long customerId = 1L;
        Long employeeId = null; // set to null to test the precondition

        // When
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            underTest.reassignCustomerToEmployee(customerId, employeeId);
        });

        // Then verify the exception message
        assertEquals("Customer or Employee IDs must not be null and must be a positive number", exception.getMessage());
        // Verify that the info logs are not triggered
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
    void itShouldThrowExceptionWhenCustomerIdIsNegativeNumber_ByCallingReassignCustomerToEmployee() {
        // Given
        Long customerId = -1L;
        Long employeeId = 1L;

        // When
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            underTest.reassignCustomerToEmployee(customerId, employeeId);
        });

        // Then verify the exception message
        assertEquals("Customer or Employee IDs must not be null and must be a positive number", exception.getMessage());
        // Verify that the info logs are not triggered
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
    void itShouldThrowExceptionWhenEmployeeIdIsNegativeNumber_ByCallingReassignCustomerToEmployee() {
        // Given
        Long customerId = 1L;
        Long employeeId = -1L;

        // When
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            underTest.reassignCustomerToEmployee(customerId, employeeId);
        });

        // Then verify the exception message
        assertEquals("Customer or Employee IDs must not be null and must be a positive number", exception.getMessage());
        // Verify that the info logs are not triggered
        assertTrue(testAppender.contains(
                String.format("EntityOrchestratorServiceImpl::reassignCustomerToEmployee customerId: %d, employeeId: %d", customerId, employeeId),
                "INFO"
        ));
        assertFalse(testAppender.contains(
                String.format("Customer reassigned: customerId= %d, newEmployeeId= %d", customerId, employeeId),
                "INFO"
        ));
    }

    /*@Test
    void itShouldValidateEmployeesExist_ByCallingReassignCustomerToEmployee() {
        Long customerId = 1L;
        Long employeeId = 2L;

        Customer customer = new Customer();
        customer.setId(customerId);

        Employee employee = new Employee();
        employee.setId(employeeId);

        // Mock the validateEmployeeExists method
        doNothing().when(entityValidator).validateEmployeeExists(anyLong());
        when(customerRepository.findById(customerId)).thenReturn(Optional.ofNullable(customer));
        when(employeeRepository.findById(employeeId)).thenReturn(Optional.ofNullable(employee));
        when(customerRepository.save(customer)).thenReturn(any(Customer.class));


        underTest.reassignCustomerToEmployee(customerId, employeeId);

        verify(entityValidator, times(1)).validateEmployeeExists(employeeId);

        // Verify that the info logs are not triggered
        assertTrue(testAppender.contains(
                String.format("EntityOrchestratorServiceImpl::reassignCustomerToEmployee customerId: %d, employeeId: %d", customerId, employeeId), "INFO"
        ));
        assertTrue(testAppender.contains(
                String.format("Customer reassigned: customerId= %d, newEmployeeId= %d", customerId, employeeId), "INFO"
        ));
        verify(customerRepository, times(1)).findById(customerId);
        verify(employeeRepository, times(1)).findById(employeeId);
        verify(customerRepository, times(1)).save(customer);
    }*/

    /*@Test
    void itShouldThrowExceptionInValidateEmployeesExist_ByCallingReassignCustomerToEmployee() {
        Long customerId = 1L;
        Long employeeId = 2L;

        Customer customer = new Customer();
        customer.setId(customerId);

        Employee employee = new Employee();
        employee.setId(employeeId);

        // Mock the validateEmployeeExists method
        doThrow(new ResourceNotFoundException("Employee not found with ID: " + employeeId))
                .when(entityValidator).validateEmployeeExists(anyLong());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            underTest.reassignCustomerToEmployee(customerId, employeeId);
        });

        assertEquals("Employee not found with ID: " + employeeId, exception.getMessage());
        verify(customerRepository, times(0)).findById(customerId);
        verify(employeeRepository, times(0)).findById(employeeId);
        // Verify that the info logs are not triggered
        assertTrue(testAppender.contains(
                String.format("EntityOrchestratorServiceImpl::reassignCustomerToEmployee customerId: %d, employeeId: %d", customerId, employeeId), "INFO"
        ));
        assertFalse(testAppender.contains(
                String.format("Customer reassigned: customerId= %d, newEmployeeId= %d", customerId, employeeId), "INFO"
        ));
    }*/

    /*@Test
    void itShouldThrowExceptionWhenCustomerNotFound_ByCallingReassignCustomerToEmployee() {
        // Given
        Long customerId = 999L;
        Long employeeId = 2L;

        // Mock the validateEmployeeExists method
        doNothing().when(entityValidator).validateEmployeeExists(anyLong());
        when(customerRepository.findById(customerId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            underTest.reassignCustomerToEmployee(customerId, employeeId);
        });

        assertEquals("Customer not found with ID: " + customerId, exception.getMessage());
        verify(customerRepository, times(1)).findById(customerId);
        verify(employeeRepository, times(0)).findById(employeeId);
        // Verify that the info logs are not triggered
        assertTrue(testAppender.contains(
                String.format("EntityOrchestratorServiceImpl::reassignCustomerToEmployee customerId: %d, employeeId: %d", customerId, employeeId), "INFO"
        ));
        assertFalse(testAppender.contains(
                String.format("Customer reassigned: customerId= %d, newEmployeeId= %d", customerId, employeeId), "INFO"
        ));
    }*/

    /*@Test
    void itShouldThrowExceptionWhenEmployeeNotFound_ByCallingReassignCustomerToEmployee() {
        // Given
        Long customerId = 1L;
        Long employeeId = 999L;

        // Mock the validateEmployeeExists method
        doNothing().when(entityValidator).validateEmployeeExists(anyLong());
        when(customerRepository.findById(customerId)).thenReturn(Optional.of(new Customer()));
        when(employeeRepository.findById(employeeId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            underTest.reassignCustomerToEmployee(customerId, employeeId);
        });

        assertEquals("Employee not found with ID: " + employeeId, exception.getMessage());
        verify(customerRepository, times(1)).findById(customerId);
        verify(employeeRepository, times(1)).findById(employeeId);
        // Verify that the info logs are not triggered
        assertTrue(testAppender.contains(
                String.format("EntityOrchestratorServiceImpl::reassignCustomerToEmployee customerId: %d, employeeId: %d", customerId, employeeId), "INFO"
        ));
        assertFalse(testAppender.contains(
                String.format("Customer reassigned: customerId= %d, newEmployeeId= %d", customerId, employeeId), "INFO"
        ));
    }*/

    /*@Test
    void itShouldReassignCustomerToEmployee_ByCallingReassignCustomerToEmployee() {
        // Given
        Long customerId = 1L;
        Long employeeId = 2L;

        Customer customer = new Customer();
        customer.setId(customerId);

        Employee employee = new Employee();
        employee.setId(employeeId);

        // Mock the validateEmployeeExists method
        doNothing().when(entityValidator).validateEmployeeExists(anyLong());
        when(customerRepository.findById(customerId)).thenReturn(Optional.ofNullable(customer));
        when(employeeRepository.findById(employeeId)).thenReturn(Optional.ofNullable(employee));
        when(customerRepository.save(customer)).thenReturn(any(Customer.class));

        // When
        underTest.reassignCustomerToEmployee(customerId, employeeId);

        // Then verify that the info logs are triggered
        assertTrue(testAppender.contains(
                String.format("EntityOrchestratorServiceImpl::reassignCustomerToEmployee customerId: %d, employeeId: %d", customerId, employeeId), "INFO"
        ));
        assertTrue(testAppender.contains(
                String.format("Customer reassigned: customerId= %d, newEmployeeId= %d", customerId, employeeId), "INFO"
        ));
    }*/


    @Test
    void itShouldThrowExceptionWhenOldEmployeeIdIsNull_ByCallingDeleteEmployeeAndReassignCustomers() {
        // Given
        Long oldEmployeeId = null; // set to null to test the precondition
        Long newEmployeeId = 2L;

        // When
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            underTest.deleteEmployeeAndReassignCustomers(oldEmployeeId, newEmployeeId);
        });

        // Then verify the exception message
        assertEquals("Employee IDs must not be null and must be a positive number", exception.getMessage());

        // Verify that the info logs are not triggered
        assertTrue(testAppender.contains(
                String.format("EntityOrchestratorServiceImpl::deleteEmployeeAndReassignCustomers oldEmployeeId: %d, newEmployeeId: %d", oldEmployeeId, newEmployeeId), "INFO"
        ));

        assertFalse(testAppender.contains(
                String.format("Employee deleted and customers reassigned: oldEmployeeId= %d, newEmployeeId= %d", oldEmployeeId, newEmployeeId), "INFO"
        ));
    }

    @Test
    void itShouldThrowExceptionWhenNewEmployeeIdIsNull_ByCallingDeleteEmployeeAndReassignCustomers() {
        // Given
        Long oldEmployeeId = 1L;
        Long newEmployeeId = null; // set to null to test the precondition

        // When
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            underTest.deleteEmployeeAndReassignCustomers(oldEmployeeId, newEmployeeId);
        });

        // Then verify the exception message
        assertEquals("Employee IDs must not be null and must be a positive number", exception.getMessage());
        // Verify that the info logs are not triggered
        assertTrue(testAppender.contains(
                String.format("EntityOrchestratorServiceImpl::deleteEmployeeAndReassignCustomers oldEmployeeId: %d, newEmployeeId: %d", oldEmployeeId, newEmployeeId), "INFO"
        ));
        assertFalse(testAppender.contains(
                String.format("Employee deleted and customers reassigned: oldEmployeeId= %d, newEmployeeId= %d", oldEmployeeId, newEmployeeId), "INFO"
        ));
    }

    @Test
    void itShouldThrowExceptionWhenIdsAreEquals_ByCallingDeleteEmployeeAndReassignCustomers() {
        // Given
        Long oldEmployeeId = 1L;
        Long newEmployeeId = 1L;

        // When
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            underTest.deleteEmployeeAndReassignCustomers(oldEmployeeId, newEmployeeId);
        });

        // Then verify the exception message
        assertEquals("Old and new employee IDs must be different", exception.getMessage());

        // Verify that the info log is triggered
        assertTrue(testAppender.contains(
                String.format("EntityOrchestratorServiceImpl::deleteEmployeeAndReassignCustomers oldEmployeeId: %d, newEmployeeId: %d", oldEmployeeId, newEmployeeId), "INFO"
        ));
        // Verify that the info log is not triggered
        assertFalse(testAppender.contains(
                String.format("Employee deleted and customers reassigned: oldEmployeeId= %d, newEmployeeId= %d", oldEmployeeId, newEmployeeId), "INFO"
        ));
        // Verify that the warning log is triggered
        assertTrue(testAppender.contains("Old and new employee IDs must be different", "WARN"));
    }

    /*@Test
    void itShouldValidateEmployeesExist_ByCallingDeleteEmployeeAndReassignCustomers() {
        Long oldEmployeeId = 1L;
        Long newEmployeeId = 2L;

        Employee oldEmployee = new Employee();
        oldEmployee.setId(oldEmployeeId);
        Employee newEmployee = new Employee();
        newEmployee.setId(newEmployeeId);

        Customer customer1 = new Customer();
        customer1.setId(101L);
        Customer customer2 = new Customer();
        customer2.setId(102L);
        List<Customer> customers = List.of(customer1, customer2);

        // Mock the validateEmployeeExists method
        doNothing().when(entityValidator).validateEmployeeExists(oldEmployeeId);
        doNothing().when(entityValidator).validateEmployeeExists(newEmployeeId);
        when(employeeRepository.findById(oldEmployeeId)).thenReturn(Optional.ofNullable(oldEmployee));
        when(employeeRepository.findById(newEmployeeId)).thenReturn(Optional.ofNullable(newEmployee));
        //doNothing().when(underTest).reassignCustomers(oldEmployeeId, newEmployeeId);
        doNothing().when(entityValidator).validateEmployeeExists(oldEmployeeId);
        doNothing().when(entityValidator).validateEmployeeExists(newEmployeeId);
        when(customerService.getCustomersByEmployeeId(oldEmployeeId)).thenReturn(customers);
        when(employeeRepository.findById(newEmployeeId)).thenReturn(Optional.ofNullable(newEmployee));
        when(customerRepository.saveAll(customers)).thenReturn(customers);
        when(inactiveEmployeeService.createInactiveEmployee(oldEmployee)).thenReturn(new InactiveEmployee());
        doNothing().when(employeeRepository).delete(oldEmployee);


        underTest.deleteEmployeeAndReassignCustomers(oldEmployeeId, newEmployeeId);

        assertTrue(testAppender.contains(
                String.format("EntityOrchestratorServiceImpl::deleteEmployeeAndReassignCustomers oldEmployeeId: %d, newEmployeeId: %d", oldEmployeeId, newEmployeeId), "INFO"
        ));
        assertTrue(testAppender.contains(
                String.format("EntityOrchestratorServiceImpl::reassignCustomers oldEmployeeId: %d, newEmployeeId: %d", oldEmployeeId, newEmployeeId), "INFO"
        ));
        assertTrue(testAppender.contains(
                String.format("Customers reassigned successfully: oldEmployeeId= %d, newEmployeeId= %d", oldEmployeeId, newEmployeeId), "INFO"
        ));
        assertTrue(testAppender.contains(
                String.format("Reassigning customer ID: %d to new employee ID: %d", 101L, newEmployeeId), "INFO"
        ));
        assertTrue(testAppender.contains(
                String.format("Reassigning customer ID: %d to new employee ID: %d", 102L, newEmployeeId), "INFO"
        ));
        assertTrue(testAppender.contains(
                "Customers reassigned successfully", "INFO"
        ));
        // Verify that the info log is not triggered
        assertTrue(testAppender.contains(
                String.format("Employee deleted and customers reassigned: oldEmployeeId= %d, newEmployeeId= %d", oldEmployeeId, newEmployeeId), "INFO"
        ));

        // Verify method calls
        verify(entityValidator, times(2)).validateEmployeeExists(oldEmployeeId);
        verify(entityValidator, times(2)).validateEmployeeExists(newEmployeeId);
        verify(customerService, times(1)).getCustomersByEmployeeId(oldEmployeeId);
        verify(customerRepository, times(1)).saveAll(customers);
        verify(inactiveEmployeeService, times(1)).createInactiveEmployee(oldEmployee);
        verify(employeeRepository, times(1)).delete(oldEmployee);

//        // Given
//        Long oldEmployeeId = 1L;
//        Long newEmployeeId = 2L;
//
//        // Erstelle eine anonyme Unterklasse, um reassignCustomers zu überschreiben
//        EntityOrchestratorServiceImpl spyService = new EntityOrchestratorServiceImpl(
//                employeeRepository,
//                customerService,
//                customerRepository,
//                inactiveEmployeeService,
//                validationService
//        ) {
//            @Override
//            public void reassignCustomers(Long oldId, Long newId) {
//                // Überschreiben der Methode ohne Inhalt, um die Ausführung zu verhindern
//            }
//        };
//
//        // Mock the validateEmployeeExists method
//        doNothing().when(validationService).validateEmployeeExists(anyLong());
//
//        // Mock the findById method für die Validierungs- und Repository-Aufrufe
//        when(employeeRepository.findById(oldEmployeeId)).thenReturn(Optional.of(new Employee()));
//        when(employeeRepository.findById(newEmployeeId)).thenReturn(Optional.of(new Employee()));
//
//        // Call the method to test
//        spyService.deleteEmployeeAndReassignCustomers(oldEmployeeId, newEmployeeId);
//
//        // Verify interactions
//        verify(validationService, times(1)).validateEmployeeExists(oldEmployeeId);
//        verify(validationService, times(1)).validateEmployeeExists(newEmployeeId);
    }*/

    /*@Test
    void itShouldThrowExceptionWhenOldEmployeeNotFound_ByCallingDeleteEmployeeAndReassignCustomers() {
        Long oldEmployeeId = 1L;
        Long newEmployeeId = 2L;

        // Mock the validateEmployeeExists method to throw an exception for oldEmployeeId
        String message = String.format("Employee not found with ID: %d", oldEmployeeId);
        doThrow(new ResourceNotFoundException(message))
                .when(entityValidator).validateEmployeeExists(oldEmployeeId);

        // Call the method to test and expect ResourceNotFoundException
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            underTest.deleteEmployeeAndReassignCustomers(oldEmployeeId, newEmployeeId);
        });

        // Verify the exception message
        assertEquals(message, exception.getMessage());
        // Verify that the info log is triggered
        assertTrue(testAppender.contains(
                String.format("EntityOrchestratorServiceImpl::deleteEmployeeAndReassignCustomers oldEmployeeId: %d, newEmployeeId: %d", oldEmployeeId, newEmployeeId), "INFO"
        ));
        // Verify that the info log is not triggered
        assertFalse(testAppender.contains(
                String.format("Employee deleted and customers reassigned: oldEmployeeId= %d, newEmployeeId= %d", oldEmployeeId, newEmployeeId), "INFO"
        ));
    }*/

    /*@Test
    void itShouldReassignCustomersAndArchiveEmployee_ByCallingDeleteEmployeeAndReassignCustomers() {
        Long oldEmployeeId = 1L;
        Long newEmployeeId = 2L;

        Employee oldEmployee = new Employee();
        oldEmployee.setId(oldEmployeeId);
        Employee newEmployee = new Employee();
        newEmployee.setId(newEmployeeId);

        Customer customer1 = new Customer();
        customer1.setId(101L);
        Customer customer2 = new Customer();
        customer2.setId(102L);
        List<Customer> customers = List.of(customer1, customer2);

        when(employeeRepository.findById(oldEmployeeId)).thenReturn(Optional.of(oldEmployee));
        //when(employeeRepository.findById(newEmployeeId)).thenReturn(Optional.of(newEmployee));
        when(customerService.getCustomersByEmployeeId(oldEmployeeId)).thenReturn(customers);
        doNothing().when(entityValidator).validateEmployeeExists(anyLong());

        underTest.deleteEmployeeAndReassignCustomers(oldEmployeeId, newEmployeeId);

        verify(entityValidator, times(4)).validateEmployeeExists(anyLong());
        verify(employeeRepository, times(2)).findById(anyLong());
        verify(entityValidator, times(2)).validateEmployeeExists(oldEmployeeId);
        verify(entityValidator, times(2)).validateEmployeeExists(newEmployeeId);
        verify(employeeRepository, times(1)).findById(oldEmployeeId);
        verify(employeeRepository, times(1)).findById(newEmployeeId);
        verify(customerRepository, times(1)).saveAll(anyList());
        verify(inactiveEmployeeService, times(1)).createInactiveEmployee(oldEmployee);
        verify(employeeRepository, times(1)).delete(oldEmployee);
        // Überprüft, ob die Methode customerRepository.saveAll mit einer Liste von Kunden aufgerufen wird,
        // die sowohl customer1 als auch customer2 enthält.
        verify(customerRepository).saveAll(argThat(customer_s -> {
            // Initialisiere Booleans, um zu verfolgen, ob customer1 und customer2 in der Liste enthalten sind
            boolean containsCustomer1 = false;
            boolean containsCustomer2 = false;

            // Iteriere durch die übergebene Liste von Kunden
            for (Customer customer : customers) {
                // Überprüfe, ob der aktuelle Kunde customer1 ist
                if (customer.equals(customer1)) {
                    containsCustomer1 = true; // Setze containsCustomer1 auf true, wenn customer1 gefunden wurde
                } else if (customer.equals(customer2)) {
                    containsCustomer2 = true; // Setze containsCustomer2 auf true, wenn customer2 gefunden wurde
                }
            }

            // Die Bedingung, dass beide Kunden in der Liste enthalten sind,
            // muss erfüllt sein, damit der Test erfolgreich ist
            return containsCustomer1 && containsCustomer2;
        }));


        // Check if customers are reassigned to the new employee
        assertEquals(newEmployee, customer1.getEmployee());
        assertEquals(newEmployee, customer2.getEmployee());
        // Verify that the info logs are triggered
        assertTrue(testAppender.contains(
                String.format("EntityOrchestratorServiceImpl::deleteEmployeeAndReassignCustomers oldEmployeeId: %d, newEmployeeId: %d", oldEmployeeId, newEmployeeId), "INFO")
        );
        assertTrue(testAppender.contains(
                String.format("Employee deleted and customers reassigned: oldEmployeeId= %d, newEmployeeId= %d", oldEmployeeId, newEmployeeId), "INFO"
        ));
    }*/
}
