package edu.yacoubi.crm.security.repository;

import edu.yacoubi.crm.security.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {
}
