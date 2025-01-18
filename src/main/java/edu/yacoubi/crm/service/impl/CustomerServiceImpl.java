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

/**
 * Implementation of the Customer Service.
 *
 * <p>This class implements the logic for managing customers, including
 * creating, updating, and deleting customer records.</p>
 *
 * @author A. El Yacoubi
 */
@Service
@RequiredArgsConstructor
@Slf4j
// TODO validate method parameters
public class CustomerServiceImpl implements ICustomerService {
    private final CustomerRepository customerRepository;
    private final ICustomerCustomRepository customerCustomRepository;
    private final EntityValidator entityValidator;

    /**
     * Creates a new customer.
     *
     * @param customer the customer to be created
     * @return the created customer
     */
    @Override
    public Customer createCustomer(final Customer customer) {
        if (log.isInfoEnabled()) {
            log.info("::createCustomer started with: customer {}", customer);
        }

        customer.setId(null);
        final Customer savedCustomer = customerRepository.save(customer);

        if (log.isInfoEnabled()) {
            log.info("::createCustomer completed successfully");
        }
        return savedCustomer;
    }

    /**
     * Retrieves a customer by their ID.
     *
     * @param customerId the ID of the customer to retrieve
     * @return an Optional containing the found customer, or an empty Optional if no customer was found
     */
    @Override
    public Optional<Customer> getCustomerById(final Long customerId) {
        if (log.isInfoEnabled()) {
            log.info("::getCustomerById started with: customerId {}", customerId);
        }

        entityValidator.validateCustomerExists(customerId);

        final Optional<Customer> optionalCustomer = customerRepository.findById(customerId);

        if (log.isInfoEnabled()) {
            log.info("::getCustomerById completed successfully");
        }
        return optionalCustomer;
    }

    /**
     * Updates an existing customer by their ID.
     *
     * @param customerId      the ID of the customer to update
     * @param customerRequest the updated customer details
     * @return the updated customer
     */
    @Override
    @Transactional
    public Customer updateCustomer(final Long customerId, final Customer customerRequest) {
        if (log.isInfoEnabled()) {
            log.info("::updateCustomer started with: customerId {}, customer {}",
                    customerId, customerRequest);
        }

        // Validate that the customer exists
        entityValidator.validateCustomerExists(customerId);

        // Load the existing customer
        final Customer existingCustomer = customerRepository.findById(customerId).get();

        // Load the existing notes
        final List<Note> existingNotes = existingCustomer.getNotes();

        // Set the ID and other unchanged properties
        customerRequest.setId(customerId);
        customerRequest.setEmployee(existingCustomer.getEmployee());
        customerRequest.setNotes(existingNotes); // Set the existing notes

        final Customer updatedCustomer = customerRepository.save(customerRequest);

        if (log.isInfoEnabled()) {
            log.info("::updateCustomer completed successfully");
        }
        return updatedCustomer;
    }

    /**
     * Deletes a customer by their ID.
     *
     * @param customerId the ID of the customer to delete
     */
    @Override
    public void deleteCustomer(final Long customerId) {
        if (log.isInfoEnabled()) {
            log.info("::deleteCustomer started with: customerId {}", customerId);
        }

        entityValidator.validateCustomerExists(customerId);

        customerRepository.deleteById(customerId);

        if (log.isInfoEnabled()) {
            log.info("::deleteCustomer completed successfully");
        }
    }

    /**
     * Retrieves a customer by their email.
     *
     * @param email the email of the customer to retrieve
     * @return an Optional containing the found customer, or an empty Optional if no customer was found
     */
    @Override
    public Optional<Customer> getCustomerByEmail(final String email) {
        if (log.isInfoEnabled()) {
            log.info("::getCustomerByEmail started with: email {}", email);
        }

        final Optional<Customer> optionalCustomer = customerRepository.findByEmail(email);

        if (log.isInfoEnabled()) {
            log.info("::getCustomerByEmail completed successfully");
        }
        return optionalCustomer;
    }

