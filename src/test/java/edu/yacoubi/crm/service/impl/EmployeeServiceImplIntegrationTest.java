package edu.yacoubi.crm.service.impl;

import edu.yacoubi.crm.TestDataUtil;
import edu.yacoubi.crm.model.Customer;
import edu.yacoubi.crm.model.Employee;
import edu.yacoubi.crm.model.InactiveEmployee;
import edu.yacoubi.crm.repository.EmployeeRepository;
import edu.yacoubi.crm.repository.InactiveEmployeeRepository;
import edu.yacoubi.crm.service.ICustomerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Transactional
class EmployeeServiceIntegrationTest {

    @Autowired
    private EmployeeServiceImpl underTest;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private ICustomerService customerService;

    @Autowired
    private InactiveEmployeeRepository inactiveEmployeeRepository;

    private Employee oldEmployee;
    private Employee newEmployee;
    private Customer customerA;
    private Customer customerB;

    @BeforeEach
    void setUp() {
        // Initialisierung der Testdaten
        oldEmployee = employeeRepository.save(TestDataUtil.createEmployeeA());
        newEmployee = employeeRepository.save(TestDataUtil.createStandardEmployee());

        customerA = customerService.createCustomer(TestDataUtil.createCustomerA(oldEmployee));
        customerB = customerService.createCustomer(TestDataUtil.createCustomerB(oldEmployee));
    }

    @Test
    void itShouldReassignCustomersAndDeleteEmployee() {
        // When
        underTest.deleteEmployee(oldEmployee.getId(), newEmployee.getId());

        // Then
        // Assert: Überprüfung, dass alle Kunden neu zugewiesen wurden
        List<Customer> reassignedCustomers = customerService.getCustomersByEmployeeId(newEmployee.getId());
        assertEquals(2, reassignedCustomers.size());
        assertEquals(newEmployee, reassignedCustomers.get(0).getEmployee());
        assertEquals(newEmployee, reassignedCustomers.get(1).getEmployee());

        // Assert: Überprüfung, dass der alte Mitarbeiter gelöscht wurde
        assertThat(employeeRepository.findById(oldEmployee.getId())).isEmpty();
        Optional<InactiveEmployee> archivedEmployee = inactiveEmployeeRepository.findByEmail(oldEmployee.getEmail());
        assertThat(inactiveEmployeeRepository.findByEmail(oldEmployee.getEmail())).isPresent();
        assertEquals(oldEmployee.getFirstName(), archivedEmployee.get().getFirstName());
    }
}
