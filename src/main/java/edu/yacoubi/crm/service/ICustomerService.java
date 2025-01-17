package edu.yacoubi.crm.service;

import edu.yacoubi.crm.dto.customer.CustomerPatchDTO;
import edu.yacoubi.crm.dto.customer.CustomerRequestDTO;
import edu.yacoubi.crm.model.Customer;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Optional;

/**
 * Service interface for managing Customer entities.
 */
public interface ICustomerService {

    /**
     * Creates a new customer.
     *
     * @param customer the customer to be created.
     * @return the created customer.
     */
    Customer createCustomer(Customer customer);

    /**
     * Retrieves a customer by their ID.
     *
     * @param customerId the ID of the customer.
     * @return an Optional containing the found customer, or empty if not found.
     */
    Optional<Customer> getCustomerById(Long customerId);

    /**
     * Updates an existing customer.
     *
     * @param customerId the ID of the customer to be updated.
     * @param customer   the updated customer information.
     * @return the updated customer.
     */
    Customer updateCustomer(Long customerId, Customer customer);

    /**
     * Deletes a customer by their ID.
     *
     * @param customerId the ID of the customer to be deleted.
     */
    void deleteCustomer(Long customerId);

    /**
     * Retrieves a customer by their email.
     *
     * @param email the email of the customer.
     * @return an Optional containing the found customer, or empty if not found.
     */
    Optional<Customer> getCustomerByEmail(String email);

    /**
     * Retrieves customers based on an example customer DTO.
     *
     * @param customerDTO the example customer DTO.
     * @return a list of customers matching the example.
     */
    List<Customer> getCustomersByExample(CustomerRequestDTO customerDTO);

    /**
     * Updates an existing customer based on an example customer DTO.
     *
     * @param customerExample the example customer DTO.
     * @param customerId      the ID of the customer to be updated.
     * @return the updated customer.
     */
    Customer updateCustomerByExample(CustomerRequestDTO customerExample, Long customerId);

    /**
     * Retrieves a customer by their email, including notes and employee customers.
     *
     * @param email the email of the customer.
     * @return an Optional containing the found customer, or empty if not found.
     */
    Optional<Customer> getCustomerByEmailWithNotesAndEmployeeCustomers(String email);

    /**
     * Retrieves a customer along with their notes.
     *
     * @param customerId the ID of the customer.
     * @return the customer with their notes.
     */
    Customer getCustomerWithNotes(Long customerId);

    /**
     * Partially updates an existing customer based on a patch DTO.
     *
     * @param customerId       the ID of the customer to be partially updated.
     * @param customerPatchDTO the partial update information.
     */
    void partialUpdateCustomer(Long customerId, CustomerPatchDTO customerPatchDTO);

    /**
     * Retrieves customers by either their first name or email, in a paginated format.
     *
     * @param search the search string for the first name or email.
     * @param page   the page number to retrieve.
     * @param size   the number of customers per page.
     * @return a page of customers matching the search criteria.
     */
    Page<Customer> getCustomersByFirstNameOrEmail(String search, int page, int size);

    /**
     * Retrieves customers in a paginated format.
     *
     * @param page the page number to retrieve.
     * @param size the number of customers per page.
     * @return a page of customers.
     */
    Page<Customer> getCustomersWithPagination(int page, int size);

    /**
     * Retrieves customers by their employee ID.
     *
     * @param employeeId the ID of the employee.
     * @return a list of customers assigned to the employee.
     */
    List<Customer> getCustomersByEmployeeId(Long employeeId);

    /**
     * Updates a list of customers.
     *
     * @param customers the list of customers to be updated.
     */
    void updateCustomers(List<Customer> customers);
}