package edu.yacoubi.crm.service;

import edu.yacoubi.crm.dto.note.NotePatchDTO;
import edu.yacoubi.crm.model.Note;

import java.util.List;
import java.util.Optional;

public interface INoteService {
    Note createNote(Note note);

    Optional<Note> getNoteById(Long noteId);

    Note updateNote(Long noteId, Note note);

    void deleteNote(Long noteId);

    List<Note> getNotesByCustomerId(Long customerId);

    void partialUpdateNote(Long noteId, NotePatchDTO notePatchDTO);
}
