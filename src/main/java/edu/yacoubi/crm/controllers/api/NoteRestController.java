package edu.yacoubi.crm.controllers.api;

import edu.yacoubi.crm.dto.NoteRequestDTO;
import edu.yacoubi.crm.dto.NoteResponseDTO;
import edu.yacoubi.crm.exception.ResourceNotFoundException;
import edu.yacoubi.crm.model.Note;
import edu.yacoubi.crm.service.ICustomerService;
import edu.yacoubi.crm.service.INoteService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static edu.yacoubi.crm.controllers.Mapper.convertToEntity;
import static edu.yacoubi.crm.controllers.Mapper.convertToResponseDTO;

@RestController
@RequestMapping("/api/notes")
@RequiredArgsConstructor
public class NoteRestController {
    private final INoteService noteService;
    private final ICustomerService customerService;

    @Operation(summary = "Get note by ID", description = "Retrieve a note by its unique ID.")
    @GetMapping("/{id}")
    public ResponseEntity<NoteResponseDTO> getNoteById(@PathVariable Long id) {
        Note existingNote = noteService
                .getNoteById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Note not found with ID: " + id));
        return ResponseEntity.ok(convertToResponseDTO(existingNote));
    }


    @Operation(summary = "Create a new note", description = "This operation creates a new note for a specified customer in the CRM system.")
    @PostMapping
    public ResponseEntity<NoteResponseDTO> createNote(
            @RequestBody NoteRequestDTO noteRequestDTO,
            @RequestParam Long customerId) {
        customerService.getCustomerById(customerId).orElseThrow(
                () -> new ResourceNotFoundException("Customer not found with ID: " + customerId)
        );

        Note noteRequest = convertToEntity(noteRequestDTO);
        Note createdNote = noteService.createNoteForCustomer(noteRequest, customerId);

        return ResponseEntity.status(HttpStatus.CREATED).body(convertToResponseDTO(createdNote));
    }

    @Operation(summary = "Update note", description = "Update the details of an existing note by its unique ID.")
    @PutMapping("/{id}")
    public ResponseEntity<NoteResponseDTO> updateNote(
            @PathVariable Long id,
            @RequestBody NoteRequestDTO noteRequestDTO) {

        Note existingNote = noteService.getNoteById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Note not found with ID: " + id));

        Note noteRequest = convertToEntity(noteRequestDTO);
        noteRequest.setId(id);
        noteRequest.setCustomer(existingNote.getCustomer());

        Note updatedNote = noteService.updateNote(id, noteRequest);

        return ResponseEntity.ok(convertToResponseDTO(updatedNote));
    }

    @Operation(summary = "Delete note", description = "Delete an existing note by its unique ID.")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNoteById(@PathVariable Long id) {
        noteService.deleteNote(id);
        return ResponseEntity.noContent().build();
    }
}

