package edu.yacoubi.crm.repository;

import edu.yacoubi.crm.dto.customer.CustomerPatchDTO;

public interface ICustomerCustomRepository {
    void partialUpdateCustomer(Long customerId, CustomerPatchDTO customerPatchDTO);
}
