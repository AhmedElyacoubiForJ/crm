package edu.yacoubi.crm.security.repository;

import edu.yacoubi.crm.security.model.UserEmployee;
import edu.yacoubi.crm.security.model.UserEmployeeKey;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserEmployeeRepository extends JpaRepository<UserEmployee, UserEmployeeKey> {
}
