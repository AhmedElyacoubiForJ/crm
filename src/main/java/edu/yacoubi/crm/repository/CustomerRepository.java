package edu.yacoubi.crm.repository;

import edu.yacoubi.crm.model.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.query.QueryByExampleExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
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

    Page<Customer> findAll(Pageable pageable);

    Page<Customer> findByFirstNameContainingIgnoreCaseOrEmailContainingIgnoreCase(
            String firstName,
            String email,
            Pageable pageable
    );

    List<Customer> findByEmployeeId(Long employeeId);

    //@Query("SELECT c FROM Customer c WHERE c.employee.id = :employeeId")
    //List<Customer> findByEmployeeId(@Param("employeeId") Long employeeId);

    @Query("SELECT c FROM Customer c WHERE LOWER(c.firstName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR LOWER(c.lastName) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    Page<Customer> findBySearchTerm(@Param("searchTerm") String searchTerm, Pageable pageable);

    //@Query("SELECT COUNT(c) > 0 FROM Customer c WHERE c.employee.id = :employeeId")
    //boolean hasCustomers(@Param("employeeId") Long employeeId);
}
