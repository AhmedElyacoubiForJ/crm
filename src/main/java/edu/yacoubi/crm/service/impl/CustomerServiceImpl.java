package edu.yacoubi.crm.service.impl;

import edu.yacoubi.crm.dto.customer.CustomerPatchDTO;
import edu.yacoubi.crm.dto.customer.CustomerRequestDTO;
import edu.yacoubi.crm.exception.ResourceNotFoundException;
import edu.yacoubi.crm.model.Customer;
import edu.yacoubi.crm.repository.CustomerRepository;
import edu.yacoubi.crm.service.ICustomerService;
import edu.yacoubi.crm.service.validation.EntityValidator;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaUpdate;
import jakarta.persistence.criteria.Root;
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
public class CustomerServiceImpl implements ICustomerService {

    private final CustomerRepository customerRepository;
    private final EntityManager entityManager;
    private final EntityValidator entityValidator;

    @Override
    public Customer createCustomer(Customer customer) {
        log.info("CustomerServiceImpl::createCustomer customer {}", customer);
        return customerRepository.save(customer);
    }

    @Override
    public Optional<Customer> getCustomerById(Long customerId) {
        log.info("CustomerServiceImpl::getCustomerById customerId {}", customerId);

        entityValidator.validateCustomerExists(customerId);

        return customerRepository.findById(customerId);
    }

    @Override
    // Aktualisieren eines bestehenden Kunden
    public Customer updateCustomer(Long customerId, Customer customer) {
        log.info("CustomerServiceImpl::updateCustomer customerId {}, customer {}", customerId, customer);
        entityValidator.validateCustomerExists(customerId);
        return customerRepository.save(customer);
    }

    @Override
    public void deleteCustomer(Long customerId) {
        log.info("CustomerServiceImpl::deleteCustomer customerId {}", customerId);
        entityValidator.validateCustomerExists(customerId);
        customerRepository.deleteById(customerId);
    }

    @Override
    public Optional<Customer> getCustomerByEmail(String email) {
        log.info("CustomerServiceImpl::getCustomerByEmail email {}", email);
        return customerRepository.findByEmail(email);
    }

    @Override
    public List<Customer> getCustomersByExample(CustomerRequestDTO customerDTO) {
        log.info("CustomerServiceImpl::getCustomersByExample customerDTO {}", customerDTO);
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
    @Deprecated // Example nur für suche, obwohl die Methode funktioniert.
    public Customer updateCustomerByExample(CustomerRequestDTO customerExample, Long customerId) {
        log.info("CustomerServiceImpl::updateCustomerByExample id {}, customerExample {}", customerId, customerExample);
        Customer existingCustomer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with ID: " + customerId));
        return customerRepository.updateCustomerByExample(customerExample, customerId);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Customer> getCustomerByEmailWithNotesAndEmployeeCustomers(String email) {
        log.info("CustomerServiceImpl::getCustomerByEmailWithNotesAndEmployeeCustomers email: {}", email);
        return customerRepository.findByEmailWithNotesAndEmployeeCustomers(email);
    }

    @Override
    @Transactional(readOnly = true)
    public Customer getCustomerWithNotes(Long customerId) {
        log.info("CustomerServiceImpl::getCustomerWithNotes customerId {}", customerId);

        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found with ID: " + customerId));
        customer.getNotes().size(); // Durch den Zugriff werden die Notes initialisiert

        return customer;
    }

    @Override
    @Transactional
    public void partialUpdateCustomer(Long customerId, CustomerPatchDTO customerPatchDTO) {
        log.info("CustomerServiceImpl::partialUpdateCustomer customerId {}, customerPatchDTO {}", customerId, customerPatchDTO);

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

        log.info("Partial update executed for customer ID: {}", customerId);
        entityManager.createQuery(update).executeUpdate();
    }

    @Override
    public Page<Customer> getCustomersByFirstNameOrEmail(String search, int page, int size) {
        log.info("CustomerServiceImpl::getCustomersByFirstNameOrEmail searchString: {}, page: {}, size: {}", search, page, size);

        Pageable pageable = PageRequest.of(page, size);
        return customerRepository
                .findByFirstNameContainingIgnoreCaseOrEmailContainingIgnoreCase(search, search, pageable);
    }

    @Override
    public Page<Customer> getCustomersWithPagination(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return customerRepository.findAll(pageable);
    }

    @Override
    public List<Customer> getCustomersByEmployeeId(Long employeeId) {
        log.info("CustomerServiceImpl::getCustomersByEmployeeId employeeId: {}", employeeId);

        entityValidator.validateEmployeeExists(employeeId);

        log.info("Get customers by employee ID: {}", employeeId);
        return customerRepository.findByEmployeeId(employeeId);
    }
}
