package edu.yacoubi.crm.service.validation;

import ch.qos.logback.classic.Logger;
import edu.yacoubi.crm.exception.ResourceNotFoundException;
import edu.yacoubi.crm.model.Customer;
import edu.yacoubi.crm.model.Employee;
import edu.yacoubi.crm.model.InactiveEmployee;
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

        // When
        underTest.validateEmployeeExists(employeeId);

        // Then
        assertTrue(
                testAppender.contains(
                        String.format(
                                "EntityValidator::validateEmployeeExists employeeId: %d",
                                employeeId
                        ),
                        "INFO"
                ),
                "should indicate the validation entry point employee exists"
        );
        assertTrue(
                testAppender.contains(
                        String.format(
                                "EntityValidator::validateEmployeeExists employeeId: %d successfully validated",
                                employeeId
                        ),
                        "INFO"
                ),
                "should indicate the validation exit point employee exists"
        );
    }

    @Test
    void itShouldThrowWhenEmployeeDoesNotExist() {
        // Given
        final Long employeeId = 999L;
        final String errorMessage = "Employee not found with ID: " + employeeId;

        // When
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class, () -> underTest.validateEmployeeExists(employeeId)
        );

        // Then
        assertEquals(errorMessage, exception.getMessage());
        assertTrue(
                testAppender.contains(
                        String.format(
                                "EntityValidator::validateEmployeeExists employeeId: %d",
                                employeeId
                        ),
                        "INFO"
                ),
                "should indicate the validation entry point employee exists"
        );
        assertTrue(
                testAppender.contains(
                        String.format(
                                "EntityValidator::validateEmployeeExists error: %s",
                                errorMessage
                        ),
                        "ERROR"
                ),
                "should indicate error employee does not exist"
        );
        assertFalse(
                testAppender.contains(
                        String.format(
                                "EntityValidator::validateEmployeeExists employeeId: %d successfully validated",
                                employeeId
                        ),
                        "INFO"
                ),
                "should indicate the validation exit point employee exists"
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

        // When
        underTest.validateNoteExists(noteId);

        // Then
        assertTrue(
                testAppender.contains(
                        String.format(
                                "EntityValidator::validateNoteExists id: %d",
                                noteId
                        ),
                        "INFO"
                ),
                "should indicate the validation entry point note exists"
        );
        assertTrue(
                testAppender.contains(
                        String.format(
                                "EntityValidator::validateNoteExists id: %d successfully validated",
                                noteId
                        ),
                        "INFO"
                ),
                "should indicate the validation exit point note exists"
        );
    }

    @Test
    void itShouldThrowWhenNoteDoesNotExist() {
        // Given
        final Long noteId = 999L;
        final String errorMessage = "Note not found with ID: " + noteId;

        // When
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class, () -> underTest.validateNoteExists(noteId)
        );

        // Then
        assertEquals(errorMessage, exception.getMessage());
        assertTrue(
                testAppender.contains(
                        String.format(
                                "EntityValidator::validateNoteExists id: %d",
                                noteId
                        ),
                        "INFO"
                ),
                "should indicate the validation entry point note exists"
        );
        assertTrue(
                testAppender.contains(
                        String.format(
                                "EntityValidator::validateNoteExists error: %s",
                                errorMessage
                        ),
                        "ERROR"
                ),
                "should indicate error note does not exist"
        );
        assertFalse(
                testAppender.contains(
                        String.format(
                                "EntityValidator::validateNoteExists id: %d successfully validated",
                                noteId
                        ),
                        "INFO"
                ),
                "should indicate the validation exit point note exists"
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

        // When
        underTest.validateCustomerExists(customerId);

        // Then
        assertTrue(
                testAppender.contains(
                        String.format(
                                "EntityValidator::validateCustomerExists id: %d",
                                customerId
                        ),
                        "INFO"
                ),
                "should indicate the validation entry point for customer exists"
        );
        assertTrue(
                testAppender.contains(
                        String.format(
                                "EntityValidator::validateCustomerExists id: %d successfully validated",
                                customerId
                        ),
                        "INFO"
                ),
                "should indicate the validation exit point for customer exists"
        );
    }

    @Test
    void itShouldThrowWhenCustomerDoesNotExist() {
        // Given
        final Long customerId = 999L;
        final String errorMessage = "Customer not found with ID: " + customerId;

        // When
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class, () -> underTest.validateCustomerExists(customerId)
        );

        // Then
        assertEquals(errorMessage, exception.getMessage());
        assertTrue(
                testAppender.contains(
                        String.format(
                                "EntityValidator::validateCustomerExists id: %d",
                                customerId
                        ),
                        "INFO"
                ),
                "should indicate the validation entry point for customer exists"
        );
        assertTrue(
                testAppender.contains(
                        String.format(
                                "EntityValidator::validateCustomerExists error: %s",
                                errorMessage
                        ),
                        "ERROR"
                ),
                "should indicate error customer does not exist"
        );
        assertFalse(
                testAppender.contains(
                        String.format(
                                "EntityValidator::validateCustomerExists id: %d successfully validated",
                                customerId
                        ),
                        "INFO"
                ),
                "should indicate the validation exit point for customer exists"
        );
    }

    @Test
    void itShouldValidateInactiveEmployeeExits() {
        // Given
        final Long originalEmployeeId = 101L;
        final InactiveEmployee existingInactiveEmployee = inactiveEmployeeRepository.save(
                TestDataUtil.createInactiveEmployeeA(originalEmployeeId)
        );

        // When
        underTest.validateInactiveEmployeeExists(originalEmployeeId);

        // Then
        assertTrue(
                testAppender.contains(
                        String.format(
                                "EntityValidator::validateInactiveEmployeeExists originalEmployeeId: %d",
                                originalEmployeeId
                        ),
                        "INFO"
                ),
                "Should indicate the validation entry point for inactiveEmployee exists"
        );
        assertTrue(testAppender.contains(
                        String.format(
                                "EntityValidator::validateInactiveEmployeeExists originalEmployeeId: %d successfully validated",
                                originalEmployeeId
                        ),
                        "INFO"
                ),
                "Should indicate the validation exit point for inactiveEmployee exists"
        );
    }

    @Test
    void itShouldThrowWhenInactiveEmployeeDoesNotExit() {
        // Given
        final Long originalEmployeeId = 999L;
        final String errorMessage = "Inactive employee not found with ID: " + originalEmployeeId;

        // When
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class, () -> underTest.validateInactiveEmployeeExists(originalEmployeeId)
        );

        // Then
        assertEquals(errorMessage, exception.getMessage());
        assertTrue(
                testAppender.contains(
                        String.format(
                                "EntityValidator::validateInactiveEmployeeExists originalEmployeeId: %d",
                                originalEmployeeId
                        ),
                        "INFO"
                ),
                "Should indicate the validation entry point for inactiveEmployee exists"
        );
        assertTrue(
                testAppender.contains(
                        String.format("EntityValidator::validateInactiveEmployeeExists error: %s", errorMessage),
                        "ERROR"
                ),
                "Should indicate error inactive employee does not exist"
        );
        assertFalse(
                testAppender.contains(
                        String.format(
                                "EntityValidator::validateInactiveEmployeeExists originalEmployeeId: %d successfully validated",
                                originalEmployeeId
                        ),
                        "INFO"
                ),
                "Should indicate the validation exit point for inactiveEmployee exists"
        );
    }

    @Test
    void itShouldReturnFalseWhenEmployeeHasNoCustomers() {
        // Given
        final Employee existingEmployeeWithoutCustomers = employeeRepository.save(TestDataUtil.createEmployeeA());
        final Long employeeId = existingEmployeeWithoutCustomers.getId();

        // When
        final boolean hasCustomers = underTest.hasCustomers(employeeId);

        // Then
        assertFalse(hasCustomers);
        assertTrue(
                testAppender.contains(
                        String.format(
                                "EntityValidator::hasCustomers employeeId: %d",
                                employeeId
                        ),
                        "INFO"
                ),
                "Should indicate the validation entry point for hasCustomers"
        );
        assertTrue(
                testAppender.contains(
                        String.format(
                                "EntityValidator::hasCustomers employeeId: %d hasCustomers: %s",
                                employeeId,
                                hasCustomers
                        ),
                        "INFO"
                ),
                "Should indicate the validation exit point for hasCustomers"
        );
    }

    @Test
    void itShouldReturnTrueWhenEmployeeHasCustomers() {
        // Given
        final Employee employee = TestDataUtil.createEmployeeA();
        final Employee existingEmployee = employeeRepository.save(employee);
        final Long employeeId = existingEmployee.getId();
        final Customer customerB = TestDataUtil.createCustomerB(existingEmployee);
        existingEmployee.getCustomers().add(customerB);
        employeeRepository.save(existingEmployee);

        // When
        final boolean hasCustomers = underTest.hasCustomers(employeeId);

        // Then
        assertTrue(
                testAppender.contains(
                        String.format(
                                "EntityValidator::hasCustomers employeeId: %d",
                                employeeId
                        ),
                        "INFO"
                ),
                "Should indicate the validation entry point for hasCustomers"
        );
        assertTrue(
                testAppender.contains(
                        String.format(
                                "EntityValidator::hasCustomers employeeId: %d hasCustomers: %s",
                                employeeId,
                                hasCustomers
                        ),
                        "INFO"
                ),
                "Should indicate the validation exit point for hasCustomers"
        );
    }
}
