package edu.yacoubi.crm.service.impl;

import edu.yacoubi.crm.dto.customer.CustomerPatchDTO;
import edu.yacoubi.crm.dto.customer.CustomerRequestDTO;
import edu.yacoubi.crm.exception.ResourceNotFoundException;
import edu.yacoubi.crm.model.Customer;
import edu.yacoubi.crm.model.Employee;
import edu.yacoubi.crm.repository.CustomerRepository;
import edu.yacoubi.crm.repository.EmployeeRepository;
import edu.yacoubi.crm.service.ICustomerService;
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

    private final EmployeeRepository employeeRepository;

    private final EntityManager entityManager;

    @Override
    public List<Customer> getAllCustomers() {
        log.info("CustomerServiceImpl::getAllCustomers");
        return customerRepository.findAll();
    }

    @Override
    public Customer createCustomer(Customer customer) {
        log.info("CustomerServiceImpl::createCustomer customer {}", customer);
        return customerRepository.save(customer);
    }

    @Override
    public Optional<Customer> getCustomerById(Long id) {
        log.info("CustomerServiceImpl::getCustomerById id {}", id);
        ensureCustomerExists(id);
        return customerRepository.findById(id);
    }

    @Override
    // Aktualisieren eines bestehenden Kunden
    public Customer updateCustomer(Long id, Customer customer) {
        log.info("CustomerServiceImpl::updateCustomer id {}, customer {}", id, customer);
        ensureCustomerExists(id);
        return customerRepository.save(customer);
    }

    @Override
    public void deleteCustomer(Long id) {
        log.info("CustomerServiceImpl::deleteCustomer id: {}", id);
        ensureCustomerExists(id);
        customerRepository.deleteById(id);
    }

    @Override
    public Optional<Customer> getCustomerByEmail(String email) {
        log.info("CustomerServiceImpl::getCustomerByEmail email: {}", email);
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
    public Customer updateCustomerByExample(CustomerRequestDTO customerExample, Long id) {
        log.info("CustomerServiceImpl::updateCustomerByExample id {}, customerExample {}", id, customerExample);
        Customer existingCustomer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with ID: " + id));
        return customerRepository.updateCustomerByExample(customerExample, id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Customer> getCustomerByEmailWithNotesAndEmployeeCustomers(String email) {
        log.info("CustomerServiceImpl::getCustomerByEmailWithNotesAndEmployeeCustomers email: {}", email);
        return customerRepository.findByEmailWithNotesAndEmployeeCustomers(email);
    }

    @Override
    @Transactional(readOnly = true)
    public Customer getCustomerWithNotes(Long id) {
        log.info("CustomerServiceImpl::getCustomerWithNotes id: {}", id);
        Customer customer = customerRepository
                .findById(id)
                .orElseThrow(() -> new RuntimeException("Kunde nicht gefunden"));
        customer.getNotes().size(); // Durch den Zugriff werden die Notes initialisiert
        return customer;
    }

    @Override
    @Transactional
    public void partialUpdateCustomer(Long id, CustomerPatchDTO customerPatchDTO) {
        log.info("CustomerServiceImpl::partialUpdateCustomer id {}, customerPatchDTO {}", id, customerPatchDTO);

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

        update.where(cb.equal(root.get("id"), id));

        log.info("Partial update executed for customer ID: {}", id);
        entityManager.createQuery(update).executeUpdate();
    }

    @Override
    public Page<Customer> getCustomersByFirstNameOrEmail(String search, int page, int size) {
        log.info("CustomerServiceImpl::getCustomersByFirstNameOrEmail searchString: {}, page: {}, size: {}", search, page, size);

        Pageable pageable = PageRequest.of(page, size);
        return customerRepository.findByFirstNameContainingIgnoreCaseOrEmailContainingIgnoreCase(
                search,
                search,
                pageable
        );
    }

    @Override
    public Page<Customer> getCustomersWithPagination(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return customerRepository.findAll(pageable);
    }

    @Override
    public void assignCustomerToEmployee(Long customerId, Long employeeId) {
        Customer customer = customerRepository.findById(customerId).orElseThrow(
                () -> new ResourceNotFoundException("Customer not found with ID: " + customerId)
        );

        Employee employee = employeeRepository.findById(employeeId).orElseThrow(
                () -> new ResourceNotFoundException("Employee not found with ID: " + employeeId)
        );

        customer.setEmployee(employee);
        customerRepository.save(customer);
    }

    @Override
    public List<Customer> getCustomersByEmployeeId(Long employeeId) {
        return customerRepository.findByEmployeeId(employeeId);
    }

    @Override
    public void ensureCustomerExists(Long id) {
        log.info("CustomerServiceImpl::ensureCustomerExists id: {} exists", id);
        if (!customerRepository.existsById(id)) {
            log.warn("Customer not found with ID: {}", id);
            throw new ResourceNotFoundException("Customer not found with ID: " + id);
        }
    }
}
