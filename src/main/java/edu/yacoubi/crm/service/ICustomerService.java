package edu.yacoubi.crm.service;

import edu.yacoubi.crm.dto.customer.CustomerPatchDTO;
import edu.yacoubi.crm.dto.customer.CustomerRequestDTO;
import edu.yacoubi.crm.model.Customer;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Optional;

public interface ICustomerService {
    Customer createCustomer(Customer customer);

    Optional<Customer> getCustomerById(Long customerId);

    Customer updateCustomer(Long customerId, Customer customer);

    void deleteCustomer(Long customerId);

    Optional<Customer> getCustomerByEmail(String email);

    List<Customer> getCustomersByExample(CustomerRequestDTO customerDTO);

    Customer updateCustomerByExample(CustomerRequestDTO customerExample, Long customerId);

    Optional<Customer> getCustomerByEmailWithNotesAndEmployeeCustomers(String email);

    Customer getCustomerWithNotes(Long customerId);

    void partialUpdateCustomer(Long customerId, CustomerPatchDTO customerPatchDTO);

    Page<Customer> getCustomersByFirstNameOrEmail(String search, int page, int size);

    Page<Customer> getCustomersWithPagination(int page, int size);

    List<Customer> getCustomersByEmployeeId(Long employeeId);
}
