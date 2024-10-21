package edu.yacoubi.crm.service.impl;

import edu.yacoubi.crm.model.Customer;
import edu.yacoubi.crm.repository.CustomerRepository;
import edu.yacoubi.crm.service.ICustomerService;
import edu.yacoubi.crm.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomerServiceImpl implements ICustomerService {
    private final CustomerRepository customerRepository;

    @Override
    // Erstellen eines neuen Kunden
    public Customer createCustomer(Customer customer) {
        log.info("Creating new Customer");
        // Hier wird später die Validierungslogik hinzugefügt
        return customerRepository.save(customer);
    }

    @Override
    // Finden eines Kunden nach ID
    public Optional<Customer> getCustomerById(Long id) {
        log.info("Fetching customer with ID: {}", id);
        ensureCustomerExists(id);
        return customerRepository.findById(id);
    }

    @Override
    // Aktualisieren eines bestehenden Kunden
    public Customer updateCustomer(Long id, Customer customer) {
        log.info("Updating Customer with ID: {}", id);
        ensureCustomerExists(id);
        return customerRepository.save(customer);
    }

    @Override
    public void deleteCustomer(Long id) {
        log.info("Deleting Customer with ID: {}", id);
        ensureCustomerExists(id);
        customerRepository.deleteById(id);
    }

    @Override
    public Optional<Customer> getCustomerByEmail(String email) {
        log.info("Fetching customer with email: {}", email);
        return customerRepository.findByEmail(email);
    }

    @Override
    public void ensureCustomerExists(Long id) {
        log.info("Ensuring customer with ID: {} exists", id);
        if (!customerRepository.existsById(id)) {
            log.warn("Customer not found with ID: {}", id);
            throw new ResourceNotFoundException("Customer not found with ID: " + id);
        }
    }
}
