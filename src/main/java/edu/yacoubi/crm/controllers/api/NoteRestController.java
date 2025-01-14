package edu.yacoubi.crm.controllers.api;

import edu.yacoubi.crm.dto.APIResponse;
import edu.yacoubi.crm.dto.note.NotePatchDTO;
import edu.yacoubi.crm.dto.note.NoteRequestDTO;
import edu.yacoubi.crm.dto.note.NoteResponseDTO;
import edu.yacoubi.crm.model.Note;
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
    private final INoteOrchestratorService noteOrchestratorService;

    @Operation(
            summary = "Get note by ID",
            description = "Retrieve a note by its unique ID."
    )
    @GetMapping("/{id}")
    public ResponseEntity<APIResponse<NoteResponseDTO>> getNoteById(@PathVariable Long customerId) {
        log.info("::getNoteById started with: customerId {}", customerId);

        Note existingNote = noteService.getNoteById(customerId).get();

        NoteResponseDTO noteResponseDTO = TransformerUtil.transform(
                EntityTransformer.noteToNoteResponseDto,
                existingNote
        );

        APIResponse<NoteResponseDTO> response = APIResponse.<NoteResponseDTO>builder()
                .status("success")
                .statusCode(HttpStatus.OK.value())
                .data(noteResponseDTO)
                .build();

        log.info("::getNoteById completed successfully with: response {}", jsonAsString(response));
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Create a new note",
            description = "This operation creates a new note for a specified customer in the CRM system."
    )
    @PostMapping
    public ResponseEntity<APIResponse<NoteResponseDTO>> createNoteForCustomer(
            @Valid @RequestBody NoteRequestDTO noteRequestDTO,
            @RequestParam Long customerId) {
        log.info("::createNoteForCustomer started with: noteRequestDTO {}, customerId {}", noteRequestDTO, customerId);

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

        log.info("::createNoteForCustomer completed successfully with: response {}", jsonAsString(response));
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(
            summary = "Full update note",
            description = "Full update of an existing note by its unique ID."
    )
    @PutMapping("/{id}")
    public ResponseEntity<APIResponse<NoteResponseDTO>> updateNote(
            @PathVariable Long noteId,
            @Valid @RequestBody NoteRequestDTO noteRequestDTO) {
        log.info("::updateNote started with: noteId {}, noteRequestDTO {}", noteId, noteRequestDTO);

        Note existingNote = noteService.getNoteById(noteId).get();

        Note noteRequest = TransformerUtil.transform(EntityTransformer.noteRequestDtoToNote, noteRequestDTO);

        noteRequest.setId(noteId);
        noteRequest.setCustomer(existingNote.getCustomer());

        Note updatedNote = noteService.updateNote(noteId, noteRequest);

        NoteResponseDTO noteResponseDTO = TransformerUtil.transform(
                EntityTransformer.noteToNoteResponseDto,
                updatedNote
        );

        APIResponse<NoteResponseDTO> response = APIResponse.<NoteResponseDTO>builder()
                .status("success")
                .statusCode(HttpStatus.OK.value())
                .data(noteResponseDTO)
                .build();

        log.info("::updateNote completed successfully with: response {}", jsonAsString(response));
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Partial update of note",
            description = "Partial update of an existing note by their unique ID."
    )
    @PatchMapping("/{id}")
    public ResponseEntity<APIResponse<NoteResponseDTO>> patchNote(
            @PathVariable Long noteId,
            @Valid @RequestBody NotePatchDTO notePatchDTO) {
        log.info("::patchNote started with: noteId {}, notePatchDTO {}", noteId, jsonAsString(notePatchDTO));

        noteService.partialUpdateNote(noteId, notePatchDTO);

        Note updatedNote = noteService.getNoteById(noteId).get();

        NoteResponseDTO noteResponseDTO = TransformerUtil.transform(
                EntityTransformer.noteToNoteResponseDto,
                updatedNote
        );

        APIResponse<NoteResponseDTO> response = APIResponse.<NoteResponseDTO>builder()
                .status("success")
                .statusCode(HttpStatus.OK.value())
                .data(noteResponseDTO)
                .build();

        log.info("::patchNote completed successfully with: response {}", jsonAsString(response));
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Delete note",
            description = "Delete an existing note by its unique ID."
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<APIResponse<Void>> deleteNoteById(@PathVariable Long customerId) {
        log.info("::deleteNoteById started with: customerId {}", customerId);

        noteService.deleteNote(customerId);

        APIResponse<Void> response = APIResponse.<Void>builder()
                .status("success")
                .statusCode(HttpStatus.NO_CONTENT.value())
                .build();

        log.info("::deleteNote completed successfully with: response {}");
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(response);
    }
}

