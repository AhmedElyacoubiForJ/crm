package edu.yacoubi.crm.security.repository;

import edu.yacoubi.crm.security.model.UserCustomer;
import edu.yacoubi.crm.security.model.UserCustomerKey;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserCustomerRepository extends JpaRepository<UserCustomer, UserCustomerKey> {
}
