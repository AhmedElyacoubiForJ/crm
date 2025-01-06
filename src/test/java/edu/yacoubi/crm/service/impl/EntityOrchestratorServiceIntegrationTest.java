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
    private static final String LOG_MSG_REASSIGN_CUSTOMER_TO_EMPLOYEE_ENTRY_POINT =
            "EntityOrchestratorServiceImpl::reassignCustomerToEmployee customerId: %d, employeeId: %d";
    private static final String LOG_MSG_REASSIGN_CUSTOMERS_EXIT_POINT =
            "Customers reassigned successfully: oldEmployeeId= %d, newEmployeeId= %d";
    private static final String LOG_MSG_REASSIGN_CUSTOMER_TO_EMPLOYEE_EXIT_POINT =
            "Customer reassigned: customerId= %d, newEmployeeId= %d";
    private static final String LOG_WARN_MSG =
            "Warn message should be %s: ";
    // assert msg
    private static final String LOG_INFO_MSG_ENTRY_POINT =
            "Should indicate the entry point for reassigning customers";
    private static final String LOG_INFO_MSG_EXIT_POINT =
            "Should indicate the exit point for reassigning customers";

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

    // tests for reassignCustomers(Long oldEmployeeId, Long newEmployeeId)
    // Hinweis: Diese Tests für die Parameter-Validierung ähneln denen der Unit-Tests,
    // werden jedoch auch hier im Integrationstest zur zusätzlichen Sicherheit wiederholt.
    @Test
    void itShouldThrowExceptionWhenOldEmployeeIdIsNullByCallingReassignCustomers() {
        // Given
        final Long oldEmployeeId = null;
        final Long newEmployeeId = 1L;

        // When
        final IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            underTest.reassignCustomers(oldEmployeeId, newEmployeeId);
        });

        // Then
        // Verify the exception message
        assertEquals(
                // Erwarteter Wert
                ERROR_MSG_INVALID_IDS,
                // Tatsächlicher Wert
                exception.getMessage(),
                // Nachricht bei Fehlschlag
                String.format(ERROR_INFO_MSG, ERROR_MSG_INVALID_IDS)
        );
        // Verify logger entry message
        assertTrue(
                testAppender.contains(
                        String.format(LOG_MSG_REASSIGN_CUSTOMERS_ENTRY_POINT, oldEmployeeId, newEmployeeId),
                        "INFO"
                ),
                LOG_INFO_MSG_ENTRY_POINT
        );
        // Verify logger exit message
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
        // Verify logger entry message
        assertTrue(
                testAppender.contains(
                        String.format(LOG_MSG_REASSIGN_CUSTOMERS_ENTRY_POINT, oldEmployeeId, newEmployeeId),
                        "INFO"
                ),
                LOG_INFO_MSG_ENTRY_POINT
        );
        // Verify logger exit message
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
        // Then
        // Verify logger entry message
        assertTrue(testAppender.contains(
                        String.format(LOG_MSG_REASSIGN_CUSTOMERS_ENTRY_POINT, oldEmployeeId, newEmployeeId),
                        "INFO"
                ),
                LOG_INFO_MSG_ENTRY_POINT
        );
        // Verify logger exit message
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
        // Verify logger entry
        assertTrue(
                testAppender.contains(
                        String.format(LOG_MSG_REASSIGN_CUSTOMERS_ENTRY_POINT, oldEmployeeId, newEmployeeId),
                        "INFO"
                ),
                LOG_INFO_MSG_ENTRY_POINT
        );
        // Verify logger exit message
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
        // Verify logger entry message
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
        // Verify logger exit message
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
        final Employee existingOldEmployeeA = employeeService.createEmployee(TestDataUtil.createEmployeeA());
        final Long oldEmployeeId = existingOldEmployeeA.getId();
        final Long newEmployeeId = 2L;

        // When
        final ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            underTest.reassignCustomers(oldEmployeeId, newEmployeeId);
        });

        // Then
        // Verify oldEmployee exists
        //assertNotNull(existingOldEmployeeA);
        assertTrue(employeeService.getEmployeeById(oldEmployeeId).isPresent(), "Old employee should exist");

        // Verify the exception message
        assertEquals(
                String.format(ERROR_MSG_NO_CUSTOMERS, oldEmployeeId),
                exception.getMessage(),
                String.format(ERROR_INFO_MSG, String.format(ERROR_MSG_NO_CUSTOMERS, oldEmployeeId))
        );
        // Verify logger entry message
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
        // Verify logger exit message
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

        // When
        final ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            underTest.reassignCustomers(oldEmployeeId, newEmployeeId);
        });

        // Then
        assertThrows(ResourceNotFoundException.class, () ->
                employeeService.getEmployeeById(oldEmployeeId).get()
        );
        // Verify exception message
        assertEquals(
                errorMessage,
                exception.getMessage(),
                String.format(ERROR_INFO_MSG, errorMessage)
        );
        // Verify logger entry message
        assertTrue(
                testAppender.contains(
                        String.format(LOG_MSG_REASSIGN_CUSTOMERS_ENTRY_POINT, oldEmployeeId, newEmployeeId),
                        "INFO"
                ),
                LOG_INFO_MSG_ENTRY_POINT
        );
        // Verify logger exist message
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
        final Employee existingOldEmployeeA = employeeService.createEmployee(TestDataUtil.createEmployeeA());
        final Long oldEmployeeId = existingOldEmployeeA.getId();
        final String errorMessage = "No customers found for oldEmployee ID: " + oldEmployeeId;
        final Long newEmployeeId = 2L;

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
        // Verify logger entry message
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
        // Verify logger exit message
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

        // Then
        // Verify the exception message
        assertEquals(
                errorMessage,
                exception.getMessage(),
                String.format(ERROR_INFO_MSG, errorMessage)
        );
        // Verify logger entry message
        assertTrue(
                testAppender.contains(
                        String.format(LOG_MSG_REASSIGN_CUSTOMERS_ENTRY_POINT, oldEmployeeId, newEmployeeId),
                        "INFO"
                ),
                LOG_INFO_MSG_ENTRY_POINT
        );
        // Verify logger exist message
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

        // Then
        // Verify that old employee no longer has customers
        assertTrue(
                customerService.getCustomersByEmployeeId(oldEmployeeId).isEmpty(),
                "Old employee should have no customers assigned"
        );
        // Verify that new employee has customers
        List<Customer> newEmployeeCustomers = customerService.getCustomersByEmployeeId(newEmployeeId);
        assertFalse(
                newEmployeeCustomers.isEmpty(),
                "New employee should have customers assigned"
        );
        // Verify that all customers are reassigned to the new employee
        newEmployeeCustomers.forEach(customer -> {
            assertEquals(
                    newEmployeeId,
                    customer.getEmployee().getId(),
                    "Customer should be reassigned to new employee"
            );
        });
        // Verify logger entry message
        assertTrue(
                testAppender.contains(
                        String.format(LOG_MSG_REASSIGN_CUSTOMERS_ENTRY_POINT, oldEmployeeId, newEmployeeId),
                        "INFO"
                ),
                LOG_INFO_MSG_ENTRY_POINT
        );
        newEmployeeCustomers.forEach(
                customer -> assertTrue(testAppender.contains(
                        String.format(
                                "Reassigning customer ID: %d to new employee ID: %d",
                                customer.getId(),
                                newEmployeeId
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
        // Verify logger exit message
        assertTrue(
                testAppender.contains(
                        String.format(LOG_MSG_REASSIGN_CUSTOMERS_EXIT_POINT, oldEmployeeId, newEmployeeId),
                        "INFO"
                ),
                LOG_INFO_MSG_EXIT_POINT
        );
    }


    // tests for reassignCustomerToEmployee(Long customerId, Long employeeId)
    // tests for deleteEmployeeAndReassignCustomers(Long oldEmployeeId, Long newEmployeeId)


    /**
     * Prüft, ob ein Mitarbeiter gelöscht und seine Kunden korrekt zugewiesen werden.
     */
//    @Test
//    void testDeleteEmployeeAndReassignCustomers() {
//        Employee oldEmployee = new Employee();
//        oldEmployee.setFirstName("Old Employee");
//        oldEmployee = employeeRepository.save(oldEmployee);
//
//        Employee newEmployee = new Employee();
//        newEmployee.setFirstName("New Employee");
//        newEmployee = employeeRepository.save(newEmployee);
//
//        Customer customer = new Customer();
//        customer.setFirstName("Test Customer");
//        customer.setEmployee(oldEmployee);
//        customer = customerRepository.save(customer);
//
//        entityOrchestratorService.deleteEmployeeAndReassignCustomers(oldEmployee.getId(), newEmployee.getId());
//
//        Customer updatedCustomer = customerRepository.findById(customer.getId()).orElse(null);
//        assertNotNull(updatedCustomer);
//        assertEquals(newEmployee.getId(), updatedCustomer.getEmployee().getId());
//        assertFalse(employeeRepository.existsById(oldEmployee.getId()));
//    }
//
//    /**
//     * Prüft, ob ein Kunde korrekt einem Mitarbeiter zugewiesen wird.
//     */
//    @Test
//    void testReassignCustomerToEmployee() {
//        Employee employee = new Employee();
//        employee.setFirstName("Test Employee");
//        employee = employeeRepository.save(employee);
//
//        Customer customer = new Customer();
//        customer.setFirstName("Test Customer");
//        customer = customerRepository.save(customer);
//
//        entityOrchestratorService.reassignCustomerToEmployee(customer.getId(), employee.getId());
//
//        Customer updatedCustomer = customerRepository.findById(customer.getId()).orElse(null);
//        assertNotNull(updatedCustomer);
//        assertEquals(employee.getId(), updatedCustomer.getEmployee().getId());
//    }
//
//    /**
//     * Prüft, ob alle Kunden eines Mitarbeiters korrekt einem neuen Mitarbeiter zugewiesen werden.
//     */
//    @Test
//    void testReassignCustomers() {
//        Employee oldEmployee = new Employee();
//        oldEmployee.setFirstName("Old Employee");
//        oldEmployee = employeeRepository.save(oldEmployee);
//
//        Employee newEmployee = new Employee();
//        newEmployee.setFirstName("New Employee");
//        newEmployee = employeeRepository.save(newEmployee);
//
//        Customer customer1 = new Customer();
//        customer1.setFirstName("Customer 1");
//        customer1.setEmployee(oldEmployee);
//
//        Customer customer2 = new Customer();
//        customer2.setFirstName("Customer 2");
//        customer2.setEmployee(oldEmployee);
//
//        customerRepository.saveAll(List.of(customer1, customer2));
//
//        entityOrchestratorService.reassignCustomers(oldEmployee.getId(), newEmployee.getId());
//
//        List<Customer> customers = customerRepository.findAll();
//        for (Customer customer : customers) {
//            assertEquals(newEmployee.getId(), customer.getEmployee().getId());
//        }
//    }
}
