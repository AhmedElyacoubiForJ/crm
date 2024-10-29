package edu.yacoubi.crm.service.impl;

import edu.yacoubi.crm.TestDataUtil;
import edu.yacoubi.crm.model.Customer;
import edu.yacoubi.crm.model.Note;
import edu.yacoubi.crm.repository.NoteRepository;
import edu.yacoubi.crm.service.ICustomerService;
import edu.yacoubi.crm.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class NoteServiceImplUnitTest {

    @Mock
    private NoteRepository noteRepository;

    @Mock
    private ICustomerService customerService;

    @InjectMocks
    private NoteServiceImpl underTest;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void itShouldCreateNoteForCustomer() {
        // Given
        Long customerId = 1L;
        Note note = TestDataUtil.createNoteA(null);
        Customer customer = TestDataUtil.createCustomerA(null);
        when(customerService.getCustomerById(customerId)).thenReturn(Optional.of(customer));
        when(noteRepository.save(any(Note.class))).thenReturn(note);

        // When
        Note savedNote = underTest.createNoteForCustomer(note, customerId);

        // Then
        assertNotNull(savedNote);
        assertEquals(customer, savedNote.getCustomer());
        verify(noteRepository, times(1)).save(note);
    }

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

        // When
        Note updatedNote = underTest.updateNote(noteId, note);

        // Then
        assertNotNull(updatedNote);
        verify(noteRepository, times(1)).save(note);
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
    }

    @Test
    public void itShouldGetNotesByCustomerId() {
        // Given
        Long customerId = 1L;
        List<Note> notes = List.of(TestDataUtil.createNoteA(null), TestDataUtil.createNoteB(null));
        when(noteRepository.findAllByCustomerId(customerId)).thenReturn(notes);
        when(customerService.getCustomerById(customerId)).thenReturn(Optional.of(new Customer())); // Mock für existierende ID hinzufügen

        // When
        List<Note> foundNotes = underTest.getNotesByCustomerId(customerId);

        // Then
        assertNotNull(foundNotes);
        assertEquals(2, foundNotes.size());
        verify(noteRepository, times(1)).findAllByCustomerId(customerId);
    }

    @Test
    public void itShouldThrowWhenDeleteNoteDoesNotExist() {
        // Given
        Long noteId = 1L;
        when(noteRepository.existsById(noteId)).thenReturn(false);

        // When & Then
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class, () -> underTest.deleteNote(noteId));
        assertEquals("Note not found with ID: " + noteId, exception.getMessage());
    }
}
