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
        log.info("::validateEmployeeExists started with: employeeId {}", employeeId);

        if (!employeeRepository.existsById(employeeId)) {
            String errorMessage = "Employee not found with ID: " + employeeId;
            log.error("::validateEmployeeExists error: {}", errorMessage);
            throw new ResourceNotFoundException(errorMessage);
        }

        log.info("::validateEmployeeExists completed successfully");
    }

    public void validateNoteExists(Long noteId) {
        log.info("::validateNoteExists started with: noteId {}", noteId);

        if (!noteRepository.existsById(noteId)) {
            String errorMessage = "Note not found with ID: " + noteId;
            log.error("::validateNoteExists error: {}", errorMessage);
            throw new ResourceNotFoundException(errorMessage);
        }

        log.info("::validateNoteExists completed successfully");
    }

    public void validateCustomerExists(Long customerId) {
        log.info("::validateCustomerExists started with: customerId {}", customerId);

        if (!customerRepository.existsById(customerId)) {
            String errorMessage = "Customer not found with ID: " + customerId;
            log.error("::validateCustomerExists error: {}", errorMessage);
            throw new ResourceNotFoundException(errorMessage);
        }

        log.info("::validateCustomerExists completed successfully");
    }

    public void validateInactiveEmployeeExists(Long originalEmployeeId) {
        log.info("::validateInactiveEmployeeExists started with: originalEmployeeId: {}", originalEmployeeId);

        if (!inactiveEmployeeRepository.existsByOriginalEmployeeId(originalEmployeeId)) {
            String errorMessage = "Inactive employee not found with ID: " + originalEmployeeId;
            log.error("::validateInactiveEmployeeExists error: {}", errorMessage);
            throw new ResourceNotFoundException(errorMessage);
        }

        log.info("::validateInactiveEmployeeExists completed successfully");
    }

    // Methode zur Überprüfung, ob ein Employee Kunden hat
    public boolean validateEmployeeHasCustomers(Long employeeId) {
        log.info("::validateEmployeeHasCustomers started with: employeeId: {}", employeeId);

        boolean hasCustomers = employeeRepository.hasCustomers(employeeId);

        log.info("::validateEmployeeHasCustomers completed successfully: employeeId: {} hasCustomers: {}", employeeId, hasCustomers);
        return hasCustomers;
    }
}
