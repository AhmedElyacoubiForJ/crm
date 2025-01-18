package edu.yacoubi.crm.service.impl;

import edu.yacoubi.crm.dto.note.NotePatchDTO;
import edu.yacoubi.crm.model.Note;
import edu.yacoubi.crm.repository.INoteCustomRepository;
import edu.yacoubi.crm.repository.NoteRepository;
import edu.yacoubi.crm.service.INoteService;
import edu.yacoubi.crm.service.validation.EntityValidator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Implementation of the Note Service.
 *
 * <p>This class implements the logic for managing notes, including
 * creating, updating, and deleting note records.</p>
 *
 * @author A. El Yacoubi
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class NoteServiceImpl implements INoteService {
    private final NoteRepository noteRepository;
    private final INoteCustomRepository noteCustomRepository;
    private final EntityValidator entityValidator;

    /**
     * Creates a new note.
     *
     * @param note the note to be created
     * @return the created note
     * @throws IllegalArgumentException if the note or its customer is invalid
     */
    @Override
    public Note createNote(final Note note) {
        if (log.isInfoEnabled()) {
            log.info("::createNote started with: note {}", note);
        }

        if (note == null || note.getCustomer() == null) {
            throw new IllegalArgumentException("Note is invalid");
        }

        note.setId(null);
        final Note savedNote = noteRepository.save(note);

        if (log.isInfoEnabled()) {
            log.info("::createNote completed successfully");
        }
        return savedNote;
    }

    /**
     * Retrieves a note by its ID.
     *
     * @param noteId the ID of the note to retrieve
     * @return an Optional containing the found note, or an empty Optional if no note was found
     */
    @Override
    public Optional<Note> getNoteById(final Long noteId) {
        if (log.isInfoEnabled()) {
            log.info("::getNoteById started with: noteId {}", noteId);
        }

        entityValidator.validateNoteExists(noteId);

        final Optional<Note> optionalNote = noteRepository.findById(noteId);

        if (log.isInfoEnabled()) {
            log.info("::getNoteById completed successfully");
        }
        return optionalNote;
    }

    /**
     * Fully updates a note.
     *
     * @param noteId the ID of the note to update
     * @param note   the updated note details
     * @return the updated note
     */
    @Override
    public Note updateNote(final Long noteId, final Note note) {
        if (log.isInfoEnabled()) {
            log.info("::updateNote started with: noteId {}, note {}", noteId, note);
        }

        entityValidator.validateNoteExists(noteId);
        note.setId(noteId);

        final Note updatedNote = noteRepository.save(note);

        if (log.isInfoEnabled()) {
            log.info("::updateNote completed successfully");
        }
        return updatedNote;
    }

    /**
     * Deletes a note by its ID.
     *
     * @param noteId the ID of the note to delete
     */
    @Override
    public void deleteNote(final Long noteId) {
        if (log.isInfoEnabled()) {
            log.info("::deleteNote started with: noteId {}", noteId);
        }

        entityValidator.validateNoteExists(noteId);

        noteRepository.deleteById(noteId);

        if (log.isInfoEnabled()) {
            log.info("::deleteNote completed successfully");
        }
    }

    /**
     * Retrieves a list of notes by the customer's ID.
     *
     * @param customerId the ID of the customer whose notes to retrieve
     * @return a list of notes assigned to the customer
     */
    @Override
    public List<Note> getNotesByCustomerId(final Long customerId) {
        if (log.isInfoEnabled()) {
            log.info("::getNotesByCustomerId started with: customerId {}", customerId);
        }

        // Since the exception is already thrown if the customer does not exist
        entityValidator.validateCustomerExists(customerId);

        final List<Note> notesByCustomerId = noteRepository.findAllByCustomerId(customerId);

        if (log.isInfoEnabled()) {
            log.info("::getNotesByCustomerId completed successfully");
        }
        return notesByCustomerId;
    }

    /**
     * Partially updates a note's details.
     *
     * @param noteId       the ID of the note to update
     * @param notePatchDTO the partial update details
     */
    @Override
    @Transactional
    public void partialUpdateNote(final Long noteId, final NotePatchDTO notePatchDTO) {
        if (log.isInfoEnabled()) {
            log.info("::partialUpdateNote started with: noteId {}, notePatchDTO {}", noteId, notePatchDTO);
        }

        entityValidator.validateNoteExists(noteId);

        // delegate
        noteCustomRepository.partialUpdateNote(noteId, notePatchDTO);

        if (log.isInfoEnabled()) {
            log.info("::partialUpdateNote completed successfully");
        }
    }
}
