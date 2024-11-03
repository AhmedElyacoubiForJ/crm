package edu.yacoubi.crm.service.impl;

import edu.yacoubi.crm.model.Customer;
import edu.yacoubi.crm.model.Note;
import edu.yacoubi.crm.repository.NoteRepository;
import edu.yacoubi.crm.service.ICustomerService;
import edu.yacoubi.crm.service.INoteService;
import edu.yacoubi.crm.exception.ResourceNotFoundException;
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
    private final ICustomerService customerService;


    @Override
    public Note createNoteForCustomer(Note note, Long customerId) {
        log.info("NoteServiceImpl::createNoteForCustomer execution start: note {}, customerId {}", note, customerId);

        // Da die Ausnahme bereits geworfen wird, wenn der Kunde nicht existiert
        Customer customer = customerService.getCustomerById(customerId).get();
        note.setCustomer(customer);

        log.info("NoteServiceImpl::createNoteForCustomer execution end");
        return noteRepository.save(note);
    }

    @Override
    public Optional<Note> getNoteById(Long id) {
        log.info("NoteServiceImpl::getNoteById id {}", id);

        return noteRepository.findById(id);
    }

    @Override
    public Note updateNote(Long id, Note note) {
        log.info("NoteServiceImpl::updateNote execution start: id {}, note {}", id, note);

        validateNoteId(id);

        log.info("NoteServiceImpl::updateNote execution end");
        return noteRepository.save(note);
    }

    @Override
    public void deleteNote(Long id) {
        log.info("NoteServiceImpl::deleteNote execution start: id {}", id);

        validateNoteId(id);

        log.info("NoteServiceImpl::deleteNote execution end");
        noteRepository.deleteById(id);
    }

    @Override
    public List<Note> getNotesByCustomerId(Long customerId) {
        log.info("NoteServiceImpl::getNotesByCustomerId execution start: customerId {}", customerId);

        // Da die Ausnahme bereits geworfen wird, wenn der Kunde nicht existiert
        customerService.ensureCustomerExists(customerId);

        log.info("NoteServiceImpl::getNotesByCustomerId execution end");
        return noteRepository.findAllByCustomerId(customerId);
    }

    private void validateNoteId(Long id) {
        if (!noteRepository.existsById(id)) {
            throw new ResourceNotFoundException("Note not found with ID: " + id);
        }
    }
}
