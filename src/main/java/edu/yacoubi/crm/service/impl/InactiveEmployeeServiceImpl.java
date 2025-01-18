package edu.yacoubi.crm.service.impl;

import edu.yacoubi.crm.model.Employee;
import edu.yacoubi.crm.model.InactiveEmployee;
import edu.yacoubi.crm.repository.InactiveEmployeeRepository;
import edu.yacoubi.crm.service.IInactiveEmployeeService;
import edu.yacoubi.crm.service.validation.EntityValidator;
import edu.yacoubi.crm.util.EntityTransformer;
import edu.yacoubi.crm.util.TransformerUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Implementation of the Inactive Employee Service.
 *
 * <p>This class implements the logic for managing inactive employees, including
 * creating, retrieving, and checking existence of inactive employee records.</p>
 *
 * @author A. El Yacoubi
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class InactiveEmployeeServiceImpl implements IInactiveEmployeeService {

    private final InactiveEmployeeRepository inactiveEmployeeRepository;
    private final EntityValidator entityValidator;

    /**
     * Creates a new inactive employee.
     *
     * @param employee the employee to be marked as inactive
     * @return the created inactive employee
     * @throws IllegalArgumentException if the employee is null or has assigned customers
     */
    @Override
    public InactiveEmployee createInactiveEmployee(final Employee employee) {
        if (log.isInfoEnabled()) {
            log.info("::createInactiveEmployee started with: employee {}", employee);
        }

        // Parameter-Validierung
        if (employee == null) {
            throw new IllegalArgumentException("Employee is null.");
        }

        if (entityValidator.validateEmployeeHasCustomers(employee.getId())) {
            throw new IllegalArgumentException("Employee has assigned customers.");
        }

        final InactiveEmployee inactiveEmployee = TransformerUtil
                .transform(EntityTransformer.employeeToInactiveEmployee, employee);

        final InactiveEmployee savedInactiveEmployee = inactiveEmployeeRepository.save(inactiveEmployee);

        if (log.isInfoEnabled()) {
            log.info("::createInactiveEmployee completed successfully");
        }
        return savedInactiveEmployee;
    }

    /**
     * Retrieves an inactive employee by their ID.
     *
     * @param id the ID of the inactive employee to retrieve
     * @return an Optional containing the found inactive employee, or an empty Optional if no employee was found
     */
    @Override
    public Optional<InactiveEmployee> getInactiveEmployeeById(final Long id) {
        if (log.isInfoEnabled()) {
            log.info("::getInactiveEmployeeById started with: id {}", id);
        }

        final Optional<InactiveEmployee> optionalInactiveEmployee = inactiveEmployeeRepository.findById(id);

        if (log.isInfoEnabled()) {
            log.info("::getInactiveEmployeeById completed successfully");
        }
        return optionalInactiveEmployee;
    }

    /**
     * Checks if an inactive employee exists by their original employee ID.
     *
     * @param originalEmployeeId the original employee ID to check
     * @return true if an inactive employee exists with the original employee ID, false otherwise
     */
    @Override
    public boolean existsByOriginalEmployeeId(final Long originalEmployeeId) {
        if (log.isInfoEnabled()) {
            log.info("::existsByOriginalEmployeeId started with: originalEmployeeId {}", originalEmployeeId);
        }

        final boolean exists = inactiveEmployeeRepository.existsByOriginalEmployeeId(originalEmployeeId);

        if (log.isInfoEnabled()) {
            log.info("::existsByOriginalEmployeeId completed successfully");
        }
        return exists;
    }

    // Methode zur Basis-Validierung von Employee-Feldern
    private void validateBasicEmployeeFields(final Employee employee) {
        if (log.isInfoEnabled()) {
            log.info("validateBasicEmployeeFields employeeId: {}", employee.getId());
        }

        if (employee.getFirstName() == null || employee.getFirstName().isEmpty() ||
                employee.getLastName() == null || employee.getLastName().isEmpty() ||
                employee.getEmail() == null || employee.getEmail().isEmpty() ||
                employee.getDepartment() == null || employee.getDepartment().isEmpty()) {
            throw new IllegalArgumentException("Employee has incomplete information.");
        }
    }
}
