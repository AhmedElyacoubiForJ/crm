package edu.yacoubi.crm.controllers.api;

import edu.yacoubi.crm.dto.APIResponse;
import edu.yacoubi.crm.dto.note.NotePatchDTO;
import edu.yacoubi.crm.dto.note.NoteRequestDTO;
import edu.yacoubi.crm.dto.note.NoteResponseDTO;
import edu.yacoubi.crm.model.Note;
import edu.yacoubi.crm.service.INoteOrchestratorService;
import edu.yacoubi.crm.service.INoteService;
import edu.yacoubi.crm.util.ApiResponseHelper;
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

/**
 * REST controller for managing note resources in the CRM system.
 *
 * @author A. El Yacoubi
 */
@RestController
@RequestMapping("/api/notes")
@RequiredArgsConstructor
@Slf4j
public class NoteRestController {

    /**
     * Success message.
     */
    public static final String SUCCESS = "Success";

    /**
     * Completion message for a successful operation.
     */
    public static final String COMPLETED = "Operation completed";

    /**
     * Service for note operations.
     */
    private final INoteService noteService;

    /**
     * Orchestrator service for creating note for customer.
     */
    private final INoteOrchestratorService noteOrchestratorService;

    /**
     * Retrieve a note by its unique ID.
     *
     * @param customerId the unique ID of the note to retrieve
     * @return the retrieved note details wrapped in an APIResponse
     */
    @Operation(
            summary = "Get note by ID",
            description = "Retrieve a note by its unique ID."
    )
    @GetMapping("/{customerId}")
    public ResponseEntity<APIResponse<NoteResponseDTO>> getNoteById(
            final @PathVariable Long customerId) {
        if (log.isInfoEnabled()) {
            log.info("::getNoteById started with: customerId {}", customerId);
        }

        final Note existingNote = noteService.getNoteById(customerId).get();

        final NoteResponseDTO noteResponseDTO = TransformerUtil.transform(
                EntityTransformer.noteToNoteResponseDto,
                existingNote
        );

        final APIResponse<NoteResponseDTO> response = ApiResponseHelper.getDTOAPIResponse(
                COMPLETED, SUCCESS, HttpStatus.OK, noteResponseDTO
        );

        if (log.isInfoEnabled()) {
            log.info("::getNoteById completed successfully with: response {}", jsonAsString(response));
        }
        return ResponseEntity.ok(response);
    }

