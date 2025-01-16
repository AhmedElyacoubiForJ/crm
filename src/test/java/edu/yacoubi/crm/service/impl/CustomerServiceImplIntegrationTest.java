package edu.yacoubi.crm.service.impl;

import edu.yacoubi.crm.dto.customer.CustomerRequestDTO;
import edu.yacoubi.crm.exception.ResourceNotFoundException;
import edu.yacoubi.crm.model.Customer;
import edu.yacoubi.crm.model.Employee;
import edu.yacoubi.crm.repository.CustomerRepository;
import edu.yacoubi.crm.repository.EmployeeRepository;
import edu.yacoubi.crm.util.TestDataUtil;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

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

    // TODO FIX cause refactor of the the new customer update method
    @Test
    @Transactional
    public void itShouldUpdateCustomer() {
        // Given
        Employee employee = TestDataUtil.createEmployeeA();
        Employee savedEmployee = employeeRepository.save(employee);

        Customer customer = TestDataUtil.createCustomerA(savedEmployee);
        Customer savedCustomer = underTest.createCustomer(customer);

        // Aktualisierung des Kundennamens
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

    @Test
    public void itShouldFindCustomerByExample() {
        // Given
        Employee employee = TestDataUtil.createEmployeeA();
        Employee savedEmployee = employeeRepository.save(employee);
        Customer customerA = TestDataUtil.createCustomerA(savedEmployee);
        Customer customerB = TestDataUtil.createCustomerB(savedEmployee);
        customerRepository.save(customerA);
        customerRepository.save(customerB);

        CustomerRequestDTO customerDTO = CustomerRequestDTO.builder().firstName("John").build();

        // When
        List<Customer> foundCustomers = underTest.getCustomersByExample(customerDTO);

        // Then
        assertEquals(1, foundCustomers.size());
        assertEquals(customerA.getId(), foundCustomers.get(0).getId());
    }

    @Test
    @Transactional
    public void itShouldReturnCustomersByEmployeeId() {
        // Given
        Employee employee = TestDataUtil.createEmployeeA();
        Employee savedEmployee = employeeRepository.save(employee);
        Customer customerA = TestDataUtil.createCustomerA(savedEmployee);
        Customer customerB = TestDataUtil.createCustomerB(savedEmployee);
        customerRepository.save(customerA);
        customerRepository.save(customerB);

        // When
        List<Customer> foundCustomers = underTest.getCustomersByEmployeeId(savedEmployee.getId());

        // Then
        assertEquals(2, foundCustomers.size());
        assertEquals(customerA.getFirstName(), foundCustomers.get(0).getFirstName());

        assertTrue(foundCustomers.contains(customerA));
        assertTrue(foundCustomers.contains(customerB));
    }
}
