package edu.yacoubi.crm.service;

import edu.yacoubi.crm.model.Note;

public interface INoteOrchestratorService {
    Note createNoteForCustomer(Note note, Long customerId);
}
