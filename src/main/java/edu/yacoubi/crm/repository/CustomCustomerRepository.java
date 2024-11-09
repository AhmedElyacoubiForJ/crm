package edu.yacoubi.crm.repository;

import edu.yacoubi.crm.dto.customer.CustomerRequestDTO;
import edu.yacoubi.crm.model.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CustomCustomerRepository {
    Customer  updateCustomerByExample(CustomerRequestDTO customerExample, Long id);
}
