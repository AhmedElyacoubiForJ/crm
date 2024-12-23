package edu.yacoubi.crm.service.impl;

import edu.yacoubi.crm.dto.note.NotePatchDTO;
import edu.yacoubi.crm.model.Customer;
import edu.yacoubi.crm.model.Note;
import edu.yacoubi.crm.repository.INoteCustomRepository;
import edu.yacoubi.crm.repository.NoteRepository;
import edu.yacoubi.crm.service.validation.EntityValidator;
import edu.yacoubi.crm.service.ICustomerService;
import edu.yacoubi.crm.service.INoteService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class NoteServiceImpl implements INoteService{
    private final NoteRepository noteRepository;
    private final INoteCustomRepository noteCustomRepository;
    private final ICustomerService customerService;
    private final EntityValidator entityValidator;


    @Override
    public Note createNoteForCustomer(Note note, Long customerId) {
        log.info("NoteServiceImpl::createNoteForCustomer execution start: note {}, customerId {}", note, customerId);

        // Da die Ausnahme bereits geworfen wird, wenn der Kunde nicht existiert
        Customer customer = customerService.getCustomerById(customerId).get();
        note.setCustomer(customer);

        Note savedNote = noteRepository.save(note);

        log.info("NoteServiceImpl::createNoteForCustomer execution end");
        return savedNote;
    }

    @Override
    public Optional<Note> getNoteById(Long id) {
        log.info("NoteServiceImpl::getNoteById execution start: id {}", id);

        Optional<Note> optionalNote = noteRepository.findById(id);

        log.info("NoteServiceImpl::getNoteById execution end");
        return optionalNote;
    }

    @Override
    public Note updateNote(Long id, Note note) {
        log.info("NoteServiceImpl::updateNote execution start: id {}, note {}", id, note);

        entityValidator.validateNoteExists(id);
        //validateNoteId(id);
        Note updatedNote = noteRepository.save(note);

        log.info("NoteServiceImpl::updateNote execution end");
        return updatedNote;
    }

    @Override
    public void deleteNote(Long id) {
        log.info("NoteServiceImpl::deleteNote execution start: id {}", id);

        entityValidator.validateNoteExists(id);

        noteRepository.deleteById(id);

        log.info("NoteServiceImpl::deleteNote execution end");
    }

    @Override
    public List<Note> getNotesByCustomerId(Long customerId) {
        log.info("NoteServiceImpl::getNotesByCustomerId execution start: customerId {}", customerId);

        // Da die Ausnahme bereits geworfen wird, wenn der Kunde nicht existiert
        customerService.ensureCustomerExists(customerId);

        List<Note> notesByCustomerId = noteRepository.findAllByCustomerId(customerId);

        log.info("NoteServiceImpl::getNotesByCustomerId execution end");
        return notesByCustomerId;
    }

    @Override
    @Transactional
    public void partialUpdateNote(Long id, NotePatchDTO notePatchDTO) {
        log.info("NoteServiceImpl::partialUpdateNote execution start: id {}, notePatchDTO {}", id, notePatchDTO);

        // Validate parameters first
//        if (id == null || notePatchDTO == null || id < 0 ) {
//            log.warn("Note id or NotePatchDTO must not be null and id must be a positive number");
//            throw new IllegalArgumentException("Note id or NotePatchDTO must not be null and id must be a positive number");
//        }

        entityValidator.validateNoteExists(id);

        // delegate
        noteCustomRepository.partialUpdateNote(id, notePatchDTO);

        log.info("NoteServiceImpl::partialUpdateNote execution end");
    }
}
