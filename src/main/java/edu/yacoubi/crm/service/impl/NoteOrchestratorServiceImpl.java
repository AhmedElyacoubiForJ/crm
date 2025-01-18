package edu.yacoubi.crm.service.impl;

import edu.yacoubi.crm.model.Customer;
import edu.yacoubi.crm.model.Note;
import edu.yacoubi.crm.service.ICustomerService;
import edu.yacoubi.crm.service.INoteOrchestratorService;
import edu.yacoubi.crm.service.INoteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Implementation of the Note Orchestrator Service.
 *
 * <p>This class implements the logic for orchestrating note creation for customers, including
 * validating the customer and delegating the note creation to the note service.</p>
 *
 * @author A. El Yacoubi
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class NoteOrchestratorServiceImpl implements INoteOrchestratorService {
    private final ICustomerService customerService;
    private final INoteService noteService;

    /**
     * Creates a note for a customer.
     *
     * @param note       the note to be created
     * @param customerId the ID of the customer for whom the note is being created
     * @return the created note
     */
    @Override
    public Note createNoteForCustomer(final Note note, final Long customerId) {
        if (log.isInfoEnabled()) {
            log.info("::createNoteForCustomer started with: note {}, customerId: {}", note, customerId);
        }

        // Abruf des Kunden mit get(), da die Validierung im CustomerService stattfindet
        final Customer customer = customerService.getCustomerById(customerId).get();

        // Setzen des Kunden und Delegation an NoteService
        note.setCustomer(customer);
        final Note savedNote = noteService.createNote(note);

        if (log.isInfoEnabled()) {
            log.info("::createNoteForCustomer completed successfully with: noteId {}", savedNote.getId());
        }
        return savedNote;
    }
}
