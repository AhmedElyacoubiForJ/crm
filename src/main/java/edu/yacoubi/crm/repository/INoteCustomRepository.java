package edu.yacoubi.crm.repository;

import edu.yacoubi.crm.dto.note.NotePatchDTO;

public interface INoteCustomRepository {
    void partialUpdateNote(Long id, NotePatchDTO notePatchDTO);
}
