package edu.yacoubi.crm.service.validation;

import edu.yacoubi.crm.exception.ResourceNotFoundException;
import edu.yacoubi.crm.repository.CustomerRepository;
import edu.yacoubi.crm.repository.EmployeeRepository;
import edu.yacoubi.crm.repository.InactiveEmployeeRepository;
import edu.yacoubi.crm.repository.NoteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Validator for entities in the system.
 * <p>This class provides methods to validate the existence of various entities.</p>
 *
 * @author A. El Yacoubi
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class EntityValidator {

    private final EmployeeRepository employeeRepository;
    private final NoteRepository noteRepository;
    private final CustomerRepository customerRepository;
    private final InactiveEmployeeRepository inactiveEmployeeRepository;

    /**
     * Validates if an employee exists.
     *
     * @param employeeId the ID of the employee to validate
     * @throws ResourceNotFoundException if the employee does not exist
     */
    public void validateEmployeeExists(final Long employeeId) {
        if (log.isInfoEnabled()) {
            log.info("::validateEmployeeExists started with: employeeId {}", employeeId);
        }

        if (!employeeRepository.existsById(employeeId)) {
            String errorMessage = "Employee not found with ID: " + employeeId;
            log.error("::validateEmployeeExists error: {}", errorMessage);
            throw new ResourceNotFoundException(errorMessage);
        }

        if (log.isInfoEnabled()) {
            log.info("::validateEmployeeExists completed successfully");
        }
    }

    /**
     * Validates if a note exists.
     *
     * @param noteId the ID of the note to validate
     * @throws ResourceNotFoundException if the note does not exist
     */
    public void validateNoteExists(final Long noteId) {
        if (log.isInfoEnabled()) {
            log.info("::validateNoteExists started with: noteId {}", noteId);
        }

        if (!noteRepository.existsById(noteId)) {
            String errorMessage = "Note not found with ID: " + noteId;
            log.error("::validateNoteExists error: {}", errorMessage);
            throw new ResourceNotFoundException(errorMessage);
        }

        if (log.isInfoEnabled()) {
            log.info("::validateNoteExists completed successfully");
        }
    }

    /**
     * Validates if a customer exists.
     *
     * @param customerId the ID of the customer to validate
     * @throws ResourceNotFoundException if the customer does not exist
     */
    public void validateCustomerExists(final Long customerId) {
        if (log.isInfoEnabled()) {
            log.info("::validateCustomerExists started with: customerId {}", customerId);
        }

        if (!customerRepository.existsById(customerId)) {
            String errorMessage = "Customer not found with ID: " + customerId;
            log.error("::validateCustomerExists error: {}", errorMessage);
            throw new ResourceNotFoundException(errorMessage);
        }

        if (log.isInfoEnabled()) {
            log.info("::validateCustomerExists completed successfully");
        }
    }

    /**
     * Validates if an inactive employee exists.
     *
     * @param originalEmployeeId the original ID of the employee to validate
     * @throws ResourceNotFoundException if the inactive employee does not exist
     */
    public void validateInactiveEmployeeExists(final Long originalEmployeeId) {
        if (log.isInfoEnabled()) {
            log.info("::validateInactiveEmployeeExists started with: originalEmployeeId: {}", originalEmployeeId);
        }

        if (!inactiveEmployeeRepository.existsByOriginalEmployeeId(originalEmployeeId)) {
            String errorMessage = "Inactive employee not found with ID: " + originalEmployeeId;
            log.error("::validateInactiveEmployeeExists error: {}", errorMessage);
            throw new ResourceNotFoundException(errorMessage);
        }

        if (log.isInfoEnabled()) {
            log.info("::validateInactiveEmployeeExists completed successfully");
        }
    }

    /**
     * Validates if an employee has customers.
     *
     * @param employeeId the ID of the employee to check
     * @return true if the employee has customers, false otherwise
     */
    public boolean validateEmployeeHasCustomers(final Long employeeId) {
        if (log.isInfoEnabled()) {
            log.info("::validateEmployeeHasCustomers started with: employeeId: {}", employeeId);
        }

        boolean hasCustomers = employeeRepository.hasCustomers(employeeId);

        if (log.isInfoEnabled()) {
            log.info("::validateEmployeeHasCustomers completed successfully: employeeId: {} hasCustomers: {}",
                    employeeId, hasCustomers);
        }
        return hasCustomers;
    }
}

