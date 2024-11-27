package edu.yacoubi.crm.service.impl;

import edu.yacoubi.crm.TestDataUtil;
import edu.yacoubi.crm.model.Customer;
import edu.yacoubi.crm.model.Employee;
import edu.yacoubi.crm.model.InactiveEmployee;
import edu.yacoubi.crm.repository.CustomerRepository;
import edu.yacoubi.crm.repository.EmployeeRepository;
import edu.yacoubi.crm.service.ICustomerService;
import edu.yacoubi.crm.service.IInactiveEmployeeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class EmployeeServiceImplIntegrationTest {

    @Autowired
    private EmployeeServiceImpl underTest;

    @Autowired
    private ICustomerService customerService;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private IInactiveEmployeeService inactiveEmployeeService;

    @BeforeEach
    public void setUp() {
        //customerRepository.deleteAll();
        //employeeRepository.deleteAll();
    }

    @Test
    public void itShouldReassignCustomersAndDeleteEmployee() {
        // Given
        Long oldEmployeeId = 1L;
        Long newEmployeeId = 2L;

        Employee oldEmployee = TestDataUtil.createEmployeeA();
        oldEmployee.setId(oldEmployeeId);
        Employee newEmployee = TestDataUtil.createEmployeeB();
        newEmployee.setId(newEmployeeId);

        Customer customerA = TestDataUtil.createCustomerA(oldEmployee);
        Customer customerB = TestDataUtil.createCustomerB(oldEmployee);

        when(employeeRepository.findById(oldEmployeeId)).thenReturn(Optional.of(oldEmployee));
        when(employeeRepository.findById(newEmployeeId)).thenReturn(Optional.of(newEmployee));
        when(customerRepository.findByEmployeeId(oldEmployeeId)).thenReturn(Arrays.asList(customerA, customerB));

        // When
        underTest.reassignCustomersAndDeleteEmployee(oldEmployeeId, newEmployeeId);

        // Then
        verify(customerRepository, times(1)).saveAll(anyList());
        verify(employeeRepository, times(1)).deleteById(oldEmployeeId);

        assertEquals(newEmployee, customerA.getEmployee());
        assertEquals(newEmployee, customerB.getEmployee());
    }

}