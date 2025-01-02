package edu.yacoubi.crm.controllers.api;

import edu.yacoubi.crm.dto.APIResponse;
import edu.yacoubi.crm.dto.note.NotePatchDTO;
import edu.yacoubi.crm.dto.note.NoteRequestDTO;
import edu.yacoubi.crm.dto.note.NoteResponseDTO;
import edu.yacoubi.crm.model.Note;
import edu.yacoubi.crm.service.ICustomerService;
import edu.yacoubi.crm.service.INoteOrchestratorService;
import edu.yacoubi.crm.service.INoteService;
import edu.yacoubi.crm.util.EntityTransformer;
import edu.yacoubi.crm.util.TransformerUtil;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static edu.yacoubi.crm.util.ValueMapper.jsonAsString;

@RestController
@RequestMapping("/api/notes")
@RequiredArgsConstructor
@Slf4j
public class NoteRestController {
    private final INoteService noteService;
    private final ICustomerService customerService;
    private final INoteOrchestratorService noteOrchestratorService;

    @Operation(
            summary = "Get note by ID",
            description = "Retrieve a note by its unique ID."
    )
    @GetMapping("/{id}")
    public ResponseEntity<APIResponse<NoteResponseDTO>> getNoteById(@PathVariable Long id) {
        log.info("NoteRestController::getNoteById request id {}", id);

        Note existingNote = noteService.getNoteById(id).get();

        NoteResponseDTO noteResponseDTO = TransformerUtil.transform(
                EntityTransformer.noteToNoteResponseDto,
                existingNote
        );

        APIResponse<NoteResponseDTO> response = APIResponse.<NoteResponseDTO>builder()
                .status("success")
                .statusCode(HttpStatus.OK.value())
                .data(noteResponseDTO)
                .build();

        log.info("NoteRestController::getNoteById response {}", jsonAsString(response));
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Create a new note",
            description = "This operation creates a new note for a specified customer in the CRM system."
    )
    @PostMapping
    public ResponseEntity<APIResponse<NoteResponseDTO>> createNote(
            @Valid @RequestBody NoteRequestDTO noteRequestDTO,
            @RequestParam Long customerId) {
        log.info("NoteRestController::createNote request id {}, note {}", customerId, noteRequestDTO);

        Note noteRequest = TransformerUtil.transform(
                EntityTransformer.noteRequestDtoToNote,
                noteRequestDTO
        );

        Note createdNote = noteOrchestratorService.createNoteForCustomer(noteRequest, customerId);

        NoteResponseDTO noteResponseDTO = TransformerUtil.transform(
                EntityTransformer.noteToNoteResponseDto,
                createdNote
        );

        APIResponse<NoteResponseDTO> response = APIResponse.<NoteResponseDTO>builder()
                .status("success")
                .statusCode(HttpStatus.CREATED.value())
                .data(noteResponseDTO)
                .build();

        log.info("NoteRestController::createNote response {}", jsonAsString(response));
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(
            summary = "Full update note",
            description = "Full update of an existing note by its unique ID."
    )
    @PutMapping("/{id}")
    public ResponseEntity<APIResponse<NoteResponseDTO>> updateNote(
            @PathVariable Long id,
            @Valid @RequestBody NoteRequestDTO noteRequestDTO) {
        log.info("NoteRestController::updateNote request id {}, note {}", id, noteRequestDTO);

        Note existingNote = noteService.getNoteById(id).get();

        Note noteRequest = TransformerUtil.transform(EntityTransformer.noteRequestDtoToNote, noteRequestDTO);

        noteRequest.setId(id);
        noteRequest.setCustomer(existingNote.getCustomer());

        Note updatedNote = noteService.updateNote(id, noteRequest);

        NoteResponseDTO noteResponseDTO = TransformerUtil.transform(
                EntityTransformer.noteToNoteResponseDto,
                updatedNote
        );

        APIResponse<NoteResponseDTO> response = APIResponse.<NoteResponseDTO>builder()
                .status("success")
                .statusCode(HttpStatus.OK.value())
                .data(noteResponseDTO)
                .build();

        log.info("NoteRestController::updateNote response {}", jsonAsString(response));
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Partial update of note",
            description = "Partial update of an existing note by their unique ID."
    )
    @PatchMapping("/{id}")
    public ResponseEntity<APIResponse<NoteResponseDTO>> patchNote(
            @PathVariable Long id,
            @Valid @RequestBody NotePatchDTO notePatchDTO) {
        log.info("NoteRestController::patchNote request id {}, note {}", id, jsonAsString(notePatchDTO));

        noteService.partialUpdateNote(id, notePatchDTO);

        Note updatedNote = noteService.getNoteById(id).get();

        NoteResponseDTO noteResponseDTO = TransformerUtil.transform(
                EntityTransformer.noteToNoteResponseDto,
                updatedNote
        );

        APIResponse<NoteResponseDTO> response = APIResponse.<NoteResponseDTO>builder()
                .status("success")
                .statusCode(HttpStatus.OK.value())
                .data(noteResponseDTO)
                .build();

        log.info("NoteRestController::patchNote response {}", jsonAsString(response));
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Delete note",
            description = "Delete an existing note by its unique ID."
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<APIResponse<Void>> deleteNoteById(@PathVariable Long id) {
        log.info("NoteRestController::deleteNoteById request id {}", id);

        noteService.deleteNote(id);

        APIResponse<Void> response = APIResponse.<Void>builder()
                .status("success")
                .statusCode(HttpStatus.NO_CONTENT.value())
                .build();

        log.info("NoteRestController::deleteNote");
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(response);
    }
}

