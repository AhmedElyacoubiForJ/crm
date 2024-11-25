package edu.yacoubi.crm.repository;

import edu.yacoubi.crm.model.InactiveEmployee;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InactiveEmployeeRepository extends JpaRepository<InactiveEmployee, Long> {
}
