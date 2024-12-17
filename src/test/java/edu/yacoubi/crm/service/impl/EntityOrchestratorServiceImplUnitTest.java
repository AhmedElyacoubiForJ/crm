package edu.yacoubi.crm.service.impl;

import edu.yacoubi.crm.exception.ResourceNotFoundException;
import edu.yacoubi.crm.model.Customer;
import edu.yacoubi.crm.model.Employee;
import edu.yacoubi.crm.model.InactiveEmployee;
import edu.yacoubi.crm.repository.CustomerRepository;
import edu.yacoubi.crm.repository.EmployeeRepository;
import edu.yacoubi.crm.service.ICustomerService;
import edu.yacoubi.crm.service.IInactiveEmployeeService;
import edu.yacoubi.crm.service.ValidationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

/**
 * Dieser Code enthält Unit-Tests für die Hauptmethoden meines EntityOrchestratorServiceImpl.
 * Er deckt die Erfolgsfälle sowie Fälle ab, in denen Ausnahmen geworfen werden, wenn die Validierung fehlschlägt.
 * Die Tests verwenden Mockito, um die Abhängigkeiten zu mocken und das Verhalten zu verifizieren.
 */
class EntityOrchestratorServiceImplUnitTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private ICustomerService customerService;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private IInactiveEmployeeService inactiveEmployeeService;

    @Mock
    private ValidationService validationService;

    @InjectMocks
    private EntityOrchestratorServiceImpl entityOrchestratorService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testDeleteEmployeeAndReassignCustomers() {
        Long oldEmployeeId = 1L;
        Long newEmployeeId = 2L;

        Employee oldEmployee = new Employee();
        oldEmployee.setId(oldEmployeeId);
        Employee newEmployee = new Employee();
        newEmployee.setId(newEmployeeId);

        Customer customer1 = new Customer();
        customer1.setId(101L);
        customer1.setEmployee(oldEmployee);

        Customer customer2 = new Customer();
        customer2.setId(102L);
        customer2.setEmployee(oldEmployee);

        when(employeeRepository.findById(oldEmployeeId)).thenReturn(Optional.of(oldEmployee));
        when(employeeRepository.findById(newEmployeeId)).thenReturn(Optional.of(newEmployee));

        // Mock the createInactiveEmployee method
        when(inactiveEmployeeService.createInactiveEmployee(any(Employee.class)))
                .thenReturn(new InactiveEmployee());

        // Mock the getCustomersByEmployeeId method
        when(customerService.getCustomersByEmployeeId(oldEmployeeId)).thenReturn(Arrays.asList(customer1, customer2));
        doNothing().when(validationService).validateEmployeeExists(anyLong());

        // Call the method to test
        entityOrchestratorService.deleteEmployeeAndReassignCustomers(oldEmployeeId, newEmployeeId);

        // Verify interactions
        verify(employeeRepository).delete(oldEmployee);
        verify(inactiveEmployeeService).createInactiveEmployee(oldEmployee);

        // Überprüft, ob die Methode customerRepository.saveAll mit einer Liste von Kunden aufgerufen wird,
        // die sowohl customer1 als auch customer2 enthält.
        verify(customerRepository).saveAll(argThat(customers -> {
            // Initialisiere Booleans, um zu verfolgen, ob customer1 und customer2 in der Liste enthalten sind
            boolean containsCustomer1 = false;
            boolean containsCustomer2 = false;

            // Iteriere durch die übergebene Liste von Kunden
            for (Customer customer : customers) {
                // Überprüfe, ob der aktuelle Kunde customer1 ist
                if (customer.equals(customer1)) {
                    containsCustomer1 = true; // Setze containsCustomer1 auf true, wenn customer1 gefunden wurde
                } else if (customer.equals(customer2)) {
                    containsCustomer2 = true; // Setze containsCustomer2 auf true, wenn customer2 gefunden wurde
                }
            }

            // Die Bedingung, dass beide Kunden in der Liste enthalten sind,
            // muss erfüllt sein, damit der Test erfolgreich ist
            return containsCustomer1 && containsCustomer2;
        }));


        // Check if customers are reassigned to the new employee
        assertEquals(newEmployee, customer1.getEmployee());
        assertEquals(newEmployee, customer2.getEmployee());
    }


//    @Test
//    void testReassignCustomerToEmployee() {
//        Long customerId = 1L;
//        Long employeeId = 2L;
//
//        Customer customer = new Customer();
//        customer.setId(customerId);
//        Employee employee = new Employee();
//        employee.setId(employeeId);
//
//        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));
//        when(employeeRepository.findById(employeeId)).thenReturn(Optional.of(employee));
//        doNothing().when(validationService).validateEmployeeExists(anyLong());
//
//        entityOrchestratorService.reassignCustomerToEmployee(customerId, employeeId);
//
//        assertEquals(employee, customer.getEmployee());
//        verify(customerRepository).save(customer);
//    }
//
//    @Test
//    void testReassignCustomers() {
//        Long oldEmployeeId = 1L;
//        Long newEmployeeId = 2L;
//
//        Employee newEmployee = new Employee();
//        newEmployee.setId(newEmployeeId);
//        Customer customer = new Customer();
//        customer.setId(1L);
//
//        when(employeeRepository.findById(newEmployeeId)).thenReturn(Optional.of(newEmployee));
//        when(customerService.getCustomersByEmployeeId(oldEmployeeId)).thenReturn(Collections.singletonList(customer));
//        doNothing().when(validationService).validateEmployeeExists(anyLong());
//
//        entityOrchestratorService.reassignCustomers(oldEmployeeId, newEmployeeId);
//
//        assertEquals(newEmployee, customer.getEmployee());
//        verify(customerRepository).saveAll(anyList());
//    }
//
//    @Test
//    void testDeleteEmployeeAndReassignCustomers_ThrowsException() {
//        Long oldEmployeeId = 1L;
//        Long newEmployeeId = 2L;
//
//        doThrow(new ResourceNotFoundException("Employee not found"))
//                .when(validationService)
//                .validateEmployeeExists(anyLong());
//
//        assertThrows(ResourceNotFoundException.class, () -> entityOrchestratorService.deleteEmployeeAndReassignCustomers(oldEmployeeId, newEmployeeId));
//    }
//
//    @Test
//    void testReassignCustomerToEmployee_ThrowsException() {
//        Long customerId = 1L;
//        Long employeeId = 2L;
//
//        doThrow(new ResourceNotFoundException("Employee not found")).when(validationService).validateEmployeeExists(employeeId);
//
//        assertThrows(ResourceNotFoundException.class, () -> entityOrchestratorService.reassignCustomerToEmployee(customerId, employeeId));
//    }
//
//    @Test
//    void testReassignCustomers_ThrowsException() {
//        Long oldEmployeeId = 1L;
//        Long newEmployeeId = 2L;
//
//        doThrow(new ResourceNotFoundException("Employee not found")).when(validationService).validateEmployeeExists(anyLong());
//
//        assertThrows(ResourceNotFoundException.class, () -> entityOrchestratorService.reassignCustomers(oldEmployeeId, newEmployeeId));
//    }
}