    /**
     * This operation creates a new note for a specified customer in the CRM system.
     *
     * @param noteRequestDTO the note request data transfer object containing the details of the note to be created
     * @param customerId     the unique ID of the customer to whom the note will be assigned
     * @return the created note details wrapped in an APIResponse
     */
    @Operation(
            summary = "Create a new note",
            description = "This operation creates a new note for a specified customer in the CRM system."
    )
    @PostMapping
    public ResponseEntity<APIResponse<NoteResponseDTO>> createNoteForCustomer(
            final @Valid @RequestBody NoteRequestDTO noteRequestDTO,
            final @RequestParam Long customerId) {
        if (log.isInfoEnabled()) {
            log.info("::createNoteForCustomer started with: noteRequestDTO {}, customerId {}",
                    jsonAsString(noteRequestDTO), customerId);
        }

        final Note noteRequest = TransformerUtil.transform(
                EntityTransformer.noteRequestDtoToNote,
                noteRequestDTO
        );

        final Note createdNote = noteOrchestratorService.createNoteForCustomer(noteRequest, customerId);

        final NoteResponseDTO noteResponseDTO = TransformerUtil.transform(
                EntityTransformer.noteToNoteResponseDto,
                createdNote
        );

        final APIResponse<NoteResponseDTO> response = ApiResponseHelper.getDTOAPIResponse(
                COMPLETED, SUCCESS, HttpStatus.CREATED, noteResponseDTO
        );

        if (log.isInfoEnabled()) {
            log.info("::createNoteForCustomer completed successfully with: response {}", jsonAsString(response));
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Full update of an existing note by its unique ID.
     *
     * @param noteId         the unique ID of the note to update
     * @param noteRequestDTO the note request data transfer object containing the details of the note to be updated
     * @return the updated note details wrapped in an APIResponse
     */
    @Operation(
            summary = "Full update note",
            description = "Full update of an existing note by its unique ID."
    )
    @PutMapping("/{noteId}")
    public ResponseEntity<APIResponse<NoteResponseDTO>> updateNote(
            final @PathVariable Long noteId,
            final @Valid @RequestBody NoteRequestDTO noteRequestDTO) {
        if (log.isInfoEnabled()) {
            log.info("::updateNote started with: noteId {}, noteRequestDTO {}", noteId, noteRequestDTO);
        }


        Note noteRequest = TransformerUtil.transform(
                EntityTransformer.noteRequestDtoToNote, noteRequestDTO
        );

        // TODO: Auslagerung in den Service-Layer
        final Note existingNote = noteService.getNoteById(noteId).get();
        noteRequest.setId(noteId);
        noteRequest.setCustomer(existingNote.getCustomer());

        final Note updatedNote = noteService.updateNote(noteId, noteRequest);

        final NoteResponseDTO noteResponseDTO = TransformerUtil.transform(
                EntityTransformer.noteToNoteResponseDto,
                updatedNote
        );

        final APIResponse<NoteResponseDTO> response = ApiResponseHelper.getDTOAPIResponse(
                COMPLETED, SUCCESS, HttpStatus.OK, noteResponseDTO
        );

        if (log.isInfoEnabled()) {
            log.info("::updateNote completed successfully with: response {}", jsonAsString(response));
        }
        return ResponseEntity.ok(response);
    }

    /**
     * Partial update of an existing note by their unique ID.
     *
     * @param noteId       the unique ID of the note to update
     * @param notePatchDTO the note patch data transfer object containing the partial updates of the note
     * @return the updated note details wrapped in an APIResponse
     */
    @Operation(
            summary = "Partial update of note",
            description = "Partial update of an existing note by their unique ID."
    )
    @PatchMapping("/{noteId}")
    public ResponseEntity<APIResponse<NoteResponseDTO>> patchNote(
            final @PathVariable Long noteId,
            final @Valid @RequestBody NotePatchDTO notePatchDTO) {
        if (log.isInfoEnabled()) {
            log.info("::patchNote started with: noteId {}, notePatchDTO {}", noteId, jsonAsString(notePatchDTO));
        }

        noteService.partialUpdateNote(noteId, notePatchDTO);

        final Note updatedNote = noteService.getNoteById(noteId).get();

        final NoteResponseDTO noteResponseDTO = TransformerUtil.transform(
                EntityTransformer.noteToNoteResponseDto,
                updatedNote
        );

        final APIResponse<NoteResponseDTO> response = ApiResponseHelper.getDTOAPIResponse(
                COMPLETED, SUCCESS, HttpStatus.OK, noteResponseDTO
        );

        if (log.isInfoEnabled()) {
            log.info("::patchNote completed successfully with: response {}", jsonAsString(response));
        }
        return ResponseEntity.ok(response);
    }

    /**
     * Delete an existing note by its unique ID.
     *
     * @param noteId the unique ID of the note to delete
     * @return a response indicating the success of the deletion
     */
    @Operation(
            summary = "Delete note",
            description = "Delete an existing note by its unique ID."
    )
    @DeleteMapping("/{noteId}")
    public ResponseEntity<APIResponse<Void>> deleteNoteById(
            final @PathVariable Long noteId) {
        if (log.isInfoEnabled()) {
            log.info("::deleteNoteById started with: noteId {}", noteId);
        }

        noteService.deleteNote(noteId);

        final APIResponse<Void> response = ApiResponseHelper.getVoidAPIResponse(
                COMPLETED, SUCCESS, HttpStatus.NO_CONTENT
        );

        if (log.isInfoEnabled()) {
            log.info("::deleteNote completed successfully with: response {}", jsonAsString(response));
        }
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(response);
    }
}

//    /**
//     * Retrieve all notes from the system.
//     *
//     * @return a list of all notes wrapped in an APIResponse
//     */
//    @Operation(
//            summary = "Get all notes",
//            description = "Retrieve all notes from the system."
//    )
//    @GetMapping
//    public ResponseEntity<APIResponse<List<NoteResponseDTO>>> getAllNotes() {
//        if (log.isInfoEnabled()) {
//            log.info("::getAllNotes started");
//        }
//
//        List<NoteResponseDTO> notes = noteService.getAllNotes();
//
//        final APIResponse<List<NoteResponseDTO>> response = ApiResponseHelper.getDTOAPIResponse(
//                COMPLETED, SUCCESS, HttpStatus.OK, notes
//        );
//
//        if (log.isInfoEnabled()) {
//            log.info("::getAllNotes completed successfully with: response {}", jsonAsString(response));
//        }
//        return ResponseEntity.ok(response);
//    }