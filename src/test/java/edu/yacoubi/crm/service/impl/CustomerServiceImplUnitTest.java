package edu.yacoubi.crm.service.impl;

import edu.yacoubi.crm.TestDataUtil;
import edu.yacoubi.crm.model.Customer;
import edu.yacoubi.crm.repository.CustomerRepository;
import edu.yacoubi.crm.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;

class CustomerServiceImplUnitTest {
    @Mock
    private CustomerRepository customerRepository;

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
        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer)); // Verwende eine spezifische ID
        when(customerRepository.existsById(customerId)).thenReturn(true); // Mock für existierende ID hinzufügen

        // When
        Optional<Customer> foundCustomer = underTest.getCustomerById(customerId);

        // Logging for debugging
        System.out.println("Found Customer: " + foundCustomer);

        // Then
        assertTrue(foundCustomer.isPresent());
        assertEquals(customer.getEmail(), foundCustomer.get().getEmail());
        verify(customerRepository, times(1)).findById(customerId);
        verify(customerRepository, times(1)).existsById(customerId); // Überprüfung der Mock-Interaktionen
    }

    @Test
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
        when(customerRepository.existsById(customerId)).thenReturn(false);

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

}