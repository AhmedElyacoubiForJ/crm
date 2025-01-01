package edu.yacoubi.crm.service.validation;

import edu.yacoubi.crm.exception.ResourceNotFoundException;
import edu.yacoubi.crm.repository.CustomerRepository;
import edu.yacoubi.crm.repository.EmployeeRepository;
import edu.yacoubi.crm.repository.InactiveEmployeeRepository;
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
    private final CustomerRepository customerRepository;
    private final InactiveEmployeeRepository inactiveEmployeeRepository;

    public void validateEmployeeExists(Long employeeId) {
        log.info("EntityValidator::validateEmployeeExists employeeId: {}", employeeId);

        if (!employeeRepository.existsById(employeeId)) {
            String errorMessage = "Employee not found with ID: " + employeeId;
            log.error("EntityValidator::validateEmployeeExists error: {}", errorMessage);
            throw new ResourceNotFoundException(errorMessage);
        }

        log.info("EntityValidator::validateEmployeeExists employeeId: {} successfully validated", employeeId);
    }

    public void validateNoteExists(Long id) {
        log.info("EntityValidator::validateNoteExists id: {}", id);

        if (!noteRepository.existsById(id)) {
            String errorMessage = "Note not found with ID: " + id;
            log.error("EntityValidator::validateNoteExists error: {}", errorMessage);
            throw new ResourceNotFoundException(errorMessage);
        }

        log.info("EntityValidator::validateNoteExists id: {} successfully validated", id);
    }

    public void validateCustomerExists(Long id) {
        log.info("EntityValidator::validateCustomerExists id: {}", id);

        if (!customerRepository.existsById(id)) {
            String errorMessage = "Customer not found with ID: " + id;
            log.error("EntityValidator::validateCustomerExists error: {}", errorMessage);
            throw new ResourceNotFoundException(errorMessage);
        }

        log.info("EntityValidator::validateCustomerExists id: {} successfully validated", id);
    }

    public void validateInactiveEmployeeExists(Long originalEmployeeId) {
        log.info("EntityValidator::validateInactiveEmployeeExists originalEmployeeId: {}", originalEmployeeId);

        if (!inactiveEmployeeRepository.existsByOriginalEmployeeId(originalEmployeeId)) {
            String errorMessage = "Inactive employee not found with ID: " + originalEmployeeId;
            log.error("EntityValidator::validateInactiveEmployeeExists error: {}", errorMessage);
            throw new ResourceNotFoundException(errorMessage);
        }

        log.info("EntityValidator::validateInactiveEmployeeExists originalEmployeeId: {} successfully validated", originalEmployeeId);
    }
}
