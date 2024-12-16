package edu.yacoubi.crm.service.impl;

import edu.yacoubi.crm.model.Customer;
import edu.yacoubi.crm.model.Employee;
import edu.yacoubi.crm.repository.EmployeeRepository;
import edu.yacoubi.crm.service.ICustomerService;
import edu.yacoubi.crm.service.IInactiveEmployeeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.Arrays;
import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceImplUnitTest {

    @InjectMocks
    private EmployeeServiceImpl underTest;

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private ICustomerService customerService;

    @Mock
    private IInactiveEmployeeService inactiveEmployeeService;

    private Employee oldEmployee;
    private Employee newEmployee;

    @BeforeEach
    void setUp() {
        oldEmployee = new Employee();
        oldEmployee.setId(1L);
        newEmployee = new Employee();
        newEmployee.setId(2L);
    }

//    @Test
//    void itShouldDeleteEmployee() {
//        // Given
//        Long oldEmployeeId = 1L;
//        Long newEmployeeId = 2L;
//
//        Customer customer1 = new Customer();
//        customer1.setId(101L);
//        customer1.setEmployee(oldEmployee);
//
//        Customer customer2 = new Customer();
//        customer2.setId(102L);
//        customer2.setEmployee(oldEmployee);
//
//        when(employeeRepository.findById(oldEmployeeId)).thenReturn(Optional.of(oldEmployee));
//        when(employeeRepository.findById(newEmployeeId)).thenReturn(Optional.of(newEmployee));
//        when(customerService.getCustomersByEmployeeId(oldEmployeeId))
//                .thenReturn(Arrays.asList(customer1, customer2));
//
//        // When
//        underTest.deleteEmployee(oldEmployeeId, newEmployeeId);
//
//        // Then
//        verify(customerService, times(1)).reassignCustomerToEmployee(customer1.getId(), newEmployeeId);
//        verify(customerService, times(1)).reassignCustomerToEmployee(customer2.getId(), newEmployeeId);
//        verify(inactiveEmployeeService, times(1)).createInactiveEmployee(oldEmployee);
//        verify(employeeRepository, times(1)).delete(oldEmployee);
//    }
}
