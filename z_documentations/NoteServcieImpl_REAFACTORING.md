> Lass uns die NoteServiceImpl Klasse nach dem Prinzip der "Separation of Concerns" verbessern. Das Ziel ist, die Verantwortlichkeiten klarer zu trennen und die Klasse dadurch wartbarer, testbarer und verständlicher zu machen.

*Analyse der aktuellen Situation:*

Die aktuelle NoteServiceImpl hat mehrere Verantwortlichkeiten:

1.  *Datenzugriff:* Sie verwendet NoteRepository, um auf die Datenbank zuzugreifen.
2.  *Geschäftslogik:* Sie enthält Geschäftslogik für das Erstellen, Abrufen, Aktualisieren und Löschen von Notizen.
3.  *Validierung:* Sie validiert die Existenz von Notizen und Kunden.
4.  *Teilweise Aktualisierung:* Sie verwendet EntityManager für die teilweise Aktualisierung von Notizen.
5.  *Logging:* Sie verwendet Slf4j für Logging.

*Verbesserungen:*

Hier sind die wichtigsten Schritte, um die Verantwortlichkeiten zu trennen:

1.  *Validierung:* Die Validierungslogik (z.B. validateNoteId) sollte in eine separate Komponente oder Utility-Klasse verschoben werden.
2.  *Datenzugriff:* Der direkte Aufruf von EntityManager für teilweise Updates sollte idealerweise in einer separaten Repository-Klasse oder einer Helper-Klasse für komplexere Abfragen gekapselt werden.
3.  *Geschäftslogik:* Die Geschäftslogik sollte innerhalb des Service verbleiben, aber die Validierung und der Datenzugriff sollte an die entsprechenden Schichten delegiert werden.
4.  *Logging:* Das Logging ist in dieser Klasse passend aufgehoben.

*Umgesetzte Verbesserungen:*

Hier ist die verbesserte Version der Klasse, mit Kommentaren für Erklärungen:

```java
package edu.yacoubi.crm.service.impl;

import edu.yacoubi.crm.dto.note.NotePatchDTO;
import edu.yacoubi.crm.model.Customer;
import edu.yacoubi.crm.model.Note;
import edu.yacoubi.crm.repository.NoteRepository;
import edu.yacoubi.crm.repository.INoteRepositoryCustom; // Neu: Custom Repository
import edu.yacoubi.crm.service.ICustomerService;
import edu.yacoubi.crm.service.INoteService;
import edu.yacoubi.crm.service.validation.EntityValidator; // Neu: Validator
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class NoteServiceImpl implements INoteService {

    private final NoteRepository noteRepository;
    private final INoteRepositoryCustom noteRepositoryCustom; // Neu: Custom Repository
    private final ICustomerService customerService;
    private final EntityValidator entityValidator; // Neu: Validator

    @Override
    public Note createNoteForCustomer(Note note, Long customerId) {
        log.info("NoteServiceImpl::createNoteForCustomer execution start: note {}, customerId {}", note, customerId);

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

        entityValidator.validateNoteId(id); // Delegiert Validierung

        log.info("NoteServiceImpl::updateNote execution end");
        return noteRepository.save(note);
    }

    @Override
    public void deleteNote(Long id) {
        log.info("NoteServiceImpl::deleteNote execution start: id {}", id);

        entityValidator.validateNoteId(id); // Delegiert Validierung

        log.info("NoteServiceImpl::deleteNote execution end");
        noteRepository.deleteById(id);
    }

    @Override
    public List<Note> getNotesByCustomerId(Long customerId) {
        log.info("NoteServiceImpl::getNotesByCustomerId execution start: customerId {}", customerId);

        customerService.ensureCustomerExists(customerId);

        log.info("NoteServiceImpl::getNotesByCustomerId execution end");
        return noteRepository.findAllByCustomerId(customerId);
    }

    @Override
    @Transactional
    public void partialUpdateNote(Long id, NotePatchDTO notePatchDTO) {
        log.info("NoteServiceImpl::partialUpdateNote execution start: id {}, notePatchDTO {}", id, notePatchDTO);

        entityValidator.validateNoteId(id); // Delegiert Validierung

        noteRepositoryCustom.partialUpdateNote(id, notePatchDTO); //Delegiert teilweise Update

        log.info("NoteServiceImpl::partialUpdateNote execution end");
    }
}
```

