package edu.yacoubi.crm.repository;

import edu.yacoubi.crm.dto.customer.CustomerRequestDTO;
import edu.yacoubi.crm.model.Customer;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaUpdate;
import jakarta.persistence.criteria.Root;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public class CustomCustomerRepositoryImpl implements CustomCustomerRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Customer updateCustomerByExample(CustomerRequestDTO customerExample, Long id) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaUpdate<Customer> update = cb.createCriteriaUpdate(Customer.class);
        Root<Customer> root = update.from(Customer.class);

        boolean hasUpdates = false;

        if (customerExample.getFirstName() != null) {
            update.set("firstName", customerExample.getFirstName());
            hasUpdates = true;
        }
        if (customerExample.getLastName() != null) {
            update.set("lastName", customerExample.getLastName());
            hasUpdates = true;
        }
        if (customerExample.getEmail() != null) {
            update.set("email", customerExample.getEmail());
            hasUpdates = true;
        }
        if (customerExample.getAddress() != null) {
            update.set("address", customerExample.getAddress());
            hasUpdates = true;
        }
        // Füge weitere Felder nach Bedarf hinzu

        if (hasUpdates) {
            update.where(cb.equal(root.get("id"), id));
            entityManager.createQuery(update).executeUpdate();
            entityManager.flush();
            entityManager.clear();
            return entityManager.find(Customer.class, id); // Rückgabe des aktualisierten Kunden
        } else {
            throw new InvalidDataAccessApiUsageException("No updates specified for the customer.");
        }
    }
}

