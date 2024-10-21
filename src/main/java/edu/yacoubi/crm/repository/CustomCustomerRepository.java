package edu.yacoubi.crm.repository;

import edu.yacoubi.crm.dto.CustomerDTO;
import edu.yacoubi.crm.model.Customer;

public interface CustomCustomerRepository {
    Customer  updateCustomerByExample(CustomerDTO customerExample, Long id);
}
