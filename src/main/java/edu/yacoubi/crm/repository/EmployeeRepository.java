package edu.yacoubi.crm.repository;

import edu.yacoubi.crm.model.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for managing Employee entities.
 */
@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    /**
     * Retrieves all employees in a paginated format.
     *
     * @param pageable the pagination information.
     * @return a page of employees.
     */
    Page<Employee> findAll(Pageable pageable);

    /**
     * Finds an employee by their email.
     *
     * @param email the email of the employee.
     * @return an Optional containing the found employee, or empty if not found.
     */
    Optional<Employee> findByEmail(String email);

    /**
     * Finds employees whose first name contains the specified search string (case-insensitive).
     *
     * @param searchString the search string for the first name.
     * @return an Optional containing a list of matching employees.
     */
    Optional<List<Employee>> findByFirstNameIgnoreCaseContaining(String searchString);

    /**
     * Finds employees whose email contains the specified search string (case-insensitive).
     *
     * @param searchString the search string for the email.
     * @return an Optional containing a list of matching employees.
     */
    Optional<List<Employee>> findByEmailIgnoreCaseContaining(String searchString);

    /**
     * Finds employees by either their first name or department, containing the specified search strings (case-insensitive).
     *
     * @param firstName  the search string for the first name.
     * @param department the search string for the department.
     * @param pageable   the pagination information.
     * @return a page of employees matching the search criteria.
     */
    Page<Employee> findByFirstNameContainingIgnoreCaseOrDepartmentContainingIgnoreCase(
            String firstName,
            String department,
            Pageable pageable
    );

    /**
     * Retrieves all unique department names of employees.
     *
     * @return an Optional containing a list of unique department names.
     */
    @Query("SELECT distinct e.department FROM Employee e")
    Optional<List<String>> findAllDepartments();

    /**
     * Checks if an employee has any assigned customers.
     * This query performs a join operation between the Employee and Customer tables.
     * It counts the number of Customer entities associated with the given Employee ID.
     * If the count is greater than zero, it returns true, indicating that the employee has customers.
     * Otherwise, it returns false.
     *
     * @param employeeId the ID of the employee.
     * @return true if the employee has customers, false otherwise.
     */
    @Query("SELECT CASE WHEN COUNT(e) > 0 THEN TRUE ELSE FALSE END FROM Employee e JOIN e.customers c WHERE e.id = :employeeId")
    boolean hasCustomers(@Param("employeeId") Long employeeId);

    /**
     * Finds an employee by their ID and fetches their customers eagerly.
     * This query performs a left join fetch operation to ensure that the customers list is loaded along with the employee.
     * <p>
     * The `LEFT JOIN FETCH` ensures that all Employee records are fetched, and any related Customer records are also fetched.
     * If an Employee has no related Customer, the Employee record is still fetched, but the Customer list will be empty.
     * This is useful for eager loading associated entities without additional queries.
     *
     * @param employeeId the ID of the employee.
     * @return an Optional containing the found employee with their customers, or empty if not found.
     */
    @Query("SELECT e FROM Employee e LEFT JOIN FETCH e.customers WHERE e.id = :employeeId")
    Optional<Employee> findByIdWithCustomers(@Param("employeeId") Long employeeId);
}
