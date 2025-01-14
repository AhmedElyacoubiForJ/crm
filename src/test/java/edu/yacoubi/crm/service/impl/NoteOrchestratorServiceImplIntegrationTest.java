package edu.yacoubi.crm.service.impl;

import ch.qos.logback.classic.Logger;
import edu.yacoubi.crm.model.Customer;
import edu.yacoubi.crm.model.Note;
import edu.yacoubi.crm.service.ICustomerService;
import edu.yacoubi.crm.service.IEmployeeService;
import edu.yacoubi.crm.service.INoteService;
import edu.yacoubi.crm.util.TestAppender;
import edu.yacoubi.crm.util.TestDataUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.annotation.DirtiesContext;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class NoteOrchestratorServiceImplIntegrationTest {
    // Assert supplied failure message
    private static final String INFO_SUPPLIED_MSG = "Info message should be: %s";

    private static TestAppender testAppender;

    @Autowired
    private ApplicationContext context;
    @Autowired
    private ICustomerService customerService;
    @Autowired
    private INoteService noteService;

    @Autowired
    private NoteOrchestratorServiceImpl underTest;

    private Note noteA;
    private Long customerId;

    @BeforeEach
    void setUp() {
        final Logger logger = (Logger) LoggerFactory.getLogger(NoteOrchestratorServiceImpl.class);
        testAppender = new TestAppender();
        testAppender.start();
        logger.addAppender(testAppender);

        IEmployeeService employeeService = context.getBean(IEmployeeService.class);

        Customer customerA = TestDataUtil.createCustomerB(
                employeeService.createEmployee(TestDataUtil.createEmployeeA())
        );

        Customer existingCustomer = customerService.createCustomer(customerA);
        customerId = existingCustomer.getId();

        noteA = TestDataUtil.createNoteA(existingCustomer);
        noteA.setId(null); // Set ID to null to simulate creation of a new note
    }

    @AfterEach
    void tearDown() {
        final Logger logger = (Logger) LoggerFactory.getLogger(NoteOrchestratorServiceImpl.class);
        logger.detachAppender(testAppender);
        testAppender.stop();
    }

    @Test
    void itShouldCreateNoteForCustomer() {
        // Given
        final String expectedEntryLogMsg = String.format(
                "::createNoteForCustomer started with: note %s, customerId: %d",
                noteA, customerId
        );

        // When
        Note createdNote = underTest.createNoteForCustomer(noteA, customerId);
        final String expectedExitLogMsg = String.format(
                "::createNoteForCustomer completed successfully with: noteId %d",
                createdNote.getId()
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

        // Additional validation to ensure the note was created correctly
        assertTrue(createdNote.getId() != null);
        assertTrue(createdNote.getCustomer().getId().equals(customerId));
    }
}
