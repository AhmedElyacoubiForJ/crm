package edu.yacoubi.crm.service.validation;

import ch.qos.logback.classic.Logger;
import edu.yacoubi.crm.exception.ResourceNotFoundException;
import edu.yacoubi.crm.model.Customer;
import edu.yacoubi.crm.model.Employee;
import edu.yacoubi.crm.model.Note;
import edu.yacoubi.crm.repository.CustomerRepository;
import edu.yacoubi.crm.repository.EmployeeRepository;
import edu.yacoubi.crm.repository.InactiveEmployeeRepository;
import edu.yacoubi.crm.repository.NoteRepository;
import edu.yacoubi.crm.util.TestAppender;
import edu.yacoubi.crm.util.TestDataUtil;
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
    // Assert supplied failure message
    private static final String ERROR_SUPPLIED_MSG = "Error message should be: %s";
    private static final String INFO_SUPPLIED_MSG = "Info message should be: %s";

    @Autowired
    private EmployeeRepository employeeRepository;
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private NoteRepository noteRepository;
    @Autowired
    private InactiveEmployeeRepository inactiveEmployeeRepository;

    @Autowired
    private EntityValidator underTest;

    private TestAppender testAppender;

    @BeforeEach
    public void setUp() {
        final Logger logger = (Logger) LoggerFactory.getLogger(EntityValidator.class);
        testAppender = new TestAppender();
        testAppender.start();
        logger.addAppender(testAppender);
    }

    @Test
    void itShouldValidateEmployeeExists() {
        // Given
        final Employee existingEmployee = employeeRepository.save(TestDataUtil.createEmployeeA());
        final Long employeeId = existingEmployee.getId();
        final String expectedEntryLogMsg = String.format(
                "EntityValidator::validateEmployeeExists employeeId: %d",
                employeeId
        );
        final String expectedExitLogMsg = String.format(
                "EntityValidator::validateEmployeeExists employeeId: %d successfully validated",
                employeeId
        );

        // When
        underTest.validateEmployeeExists(employeeId);

        // Then
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
    void itShouldThrowWhenEmployeeDoesNotExist() {
        // Given
        final Long employeeId = 999L;
        final String errorMessage = "Employee not found with ID: " + employeeId;
        final String expectedErrorLogMsg = String.format(
                "EntityValidator::validateEmployeeExists error: %s",
                errorMessage
        );
        final String expectedEntryLogMsg = String.format(
                "EntityValidator::validateEmployeeExists employeeId: %d",
                employeeId
        );
        final String expectedExitLogMsg = String.format(
                "EntityValidator::validateEmployeeExists employeeId: %d successfully validated",
                employeeId
        );

        // When
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> underTest.validateEmployeeExists(employeeId)
        );

        // Then
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
        final Note existingNote = noteRepository
                .save(TestDataUtil.createNoteA(customerRepository
                        .save(TestDataUtil.createCustomerB(employeeRepository
                                .save(TestDataUtil.createEmployeeA())
                        )))
                );
        final Long noteId = existingNote.getId();
        final String expectedEntryLogMsg = String.format(
                "EntityValidator::validateNoteExists id: %d",
                noteId
        );
        final String expectedExitLogMsg = String.format(
                "EntityValidator::validateNoteExists id: %d successfully validated",
                noteId
        );

        // When
        underTest.validateNoteExists(noteId);

        // Then
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
    void itShouldThrowWhenNoteDoesNotExist() {
        // Given
        final Long noteId = 999L;
        final String errorMessage = "Note not found with ID: " + noteId;
        final String expectedErrorLogMsg = String.format(
                "EntityValidator::validateNoteExists error: %s",
                errorMessage
        );
        final String expectedEntryLogMsg = String.format(
                "EntityValidator::validateNoteExists id: %d",
                noteId
        );
        final String expectedExitLogMsg = String.format(
                "EntityValidator::validateNoteExists id: %d successfully validated",
                noteId
        );

        // When
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> underTest.validateNoteExists(noteId)
        );

        // Then
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
        final Customer existingCustomer = customerRepository.save(
                TestDataUtil.createCustomerA(employeeRepository.save(
                        TestDataUtil.createEmployeeA())
                )
        );
        final Long customerId = existingCustomer.getId();
        final String expectedEntryLogMsg = String.format(
                "EntityValidator::validateCustomerExists id: %d",
                customerId
        );
        final String expectedExitLogMsg = String.format(
                "EntityValidator::validateCustomerExists id: %d successfully validated",
                customerId
        );

        // When
        underTest.validateCustomerExists(customerId);

        // Then
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
    void itShouldThrowWhenCustomerDoesNotExist() {
        // Given
        final Long customerId = 999L;
        final String errorMessage = "Customer not found with ID: " + customerId;
        final String expectedErrorLogMsg = String.format(
                "EntityValidator::validateCustomerExists error: %s",
                errorMessage
        );
        final String expectedEntryLogMsg = String.format(
                "EntityValidator::validateCustomerExists id: %d",
                customerId
        );
        final String expectedExitLogMsg = String.format(
                "EntityValidator::validateCustomerExists id: %d successfully validated",
                customerId
        );

        // When
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> underTest.validateCustomerExists(customerId)
        );

        // Then
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
        final Long originalEmployeeId = 101L;
        final String expectedEntryLogMsg = String.format(
                "EntityValidator::validateInactiveEmployeeExists originalEmployeeId: %d",
                originalEmployeeId
        );
        final String expectedExitLogMsg = String.format(
                "EntityValidator::validateInactiveEmployeeExists originalEmployeeId: %d successfully validated",
                originalEmployeeId
        );
        inactiveEmployeeRepository.save(
                TestDataUtil.createInactiveEmployeeA(originalEmployeeId)
        );

        // When
        underTest.validateInactiveEmployeeExists(originalEmployeeId);

        // Then
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
        final Long originalEmployeeId = 999L;
        final String errorMessage = "Inactive employee not found with ID: " + originalEmployeeId;
        final String expectedErrorLogMsg = String.format(
                "EntityValidator::validateInactiveEmployeeExists error: %s",
                errorMessage
        );
        final String expectedEntryLogMsg = String.format(
                "EntityValidator::validateInactiveEmployeeExists originalEmployeeId: %d",
                originalEmployeeId
        );
        final String expectedExitLogMsg = String.format(
                "EntityValidator::validateInactiveEmployeeExists originalEmployeeId: %d successfully validated",
                originalEmployeeId
        );

        // When
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> underTest.validateInactiveEmployeeExists(originalEmployeeId)
        );

        // Then
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
        final Employee existingEmployeeWithoutCustomers = employeeRepository.save(TestDataUtil.createEmployeeA());
        final Long employeeId = existingEmployeeWithoutCustomers.getId();
        final String expectedEntryLogMsg = String.format(
                "EntityValidator::validateEmployeeHasCustomers employeeId: %d",
                employeeId
        );

        // When
        final boolean hasCustomers = underTest.validateEmployeeHasCustomers(employeeId);
        final String expectedExitLogMsg = String.format(
                "EntityValidator::validateEmployeeHasCustomers employeeId: %d hasCustomers: %s",
                employeeId,
                hasCustomers
        );

        // Then
        assertFalse(hasCustomers);
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
    void itShouldReturnTrueWhenEmployeeHasCustomers() {
        // Given
        final Employee employee = TestDataUtil.createEmployeeA();
        final Employee existingEmployee = employeeRepository.save(employee);
        final Long employeeId = existingEmployee.getId();
        final String expectedEntryLogMsg = String.format(
                "EntityValidator::validateEmployeeHasCustomers employeeId: %d",
                employeeId
        );
        final Customer customerB = TestDataUtil.createCustomerB(existingEmployee);
        existingEmployee.getCustomers().add(customerB);
        employeeRepository.save(existingEmployee);

        // When
        final boolean hasCustomers = underTest.validateEmployeeHasCustomers(employeeId);
        final String expectedExitLogMsg = String.format(
                "EntityValidator::validateEmployeeHasCustomers employeeId: %d hasCustomers: %s",
                employeeId,
                hasCustomers
        );

        // Then
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
