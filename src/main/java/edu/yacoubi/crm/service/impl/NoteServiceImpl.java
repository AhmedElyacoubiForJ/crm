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

@Service
@RequiredArgsConstructor
@Slf4j
public class NoteServiceImpl implements INoteService {
    private final NoteRepository noteRepository;
    private final INoteCustomRepository noteCustomRepository;
    private final EntityValidator entityValidator;

    @Override
    public Note createNote(Note note) {
        log.info("NoteServiceImpl::createNote execution start: note {}", note);

        if (note == null || note.getCustomer() == null) {
            throw new IllegalArgumentException("Note is invalid");
        }

        Note savedNote = noteRepository.save(note);

        log.info("NoteServiceImpl::createNote execution end");
        return savedNote;
    }

    @Override
    public Optional<Note> getNoteById(Long noteId) {
        log.info("NoteServiceImpl::getNoteById execution start: noteId {}", noteId);

        entityValidator.validateNoteExists(noteId);

        Optional<Note> optionalNote = noteRepository.findById(noteId);

        log.info("NoteServiceImpl::getNoteById execution end");
        return optionalNote;
    }

    @Override
    public Note updateNote(Long noteId, Note note) {
        log.info("NoteServiceImpl::updateNote execution start: noteId {}, note {}", noteId, note);

        entityValidator.validateNoteExists(noteId);
        note.setId(noteId);

        Note updatedNote = noteRepository.save(note);

        log.info("NoteServiceImpl::updateNote execution end");
        return updatedNote;
    }

    @Override
    public void deleteNote(Long noteId) {
        log.info("NoteServiceImpl::deleteNote execution start: noteId {}", noteId);

        entityValidator.validateNoteExists(noteId);

        noteRepository.deleteById(noteId);

        log.info("NoteServiceImpl::deleteNote execution end");
    }

    @Override
    public List<Note> getNotesByCustomerId(Long customerId) {
        log.info("NoteServiceImpl::getNotesByCustomerId execution start: customerId {}", customerId);

        // Da die Ausnahme bereits geworfen wird, wenn der Kunde nicht existiert
        entityValidator.validateCustomerExists(customerId);

        List<Note> notesByCustomerId = noteRepository.findAllByCustomerId(customerId);

        log.info("NoteServiceImpl::getNotesByCustomerId execution end");
        return notesByCustomerId;
    }

    @Override
    @Transactional
    public void partialUpdateNote(Long noteId, NotePatchDTO notePatchDTO) {
        log.info("NoteServiceImpl::partialUpdateNote execution start: noteId {}, notePatchDTO {}", noteId, notePatchDTO);

        entityValidator.validateNoteExists(noteId);

        // delegate
        noteCustomRepository.partialUpdateNote(noteId, notePatchDTO);

        log.info("NoteServiceImpl::partialUpdateNote execution end");
    }
}
