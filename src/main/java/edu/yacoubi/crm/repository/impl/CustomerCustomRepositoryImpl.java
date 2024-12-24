package edu.yacoubi.crm.repository.impl;

import edu.yacoubi.crm.dto.customer.CustomerPatchDTO;
import edu.yacoubi.crm.model.Customer;
import edu.yacoubi.crm.repository.ICustomerCustomRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaUpdate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
@Slf4j
public class CustomerCustomRepositoryImpl implements ICustomerCustomRepository {
    private final EntityManager entityManager;
    //private final EntityValidator entityValidator;

    @Override
    @Transactional
    public void partialUpdateCustomer(Long customerId, CustomerPatchDTO customerPatchDTO) {
        log.info("CustomerCustomRepositoryImpl::partialUpdateCustomer execution start: customerId {}, customerPatchDTO {}", customerId, customerPatchDTO);

        // wird im service validiert
        // entityValidator.validateCustomerExists(customerId);

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaUpdate<Customer> update = cb.createCriteriaUpdate(Customer.class);
        Root<Customer> root = update.from(Customer.class);

        if (customerPatchDTO.getFirstName() != null) {
            update.set(root.get("firstName"), customerPatchDTO.getFirstName());
        }
        if (customerPatchDTO.getLastName() != null) {
            update.set(root.get("lastName"), customerPatchDTO.getLastName());
        }
        if (customerPatchDTO.getEmail() != null) {
            update.set(root.get("email"), customerPatchDTO.getEmail());
        }
        if (customerPatchDTO.getAddress() != null) {
            update.set(root.get("address"), customerPatchDTO.getAddress());
        }

        update.where(cb.equal(root.get("id"), customerId));

        entityManager.createQuery(update).executeUpdate();
        log.info("CustomerCustomRepositoryImpl::partialUpdateCustomer execution end");
    }
}
