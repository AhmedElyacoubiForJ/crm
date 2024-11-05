package edu.yacoubi.crm.repository;

import edu.yacoubi.crm.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    Optional<Employee> findByEmail(String email);
    Optional<List<Employee>> findByFirstNameIgnoreCaseContaining(String nameSearchString);
    Optional<List<Employee>> findByEmailIgnoreCaseContaining(String emailSearchString);
}
