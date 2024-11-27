package edu.yacoubi.crm.repository;

import edu.yacoubi.crm.model.InactiveEmployee;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InactiveEmployeeRepository extends JpaRepository<InactiveEmployee, Long> {
    Optional<InactiveEmployee> findByEmail(String email);
}
