package edu.yacoubi.crm.service.impl;

import edu.yacoubi.crm.dto.employee.EmployeePatchDTO;
import edu.yacoubi.crm.exception.ResourceNotFoundException;
import edu.yacoubi.crm.model.Employee;
import edu.yacoubi.crm.repository.EmployeeRepository;
import edu.yacoubi.crm.repository.IEmployeeCustomRepository;
import edu.yacoubi.crm.service.IEmployeeService;
import edu.yacoubi.crm.service.validation.EntityValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Implementation of the Employee Service.
 *
 * <p>This class implements the logic for managing employees, including
 * creating, updating, and deleting employee records.</p>
 *
 * @author A. El Yacoubi
 */
@Service
@RequiredArgsConstructor
@Slf4j
// TODO validate method parameters
public class EmployeeServiceImpl implements IEmployeeService {
    private final EmployeeRepository employeeRepository;
    private final IEmployeeCustomRepository employeeCustomRepository;
    private final EntityValidator entityValidator;

    /**
     * Creates a new employee.
     * This method is marked as transactional to ensure operations are atomic
     * and to maintain data consistency in case of rollbacks.
     *
     * @param employee the employee to be created
     * @return the created employee
     * @throws IllegalArgumentException if the employee is null
     */
    @Override
    @Transactional
    public Employee createEmployee(final Employee employee) {
        if (log.isInfoEnabled()) {
            log.info("::createEmployee started with: employee {}", employee);
        }

        if (employee == null) {
            log.warn("Employee must not be null");
            throw new IllegalArgumentException("Employee must not be null");
        }

        employee.setId(null);
        final Employee savedEmployee = employeeRepository.save(employee);

        if (log.isInfoEnabled()) {
            log.info("::createEmployee completed successfully");
        }
        return savedEmployee;
    }

    /**
     * Retrieves all employees.
     *
     * @return a list of all employees
     */
    @Override
    public List<Employee> getAllEmployees() {
        if (log.isInfoEnabled()) {
            log.info("::getAllEmployees started");
        }

        final List<Employee> employees = employeeRepository.findAll();

        if (log.isInfoEnabled()) {
            log.info("::getAllEmployees completed successfully");
        }
        return employees;
    }

    /**
     * Retrieves a paginated list of employees.
     *
     * @param page the page number to retrieve
     * @param size the number of employees per page
     * @return a paginated list of employees
     */
    @Override
    public Page<Employee> getEmployeesWithPagination(
            final int page, final int size) {
        if (log.isInfoEnabled()) {
            log.info("::getEmployeesWithPagination started with: page {}, size {}", page, size);
        }

        final Pageable pageable = PageRequest.of(page, size);
        final Page<Employee> employeePage = employeeRepository.findAll(pageable);

        if (log.isInfoEnabled()) {
            log.info("::getEmployeesWithPagination completed successfully");
        }
        return employeePage;
    }

    /**
     * Retrieves an employee by its ID.
     *
     * @param employeeId the ID of the employee to retrieve
     * @return an Optional containing the found employee, or an empty Optional if no employee was found
     * @throws IllegalArgumentException if the employee ID is null
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<Employee> getEmployeeById(final Long employeeId) {
        if (log.isInfoEnabled()) {
            log.info("::getEmployeeById started with: employeeId {}", employeeId);
        }

        if (employeeId == null) {
            log.warn("Employee ID must not be null");
            throw new IllegalArgumentException("Employee ID must not be null");
        }

        entityValidator.validateEmployeeExists(employeeId);

        final Optional<Employee> optionalEmployee = employeeRepository.findById(employeeId);

        if (log.isInfoEnabled()) {
            log.info("::getEmployeeById completed successfully");
        }
        return optionalEmployee;
    }

    /**
     * Updates an existing employee by their ID.
     *
     * @param employeeId the ID of the employee to update
     * @param employee   the updated employee details
     * @return the updated employee
     */
    @Override
    @Transactional
    public Employee updateEmployee(final Long employeeId, final Employee employee) {
        if (log.isInfoEnabled()) {
            log.info("::updateEmployee started with: employee {}", employee);
        }

        entityValidator.validateEmployeeExists(employeeId);

        employee.setId(employeeId);
        final Employee updatedEmployee = employeeRepository.save(employee);

        if (log.isInfoEnabled()) {
            log.info("::updateEmployee completed successfully");
        }
        return updatedEmployee;
    }

