package edu.yacoubi.crm.service.impl;

import edu.yacoubi.crm.dto.customer.CustomerPatchDTO;
import edu.yacoubi.crm.dto.customer.CustomerRequestDTO;
import edu.yacoubi.crm.model.Customer;
import edu.yacoubi.crm.model.Note;
import edu.yacoubi.crm.repository.CustomerRepository;
import edu.yacoubi.crm.repository.ICustomerCustomRepository;
import edu.yacoubi.crm.service.ICustomerService;
import edu.yacoubi.crm.service.validation.EntityValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
// TODO validate method parameters
public class CustomerServiceImpl implements ICustomerService {
    private final CustomerRepository customerRepository;
    private final ICustomerCustomRepository customerCustomRepository;
    private final EntityValidator entityValidator;

    @Override
    public Customer createCustomer(Customer customer) {
        log.info("::createCustomer started with: customer {}", customer);

        customer.setId(null);
        Customer savedCustomer = customerRepository.save(customer);

        log.info("::createCustomer completed successfully");
        return savedCustomer;
    }

    @Override
    public Optional<Customer> getCustomerById(Long customerId) {
        log.info("::getCustomerById started with: customerId {}", customerId);

        entityValidator.validateCustomerExists(customerId);

        Optional<Customer> optionalCustomer = customerRepository.findById(customerId);

        log.info("::getCustomerById completed successfully");
        return optionalCustomer;
    }

    @Override
    @Transactional
    public Customer updateCustomer(Long customerId, Customer customerRequest) {
        log.info("::updateCustomer started with: customerId {}, customer {}", customerId, customerRequest);

        // Überprüfen, ob der Kunde existiert
        entityValidator.validateCustomerExists(customerId);

        // Vorhandenen Kunden laden
        Customer existingCustomer = customerRepository.findById(customerId).get();

        // Laden der bestehenden Notizen
        List<Note> existingNotes = existingCustomer.getNotes();

        // Setzen der ID und anderer unveränderter Eigenschaften
        customerRequest.setId(customerId);
        customerRequest.setEmployee(existingCustomer.getEmployee());
        customerRequest.setNotes(existingNotes); // Setzen der bestehenden Notizen

        Customer updatedCustomer = customerRepository.save(customerRequest);

        log.info("::updateCustomer completed successfully");
        return updatedCustomer;
    }


    @Override
    public void deleteCustomer(Long customerId) {
        log.info("::deleteCustomer started with: customerId {}", customerId);

        entityValidator.validateCustomerExists(customerId);

        customerRepository.deleteById(customerId);

        log.info("::deleteCustomer completed successfully");
    }

    @Override
    public Optional<Customer> getCustomerByEmail(String email) {
        log.info("::getCustomerByEmail started with: email {}", email);

        Optional<Customer> optionalCustomer = customerRepository.findByEmail(email);

        log.info("::getCustomerByEmail completed successfully");
        return optionalCustomer;
    }

    @Override
    public List<Customer> getCustomersByExample(CustomerRequestDTO customerDTO) {
        log.info("::getCustomersByExample started with: customerDTO {}", customerDTO);
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
        List<Customer> customers = customerRepository.findAll(example);

        log.info("::getCustomersByExample completed successfully");
        return customers;
    }

    @Override
    @Transactional
    @Deprecated // Example nur für suche, obwohl die Methode funktioniert.
    public Customer updateCustomerByExample(CustomerRequestDTO customerExample, Long customerId) {
        log.info("::updateCustomerByExample started with: customerExample {}, customerId {}",
                customerExample, customerId);

        entityValidator.validateCustomerExists(customerId);

        Customer updatedCustomer = customerRepository.updateCustomerByExample(customerExample, customerId);

        log.info("::updateCustomerByExample completed successfully");
        return updatedCustomer;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Customer> getCustomerByEmailWithNotesAndEmployeeCustomers(String email) {
        log.info("::getCustomerByEmailWithNotesAndEmployeeCustomers started with: email: {}", email);

        Optional<Customer> optionalCustomer = customerRepository.findByEmailWithNotesAndEmployeeCustomers(email);

        log.info("::getCustomerByEmailWithNotesAndEmployeeCustomers completed successfully");
        return optionalCustomer;
    }

    @Override
    @Transactional(readOnly = true)
    public Customer getCustomerWithNotes(Long customerId) {
        log.info("::getCustomerWithNotes started with: customerId {}", customerId);

        entityValidator.validateCustomerExists(customerId);

        Customer customer = customerRepository.findById(customerId).get();
        // Durch den Zugriff werden die Notes initialisiert
        customer.getNotes().size();

        log.info("::getCustomerWithNotes completed successfully");
        return customer;
    }

    @Override
    @Transactional
    public void partialUpdateCustomer(Long customerId, CustomerPatchDTO customerPatchDTO) {
        log.info("::partialUpdateCustomer started: customerId {}, customerPatchDTO {}",
                customerId, customerPatchDTO);

        entityValidator.validateCustomerExists(customerId);

        // delegate
        customerCustomRepository.partialUpdateCustomer(customerId, customerPatchDTO);

        log.info("::partialUpdateCustomer completed successfully");
    }

    @Override
    public Page<Customer> getCustomersByFirstNameOrEmail(String search, int page, int size) {
        log.info("::getCustomersByFirstNameOrEmail started with: search: {}, page: {}, size: {}",
                search, page, size);

        Pageable pageable = PageRequest.of(page, size);
        Page<Customer> customerPage = customerRepository
                .findByFirstNameContainingIgnoreCaseOrEmailContainingIgnoreCase(search, search, pageable);

        log.info("::getCustomersByFirstNameOrEmail completed successfully");
        return customerPage;
    }

    @Override
    public Page<Customer> getCustomersWithPagination(int page, int size) {
        log.info("::getCustomersWithPagination started with: page: {}, size: {}", page, size);

        Pageable pageable = PageRequest.of(page, size);
        Page<Customer> customerPage = customerRepository.findAll(pageable);

        log.info("::getCustomersWithPagination completed successfully");
        return customerPage;
    }

    @Override
    public List<Customer> getCustomersByEmployeeId(Long employeeId) {
        log.info("::getCustomersByEmployeeId started with: employeeId: {}", employeeId);

        entityValidator.validateEmployeeExists(employeeId);

        List<Customer> customers = customerRepository.findByEmployeeId(employeeId);

        log.info("::getCustomersByEmployeeId completed successfully");
        return customers;
    }

    @Override
    public void updateCustomers(List<Customer> customers) {
        log.info("::updateCustomers started with: customers {}", customers);

        if (customers == null || customers.isEmpty()) {
            log.warn("No customers provided for update");
            throw new IllegalArgumentException("Customer list must not be null or empty");
        }

        // Überprüfen, ob alle Kunden ein zugewiesenes Employee-Objekt haben
        boolean allCustomersHaveEmployee = customers.stream()
                .allMatch(customer -> customer.getEmployee() != null);

        if (!allCustomersHaveEmployee) {
            log.warn("One or more customers do not have a assigned employee");
            throw new IllegalArgumentException("All customers must have an assigned employee");
        }

        customerRepository.saveAll(customers);
        log.info("::updateCustomers completed successfully");
    }
}
