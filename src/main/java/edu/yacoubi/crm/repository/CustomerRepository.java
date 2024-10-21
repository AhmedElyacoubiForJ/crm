package edu.yacoubi.crm.repository;

import edu.yacoubi.crm.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.QueryByExampleExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long>, QueryByExampleExecutor<Customer> {
    Optional<Customer> findByEmail(String email);
}
