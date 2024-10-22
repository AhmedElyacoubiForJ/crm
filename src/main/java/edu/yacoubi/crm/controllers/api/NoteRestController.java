package edu.yacoubi.crm.controllers.api;

import edu.yacoubi.crm.dto.NoteDTO;
import edu.yacoubi.crm.exception.ResourceNotFoundException;
import edu.yacoubi.crm.mapper.IMapper;
import edu.yacoubi.crm.model.Customer;
import edu.yacoubi.crm.model.Note;
import edu.yacoubi.crm.service.ICustomerService;
import edu.yacoubi.crm.service.INoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notes")
public class NoteRestController {

    @Autowired
    private INoteService noteService;

    @Autowired
    private ICustomerService customerService;

    @Autowired
    private IMapper<Note, NoteDTO> mapper;

    // Beispiel für einen GET-Endpunkt
    @GetMapping("/{id}")
    public ResponseEntity<NoteDTO> getNoteById(@PathVariable Long id) {
        Note existingNote = noteService
                .getNoteById(id)
                .orElseThrow(
                        () -> new ResourceNotFoundException("Note not found with ID: " + id)
                );
        NoteDTO noteDTO = mapper.mapTo(existingNote);

        return ResponseEntity.ok(noteDTO);
    }

    // Beispiel für einen POST-Endpunkt
    @PostMapping
    public ResponseEntity<NoteDTO> createNote(
            @RequestBody NoteDTO noteDTO,
            @RequestParam Long customerId) {
        // Note in Entity umwandeln
        Note note = mapper.mapFrom(noteDTO);

        // Note für Kunde erstellen
        Note createdNote = noteService.createNoteForCustomer(note, customerId);

        // Erstellte Note in DTO umwandeln
        NoteDTO createdNoteDTO = mapper.mapTo(createdNote);

        return ResponseEntity.status(HttpStatus.CREATED).body(createdNoteDTO);
    }

    // Beispiel für einen PUT-Endpunkt
    @PutMapping("/{id}")
    public ResponseEntity<NoteDTO> updateNote(
            @PathVariable Long id,
            @RequestBody NoteDTO noteDTO) {

        Note existingNote = noteService.getNoteById(id)
                .orElseThrow(
                        () -> new ResourceNotFoundException("Note not found with ID: " + id)
                );
        Note noteRequest = mapper.mapFrom(noteDTO);
        noteRequest.setId(id);
        noteRequest.setCustomer(existingNote.getCustomer());

        Note updatedNote = noteService.updateNote(id, noteRequest);

        NoteDTO updatedNoteDTO = mapper.mapTo(updatedNote);

        return ResponseEntity.ok(updatedNoteDTO);
    }

    // Beispiel für einen DELETE-Endpunkt
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNoteById(@PathVariable Long id) {
        noteService.deleteNote(id);
        return ResponseEntity.noContent().build();
    }
}

