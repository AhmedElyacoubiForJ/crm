package edu.yacoubi.crm.repository;

import edu.yacoubi.crm.dto.note.NotePatchDTO;
import edu.yacoubi.crm.model.Note;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaUpdate;
import jakarta.persistence.criteria.Root;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
@Slf4j
public class NoteCustomRepositoryImpl implements INoteCustomRepository {
    private final EntityManager entityManager;
    //private final EntityValidatorService entityValidatorService;


    @Override
    @Transactional
    public void partialUpdateNote(Long id, NotePatchDTO notePatchDTO) {
        log.info("NoteCustomRepositoryImpl::partialUpdateNote execution start: id {}, notePatchDTO {}", id, notePatchDTO);

        //entityValidatorService.validateNoteExists(id);

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
        log.info("NoteCustomRepositoryImpl::partialUpdateNote execution end");
    }
}
