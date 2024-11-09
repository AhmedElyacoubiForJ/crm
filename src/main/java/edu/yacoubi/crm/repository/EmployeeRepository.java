package edu.yacoubi.crm.repository;

import edu.yacoubi.crm.model.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    Page<Employee> findAll(Pageable pageable);
    Optional<Employee> findByEmail(String email);
    Optional<List<Employee>> findByFirstNameIgnoreCaseContaining(String searchString);
    Optional<List<Employee>> findByEmailIgnoreCaseContaining(String searchString);
    Page<Employee> findByFirstNameContainingIgnoreCaseOrDepartmentContainingIgnoreCase(
            String firstName,
            String department,
            Pageable pageable
    );
}
