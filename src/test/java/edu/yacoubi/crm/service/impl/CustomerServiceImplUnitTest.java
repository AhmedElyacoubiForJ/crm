package edu.yacoubi.crm.service.impl;

import edu.yacoubi.crm.exception.ResourceNotFoundException;
import edu.yacoubi.crm.model.Customer;
import edu.yacoubi.crm.model.Employee;
import edu.yacoubi.crm.repository.CustomerRepository;
import edu.yacoubi.crm.service.validation.EntityValidator;
import edu.yacoubi.crm.util.TestDataUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CustomerServiceImplUnitTest {
    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private EntityValidator entityValidator;

    @InjectMocks
    private CustomerServiceImpl underTest;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void itShouldCreateCustomer() {
        // Given
        Customer customer = TestDataUtil.createCustomerA(null);
        when(customerRepository.save(any(Customer.class))).thenReturn(customer);

        // When
        Customer savedCustomer = underTest.createCustomer(customer);

        // Then
        assertNotNull(savedCustomer);
        verify(customerRepository, times(1)).save(customer);
    }

    @Test
    public void itShouldFindCustomerById() {
        // Given
        Long customerId = 1L;  // Setze eine spezifische ID
        Customer customer = TestDataUtil.createCustomerA(null);
        customer.setId(customerId);  // Setze die ID im Mock-Objekt
        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));
        doNothing().when(entityValidator).validateNoteExists(anyLong());

        // When
        Optional<Customer> foundCustomer = underTest.getCustomerById(customerId);

        // Then
        assertTrue(foundCustomer.isPresent());
        assertEquals(customer.getEmail(), foundCustomer.get().getEmail());
        verify(customerRepository, times(1)).findById(customerId);
    }

    // TODO FIX cause refactor of the service method
    //@Test
    public void itShouldUpdateCustomer() {
        // Given
        Long customerId = 1L;
        Customer customer = TestDataUtil.createCustomerA(null);
        customer.setId(customerId);
        when(customerRepository.existsById(customerId)).thenReturn(true);
        when(customerRepository.save(any(Customer.class))).thenReturn(customer);

        // When
        Customer updatedCustomer = underTest.updateCustomer(customerId, customer);

        // Then
        assertNotNull(updatedCustomer);
        verify(customerRepository, times(1)).save(customer);
    }

    @Test
    public void itShouldDeleteCustomer() {
        // Given
        Long customerId = 1L;
        Customer customer = TestDataUtil.createCustomerA(null);
        customer.setId(customerId);
        when(customerRepository.existsById(customerId)).thenReturn(true);

        // When
        underTest.deleteCustomer(customerId);

        // Then
        verify(customerRepository, times(1)).deleteById(customerId);
    }

    @Test
    public void itShouldFindCustomerPerEmail() {
        // Given
        String email = "test@example.com";
        Customer customer = TestDataUtil.createCustomerA(null);
        customer.setEmail(email);
        when(customerRepository.findByEmail(email)).thenReturn(Optional.of(customer));

        // When
        Optional<Customer> foundCustomer = underTest.getCustomerByEmail(email);

        // Then
        assertTrue(foundCustomer.isPresent());
        assertEquals(email, foundCustomer.get().getEmail());
        verify(customerRepository, times(1)).findByEmail(email);
    }

    @Test
    public void itShouldThrowExceptionWhenCustomerDoesNotExist() {
        // Given
        Long customerId = 1L;
        //when(customerRepository.existsById(customerId)).thenReturn(false);
        doThrow(new ResourceNotFoundException("Customer not found with ID: " + customerId))
                .when(entityValidator).validateCustomerExists(anyLong());

        // When & Then
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class, () -> underTest.getCustomerById(customerId));
        assertEquals("Customer not found with ID: " + customerId, exception.getMessage());
    }

    @Test
    public void itShouldNotThrowExceptionWhenCustomerExists() {
        // Given
        Long customerId = 1L;
        Customer customer = TestDataUtil.createCustomerA(null);
        customer.setId(customerId);
        when(customerRepository.existsById(customerId)).thenReturn(true);
        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));

        // When
        Optional<Customer> foundCustomer = underTest.getCustomerById(customerId);

        // Then
        assertTrue(foundCustomer.isPresent());
        assertEquals(customer.getEmail(), foundCustomer.get().getEmail());
    }

    @Test
    public void itShouldReturnCustomersByEmployeeId() {
        // Given
        Long employeeId = 1L;
        List<Customer> customers = Arrays.asList(
                TestDataUtil.createCustomerA(null),
                TestDataUtil.createCustomerB(null)
        );
        Employee employeeA = TestDataUtil.createEmployeeA();
        employeeA.setId(employeeId);
        customers.forEach(customer -> customer.setEmployee(employeeA));
        when(customerRepository.findByEmployeeId(employeeId)).thenReturn(customers);
        doNothing().when(entityValidator).validateEmployeeExists(employeeId);


        // When
        List<Customer> foundCustomers = underTest.getCustomersByEmployeeId(employeeId);

        // Then
        assertEquals(customers.size(), foundCustomers.size());
        assertTrue(foundCustomers.containsAll(customers));
        //assertEquals(customers, foundCustomers); // Wenn Reihenfolge eine Rolle spielt
        verify(customerRepository, times(1)).findByEmployeeId(employeeId);
    }
}