package edu.yacoubi.crm.service.impl;

import edu.yacoubi.crm.dto.CustomerDTO;
import edu.yacoubi.crm.exception.ResourceNotFoundException;
import edu.yacoubi.crm.model.Customer;
import edu.yacoubi.crm.repository.CustomerRepository;
import edu.yacoubi.crm.service.ICustomerService;
//import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomerServiceImpl implements ICustomerService {
    private final CustomerRepository customerRepository;

    @Override
    public List<Customer> getAllCustomers() {
        log.info("Fetching all Customers");
        return customerRepository.findAll();
    }

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
    public List<Customer> getCustomersByExample(CustomerDTO customerDTO) {
        Customer customerProbe = new Customer();

        if (customerDTO.getFirstName() != null) {
            customerProbe.setFirstName(customerDTO.getFirstName());
        }
        if (customerDTO.getLastName() != null) {
            customerProbe.setLastName(customerDTO.getLastName());
        }
        if (customerDTO.getEmail() != null) {
            customerProbe.setEmail(customerDTO.getEmail());
        }
        // Setze weitere Felder nach Bedarf

        ExampleMatcher matcher = ExampleMatcher.matching()
                .withIgnorePaths("id", "notes"); // Ignoriere Felder, die nicht relevant sind
        //.withIncludeNullValues();

        Example<Customer> example = Example.of(customerProbe, matcher);
        return customerRepository.findAll(example);
    }

    @Override
    @Transactional
    public Customer updateCustomerByExample(CustomerDTO customerExample, Long id) {
        log.info("Updating Customer with ID: {} using example", id);
        Customer existingCustomer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with ID: " + id));
        return customerRepository.updateCustomerByExample(customerExample, id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Customer> getCustomerByEmailWithNotesAndEmployeeCustomers(String email) {
        return customerRepository.findByEmailWithNotesAndEmployeeCustomers(email);
    }

    @Override
    @Transactional(readOnly = true)
    public Customer getCustomerWithNotes(Long id) {
        log.info("Fetching customer with ID: {} including notes", id);
        Customer customer = customerRepository
                .findById(id)
                .orElseThrow(() -> new RuntimeException("Kunde nicht gefunden"));
        customer.getNotes().size(); // Durch den Zugriff werden die Notes initialisiert
        return customer;
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
