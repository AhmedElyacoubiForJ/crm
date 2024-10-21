package edu.yacoubi.crm.service.impl;

import edu.yacoubi.crm.TestDataUtil;
import edu.yacoubi.crm.model.Customer;
import edu.yacoubi.crm.model.Employee;
import edu.yacoubi.crm.repository.EmployeeRepository;
import edu.yacoubi.crm.repository.CustomerRepository;
import edu.yacoubi.crm.exception.ResourceNotFoundException;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CustomerServiceImplIntegrationTest {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private CustomerServiceImpl underTest;

    @BeforeEach
    public void setUp() {
        customerRepository.deleteAll();
        employeeRepository.deleteAll();
    }

    @Test
    public void itShouldCreateCustomer() {
        // Given
        Employee employee = TestDataUtil.createEmployeeA();
        Employee savedEmployee = employeeRepository.save(employee);
        Customer customer = TestDataUtil.createCustomerA(savedEmployee);

        // When
        Customer savedCustomer = underTest.createCustomer(customer);

        // Then
        assertNotNull(savedCustomer);
        assertEquals(customer.getFirstName(), savedCustomer.getFirstName());
        assertEquals(customer.getEmail(), savedCustomer.getEmail());
        assertEquals(customer.getEmployee().getId(), savedCustomer.getEmployee().getId());
    }

    @Test
    public void itShouldFindCustomerById() {
        // Given
        Employee employee = TestDataUtil.createEmployeeA();
        Employee savedEmployee = employeeRepository.save(employee);
        Customer customer = TestDataUtil.createCustomerA(savedEmployee);
        Customer savedCustomer = underTest.createCustomer(customer);

        // When
        Customer foundCustomer = underTest.getCustomerById(savedCustomer.getId()).orElse(null);

        // Then
        assertNotNull(foundCustomer);
        assertEquals(savedCustomer.getId(), foundCustomer.getId());
    }

    @Test
    @Transactional
    public void itShouldUpdateCustomer() {
        // Given
        Employee employee = TestDataUtil.createEmployeeA();
        Employee savedEmployee = employeeRepository.save(employee);
        Customer customer = TestDataUtil.createCustomerA(savedEmployee);
        Customer savedCustomer = underTest.createCustomer(customer);
        savedCustomer.setFirstName("Updated Name");

        // When
        Customer updatedCustomer = underTest.updateCustomer(savedCustomer.getId(), savedCustomer);

        // Then
        assertNotNull(updatedCustomer);
        assertEquals("Updated Name", updatedCustomer.getFirstName());
    }

    @Test
    public void itShouldDeleteCustomer() {
        // Given
        Employee employee = TestDataUtil.createEmployeeA();
        Employee savedEmployee = employeeRepository.save(employee);
        Customer customer = TestDataUtil.createCustomerA(savedEmployee);
        Customer savedCustomer = underTest.createCustomer(customer);

        // When
        underTest.deleteCustomer(savedCustomer.getId());

        // Then
        assertThrows(ResourceNotFoundException.class, () -> {
            underTest.getCustomerById(savedCustomer.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Customer not found with ID:"));
        });
    }

    @Test
    public void itShouldGetCustomerByEmail() {
        // Given
        Employee employee = TestDataUtil.createEmployeeA();
        Employee savedEmployee = employeeRepository.save(employee);
        Customer customer = TestDataUtil.createCustomerA(savedEmployee);
        Customer savedCustomer = underTest.createCustomer(customer);

        // When
        Customer foundCustomer = underTest.getCustomerByEmail(savedCustomer.getEmail()).orElse(null);

        // Then
        assertNotNull(foundCustomer);
        assertEquals(savedCustomer.getId(), foundCustomer.getId());
    }
}
