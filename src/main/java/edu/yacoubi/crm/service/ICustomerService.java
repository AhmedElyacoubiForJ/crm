package edu.yacoubi.crm.service;

import edu.yacoubi.crm.dto.CustomerRequestDTO;
import edu.yacoubi.crm.model.Customer;

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
}
