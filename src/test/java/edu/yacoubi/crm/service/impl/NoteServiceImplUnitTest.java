package edu.yacoubi.crm.service.impl;

import ch.qos.logback.classic.Logger;
import edu.yacoubi.crm.util.TestAppender;
import edu.yacoubi.crm.util.TestDataUtil;
import edu.yacoubi.crm.exception.ResourceNotFoundException;
import edu.yacoubi.crm.model.Note;
import edu.yacoubi.crm.repository.NoteRepository;
import edu.yacoubi.crm.service.validation.EntityValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class NoteServiceImplUnitTest {
    @Mock
    private NoteRepository noteRepository;

    @Mock
    private EntityValidator entityValidator;

    @InjectMocks
    private NoteServiceImpl underTest;

    private static TestAppender testAppender;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        Logger logger = (Logger) LoggerFactory.getLogger(NoteServiceImpl.class);
        testAppender = new TestAppender();
        testAppender.start();
        logger.addAppender(testAppender);
    }

//    @Test
//    public void itShouldCreateNoteForCustomer() {
//        // Given
//        Long customerId = 1L;
//        Note note = TestDataUtil.createNoteA(null);
//        Customer customer = TestDataUtil.createCustomerA(null);
//        when(customerService.getCustomerById(customerId)).thenReturn(Optional.of(customer));
//        when(noteRepository.save(any(Note.class))).thenReturn(note);
//
//        // When
//        Note savedNote = underTest.createNoteForCustomer(note, customerId);
//
//        // Then
//        assertNotNull(savedNote);
//        assertEquals(customer, savedNote.getCustomer());
//        verify(noteRepository, times(1)).save(note);
//    }

    @Test
    public void itShouldFindNoteById() {
        // Given
        Long noteId = 1L;
        Note note = TestDataUtil.createNoteA(null);
        note.setId(1L);
        when(noteRepository.findById(noteId)).thenReturn(Optional.of(note));

        // When
        Optional<Note> foundNote = underTest.getNoteById(noteId);

        // Then
        assertTrue(foundNote.isPresent());
        assertEquals(noteId, foundNote.get().getId());
        verify(noteRepository, times(1)).findById(noteId);
    }

    @Test
    public void itShouldUpdateNote() {
        // Given
        Long noteId = 1L;
        Note note = TestDataUtil.createNoteA(null);
        note.setId(noteId);
        when(noteRepository.existsById(noteId)).thenReturn(true);
        when(noteRepository.save(any(Note.class))).thenReturn(note);
        doNothing().when(entityValidator).validateNoteExists(anyLong());

        // When
        Note updatedNote = underTest.updateNote(noteId, note);

        // Then
        assertNotNull(updatedNote);
        verify(noteRepository, times(1)).save(note);
        assertTrue(testAppender.contains(
                "NoteServiceImpl::updateNote execution start: noteId 1, note " + note, "INFO"
        ));
        assertTrue(testAppender.contains("NoteServiceImpl::updateNote execution end", "INFO"));
    }

    @Test
    public void itShouldDeleteNote() {
        // Given
        Long noteId = 1L;
        when(noteRepository.existsById(noteId)).thenReturn(true);

        // When
        underTest.deleteNote(noteId);

        // Then
        verify(noteRepository, times(1)).deleteById(noteId);
        assertTrue(testAppender.contains(String.format("NoteServiceImpl::deleteNote execution start: noteId %d", noteId), "INFO"));
        assertTrue(testAppender.contains("NoteServiceImpl::deleteNote execution end", "INFO"));
    }

    @Test
    public void itShouldGetNotesByCustomerId() {
        // Given
        Long customerId = 1L;
        List<Note> notes = List.of(TestDataUtil.createNoteA(null), TestDataUtil.createNoteB(null));
        when(noteRepository.findAllByCustomerId(customerId)).thenReturn(notes);
        doNothing().when(entityValidator).validateNoteExists(anyLong()); // Mock für existierende ID

        // When
        List<Note> foundNotes = underTest.getNotesByCustomerId(customerId);

        // Then
        assertNotNull(foundNotes);
        assertEquals(2, foundNotes.size());
        verify(noteRepository, times(1)).findAllByCustomerId(customerId);
    }

    @Test
    public void itShouldThrowExceptionWhenDeleteNoteByIdIfNoteDoesNotExist() {
        // Given
        Long noteId = 1L;
        doThrow(new ResourceNotFoundException("Note not found with ID: " + noteId))
                .when(entityValidator).validateNoteExists(noteId);

        // When
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            underTest.deleteNote(noteId);
        });

        verify(entityValidator, times(1)).validateNoteExists(noteId);
        // Then verify the exception message
        assertEquals("Note not found with ID: " + noteId, exception.getMessage());
        assertTrue(testAppender.contains(String.format("NoteServiceImpl::deleteNote execution start: noteId %d", noteId), "INFO"));
        assertFalse(testAppender.contains("NoteServiceImpl::deleteNote execution end: Resource not found", "ERROR"));
    }
}
