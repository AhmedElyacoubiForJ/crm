package edu.yacoubi.crm.service.impl;

import edu.yacoubi.crm.model.Customer;
import edu.yacoubi.crm.model.Note;
import edu.yacoubi.crm.service.ICustomerService;
import edu.yacoubi.crm.service.INoteOrchestratorService;
import edu.yacoubi.crm.service.INoteService;
import edu.yacoubi.crm.service.validation.EntityValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class NoteOrchestratorServiceImpl implements INoteOrchestratorService {

    private final ICustomerService customerService;
    private final INoteService noteService;
    private final EntityValidator entityValidator;

    @Override
    public Note createNoteForCustomer(Note note, Long customerId) {
        log.info("NoteOrchestrator::createNoteForCustomer execution start: note {}, customerId {}", note, customerId);

        // Validierung des Kunden
        entityValidator.validateCustomerExists(customerId);

        // Abruf des Kunden ohne Ausnahme
        Customer customer = customerService.getCustomerById(customerId).get();

        // Setzen des Kunden und Delegation an NoteService
        note.setCustomer(customer);
        Note savedNote = noteService.createNote(note);

        log.info("NoteOrchestrator::createNoteForCustomer execution end");
        return savedNote;
    }
}

