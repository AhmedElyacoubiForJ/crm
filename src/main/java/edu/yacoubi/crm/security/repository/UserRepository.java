package edu.yacoubi.crm.security.repository;

import edu.yacoubi.crm.security.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