    /**
     * Retrieves a list of customers based on example criteria.
     *
     * @param customerDTO the criteria for finding customers
     * @return a list of customers matching the example criteria
     */
    @Override
    public List<Customer> getCustomersByExample(final CustomerRequestDTO customerDTO) {
        if (log.isInfoEnabled()) {
            log.info("::getCustomersByExample started with: customerDTO {}", customerDTO);
        }

        final Customer customerProbe = new Customer();

        if (customerDTO.getFirstName() != null) {
            customerProbe.setFirstName(customerDTO.getFirstName());
        }
        if (customerDTO.getLastName() != null) {
            customerProbe.setLastName(customerDTO.getLastName());
        }
        if (customerDTO.getEmail() != null) {
            customerProbe.setEmail(customerDTO.getEmail());
        }
        // Set other fields as needed

        final ExampleMatcher matcher = ExampleMatcher.matching()
                .withIgnorePaths("id", "notes"); // Ignore fields that are not relevant
        //.withIncludeNullValues();

        final Example<Customer> example = Example.of(customerProbe, matcher);
        final List<Customer> customers = customerRepository.findAll(example);

        if (log.isInfoEnabled()) {
            log.info("::getCustomersByExample completed successfully");
        }
        return customers;
    }
    // TODO Later
    /**
     * import org.springframework.data.jpa.domain.Specification;
     *
     * public List<Customer> findCustomersByCriteria(CustomerRequestDTO customerDTO) {
     *     Specification<Customer> specification = (root, query, criteriaBuilder) -> {
     *         List<Predicate> predicates = new ArrayList<>();
     *
     *         if (customerDTO.getFirstName() != null) {
     *             predicates.add(criteriaBuilder.equal(root.get("firstName"), customerDTO.getFirstName()));
     *         }
     *         if (customerDTO.getLastName() != null) {
     *             predicates.add(criteriaBuilder.equal(root.get("lastName"), customerDTO.getLastName()));
     *         }
     *         if (customerDTO.getEmail() != null) {
     *             predicates.add(criteriaBuilder.equal(root.get("email"), customerDTO.getEmail()));
     *         }
     *         // Füge weitere Felder nach Bedarf hinzu
     *
     *         return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
     *     };
     *
     *     return customerRepository.findAll(specification);
     * }
     */

    /**
     * Updates a customer by example criteria.
     *
     * @param customerExample the example criteria for updating the customer
     * @param customerId      the ID of the customer to update
     * @return the updated customer
     * @deprecated This method is intended for search example only, though it functions correctly.
     */
    @Override
    @Transactional
    @Deprecated // Example only for search, though the method functions.
    public Customer updateCustomerByExample(
            final CustomerRequestDTO customerExample,
            final Long customerId) {
        if (log.isInfoEnabled()) {
            log.info("::updateCustomerByExample started with: customerExample {}, customerId {}",
                    customerExample, customerId);
        }

        entityValidator.validateCustomerExists(customerId);

        final Customer updatedCustomer = customerRepository
                .updateCustomerByExample(customerExample, customerId);

        if (log.isInfoEnabled()) {
            log.info("::updateCustomerByExample completed successfully");
        }
        return updatedCustomer;
    }

    /**
     * Retrieves a customer by their email, including their notes and the customers assigned to the employee.
     *
     * @param email the email of the customer to retrieve
     * @return an Optional containing the found customer, or an empty Optional if no customer was found
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<Customer> getCustomerByEmailWithNotesAndEmployeeCustomers(
            final String email) {
        if (log.isInfoEnabled()) {
            log.info("::getCustomerByEmailWithNotesAndEmployeeCustomers started with: email: {}", email);
        }

        final Optional<Customer> optionalCustomer = customerRepository
                .findByEmailWithNotesAndEmployeeCustomers(email);

        if (log.isInfoEnabled()) {
            log.info("::getCustomerByEmailWithNotesAndEmployeeCustomers completed successfully");
        }
        return optionalCustomer;
    }

    /**
     * Retrieves a customer by their ID, including their notes.
     *
     * @param customerId the ID of the customer to retrieve
     * @return the customer with their notes initialized
     */
    @Override
    @Transactional(readOnly = true)
    public Customer getCustomerWithNotes(final Long customerId) {
        if (log.isInfoEnabled()) {
            log.info("::getCustomerWithNotes started with: customerId {}", customerId);
        }

        entityValidator.validateCustomerExists(customerId);

        final Customer customer = customerRepository.findById(customerId).get();
        // Accessing notes to initialize them
        customer.getNotes().size();

        if (log.isInfoEnabled()) {
            log.info("::getCustomerWithNotes completed successfully");
        }
        return customer;
    }

