package edu.yacoubi.crm.repository;

import edu.yacoubi.crm.model.Customer;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.query.QueryByExampleExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerRepository extends
        JpaRepository<Customer, Long>,
        QueryByExampleExecutor<Customer>,
        CustomCustomerRepository {
    Optional<Customer> findByEmail(String email);

    @EntityGraph(attributePaths = {"notes", "employee", "employee.customers"})
    @Query("SELECT c FROM Customer c WHERE c.email = :email")
    Optional<Customer> findByEmailWithNotesAndEmployeeCustomers(@Param("email") String email);

}
