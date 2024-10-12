package edu.yacoubi.crm.repository;

import edu.yacoubi.crm.model.Customer;
import edu.yacoubi.crm.model.Employee;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class CustomerRepositoryTest {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Test
    public void testCreateReadUpdateDelete() {
        // Given
        // Create a new employee
        Employee employee = Employee.builder()
                .firstName("Jane")
                .lastName("Doe")
                .email("jane.doe@example.com")
                .department("Sales")
                .build();

        // Save the employee
        Employee savedEmployee = employeeRepository.save(employee);

        // Create a new customer and associate with saved employee
        Customer customer = Customer.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .phone("1234567890")
                .address("123 Main St")
                .lastInteractionDate(LocalDate.now())
                .employee(savedEmployee) // associate customer with employee
                .build();

        // Save the customer
        Customer savedCustomer = customerRepository.save(customer);

        // Read the customer
        Customer foundCustomer = customerRepository.findById(savedCustomer.getId()).orElse(null);
        assertNotNull(foundCustomer);
        assertEquals("John", foundCustomer.getFirstName());

        // Update the customer
        foundCustomer.setFirstName("Jane");
        customerRepository.save(foundCustomer);
        Customer updatedCustomer = customerRepository.findById(savedCustomer.getId()).orElse(null);
        assertNotNull(updatedCustomer);
        assertEquals("Jane", updatedCustomer.getFirstName());

        // Delete the customer
        customerRepository.delete(updatedCustomer);
        Customer deletedCustomer = customerRepository.findById(savedCustomer.getId()).orElse(null);
        assertNull(deletedCustomer);
    }

    @Test
    public void testCreateCustomerWithoutEmployeeFailed() {
        // Given
        // Create a new customer without an employee
        Customer customer = Customer.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .phone("1234567890")
                .address("123 Main St")
                .lastInteractionDate(LocalDate.now())
                .build();

        // When
        // This should throw an exception because the employee is missing
        DataIntegrityViolationException exception = assertThrows(DataIntegrityViolationException.class, () -> {
            customerRepository.save(customer);
        });

        // Then
        String expectedMessage = "not-null property references a null";
        assertTrue(exception.getMessage().contains(expectedMessage), expectedMessage);
    }

    @Test
    public void testFindCustomerByEmail() {
        // Given
        // Create a new employee
        Employee employee = Employee.builder()
                .firstName("Jane")
                .lastName("Doe")
                .email("jane.doe@example.com")
                .department("Sales")
                .build();

        // Save the employee
        Employee savedEmployee = employeeRepository.save(employee);
        // Create and save a new customer
        Customer customer = Customer.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .phone("1234567890")
                .address("123 Main St")
                .lastInteractionDate(LocalDate.now())
                .employee(savedEmployee)
                .build();
        customerRepository.save(customer);

        // Find the customer by email
        Customer foundCustomer = customerRepository.findByEmail("john.doe@example.com").orElse(null);
        assertNotNull(foundCustomer);
        assertEquals("john.doe@example.com", foundCustomer.getEmail());
    }
}