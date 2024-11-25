package edu.yacoubi.crm.service;

import edu.yacoubi.crm.dto.customer.CustomerPatchDTO;
import edu.yacoubi.crm.dto.customer.CustomerRequestDTO;
import edu.yacoubi.crm.model.Customer;
import edu.yacoubi.crm.model.Employee;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Optional;

public interface ICustomerService {
    Customer createCustomer(Customer customer);

    Optional<Customer> getCustomerById(Long id);

    Customer updateCustomer(Long id, Customer customer);

    void deleteCustomer(Long id);

    Optional<Customer> getCustomerByEmail(String email);

    void ensureCustomerExists(Long id);

    List<Customer> getAllCustomers();

    List<Customer> getCustomersByExample(CustomerRequestDTO customerDTO);

    Customer updateCustomerByExample(CustomerRequestDTO customerExample, Long id);

    Optional<Customer> getCustomerByEmailWithNotesAndEmployeeCustomers(String email);

    Customer getCustomerWithNotes(Long id);

    void partialUpdateCustomer(Long id, CustomerPatchDTO customerPatchDTO);

    Page<Customer> getCustomersByFirstNameOrEmail(String search, int page, int size);

    Page<Customer> getCustomersWithPagination(int page, int size);

    void assignCustomerToEmployee(Long customerId, Long employeeId);

    List<Customer> findCustomersByEmployeeId(Long employeeId);
}