    /**
     * Partially updates a customer's details.
     *
     * @param customerId       the ID of the customer to update
     * @param customerPatchDTO the partial update details
     */
    @Override
    @Transactional
    public void partialUpdateCustomer(
            final Long customerId,
            final CustomerPatchDTO customerPatchDTO) {
        if (log.isInfoEnabled()) {
            log.info("::partialUpdateCustomer started: customerId {}, customerPatchDTO {}",
                    customerId, customerPatchDTO);
        }

        entityValidator.validateCustomerExists(customerId);

        // delegate
        customerCustomRepository.partialUpdateCustomer(customerId, customerPatchDTO);

        if (log.isInfoEnabled()) {
            log.info("::partialUpdateCustomer completed successfully");
        }
    }

    /**
     * Retrieves a paginated list of customers by their first name or email.
     *
     * @param search the search string to match against first name or email
     * @param page   the page number to retrieve
     * @param size   the number of customers per page
     * @return a paginated list of customers matching the search criteria
     */
    @Override
    public Page<Customer> getCustomersByFirstNameOrEmail(
            final String search,
            final int page,
            final int size) {
        if (log.isInfoEnabled()) {
            log.info("::getCustomersByFirstNameOrEmail started with: search: {}, page: {}, size: {}",
                    search, page, size);
        }

        final Pageable pageable = PageRequest.of(page, size);
        final Page<Customer> customerPage = customerRepository
                .findByFirstNameContainingIgnoreCaseOrEmailContainingIgnoreCase(search, search, pageable);

        if (log.isInfoEnabled()) {
            log.info("::getCustomersByFirstNameOrEmail completed successfully");
        }
        return customerPage;
    }

    /**
     * Retrieves a paginated list of customers.
     *
     * @param page the page number to retrieve
     * @param size the number of customers per page
     * @return a paginated list of customers
     */
    @Override
    public Page<Customer> getCustomersWithPagination(
            final int page,
            final int size) {
        if (log.isInfoEnabled()) {
            log.info("::getCustomersWithPagination started with: page: {}, size: {}",
                    page, size);
        }

        final Pageable pageable = PageRequest.of(page, size);
        final Page<Customer> customerPage = customerRepository.findAll(pageable);

        if (log.isInfoEnabled()) {
            log.info("::getCustomersWithPagination completed successfully");
        }
        return customerPage;
    }

    /**
     * Retrieves a list of customers by the employee's ID.
     *
     * @param employeeId the ID of the employee whose customers to retrieve
     * @return a list of customers assigned to the employee
     */
    @Override
    public List<Customer> getCustomersByEmployeeId(final Long employeeId) {
        if (log.isInfoEnabled()) {
            log.info("::getCustomersByEmployeeId started with: employeeId: {}", employeeId);
        }

        entityValidator.validateEmployeeExists(employeeId);

        final List<Customer> customers = customerRepository.findByEmployeeId(employeeId);

        if (log.isInfoEnabled()) {
            log.info("::getCustomersByEmployeeId completed successfully");
        }
        return customers;
    }

    /**
     * Updates a list of customers.
     *
     * @param customers the list of customers to update
     * @throws IllegalArgumentException if the customer list is null or empty, or if any customer does not have an assigned employee
     */
    @Override
    public void updateCustomers(final List<Customer> customers) {
        if (log.isInfoEnabled()) {
            log.info("::updateCustomers started with: customers {}", customers);
        }

        if (customers == null || customers.isEmpty()) {
            log.warn("No customers provided for update");
            throw new IllegalArgumentException("Customer list must not be null or empty");
        }

        // Check if all customers have an assigned employee
        final boolean allCustomersHaveEmployee = customers.stream()
                .allMatch(customer -> customer.getEmployee() != null);

        if (!allCustomersHaveEmployee) {
            log.warn("One or more customers do not have an assigned employee");
            throw new IllegalArgumentException("All customers must have an assigned employee");
        }

        customerRepository.saveAll(customers);

        if (log.isInfoEnabled()) {
            log.info("::updateCustomers completed successfully");
        }
    }
}