    /**
     * Retrieves an employee by their email.
     *
     * @param email the email of the employee to retrieve
     * @return an Optional containing the found employee, or an empty Optional if no employee was found
     */
    @Override
    public Optional<Employee> getEmployeeByEmail(final String email) {
        if (log.isInfoEnabled()) {
            log.info("::getEmployeeByEmail started with: email {}", email);
        }

        final Optional<Employee> optionalEmployee = employeeRepository.findByEmail(email);

        if (log.isInfoEnabled()) {
            log.info("::getEmployeeByEmail completed successfully");
        }
        return optionalEmployee;
    }

    /**
     * Partially updates an employee's details.
     *
     * @param employeeId       the ID of the employee to update
     * @param employeePatchDTO the partial update details
     */
    @Override
    @Transactional
    public void partialUpdateEmployee(
            final Long employeeId, final EmployeePatchDTO employeePatchDTO) {
        if (log.isInfoEnabled()) {
            log.info("::partialUpdateEmployee started with: employeeId {}, employeePatchDTO {}",
                    employeeId, employeePatchDTO);
        }

        entityValidator.validateEmployeeExists(employeeId);

        employeeCustomRepository.partialUpdateEmployee(employeeId, employeePatchDTO);

        if (log.isInfoEnabled()) {
            log.info("::partialUpdateEmployee completed successfully");
        }
    }

    /**
     * Retrieves a paginated list of employees by their first name or department.
     *
     * @param searchString the string to search for in the first name or department
     * @param page         the page number to retrieve
     * @param size         the number of employees per page
     * @return a paginated list of employees matching the search criteria
     */
    @Override
    public Page<Employee> getEmployeesByFirstNameOrDepartment(
            final String searchString, final int page, final int size) {
        if (log.isInfoEnabled()) {
            log.info("::searchByFirstNameOrDepartment started with: searchString {}, page {}, size {}", searchString, page, size);
        }

        final Pageable pageable = PageRequest.of(page, size);

        final Page<Employee> employeePage = employeeRepository
                .findByFirstNameContainingIgnoreCaseOrDepartmentContainingIgnoreCase(
                        searchString,
                        searchString,
                        pageable
                );

        if (log.isInfoEnabled()) {
            log.info("::searchByFirstNameOrDepartment completed successfully");
        }
        return employeePage;
    }


    /**
     * Retrieves a list of all departments.
     *
     * @return an Optional containing the list of all departments
     */
    @Override
    public Optional<List<String>> getAllDepartments() {
        if (log.isInfoEnabled()) {
            log.info("::getAllDepartments started");
        }

        final Optional<List<String>> optionalDepartments =
                employeeRepository.findAllDepartments();

        if (log.isInfoEnabled()) {
            log.info("::getAllDepartments completed successfully");
        }
        return optionalDepartments;
    }

    /**
     * Deletes an employee by their ID.
     *
     * @param employeeId the ID of the employee to delete
     * @throws IllegalArgumentException if the employee has assigned customers or is not archived
     */
    @Override
    @Transactional
    public void deleteEmployee(final Long employeeId) {
        if (log.isInfoEnabled()) {
            log.info("::deleteEmployee started with: employeeId: {}", employeeId);
        }

        entityValidator.validateEmployeeExists(employeeId);

        // Check if the employee has assigned customers
        if (employeeRepository.hasCustomers(employeeId)) {
            log.warn("Cannot delete employee, customers are still assigned. employeeId: {}", employeeId);
            throw new IllegalArgumentException("Cannot delete employee, customers are still assigned.");
        }

        // Check if the employee is archived
        try {
            entityValidator.validateInactiveEmployeeExists(employeeId);
        } catch (ResourceNotFoundException exception) {
            log.warn("Cannot delete not archived employee. EmployeeId: {}", employeeId);
            throw new IllegalArgumentException("Cannot delete not archived employee.");
        }

        employeeRepository.deleteById(employeeId);

        if (log.isInfoEnabled()) {
            log.info("::deleteEmployee completed successfully");
        }
    }

    /**
     * Checks if an employee has assigned customers.
     *
     * @param employeeId the ID of the employee to check
     * @return true if the employee has assigned customers, false otherwise
     */
    @Override
    @Transactional(readOnly = true)
    public boolean hasCustomers(final Long employeeId) {
        if (log.isInfoEnabled()) {
            log.info("::hasCustomers started with: employeeId {}", employeeId);
        }

        entityValidator.validateEmployeeExists(employeeId);

        // Use the query defined in the EmployeeRepository
        final boolean hasCustomers = employeeRepository.hasCustomers(employeeId);

        if (log.isInfoEnabled()) {
            log.info("::hasCustomers completed successfully");
        }
        return hasCustomers;
    }
}

