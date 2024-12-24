package edu.yacoubi.crm.service.impl;

import edu.yacoubi.crm.dto.customer.CustomerPatchDTO;
import edu.yacoubi.crm.dto.customer.CustomerRequestDTO;
import edu.yacoubi.crm.model.Customer;
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
public class CustomerServiceImpl implements ICustomerService {
    private final CustomerRepository customerRepository;
    private final ICustomerCustomRepository customerCustomRepository;
    private final EntityValidator entityValidator;

    @Override
    public Customer createCustomer(Customer customer) {
        log.info("CustomerServiceImpl::createCustomer execution start: customer {}", customer);

        Customer savedCustomer = customerRepository.save(customer);

        log.info("CustomerServiceImpl::createCustomer execution end");
        return savedCustomer;
    }

    @Override
    public Optional<Customer> getCustomerById(Long customerId) {
        log.info("CustomerServiceImpl::getCustomerById execution start: customerId {}", customerId);

        entityValidator.validateCustomerExists(customerId);

        Optional<Customer> optionalCustomer = customerRepository.findById(customerId);

        log.info("CustomerServiceImpl::getCustomerById execution end");
        return optionalCustomer;
    }

    @Override
    // Aktualisieren eines bestehenden Kunden
    public Customer updateCustomer(Long customerId, Customer customer) {
        log.info("CustomerServiceImpl::updateCustomer execution start: customerId {}, customer {}", customerId, customer);

        entityValidator.validateCustomerExists(customerId);

        Customer updatedCustomer = customerRepository.save(customer);

        log.info("CustomerServiceImpl::updateCustomer execution end");
        return updatedCustomer;
    }

    @Override
    public void deleteCustomer(Long customerId) {
        log.info("CustomerServiceImpl::deleteCustomer execution start: customerId {}", customerId);

        entityValidator.validateCustomerExists(customerId);

        customerRepository.deleteById(customerId);

        log.info("CustomerServiceImpl::deleteCustomer execution end");
    }

    @Override
    public Optional<Customer> getCustomerByEmail(String email) {
        log.info("CustomerServiceImpl::getCustomerByEmail execution start: email {}", email);

        Optional<Customer> optionalCustomer = customerRepository.findByEmail(email);

        log.info("CustomerServiceImpl::getCustomerByEmail execution end");
        return optionalCustomer;
    }

    @Override
    public List<Customer> getCustomersByExample(CustomerRequestDTO customerDTO) {
        log.info("CustomerServiceImpl::getCustomersByExample execution start: customerDTO {}", customerDTO);
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

        log.info("CustomerServiceImpl::getCustomersByExample execution end");
        return customers;
    }

    @Override
    @Transactional
    @Deprecated // Example nur für suche, obwohl die Methode funktioniert.
    public Customer updateCustomerByExample(CustomerRequestDTO customerExample, Long customerId) {
        log.info("CustomerServiceImpl::updateCustomerByExample execution start: customerId {}, customerExample {}", customerId, customerExample);

        entityValidator.validateCustomerExists(customerId);

        Customer updatedCustomer = customerRepository.updateCustomerByExample(customerExample, customerId);

        log.info("CustomerServiceImpl::updateCustomerByExample execution end");
        return updatedCustomer;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Customer> getCustomerByEmailWithNotesAndEmployeeCustomers(String email) {
        log.info("CustomerServiceImpl::getCustomerByEmailWithNotesAndEmployeeCustomers execution start: email: {}", email);

        Optional<Customer> optionalCustomer = customerRepository.findByEmailWithNotesAndEmployeeCustomers(email);

        log.info("CustomerServiceImpl::getCustomerByEmailWithNotesAndEmployeeCustomers execution end");
        return optionalCustomer;
    }

    @Override
    @Transactional(readOnly = true)
    public Customer getCustomerWithNotes(Long customerId) {
        log.info("CustomerServiceImpl::getCustomerWithNotes execution start: customerId {}", customerId);

        entityValidator.validateCustomerExists(customerId);

        Customer customer = customerRepository.findById(customerId).get();
        // Durch den Zugriff werden die Notes initialisiert
        customer.getNotes().size();

        log.info("CustomerServiceImpl::getCustomerWithNotes execution end");
        return customer;
    }

    @Override
    @Transactional
    public void partialUpdateCustomer(Long customerId, CustomerPatchDTO customerPatchDTO) {
        log.info("CustomerServiceImpl::partialUpdateCustomer execution start: customerId {}, customerPatchDTO {}", customerId, customerPatchDTO);

        entityValidator.validateCustomerExists(customerId);

        // delegate
        customerCustomRepository.partialUpdateCustomer(customerId, customerPatchDTO);

        log.info("CustomerServiceImpl::partialUpdateCustomer execution end");
    }

    @Override
    public Page<Customer> getCustomersByFirstNameOrEmail(String search, int page, int size) {
        log.info("CustomerServiceImpl::getCustomersByFirstNameOrEmail execution start: searchString: {}, page: {}, size: {}", search, page, size);

        Pageable pageable = PageRequest.of(page, size);
        Page<Customer> customerPage = customerRepository
                .findByFirstNameContainingIgnoreCaseOrEmailContainingIgnoreCase(search, search, pageable);

        log.info("CustomerServiceImpl::getCustomersByFirstNameOrEmail execution end");
        return customerPage;
    }

    @Override
    public Page<Customer> getCustomersWithPagination(int page, int size) {
        log.info("CustomerServiceImpl::getCustomersWithPagination execution start: page: {}, size: {}", page, size);

        Pageable pageable = PageRequest.of(page, size);
        Page<Customer> customerPage = customerRepository.findAll(pageable);

        log.info("CustomerServiceImpl::getCustomersWithPagination execution end");
        return customerPage;
    }

    @Override
    public List<Customer> getCustomersByEmployeeId(Long employeeId) {
        log.info("CustomerServiceImpl::getCustomersByEmployeeId execution start: employeeId: {}", employeeId);

        entityValidator.validateEmployeeExists(employeeId);

        List<Customer> customers = customerRepository.findByEmployeeId(employeeId);

        log.info("CustomerServiceImpl::getCustomersByEmployeeId execution end");
        return customers;
    }
}
