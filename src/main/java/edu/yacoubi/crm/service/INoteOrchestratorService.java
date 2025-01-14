package edu.yacoubi.crm.service;

import edu.yacoubi.crm.model.Note;

/**
 * @brief Service interface for orchestrating notes.
 *
 * This interface defines operations related to notes and customers.
 */
public interface INoteOrchestratorService {

    /**
     * @brief Creates a note and assigns it to a customer.
     *
     * @param note The note to be created.
     * @param customerId ID of the customer to whom the note will be assigned.
     * @return The created note.
     */
    Note createNoteForCustomer(Note note, Long customerId);
}
