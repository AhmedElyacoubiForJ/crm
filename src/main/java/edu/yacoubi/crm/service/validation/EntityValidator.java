package edu.yacoubi.crm.service.validation;

import edu.yacoubi.crm.exception.ResourceNotFoundException;
import edu.yacoubi.crm.repository.EmployeeRepository;
import edu.yacoubi.crm.repository.NoteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class EntityValidator {

    private final EmployeeRepository employeeRepository;
    private final NoteRepository noteRepository;

    public void validateEmployeeExists(Long employeeId) {
        log.info("EntityValidatorService::validateEmployeeExists employeeId: {}", employeeId);

        if (!employeeRepository.existsById(employeeId)) {
            log.error("EntityValidatorService::validateEmployeeExists employeeId: {} not found", employeeId);
            throw new ResourceNotFoundException("Employee not found with ID: " + employeeId);
        }

        log.info("EntityValidatorService::validateEmployeeExists employeeId: {} successfully validated", employeeId);
    }

    public void validateNoteExists(Long id) {
        log.info("EntityValidatorService::validateNoteExists id: {}", id);

        if (!noteRepository.existsById(id)) {
            log.error("EntityValidatorService::validateNoteExists id: {} not found", id);
            throw new ResourceNotFoundException("Note not found with ID: " + id);
        }

        log.info("EntityValidatorService::validateNoteExists id: {} successfully validated", id);
    }
}
