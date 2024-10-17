package edu.yacoubi.crm.service;

import edu.yacoubi.crm.model.Note;
import java.util.Optional;
import java.util.List;

public interface INoteService {

    // Erstellen einer neuen Notiz für einen spezifischen Kunden
    Note createNoteForCustomer(Note note, Long customerId);

    // Finden einer Notiz nach ID
    Optional<Note> getNoteById(Long id);

    // Aktualisieren einer bestehenden Notiz
    Note updateNote(Long id, Note note);

    // Löschen einer Notiz
    void deleteNote(Long id);

    // Finden aller Notizen eines Kunden nach Kunden-ID
    List<Note> getNotesByCustomerId(Long customerId);
}
