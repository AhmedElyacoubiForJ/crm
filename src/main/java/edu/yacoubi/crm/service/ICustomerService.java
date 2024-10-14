package edu.yacoubi.crm.service;

import edu.yacoubi.crm.model.Customer;

import java.util.Optional;

public interface ICustomerService {
    Customer createCustomer(Customer customer);

    Optional<Customer> getCustomerById(Long id);

    Customer updateCustomer(Long id, Customer customer);

    void deleteCustomer(Long id);

    Optional<Customer> getCustomerByEmail(String email);
}