*Neue Komponenten (müssen implementiert werden):*

1.  **edu.yacoubi.crm.repository.NoteRepositoryCustom (Interface und Implementierung):**

    *   Schnittstelle:
        ```java
        package edu.yacoubi.crm.repository;

        import edu.yacoubi.crm.dto.note.NotePatchDTO;

        public interface INoteRepositoryCustom {
            void partialUpdateNote(Long id, NotePatchDTO notePatchDTO);
        }
        ```

     *  Implementierung:
        ```java
        package edu.yacoubi.crm.repository.impl;

        import edu.yacoubi.crm.dto.note.NotePatchDTO;
        import edu.yacoubi.crm.model.Note;
        import jakarta.persistence.EntityManager;
        import jakarta.persistence.criteria.CriteriaBuilder;
        import jakarta.persistence.criteria.CriteriaUpdate;
        import jakarta.persistence.criteria.Root;
        import lombok.RequiredArgsConstructor;
        import org.springframework.stereotype.Repository;

        @Repository
        @RequiredArgsConstructor
        public class NoteRepositoryCustomImpl implements NoteRepositoryCustom{
        private final EntityManager entityManager;
            @Override
            public void partialUpdateNote(Long id, NotePatchDTO notePatchDTO) {
                CriteriaBuilder cb = entityManager.getCriteriaBuilder();
                CriteriaUpdate<Note> update = cb.createCriteriaUpdate(Note.class);
                Root<Note> root = update.from(Note.class);

                if (notePatchDTO.getContent() != null) {
                    update.set(root.get("content"), notePatchDTO.getContent());
                }
                if (notePatchDTO.getDate() != null) {
                    update.set(root.get("date"), notePatchDTO.getDate());
                }
                if(notePatchDTO.getInteractionType() != null) {
                    update.set(root.get("interactionType"), notePatchDTO.getInteractionType());
                }

                update.where(cb.equal(root.get("id"), id));

                entityManager.createQuery(update).executeUpdate();
            }
        }
        ```

2.  **edu.yacoubi.crm.service.validation.EntityValidator (Klasse):**

    ```java
    package edu.yacoubi.crm.service.validation;

    import edu.yacoubi.crm.exception.ResourceNotFoundException;
    import edu.yacoubi.crm.repository.NoteRepository;
    import lombok.RequiredArgsConstructor;
    import org.springframework.stereotype.Component;

    @Component
    @RequiredArgsConstructor
    public class EntityValidator {

        private final NoteRepository noteRepository;


        public void validateNoteId(Long id) {
            if (!noteRepository.existsById(id)) {
                throw new ResourceNotFoundException("Note not found with ID: " + id);
            }
        }
    }
    ```

*Vorteile der Verbesserungen:*

*   *Separation of Concerns:* Jede Klasse hat nun eine klarere Verantwortung.
*   *Testbarkeit:* Die Logik in den Service-Klassen ist nun leichter zu testen, da sie weniger mit Validierung und Datenzugriff vermischt ist.
*   *Wartbarkeit:* Änderungen in der Validierung oder im Datenzugriff haben weniger Auswirkungen auf andere Teile des Systems.
*   *Wiederverwendbarkeit:* Der Validierungscode kann in anderen Services wiederverwendet werden.
*   *Flexibilität:* Der Code ist flexibler für zukünftige Änderungen und Erweiterungen.

*Zusammenfassung der Änderungen:*

*   **NoteServiceImpl:**
    *   Delegiert Validierung an EntityValidator.
    *   Delegiert den partiellen Update an INoteRepositoryCustom.
    *   Behält Geschäftslogik und Logging.
*   **NoteRepositoryCustom:**
    *   Definiert ein Interface für eigene Repository Methoden (Hier: partial Update).
*   **NoteRepositoryCustomImpl:**
    *  Implementiert das Interface INoteRepositoryCustom und kapselt die partielle Update Logik.
*   **EntityValidator:**
    *   Kapselt die Validierungslogik.

> Diese Änderungen führen zu einer saubereren, testbareren und wartbareren Architektur. Du musst natürlich die NoteRepositoryCustom und EntityValidator Klassen mit den richtigen Implementierungen hinzufügen.