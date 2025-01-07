package edu.yacoubi.crm.repository;

import edu.yacoubi.crm.model.InactiveEmployee;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repository interface for managing InactiveEmployee entities.
 */
public interface InactiveEmployeeRepository extends JpaRepository<InactiveEmployee, Long> {

    /**
     * Finds an inactive employee by their email.
     *
     * @param email the email of the inactive employee.
     * @return an Optional containing the found inactive employee, or empty if not found.
     */
    Optional<InactiveEmployee> findByEmail(String email);

    /**
     * Checks if an inactive employee exists by their original employee ID.
     *
     * @param originalEmployeeId the ID of the original employee.
     * @return true if an inactive employee exists with the given original employee ID, false otherwise.
     */
    boolean existsByOriginalEmployeeId(Long originalEmployeeId);
}
