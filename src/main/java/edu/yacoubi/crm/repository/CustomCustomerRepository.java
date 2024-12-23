package edu.yacoubi.crm.repository;

import edu.yacoubi.crm.dto.customer.CustomerRequestDTO;
import edu.yacoubi.crm.model.Customer;

public interface CustomCustomerRepository {
    Customer  updateCustomerByExample(CustomerRequestDTO customerExample, Long id);
}
