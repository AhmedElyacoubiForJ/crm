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
}
