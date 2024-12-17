package edu.yacoubi.crm.service.impl;

import edu.yacoubi.crm.model.Customer;
import edu.yacoubi.crm.model.Employee;
import edu.yacoubi.crm.repository.CustomerRepository;
import edu.yacoubi.crm.repository.EmployeeRepository;
import edu.yacoubi.crm.service.IEntityOrchestratorService;
import edu.yacoubi.crm.service.ValidationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


/**
 * Integrationstests prüfen das Zusammenspiel der verschiedenen Komponenten des Systems
 * in einer realistischeren Umgebung im Vergleich zu Unit-Tests.
 * Diese Tests setzen voraus, dass du Spring Boot verwendest und eine entsprechende Testkonfiguration hast.
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest
@Transactional
class EntityOrchestratorServiceImplIntegrationTest {

    @Autowired
    private IEntityOrchestratorService entityOrchestratorService;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private ValidationService validationService;

    /**
     * Prüft, ob ein Mitarbeiter gelöscht und seine Kunden korrekt zugewiesen werden.
     */
//    @Test
//    void testDeleteEmployeeAndReassignCustomers() {
//        Employee oldEmployee = new Employee();
//        oldEmployee.setFirstName("Old Employee");
//        oldEmployee = employeeRepository.save(oldEmployee);
//
//        Employee newEmployee = new Employee();
//        newEmployee.setFirstName("New Employee");
//        newEmployee = employeeRepository.save(newEmployee);
//
//        Customer customer = new Customer();
//        customer.setFirstName("Test Customer");
//        customer.setEmployee(oldEmployee);
//        customer = customerRepository.save(customer);
//
//        entityOrchestratorService.deleteEmployeeAndReassignCustomers(oldEmployee.getId(), newEmployee.getId());
//
//        Customer updatedCustomer = customerRepository.findById(customer.getId()).orElse(null);
//        assertNotNull(updatedCustomer);
//        assertEquals(newEmployee.getId(), updatedCustomer.getEmployee().getId());
//        assertFalse(employeeRepository.existsById(oldEmployee.getId()));
//    }
//
//    /**
//     * Prüft, ob ein Kunde korrekt einem Mitarbeiter zugewiesen wird.
//     */
//    @Test
//    void testReassignCustomerToEmployee() {
//        Employee employee = new Employee();
//        employee.setFirstName("Test Employee");
//        employee = employeeRepository.save(employee);
//
//        Customer customer = new Customer();
//        customer.setFirstName("Test Customer");
//        customer = customerRepository.save(customer);
//
//        entityOrchestratorService.reassignCustomerToEmployee(customer.getId(), employee.getId());
//
//        Customer updatedCustomer = customerRepository.findById(customer.getId()).orElse(null);
//        assertNotNull(updatedCustomer);
//        assertEquals(employee.getId(), updatedCustomer.getEmployee().getId());
//    }
//
//    /**
//     * Prüft, ob alle Kunden eines Mitarbeiters korrekt einem neuen Mitarbeiter zugewiesen werden.
//     */
//    @Test
//    void testReassignCustomers() {
//        Employee oldEmployee = new Employee();
//        oldEmployee.setFirstName("Old Employee");
//        oldEmployee = employeeRepository.save(oldEmployee);
//
//        Employee newEmployee = new Employee();
//        newEmployee.setFirstName("New Employee");
//        newEmployee = employeeRepository.save(newEmployee);
//
//        Customer customer1 = new Customer();
//        customer1.setFirstName("Customer 1");
//        customer1.setEmployee(oldEmployee);
//
//        Customer customer2 = new Customer();
//        customer2.setFirstName("Customer 2");
//        customer2.setEmployee(oldEmployee);
//
//        customerRepository.saveAll(List.of(customer1, customer2));
//
//        entityOrchestratorService.reassignCustomers(oldEmployee.getId(), newEmployee.getId());
//
//        List<Customer> customers = customerRepository.findAll();
//        for (Customer customer : customers) {
//            assertEquals(newEmployee.getId(), customer.getEmployee().getId());
//        }
//    }
}
