package edu.yacoubi.crm.service.impl;

import edu.yacoubi.crm.TestDataUtil;
import edu.yacoubi.crm.model.Customer;
import edu.yacoubi.crm.model.Employee;
import edu.yacoubi.crm.model.Note;
import edu.yacoubi.crm.repository.NoteRepository;
import edu.yacoubi.crm.service.ICustomerService;
import edu.yacoubi.crm.service.IEmployeeService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class NoteServiceImplIntegrationTest {

    @Autowired
    private NoteRepository noteRepository;

    @Autowired
    private NoteServiceImpl underTest;

    @Autowired
    private ICustomerService customerService;

    @Autowired
    private IEmployeeService employeeService;

    @BeforeEach
    public void setUp() {
    }

    @Test
    @Transactional
    public void itShouldCreateNoteForCustomer() {
        // Given
        Employee employee = TestDataUtil.createEmployeeA();
        Employee savedEmployee = employeeService.createEmployee(employee); // Gespeicherten Employee zurückgeben

        Customer customer = TestDataUtil.createCustomerA(savedEmployee); // Den gespeicherten Employee verwenden
        Customer savedCustomer = customerService.createCustomer(customer);

        Note note = TestDataUtil.createNoteA(savedCustomer);

        // When
        Note savedNote = underTest.createNoteForCustomer(note, savedCustomer.getId());

        // Then
        assertNotNull(savedNote);
        assertEquals(savedCustomer, savedNote.getCustomer());
        assertEquals(note.getContent(), savedNote.getContent());
    }

    @Test
    public void itShouldFindNoteById() {
        // Given
        Employee employee = TestDataUtil.createEmployeeA();
        Employee savedEmployee = employeeService.createEmployee(employee); // Gespeicherten Employee zurückgeben
        Customer customer = TestDataUtil.createCustomerA(savedEmployee); // Den gespeicherten Employee verwenden
        Customer savedCustomer = customerService.createCustomer(customer);
        Long noteId = 1L;
        Note note = TestDataUtil.createNoteA(savedCustomer);
        note.setId(noteId);
        noteRepository.save(note);

        // When
        Optional<Note> foundNote = underTest.getNoteById(noteId);

        // Then
        assertTrue(foundNote.isPresent());
        assertEquals(noteId, foundNote.get().getId());
    }

    @Test
    public void itShouldUpdateNote() {
        // Given
        Employee employee = TestDataUtil.createEmployeeA();
        Employee savedEmployee = employeeService.createEmployee(employee); // Gespeicherten Employee zurückgeben
        Customer customer = TestDataUtil.createCustomerA(savedEmployee); // Den gespeicherten Employee verwenden
        Customer savedCustomer = customerService.createCustomer(customer);
        Long noteId = 1L;
        Note note = TestDataUtil.createNoteA(savedCustomer);
        note.setId(noteId);
        noteRepository.save(note);

        Note updatedNote = TestDataUtil.createNoteB(savedCustomer);
        updatedNote.setContent("Updated content");

        // When
        Note savedUpdatedNote = underTest.updateNote(noteId, updatedNote);

        // Then
        assertNotNull(savedUpdatedNote);
        assertEquals(updatedNote.getContent(), savedUpdatedNote.getContent());
    }


    @Test
    public void itShouldDeleteNote() {
        // Given
        Employee employee = TestDataUtil.createEmployeeA();
        Employee savedEmployee = employeeService.createEmployee(employee); // Gespeicherten Employee zurückgeben
        Customer customer = TestDataUtil.createCustomerA(savedEmployee); // Den gespeicherten Employee verwenden
        Customer savedCustomer = customerService.createCustomer(customer);
        Long noteId = 1L;
        Note note = TestDataUtil.createNoteA(savedCustomer);
        note.setId(noteId);
        noteRepository.save(note);

        // When
        underTest.deleteNote(noteId);

        // Then
        Optional<Note> deletedNote = noteRepository.findById(noteId);
        assertFalse(deletedNote.isPresent());
    }

    @Test
    @Transactional
    public void itShouldGetNotesByCustomerId() {
        // Given
        Employee employee = TestDataUtil.createEmployeeA();
        Employee savedEmployee = employeeService.createEmployee(employee); // Gespeicherten Employee zurückgeben

        Customer customer = TestDataUtil.createCustomerA(savedEmployee); // Den gespeicherten Employee verwenden
        Customer savedCustomer = customerService.createCustomer(customer);

        Note noteA = TestDataUtil.createNoteA(savedCustomer);
        Note noteB = TestDataUtil.createNoteB(savedCustomer);
        Note noteC = TestDataUtil.createNoteC(savedCustomer);

        List<Note> savedNotes = noteRepository.saveAll(List.of(noteA, noteB, noteC));

        // When
        List<Note> notesByCustomerId = underTest.getNotesByCustomerId(savedCustomer.getId());

        // Then
        assertEquals(3, notesByCustomerId.size());
        savedNotes.forEach(
                note -> assertTrue(notesByCustomerId.contains(note))
        );
    }

    @Test
    public void itShouldThrowWhenDeleteNoteDoesNotExist() {
        // Given
        Long nonExistingNoteId = -1L; // für 1L funktioniert nicht TODO

        // When
        assertThrows(
                RuntimeException.class,
                () -> underTest.deleteNote(nonExistingNoteId)
        );
    }
}
