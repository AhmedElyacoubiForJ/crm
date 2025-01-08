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

    private Employee employeeA;

    @BeforeEach
    public void setUp() {
        Logger logger = (Logger) LoggerFactory.getLogger(EntityValidator.class);
        testAppender = new TestAppender();
        testAppender.start();
        logger.addAppender(testAppender);
        employeeA = TestDataUtil.createEmployeeA();
    }

    @Test
    void itShouldValidateEmployeeExists() {
        // Given
        Employee savedEmployee = employeeRepository.save(employeeA);

        // When
        underTest.validateEmployeeExists(savedEmployee.getId());

        // Then
        assertTrue(testAppender.contains(
                String.format("EntityValidator::validateEmployeeExists employeeId: %d", savedEmployee.getId()),
                "INFO"
        ));
        assertTrue(testAppender.contains(
                String.format("EntityValidator::validateEmployeeExists employeeId: %d successfully validated", savedEmployee.getId()),
                "INFO"
        ));
    }

    @Test
    void itShouldThrowWhenEmployeeDoesNotExist() {
        // Given
        Long employeeId = 999L;
        String errorMessage = "Employee not found with ID: " + employeeId;

        // When & Then
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class, () -> underTest.validateEmployeeExists(employeeId)
        );

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
        Note savedNote = noteRepository
                .save(TestDataUtil.createNoteA(customerRepository.save(TestDataUtil.createCustomerB(employeeRepository.save(employeeA)))));

        // When
        underTest.validateNoteExists(savedNote.getId());

        // Then
        assertTrue(testAppender.contains(
                String.format("EntityValidator::validateNoteExists id: %d", savedNote.getId()),
                "INFO"
        ));
        assertFalse(testAppender.contains(
                String.format("EntityValidator::validateNoteExists id: %d not found", savedNote.getId()),
                "ERROR"
        ));
        assertTrue(testAppender.contains(
                String.format("EntityValidator::validateNoteExists id: %d successfully validated", savedNote.getId()),
                "INFO"
        ));
    }

    @Test
    void itShouldThrowWhenNoteDoesNotExist() {
        // Given
        Long noteId = 999L;
        String errorMessage = "Note not found with ID: " + noteId;

        // When & Then
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class, () -> underTest.validateNoteExists(noteId)
        );

        assertEquals(errorMessage, exception.getMessage());
        assertTrue(testAppender.contains(
                String.format("EntityValidator::validateNoteExists id: %d", noteId),
                "INFO"
        ));
        assertTrue(testAppender.contains(
                String.format("EntityValidator::validateNoteExists id: %d", noteId),
                "INFO"
        ));
        assertFalse(testAppender.contains(
                String.format("EntityValidator::validateNoteExists id: %d successfully validated", noteId),
                "INFO"
        ));
    }

    @Test
    void itShouldValidateCustomerExists() {
        // Given
        Customer savedCustomer = customerRepository
                .save(TestDataUtil.createCustomerA(employeeRepository.save(employeeA)));

        // When
        underTest.validateCustomerExists(savedCustomer.getId());

        // Then
        assertTrue(testAppender.contains(
                String.format("EntityValidator::validateCustomerExists id: %d", savedCustomer.getId()),
                "INFO"
        ));
        assertFalse(testAppender.contains(
                String.format("EntityValidator::validateCustomerExists id: %d not found", savedCustomer.getId()),
                "ERROR"
        ));
        assertTrue(testAppender.contains(
                String.format("EntityValidator::validateCustomerExists id: %d successfully validated", savedCustomer.getId()),
                "INFO"
        ));
    }

    @Test
    void itShouldThrowWhenCustomerDoesNotExist() {
        // Given
        Long customerId = 999L;
        String errorMessage = "Customer not found with ID: " + customerId;

        // When & Then
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class, () -> underTest.validateCustomerExists(customerId)
        );

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
        final InactiveEmployee inactiveEmployee = InactiveEmployee.builder()
                .firstName("John")
                .lastName("Doe")
                .email("johndoe@example.com")
                .department("IT")
                .originalEmployeeId(101L)
                .build();
        final InactiveEmployee existingInactiveEmployee = inactiveEmployeeRepository.save(inactiveEmployee);
        final Long originalEmployeeId = existingInactiveEmployee.getOriginalEmployeeId();

        // When
        underTest.validateInactiveEmployeeExists(originalEmployeeId);

        // Then
        // verify logs
        assertTrue(
                testAppender.contains(
                        String.format("EntityValidator::validateInactiveEmployeeExists originalEmployeeId: %d", originalEmployeeId),
                        "INFO"
                ),
                "Should indicate the entry point for inactiveEmployee exists"
        );
        assertTrue(testAppender.contains(
                        String.format("EntityValidator::validateInactiveEmployeeExists originalEmployeeId: %d successfully validated", originalEmployeeId),
                        "INFO"
                ),
                "Should indicate the exit point for inactiveEmployee successfully validated"
        );
    }

    @Test
    void itShouldThrowWhenInactiveEmployeeDoesNotExit() {
        // Given
        Long originalEmployeeId = 999L;
        String errorMessage = "Inactive employee not found with ID: " + originalEmployeeId;

        // When & Then
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class, () -> underTest.validateInactiveEmployeeExists(originalEmployeeId)
        );

        assertEquals(errorMessage, exception.getMessage());
        assertTrue(testAppender.contains(
                String.format("EntityValidator::validateInactiveEmployeeExists originalEmployeeId: %d", originalEmployeeId),
                "INFO"
        ));
        assertTrue(testAppender.contains(
                String.format("EntityValidator::validateInactiveEmployeeExists error: %s", errorMessage),
                "ERROR"
        ));
        assertFalse(testAppender.contains(
                String.format(
                        "EntityValidator::validateInactiveEmployeeExists originalEmployeeId: %d successfully validated",
                        originalEmployeeId
                ),
                "INFO"
        ));
    }

    @Test
    void itShouldReturnFalseWhenEmployeeHasNoCustomers() {
        // Given
        Employee employeeWithoutCustomers = employeeRepository.save(TestDataUtil.createEmployeeA());

        // When
        boolean hasCustomers = underTest.hasCustomers(employeeWithoutCustomers.getId());

        // Then
        assertFalse(hasCustomers);
        assertTrue(
                testAppender.contains(
                        String.format(
                                "EntityValidator::hasCustomers employeeId: %d",
                                employeeWithoutCustomers.getId()
                        ),
                        "INFO"
                ),
                "Should indicate the entry point for hasCustomers"
        );
        assertTrue(
                testAppender.contains(
                        String.format(
                                "EntityValidator::hasCustomers employeeId: %d hasCustomers: %s", employeeWithoutCustomers.getId(), hasCustomers)
                        ,
                        "INFO"
                ),
                "Should indicate the exit point for hasCustomers"
        );
    }

    @Test
    void itShouldReturnTrueWhenEmployeeHasCustomers() {
        // Given
        final Employee employee = TestDataUtil.createEmployeeA();
        final Employee existingEmployee = employeeRepository.save(employee);
        final Customer customerB = TestDataUtil.createCustomerB(existingEmployee);
        existingEmployee.getCustomers().add(customerB);
        Employee existingEmployeeWithCustomers = employeeRepository.save(existingEmployee);

        // When
        boolean hasCustomers = underTest.hasCustomers(existingEmployeeWithCustomers.getId());

        // Then
        assertTrue(hasCustomers);
        assertTrue(
                testAppender.contains(
                        String.format(
                                "EntityValidator::hasCustomers employeeId: %d", existingEmployeeWithCustomers.getId())
                        ,
                        "INFO"
                ),
                "Should indicate the entry point for hasCustomers"
        );
        assertTrue(
                testAppender.contains(
                        String.format(
                                "EntityValidator::hasCustomers employeeId: %d hasCustomers: %s", existingEmployeeWithCustomers.getId(), hasCustomers)
                        ,
                        "INFO"
                ),
                "Should indicate the exit point for hasCustomers"
        );
    }
}
