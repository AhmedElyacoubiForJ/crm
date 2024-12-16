package edu.yacoubi.crm.service.impl;

import edu.yacoubi.crm.exception.ResourceNotFoundException;
import edu.yacoubi.crm.model.Customer;
import edu.yacoubi.crm.model.Employee;
import edu.yacoubi.crm.repository.CustomerRepository;
import edu.yacoubi.crm.repository.EmployeeRepository;
import edu.yacoubi.crm.service.ICustomerService;
import edu.yacoubi.crm.service.IEmployeeCustomerOrchestratorService;
import edu.yacoubi.crm.service.IInactiveEmployeeService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmployeeCustomerOrchestratorServiceImpl implements IEmployeeCustomerOrchestratorService {
    public static final String EMPLOYEE_NOT_FOUND_WITH_ID = "Employee not found with ID: {}";
    public static final String CUSTOMER_NOT_FOUND_WITH_ID = "Customer not found with ID: {}";

    private final EmployeeRepository employeeRepository;
    private final ICustomerService customerService;
    private final CustomerRepository customerRepository;
    private final IInactiveEmployeeService inactiveEmployeeService;

    @Transactional
    @Override
    public void deleteEmployeeAndReassignCustomers(Long oldEmployeeId, Long newEmployeeId) {
        log.info(
                "EmployeeCustomerOrchestratorServiceImpl::deleteEmployeeAndReassignCustomers employeeId: {}, newEmployeeId: {}",
                oldEmployeeId,
                newEmployeeId
        );

        if (oldEmployeeId.equals(newEmployeeId)) {
            throw new IllegalArgumentException("Old and new employee IDs must be different.");
        }

        Employee oldEmployee = employeeRepository.findById(oldEmployeeId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format(EMPLOYEE_NOT_FOUND_WITH_ID, oldEmployeeId)
                ));
        Employee newEmployee = employeeRepository.findById(newEmployeeId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format(EMPLOYEE_NOT_FOUND_WITH_ID, newEmployeeId)
                ));

        // Kunden neu zuweisen
        reassignCustomers(oldEmployee.getId(), newEmployee.getId());

        // Archivierung des Mitarbeiters
        inactiveEmployeeService.createInactiveEmployee(oldEmployee);

        // Löschen des Mitarbeiters
        employeeRepository.delete(oldEmployee);

        log.info(
                "Employee deleted and customers reassigned: oldEmployeeId={}, newEmployeeId={}",
                oldEmployeeId,
                newEmployeeId
        );
    }

    @Override
    public void reassignCustomerToEmployee(Long customerId, Long employeeId) {
        log.info("EmployeeOrchestratorServiceImpl::reassignCustomerToEmployee customerId: {}, employeeId: {}",
                customerId,
                employeeId
        );

        if (customerId.equals(employeeId)) {
            log.warn("Customer and employee IDs must be different.");
            return;
        }

        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format(CUSTOMER_NOT_FOUND_WITH_ID, customerId)
                ));

        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format(EMPLOYEE_NOT_FOUND_WITH_ID, employeeId)
                ));

        customer.setEmployee(employee);
        customerRepository.save(customer);

        log.info("Customer reassigned: customerId={}, newEmployeeId={}", customerId, employeeId);
    }

    @Override
    public void reassignCustomers(Long oldEmployeeId, Long newEmployeeId) {
        log.info("EmployeeCustomerOrchestratorServiceImpl::reassignCustomers oldEmployeeId: {}, newEmployeeId: {}",
                oldEmployeeId,
                newEmployeeId
        );

        if (newEmployeeId.equals(oldEmployeeId)) {
            log.warn("Old and new employee IDs must be different.");
            return;
        }

        List<Customer> customers = customerService.getCustomersByEmployeeId(oldEmployeeId);

        if (customers.isEmpty()) {
            log.warn("No customers found for employee ID: {}", oldEmployeeId);
            return;
        }

        Employee newEmployee = employeeRepository.findById(newEmployeeId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format(EMPLOYEE_NOT_FOUND_WITH_ID, newEmployeeId)
                ));

        customers.forEach(customer -> {
            log.info("Reassigning customer ID: {} to new employee ID: {}", customer.getId(), newEmployeeId);
            customer.setEmployee(newEmployee);
        });

        customerRepository.saveAll(customers);
        log.info(
                "Customers reassigned successfully: oldEmployeeId={}, newEmployeeId={}",
                oldEmployeeId,
                newEmployeeId
        );
    }
}
