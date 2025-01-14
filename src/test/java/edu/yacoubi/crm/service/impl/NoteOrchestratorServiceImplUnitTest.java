package edu.yacoubi.crm.service.impl;

import ch.qos.logback.classic.Logger;
import edu.yacoubi.crm.model.Customer;
import edu.yacoubi.crm.model.Note;
import edu.yacoubi.crm.service.ICustomerService;
import edu.yacoubi.crm.service.INoteService;
import edu.yacoubi.crm.util.TestAppender;
import edu.yacoubi.crm.util.TestDataUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.LoggerFactory;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class NoteOrchestratorServiceImplUnitTest {
    // Assert supplied failure message
    private static final String INFO_SUPPLIED_MSG = "Info message should be: %s";
    private static TestAppender testAppender;

    @Mock
    private ICustomerService customerService;
    @Mock
    private INoteService noteService;

    @InjectMocks
    private NoteOrchestratorServiceImpl underTest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        final Logger logger = (Logger) LoggerFactory.getLogger(NoteOrchestratorServiceImpl.class);
        testAppender = new TestAppender();
        testAppender.start();
        logger.addAppender(testAppender);
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
        final Long customerId = 301L;
        final Customer customerA = TestDataUtil.createCustomerB(TestDataUtil.createEmployeeA());
        customerA.setId(customerId);
        when(customerService.getCustomerById(customerId)).thenReturn(Optional.of(customerA));


        final Note noteA = TestDataUtil.createNoteA(customerA);
        final String expectedEntryLogMsg = String.format(
                "::createNoteForCustomer started with: note %s, customerId: %d",
                noteA, customerId
        );

        // Set note ID to null to simulate creation of a new note
        noteA.setId(null);
        final Long noteId = 11L;
        when(noteService.createNote(noteA)).thenAnswer(invocation -> {
            Note createdNote = invocation.getArgument(0);
            createdNote.setId(noteId);
            return createdNote;
        });
        final String expectedExitLogMsg = String.format(
                "::createNoteForCustomer completed successfully with: noteId %d",
                noteId
        );

        // When
        underTest.createNoteForCustomer(noteA, customerId);

        // Then
        verify(customerService, times(1)).getCustomerById(customerId);
        verify(noteService, times(1)).createNote(noteA);
        verifyNoMoreInteractions(customerService, noteService);

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
