package edu.yacoubi.crm.controllers.api;

import edu.yacoubi.crm.dto.NoteDTO;
import edu.yacoubi.crm.exception.ResourceNotFoundException;
import edu.yacoubi.crm.mapper.IMapper;
import edu.yacoubi.crm.model.Note;
import edu.yacoubi.crm.service.ICustomerService;
import edu.yacoubi.crm.service.INoteService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notes")
@RequiredArgsConstructor
public class NoteRestController {
    private final INoteService noteService;
    private final ICustomerService customerService;
    private final IMapper<Note, NoteDTO> mapper;

    @Operation(summary = "Get note by ID", description = "Retrieve a note by its unique ID.")
    @GetMapping("/{id}")
    public ResponseEntity<NoteDTO> getNoteById(@PathVariable Long id) {
        Note existingNote = noteService
                .getNoteById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Note not found with ID: " + id));
        NoteDTO noteDTO = mapper.mapTo(existingNote);
        return ResponseEntity.ok(noteDTO);
    }


    @Operation(summary = "Create a new note", description = "This operation creates a new note for a specified customer in the CRM system.")
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

    @Operation(summary = "Update note", description = "Update the details of an existing note by its unique ID.")
    @PutMapping("/{id}")
    public ResponseEntity<NoteDTO> updateNote(
            @PathVariable Long id,
            @RequestBody NoteDTO noteDTO) {

        Note existingNote = noteService.getNoteById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Note not found with ID: " + id));
        Note noteRequest = mapper.mapFrom(noteDTO);
        noteRequest.setId(id);
        noteRequest.setCustomer(existingNote.getCustomer());

        Note updatedNote = noteService.updateNote(id, noteRequest);

        NoteDTO updatedNoteDTO = mapper.mapTo(updatedNote);

        return ResponseEntity.ok(updatedNoteDTO);
    }

    @Operation(summary = "Delete note", description = "Delete an existing note by its unique ID.")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNoteById(@PathVariable Long id) {
        noteService.deleteNote(id);
        return ResponseEntity.noContent().build();
    }
}

