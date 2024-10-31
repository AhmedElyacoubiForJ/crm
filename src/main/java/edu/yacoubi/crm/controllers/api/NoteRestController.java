package edu.yacoubi.crm.controllers.api;

import edu.yacoubi.crm.dto.NoteRequestDTO;
import edu.yacoubi.crm.dto.NoteResponseDTO;
import edu.yacoubi.crm.exception.ResourceNotFoundException;
import edu.yacoubi.crm.model.Note;
import edu.yacoubi.crm.service.ICustomerService;
import edu.yacoubi.crm.service.INoteService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static edu.yacoubi.crm.util.ValueMapper.convertToEntity;
import static edu.yacoubi.crm.util.ValueMapper.convertToResponseDTO;

@RestController
@RequestMapping("/api/notes")
@RequiredArgsConstructor
@Slf4j
public class NoteRestController {
    private final INoteService noteService;
    private final ICustomerService customerService;

    @Operation(
            summary = "Get note by ID",
            description = "Retrieve a note by its unique ID."
    )
    @GetMapping("/{id}")
    public ResponseEntity<NoteResponseDTO> getNoteById(@PathVariable Long id) {
        log.info("NoteRestController::getNoteById request id {}", id);

        Note existingNote = noteService.getNoteById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Note not found with ID: " + id));
        NoteResponseDTO noteResponseDTO = convertToResponseDTO(existingNote);

        log.info("NoteRestController::getNoteById response {}", noteResponseDTO);
        return ResponseEntity.ok(noteResponseDTO);
    }


    @Operation(
            summary = "Create a new note",
            description = "This operation creates a new note for a specified customer in the CRM system."
    )
    @PostMapping
    public ResponseEntity<NoteResponseDTO> createNote(
            @RequestBody NoteRequestDTO noteRequestDTO,
            @RequestParam Long customerId) {
        log.info("NoteRestController::createNote request id {}, note {}", customerId, noteRequestDTO);

        customerService.getCustomerById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with ID: " + customerId));
        Note noteRequest = convertToEntity(noteRequestDTO);
        Note createdNote = noteService.createNoteForCustomer(noteRequest, customerId);
        NoteResponseDTO noteResponseDTO = convertToResponseDTO(createdNote);

        log.info("NoteRestController::createNote response {}", noteResponseDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(noteResponseDTO);
    }

    @Operation(
            summary = "Update note",
            description = "Update the details of an existing note by its unique ID."
    )
    @PutMapping("/{id}")
    public ResponseEntity<NoteResponseDTO> updateNote(
            @PathVariable Long id,
            @RequestBody NoteRequestDTO noteRequestDTO) {
        log.info("NoteRestController::updateNote request id {}, note {}", id, noteRequestDTO);

        Note existingNote = noteService.getNoteById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Note not found with ID: " + id));

        Note noteRequest = convertToEntity(noteRequestDTO);
        noteRequest.setId(id);
        noteRequest.setCustomer(existingNote.getCustomer());

        Note updatedNote = noteService.updateNote(id, noteRequest);
        NoteResponseDTO noteResponseDTO = convertToResponseDTO(updatedNote);

        log.info("NoteRestController::updateNote response {}", noteResponseDTO);
        return ResponseEntity.ok(noteResponseDTO);
    }

    @Operation(
            summary = "Delete note",
            description = "Delete an existing note by its unique ID."
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNoteById(@PathVariable Long id) {
        log.info("NoteRestController::deleteNoteById request id {}", id);

        noteService.deleteNote(id);

        log.info("NoteRestController::deleteNote response");
        return ResponseEntity.noContent().build();
    }
}

