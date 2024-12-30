package edu.yacoubi.crm.service;

import edu.yacoubi.crm.dto.employee.EmployeePatchDTO;
import edu.yacoubi.crm.model.Employee;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Optional;

/**
 * Service interface for managing Employee entities.
 */
public interface IEmployeeService {

    /**
     * Creates a new employee.
     *
     * @param employee the employee to be created.
     * @return the created employee.
     */
    Employee createEmployee(Employee employee);

    /**
     * Retrieves all employees.
     *
     * @return a list of all employees.
     */
    List<Employee> getAllEmployees();

    /**
     * Retrieves employees in a paginated format.
     *
     * @param page the page number to retrieve.
     * @param size the number of employees per page.
     * @return a page of employees.
     */
    Page<Employee> getEmployeesWithPagination(int page, int size);

    /**
     * Retrieves an employee by their ID.
     *
     * @param employeeId the ID of the employee.
     * @return an Optional containing the found employee, or empty if not found.
     */
    Optional<Employee> getEmployeeById(Long employeeId);

    /**
     * Updates an existing employee.
     *
     * @param employeeId the ID of the employee to be updated.
     * @param employee the updated employee information.
     * @return the updated employee.
     */
    Employee updateEmployee(Long employeeId, Employee employee);

    /**
     * Retrieves an employee by their email.
     *
     * @param email the email of the employee.
     * @return an Optional containing the found employee, or empty if not found.
     */
    Optional<Employee> getEmployeeByEmail(String email);

    /**
     * Partially updates an existing employee.
     *
     * @param employeeId the ID of the employee to be partially updated.
     * @param employeePatchDTO the partial update information.
     */
    void partialUpdateEmployee(Long employeeId, EmployeePatchDTO employeePatchDTO);

    /**
     * Retrieves employees by first name or department, in a paginated format.
     *
     * @param search the search string for the first name or department.
     * @param page the page number to retrieve.
     * @param size the number of employees per page.
     * @return a page of employees matching the search criteria.
     */
    Page<Employee> getEmployeesByFirstNameOrDepartment(String search, int page, int size);

    /**
     * Retrieves all unique department names of employees.
     *
     * @return an Optional containing a list of unique department names.
     */
    Optional<List<String>> getAllDepartments();

    /**
     * Deletes an employee by their ID.
     * This method checks if the employee has any assigned customers and if the employee is archived before deletion.
     *
     * @param employeeId the ID of the employee to be deleted.
     */
    void deleteEmployee(Long employeeId);

    boolean hasCustomers(Long employeeId);
}
