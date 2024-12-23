package edu.yacoubi.crm.service;

import edu.yacoubi.crm.dto.note.NotePatchDTO;
import edu.yacoubi.crm.model.Note;

import java.util.List;
import java.util.Optional;

public interface INoteService {
    Note createNote(Note note);

    Optional<Note> getNoteById(Long id);

    Note updateNote(Long id, Note note);

    void deleteNote(Long id);

    List<Note> getNotesByCustomerId(Long customerId);

    void partialUpdateNote(Long id, NotePatchDTO notePatchDTO);
}
