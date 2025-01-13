package edu.yacoubi.crm.service.impl;

import edu.yacoubi.crm.model.Customer;
import edu.yacoubi.crm.model.Note;
import edu.yacoubi.crm.service.ICustomerService;
import edu.yacoubi.crm.service.INoteOrchestratorService;
import edu.yacoubi.crm.service.INoteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class NoteOrchestratorServiceImpl implements INoteOrchestratorService {
    private final ICustomerService customerService;
    private final INoteService noteService;

    @Override
    public Note createNoteForCustomer(Note note, Long customerId) {
        log.info("::createNoteForCustomer started with note: {}, customerId: {}", note, customerId);

        // Abruf des Kunden mit get(), da die Validierung im CustomerService stattfindet
        Customer customer = customerService.getCustomerById(customerId).get();

        // Setzen des Kunden und Delegation an NoteService
        note.setCustomer(customer);
        Note savedNote = noteService.createNote(note);

        log.info("::createNoteForCustomer completed successfully with note ID: {}", savedNote.getId());
        return savedNote;
    }
}

