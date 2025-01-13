package edu.yacoubi.crm.service.impl;

import ch.qos.logback.classic.Logger;
import edu.yacoubi.crm.exception.ResourceNotFoundException;
import edu.yacoubi.crm.model.Customer;
import edu.yacoubi.crm.model.Employee;
import edu.yacoubi.crm.service.ICustomerService;
import edu.yacoubi.crm.service.IEmployeeService;
import edu.yacoubi.crm.service.IInactiveEmployeeService;
import edu.yacoubi.crm.util.TestAppender;
import edu.yacoubi.crm.util.TestDataUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


/**
 * Integrationstests prüfen das Zusammenspiel der verschiedenen Komponenten des Systems
 * in einer realistischeren Umgebung im Vergleich zu Unit-Tests.
 * Diese Tests setzen voraus, dass du Spring Boot verwendest und eine entsprechende Testkonfiguration hast.
 */
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class EntityOrchestratorServiceImplIntegrationTest {
    // Error messages
    private static final String ERROR_INVALID_IDS_MSG =
            "Employee IDs must not be null and must be a positive number";
    private static final String ERROR_SAME_IDS_MSG =
            "Old and new employee IDs must be different";
    private static final String ERROR_MSG_NO_CUSTOMERS =
            "No customers found for oldEmployee ID: %d";

    // Logger infos
    private static final String INFO_LOG_REASSIGN_CUSTOMERS_ENTRY_POINT =
            "::reassignCustomers oldEmployeeId: %d, newEmployeeId: %d";
    private static final String INFO_LOG_REASSIGN_CUSTOMERS_EXIT_POINT =
            "Customers reassigned successfully: oldEmployeeId= %d, newEmployeeId= %d";

    private static final String INFO_LOG_REASSIGN_CUSTOMER_TO_EMPLOYEE_ENTRY_POINT =
            "::reassignCustomerToEmployee customerId: %d, employeeId: %d";
    private static final String INFO_LOG_REASSIGN_CUSTOMER_TO_EMPLOYEE_EXIT_POINT =
            "Customer reassigned: customerId= %d, newEmployeeId= %d";

    // Assert supplied failure message
    private static final String WARN_SUPPLIED_MSG  = "Warn message should be: %s";
    private static final String ERROR_SUPPLIED_MSG = "Error message should be: %s";
    private static final String INFO_SUPPLIED_MSG  = "Info message should be: %s";

    private static TestAppender testAppender;

    @Autowired
    private IEmployeeService employeeService;
    @Autowired
    private ICustomerService customerService;
    @Autowired
    private IInactiveEmployeeService inactiveEmployeeService;

    @Autowired
    private EntityOrchestratorServiceImpl underTest;

    @BeforeEach
    public void setUp() {
        final Logger logger = (Logger) LoggerFactory.getLogger(EntityOrchestratorServiceImpl.class);
        testAppender = new TestAppender();
        testAppender.start();
        logger.addAppender(testAppender);
    }

    // Hinweis: Diese Tests für die Parameter-Validierung ähneln denen der Unit-Tests,
    // werden jedoch auch hier im Integrationstest zur zusätzlichen Sicherheit wiederholt.
    @Test
    void itShouldThrowExceptionWhenOldEmployeeIdIsNullByCallingReassignCustomers() {
        // Given
        final Long oldEmployeeId = null;
        final Long newEmployeeId = 1L;

        // When
        final IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> underTest.reassignCustomers(oldEmployeeId, newEmployeeId)
        );

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
        String expectedLogMsg = String.format(INFO_LOG_REASSIGN_CUSTOMERS_ENTRY_POINT, oldEmployeeId, newEmployeeId);
        assertTrue(
                testAppender.contains(expectedLogMsg, "INFO"),
                String.format(INFO_SUPPLIED_MSG, expectedLogMsg)
        );
        expectedLogMsg = String.format(INFO_LOG_REASSIGN_CUSTOMERS_EXIT_POINT, oldEmployeeId, newEmployeeId);
        assertFalse(
                testAppender.contains(expectedLogMsg, "INFO"),
                String.format(INFO_SUPPLIED_MSG, expectedLogMsg)
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
        String expectedLogMsg = String.format(INFO_LOG_REASSIGN_CUSTOMERS_ENTRY_POINT, oldEmployeeId, newEmployeeId);
        assertTrue(
                testAppender.contains(expectedLogMsg, "INFO"),
                String.format(INFO_SUPPLIED_MSG, expectedLogMsg)
        );
        expectedLogMsg = String.format(INFO_LOG_REASSIGN_CUSTOMERS_EXIT_POINT, oldEmployeeId, newEmployeeId);
        assertFalse(
                testAppender.contains(expectedLogMsg, "INFO"),
                String.format(INFO_SUPPLIED_MSG, expectedLogMsg)
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
        String expectedLogMsg = String.format(INFO_LOG_REASSIGN_CUSTOMERS_ENTRY_POINT, oldEmployeeId, newEmployeeId);
        assertTrue(
                testAppender.contains(expectedLogMsg, "INFO"),
                String.format(INFO_SUPPLIED_MSG, expectedLogMsg)
        );
        expectedLogMsg = String.format(INFO_LOG_REASSIGN_CUSTOMERS_EXIT_POINT, oldEmployeeId, newEmployeeId);
        assertFalse(
                testAppender.contains(expectedLogMsg, "INFO"),
                String.format(INFO_SUPPLIED_MSG, expectedLogMsg)
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
        String expectedLogMsg = String.format(INFO_LOG_REASSIGN_CUSTOMERS_ENTRY_POINT, oldEmployeeId, newEmployeeId);
        assertTrue(
                testAppender.contains(expectedLogMsg, "INFO"),
                String.format(INFO_SUPPLIED_MSG, expectedLogMsg)
        );
        expectedLogMsg = String.format(INFO_LOG_REASSIGN_CUSTOMERS_EXIT_POINT, oldEmployeeId, newEmployeeId);
        assertFalse(
                testAppender.contains(expectedLogMsg, "INFO"),
                String.format(INFO_SUPPLIED_MSG, expectedLogMsg)
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
        String expectedLogMsg = String.format(INFO_LOG_REASSIGN_CUSTOMERS_ENTRY_POINT, oldEmployeeId, newEmployeeId);
        assertTrue(
                testAppender.contains(expectedLogMsg, "INFO"),
                String.format(INFO_SUPPLIED_MSG, expectedLogMsg)
        );
        expectedLogMsg = String.format(INFO_LOG_REASSIGN_CUSTOMERS_EXIT_POINT, oldEmployeeId, newEmployeeId);
        assertFalse(
                testAppender.contains(expectedLogMsg, "INFO"),
                String.format(INFO_SUPPLIED_MSG, expectedLogMsg)
        );
    }

    @Test
    void itShouldThrowExceptionWhenCustomersNotFoundByOldEmployeeIdByCallingReassignCustomers() {
        // Given
        final Employee existingOldEmployeeA = employeeService.createEmployee(TestDataUtil.createEmployeeA());
        final Long oldEmployeeId = existingOldEmployeeA.getId();
        final Long newEmployeeId = 2L;

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
        String expectedLogMsg = String.format(INFO_LOG_REASSIGN_CUSTOMERS_ENTRY_POINT, oldEmployeeId, newEmployeeId);
        assertTrue(
                testAppender.contains(expectedLogMsg, "INFO"),
                String.format(INFO_SUPPLIED_MSG, expectedLogMsg)
        );
        expectedLogMsg = String.format(INFO_LOG_REASSIGN_CUSTOMERS_EXIT_POINT, oldEmployeeId, newEmployeeId);
        assertFalse(
                testAppender.contains(expectedLogMsg, "INFO"),
                String.format(INFO_SUPPLIED_MSG, expectedLogMsg)
        );
    }

    @Test
    void itShouldThrowExceptionWhenGetCustomersByEmployeeIdAndEmployeeDoesNotExistByCallingReassignCustomers() {
        // Given
        final Long oldEmployeeId = 999L;
        final Long newEmployeeId = 2L;
        final String errorMessage = "Employee not found with ID: " + oldEmployeeId;

        // When
        final ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            underTest.reassignCustomers(oldEmployeeId, newEmployeeId);
        });

        // Then / Verify
        assertThrows(ResourceNotFoundException.class, () ->
                employeeService.getEmployeeById(oldEmployeeId).get()
        );
        // Error exception message
        assertEquals(
                errorMessage,
                exception.getMessage(),
                String.format(ERROR_SUPPLIED_MSG, errorMessage)
        );
        // Logger entry & exit message
        String expectedLogMsg = String.format(INFO_LOG_REASSIGN_CUSTOMERS_ENTRY_POINT, oldEmployeeId, newEmployeeId);
        assertTrue(
                testAppender.contains(expectedLogMsg, "INFO"),
                String.format(INFO_SUPPLIED_MSG, expectedLogMsg)
        );
        expectedLogMsg = String.format(INFO_LOG_REASSIGN_CUSTOMERS_EXIT_POINT, oldEmployeeId, newEmployeeId);
        assertFalse(
                testAppender.contains(String.format(expectedLogMsg), "INFO"),
                String.format(INFO_SUPPLIED_MSG, expectedLogMsg)
        );
    }

    @Test
    void itShouldThrowExceptionWhenGetCustomersByEmployeeIdAndEmployeeDoesNotHaveCustomersByCallingReassignCustomers() {
        // Given
        final Employee existingOldEmployeeA = employeeService.createEmployee(TestDataUtil.createEmployeeA());
        final Long oldEmployeeId = existingOldEmployeeA.getId();
        final String errorMessage = "No customers found for oldEmployee ID: " + oldEmployeeId;
        final Long newEmployeeId = 2L;

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
                        String.format("::reassignCustomers warn: %s", errorMessage),
                        "WARN"
                ),
                String.format(WARN_SUPPLIED_MSG, errorMessage)
        );
        // Logger info entry & exit messages
        String expectedLogMsg = String.format(INFO_LOG_REASSIGN_CUSTOMERS_ENTRY_POINT, oldEmployeeId, newEmployeeId);
        assertTrue(
                testAppender.contains(expectedLogMsg, "INFO"),
                String.format(INFO_SUPPLIED_MSG, expectedLogMsg)
        );
        expectedLogMsg = String.format(INFO_LOG_REASSIGN_CUSTOMERS_EXIT_POINT, oldEmployeeId, newEmployeeId);
        assertFalse(
                testAppender.contains(expectedLogMsg, "INFO"),
                String.format(INFO_SUPPLIED_MSG, expectedLogMsg)
        );
    }

    @Test
    void itShouldThrowExceptionWhenGetEmployeeByIdForNewEmployeeDoesNotExistByCallingReassignCustomers() {
        // Given
        final Employee existingOldEmployee = employeeService.createEmployee(TestDataUtil.createEmployeeA());
        final Long oldEmployeeId = existingOldEmployee.getId();
        List<Customer> customers = new ArrayList<Customer>();
        customers.add(TestDataUtil.createCustomerA(existingOldEmployee));
        customers.add(TestDataUtil.createCustomerB(existingOldEmployee));
        existingOldEmployee.setCustomers(customers);
        employeeService.updateEmployee(oldEmployeeId, existingOldEmployee);
        final Long newEmployeeId = 999L;
        final String errorMessage = "Employee not found with ID: " + newEmployeeId;

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
        String expectedLogMsg = String.format(INFO_LOG_REASSIGN_CUSTOMERS_ENTRY_POINT, oldEmployeeId, newEmployeeId);
        assertTrue(
                testAppender.contains(expectedLogMsg, "INFO"),
                String.format(INFO_SUPPLIED_MSG, expectedLogMsg)
        );
        expectedLogMsg = String.format(INFO_LOG_REASSIGN_CUSTOMERS_EXIT_POINT, oldEmployeeId, newEmployeeId);
        assertFalse(
                testAppender.contains(expectedLogMsg, "INFO"),
                String.format(INFO_SUPPLIED_MSG, expectedLogMsg)
        );
    }

    @Test
    void itShouldReassignCustomersFromOldEmployeeToNewEmployeeByCallingReassignCustomers() {
        // Given
        final Employee existingOldEmployee = employeeService
                .createEmployee(TestDataUtil.createEmployeeA());
        final Long oldEmployeeId = existingOldEmployee.getId();
        final List<Customer> customers = new ArrayList<>();
        customers.add(TestDataUtil.createCustomerA(existingOldEmployee));
        customers.add(TestDataUtil.createCustomerB(existingOldEmployee));
        existingOldEmployee.setCustomers(customers);
        employeeService.updateEmployee(oldEmployeeId, existingOldEmployee);

        final Employee existingNewEmployee = employeeService
                .createEmployee(TestDataUtil.createEmployeeB());
        final Long newEmployeeId = existingNewEmployee.getId();

        // When
        underTest.reassignCustomers(oldEmployeeId, newEmployeeId);

        // Then / Verify
        // customers reassigned from old employee to new employee
        assertTrue(
                customerService.getCustomersByEmployeeId(oldEmployeeId).isEmpty(),
                "Old employee should have no customers assigned"
        );
        List<Customer> newEmployeeCustomers = customerService.getCustomersByEmployeeId(newEmployeeId);
        assertFalse(
                newEmployeeCustomers.isEmpty(),
                "New employee should have customers assigned"
        );
        newEmployeeCustomers.forEach(customer -> {
            assertEquals(
                    newEmployeeId,
                    customer.getEmployee().getId(),
                    "Customer should be reassigned to new employee"
            );
        });
        final String expectedReassigningMsg = "Reassigning customer ID: %d to new employee ID: %d";
        newEmployeeCustomers.forEach(
                customer -> assertTrue(
                        testAppender.contains(
                                String.format(expectedReassigningMsg, customer.getId(), newEmployeeId),
                                "INFO"
                        ),
                        String.format(
                                INFO_SUPPLIED_MSG,
                                String.format(expectedReassigningMsg,
                                        customer.getId(),
                                        newEmployeeId)
                        )
                )
        );

        // Logger info entry & exit messages
        String expectedLogMsg = String.format(INFO_LOG_REASSIGN_CUSTOMERS_ENTRY_POINT, oldEmployeeId, newEmployeeId);
        assertTrue(
                testAppender.contains(
                        expectedLogMsg,
                        "INFO"
                ),
                String.format(INFO_SUPPLIED_MSG, expectedLogMsg)
        );
        expectedLogMsg = String.format(INFO_LOG_REASSIGN_CUSTOMERS_EXIT_POINT, oldEmployeeId, newEmployeeId);
        assertTrue(
                testAppender.contains(
                        expectedLogMsg,
                        "INFO"
                ),
                String.format(INFO_SUPPLIED_MSG, expectedLogMsg)
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
                        String.format("EntityOrchestratorServiceImpl parameter warn: %s", errorMessage),
                        "WARN"
                )
        );

        // Logger info entry & exit message
        String expectedLogMsg = String.format(INFO_LOG_REASSIGN_CUSTOMER_TO_EMPLOYEE_ENTRY_POINT, customerId, employeeId);
        assertTrue(
                testAppender.contains(expectedLogMsg, "INFO"),
                String.format(INFO_SUPPLIED_MSG, expectedLogMsg)
        );
        expectedLogMsg = String.format(INFO_LOG_REASSIGN_CUSTOMER_TO_EMPLOYEE_EXIT_POINT, customerId, employeeId);
        assertFalse(
                testAppender.contains(expectedLogMsg, "INFO"),
                String.format(INFO_SUPPLIED_MSG, expectedLogMsg)
        );
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

        // Then / Verify
        // Warn exception message & logger warn message
        assertEquals(
                errorMessage,
                exception.getMessage(),
                String.format(ERROR_SUPPLIED_MSG, errorMessage)
        );
        assertTrue(testAppender.contains(
                String.format("EntityOrchestratorServiceImpl parameter warn: %s", errorMessage),
                "WARN"
        ));
        // Logger info entry & exit messages
        String expectedLogMsg = String.format(INFO_LOG_REASSIGN_CUSTOMER_TO_EMPLOYEE_ENTRY_POINT, customerId, employeeId);
        assertTrue(
                testAppender.contains(expectedLogMsg, "INFO"),
                String.format(INFO_SUPPLIED_MSG, expectedLogMsg)
        );
        expectedLogMsg = String.format(INFO_LOG_REASSIGN_CUSTOMER_TO_EMPLOYEE_EXIT_POINT, customerId, employeeId);
        assertFalse(testAppender.contains(
                        String.format(INFO_LOG_REASSIGN_CUSTOMER_TO_EMPLOYEE_EXIT_POINT, customerId, employeeId),
                        "INFO"
                ),
                String.format(INFO_SUPPLIED_MSG, expectedLogMsg)
        );
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

        // Then / Verify
        // Warn exception message & logger warn message
        assertEquals(
                errorMessage,
                exception.getMessage(),
                String.format(ERROR_SUPPLIED_MSG, errorMessage)
        );
        assertTrue(testAppender.contains(
                String.format("EntityOrchestratorServiceImpl parameter warn: %s", errorMessage),
                "WARN"
        ));
        // Logger info entry & exit messages
        String expectedLogMsg = String.format(INFO_LOG_REASSIGN_CUSTOMER_TO_EMPLOYEE_ENTRY_POINT, customerId, employeeId);
        assertTrue(
                testAppender.contains(expectedLogMsg, "INFO"),
                String.format(INFO_SUPPLIED_MSG, expectedLogMsg)
        );
        expectedLogMsg = String.format(INFO_LOG_REASSIGN_CUSTOMER_TO_EMPLOYEE_EXIT_POINT, customerId, employeeId);
        assertFalse(testAppender.contains(
                        String.format(INFO_LOG_REASSIGN_CUSTOMER_TO_EMPLOYEE_EXIT_POINT, customerId, employeeId),
                        "INFO"
                ),
                String.format(INFO_SUPPLIED_MSG, expectedLogMsg)
        );
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

        // Then / Verify
        // Error exception message & logger warn
        assertEquals(
                errorMessage,
                exception.getMessage(),
                String.format(ERROR_SUPPLIED_MSG, errorMessage)
        );
        assertTrue(
                testAppender.contains(
                        String.format("EntityOrchestratorServiceImpl parameter warn: %s", errorMessage),
                        "WARN"
                ),
                String.format(WARN_SUPPLIED_MSG, String.format("EntityOrchestratorServiceImpl parameter warn: %s", errorMessage))
        );

        // Logger info entry & exit messages
        String expectedLogMsg = String.format(INFO_LOG_REASSIGN_CUSTOMER_TO_EMPLOYEE_ENTRY_POINT, customerId, employeeId);
        assertTrue(
                testAppender.contains(expectedLogMsg, "INFO"),
                String.format(INFO_SUPPLIED_MSG, expectedLogMsg)
        );
        expectedLogMsg = String.format(INFO_LOG_REASSIGN_CUSTOMER_TO_EMPLOYEE_EXIT_POINT, customerId, employeeId);
        assertFalse(
                testAppender.contains(expectedLogMsg, "INFO"),
                String.format(INFO_SUPPLIED_MSG, expectedLogMsg)
        );
    }

    @Test
    void itShouldThrowExceptionWhenCustomerDoesNotExistByCallingReassignCustomerToEmployee() {
        // Given
        final Long customerId = 999L; // does not exist in the database
        final Long employeeId = 1L;
        final String errorMessage = "Customer not found with ID: " + customerId;

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
        String expectedLogMsg = String.format(INFO_LOG_REASSIGN_CUSTOMER_TO_EMPLOYEE_ENTRY_POINT, customerId, employeeId);
        assertTrue(
                testAppender.contains(expectedLogMsg, "INFO"),
                String.format(INFO_SUPPLIED_MSG, expectedLogMsg)
        );
        expectedLogMsg = String.format(INFO_LOG_REASSIGN_CUSTOMER_TO_EMPLOYEE_EXIT_POINT, customerId, employeeId);
        assertFalse(
                testAppender.contains(expectedLogMsg, "INFO"),
                String.format(INFO_SUPPLIED_MSG, expectedLogMsg)
        );
    }

    @Test
    void itShouldThrowExceptionWhenEmployeeDoesNotExistByCallingReassignCustomerToEmployee() {
        // Given
        final Customer existingCustomer = customerService.createCustomer(
                TestDataUtil.createCustomerB(employeeService.createEmployee(TestDataUtil.createEmployeeA()))
        );
        final Long customerId = existingCustomer.getId();
        final Long employeeId = 999L; // set to a non-existing employee id to test the precondition
        final String errorMessage = "Employee not found with ID: " + employeeId;

        // When
        final ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            underTest.reassignCustomerToEmployee(customerId, employeeId);
        });

        // Then / Verify
        // customer exists as precondition
        assertTrue(customerService.getCustomerById(customerId).isPresent(), "Customer must be present");

        // Error exception message
        assertEquals(
                errorMessage,
                exception.getMessage(),
                String.format(ERROR_SUPPLIED_MSG, errorMessage)
        );

        // Logger info entry & exit messages
        String expectedLogMsg = String.format(INFO_LOG_REASSIGN_CUSTOMER_TO_EMPLOYEE_ENTRY_POINT, customerId, employeeId);
        assertTrue(
                testAppender.contains(expectedLogMsg, "INFO"),
                String.format(INFO_SUPPLIED_MSG, expectedLogMsg)
        );
        expectedLogMsg = String.format(INFO_LOG_REASSIGN_CUSTOMER_TO_EMPLOYEE_EXIT_POINT, customerId, employeeId);
        assertFalse(
                testAppender.contains(expectedLogMsg, "INFO"),
                String.format(INFO_SUPPLIED_MSG, expectedLogMsg)
        );
    }

    @Test
    void itShouldAssignCustomerToEmployeeByCallingReassignCustomerToEmployee() {
        // Given
        Employee employeeB = employeeService.createEmployee(TestDataUtil.createEmployeeB());
        final Long customerId = customerService.createCustomer(TestDataUtil.createCustomerB(employeeB))
                .getId();
        final Long employeeId = employeeService.createEmployee(TestDataUtil.createEmployeeC()).getId();

        // When
        underTest.reassignCustomerToEmployee(customerId, employeeId);

        // Then / Verify
        // customer exists as precondition
        assertTrue(customerService.getCustomerById(customerId).isPresent(), "Customer must be present");
        // employee exists as precondition
        assertTrue(employeeService.getEmployeeById(employeeId).isPresent(), "Employee must be present");

        // customer is assigned to the new employee
        assertEquals(
                employeeId,
                customerService.getCustomerById(customerId).get().getEmployee().getId(),
                "Customer should be assigned to the new employee"
        );
        // customer is not assigned more to the old employee
        assertNotEquals(
                employeeB.getId(),
                customerService.getCustomerById(customerId).get().getEmployee().getId(),
                "Customer should not be assigned to the old employee"
        );

        // Logger info entry & exit messages
        String expectedLogMsg = String.format(INFO_LOG_REASSIGN_CUSTOMER_TO_EMPLOYEE_ENTRY_POINT, customerId, employeeId);
        assertTrue(
                testAppender.contains(expectedLogMsg, "INFO"),
                String.format(INFO_SUPPLIED_MSG, expectedLogMsg)
        );
        expectedLogMsg = String.format(INFO_LOG_REASSIGN_CUSTOMER_TO_EMPLOYEE_EXIT_POINT, customerId, employeeId);
        assertTrue(
                testAppender.contains(expectedLogMsg, "INFO"),
                String.format(INFO_SUPPLIED_MSG, expectedLogMsg)
        );
    }

    // tests for deleteEmployeeAndReassignCustomers(Long oldEmployeeId, Long newEmployeeId)
    @Test
    void itShouldReassignCustomersAndArchiveEmployeeByCallingDeleteEmployeeAndReassignCustomers() {
        // Given
        final Employee oldEmployee = employeeService.createEmployee(TestDataUtil.createEmployeeA());
        final Long oldEmployeeId = oldEmployee.getId();

        final Customer customerA = customerService.createCustomer(TestDataUtil.createCustomerA(oldEmployee));
        final Long customerAId = customerA.getId();
        final Customer customerB = customerService.createCustomer(TestDataUtil.createCustomerB(oldEmployee));
        final Long customerBId = customerB.getId();
        final Customer customerC = customerService.createCustomer(TestDataUtil.createCustomerC(oldEmployee));
        final Long customerCId = customerC.getId();

        final Employee newEmployee = employeeService.createEmployee(TestDataUtil.createEmployeeB());
        final Long newEmployeeId = newEmployee.getId();

        // When
        underTest.deleteEmployeeAndReassignCustomers(oldEmployeeId, newEmployeeId);

        // Then
        // Verify customers are assigned to the new employee
        assertAll(
                "Customers should be reassigned to the new employee",
                () -> assertEquals(newEmployeeId, customerService.getCustomerById(customerAId).get().getEmployee().getId(), "Customer A should be assigned to the new employee"),
                () -> assertEquals(newEmployeeId, customerService.getCustomerById(customerBId).get().getEmployee().getId(), "Customer B should be assigned to the new employee"),
                () -> assertEquals(newEmployeeId, customerService.getCustomerById(customerCId).get().getEmployee().getId(), "Customer C should be assigned to the new employee")
        );

        // Verify old employee is archived
        assertThrows(
                ResourceNotFoundException.class,
                () -> employeeService.getEmployeeById(oldEmployeeId)
        );
        assertTrue(inactiveEmployeeService.existsByOriginalEmployeeId(oldEmployeeId), "Employee should be archived");

        // Verify logger entry message
        assertTrue(
                testAppender.contains(
                        String.format("::deleteEmployeeAndReassignCustomers oldEmployeeId: %d, newEmployeeId: %d", oldEmployeeId, newEmployeeId),
                        "INFO"
                ),
                "Should indicate the entry point for delete employee and reassign customers"
        );
        // Verify logger exit message
        assertTrue(
                testAppender.contains(
                        String.format("Employee deleted and customers reassigned: oldEmployeeId= %d, newEmployeeId= %d", oldEmployeeId, newEmployeeId),
                        "INFO"
                ),
                "Should indicate the exit point for delete employee and reassign customers"
        );
    }
}
