package edu.yacoubi.crm.service.validation;

import edu.yacoubi.crm.exception.ResourceNotFoundException;
import edu.yacoubi.crm.repository.CustomerRepository;
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
    private final CustomerRepository customerRepository;

    public void validateEmployeeExists(Long employeeId) {
        log.info("EntityValidator::validateEmployeeExists employeeId: {}", employeeId);

        if (!employeeRepository.existsById(employeeId)) {
            log.error("EntityValidator::validateEmployeeExists employeeId: {} not found", employeeId);
            throw new ResourceNotFoundException("Employee not found with ID: " + employeeId);
        }

        log.info("EntityValidator::validateEmployeeExists employeeId: {} successfully validated", employeeId);
    }

    public void validateNoteExists(Long id) {
        log.info("EntityValidator::validateNoteExists id: {}", id);

        if (!noteRepository.existsById(id)) {
            log.error("EntityValidator::validateNoteExists id: {} not found", id);
            throw new ResourceNotFoundException("Note not found with ID: " + id);
        }

        log.info("EntityValidator::validateNoteExists id: {} successfully validated", id);
    }

    public void validateCustomerExists(Long id) {
        log.info("EntityValidator::validateCustomerExists id: {}", id);

        if (!customerRepository.existsById(id)) {
            log.error("EntityValidator::validateCustomerExists id: {} not found", id);
            throw new ResourceNotFoundException("Customer not found with ID: " + id);
        }

        log.info("EntityValidator::validateCustomerExists id: {} successfully validated", id);
    }
}
