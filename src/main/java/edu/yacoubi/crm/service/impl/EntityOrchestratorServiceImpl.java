package edu.yacoubi.crm.service.impl;

import edu.yacoubi.crm.exception.ResourceNotFoundException;
import edu.yacoubi.crm.model.Customer;
import edu.yacoubi.crm.model.Employee;
import edu.yacoubi.crm.repository.CustomerRepository;
import edu.yacoubi.crm.repository.EmployeeRepository;
import edu.yacoubi.crm.service.ICustomerService;
import edu.yacoubi.crm.service.IEntityOrchestratorService;
import edu.yacoubi.crm.service.IInactiveEmployeeService;
import edu.yacoubi.crm.service.ValidationService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class EntityOrchestratorServiceImpl implements IEntityOrchestratorService {
    public static final String EMPLOYEE_NOT_FOUND_WITH_ID = "Employee not found with ID: %d";
    public static final String CUSTOMER_NOT_FOUND_WITH_ID = "Customer not found with ID: %d";

    private final EmployeeRepository employeeRepository;
    private final ICustomerService customerService;
    private final CustomerRepository customerRepository;
    private final IInactiveEmployeeService inactiveEmployeeService;
    private final ValidationService validationService;

    @Transactional
    @Override
    public void deleteEmployeeAndReassignCustomers(Long oldEmployeeId, Long newEmployeeId) {
        Assert.notNull(oldEmployeeId, "Old employee ID must not be null");
        Assert.notNull(newEmployeeId, "New employee ID must not be null");

        log.info("EntityOrchestratorServiceImpl::deleteEmployeeAndReassignCustomers employeeId: {}, newEmployeeId: {}",
                oldEmployeeId,
                newEmployeeId
        );

        if (oldEmployeeId.equals(newEmployeeId)) {
            throw new IllegalArgumentException("Old and new employee IDs must be different");
        }

        validationService.validateEmployeeExists(oldEmployeeId);
        validationService.validateEmployeeExists(newEmployeeId);

        Employee oldEmployee = employeeRepository.findById(oldEmployeeId).orElseThrow(
                () -> new ResourceNotFoundException(String.format(EMPLOYEE_NOT_FOUND_WITH_ID, oldEmployeeId))
        );
        Employee newEmployee = employeeRepository.findById(newEmployeeId).orElseThrow(
                () -> new ResourceNotFoundException(String.format(EMPLOYEE_NOT_FOUND_WITH_ID, newEmployeeId))
        );

        // Kunden neu zuweisen
        reassignCustomers(oldEmployee.getId(), newEmployee.getId());

        // Archivierung des Mitarbeiters
        inactiveEmployeeService.createInactiveEmployee(oldEmployee);

        // Löschen des Mitarbeiters
        employeeRepository.delete(oldEmployee);

        log.info("Employee deleted and customers reassigned: oldEmployeeId={}, newEmployeeId={}",
                oldEmployeeId,
                newEmployeeId
        );
    }

    @Override
    public void reassignCustomerToEmployee(Long customerId, Long employeeId) {
        Assert.notNull(customerId, "Customer ID must not be null");
        Assert.notNull(employeeId, "Employee ID must not be null");

        log.info("EntityOrchestratorServiceImpl::reassignCustomerToEmployee customerId: {}, employeeId: {}",
                customerId,
                employeeId
        );

        validationService.validateEmployeeExists(employeeId);

        if (customerId.equals(employeeId)) {
            log.warn("Customer and employee IDs must be different.");
            return;
        }

        Customer customer = customerRepository.findById(customerId).orElseThrow(
                () -> new ResourceNotFoundException(String.format(CUSTOMER_NOT_FOUND_WITH_ID, customerId))
        );
        Employee employee = employeeRepository.findById(employeeId).orElseThrow(
                () -> new ResourceNotFoundException(String.format(EMPLOYEE_NOT_FOUND_WITH_ID, employeeId))
        );

        customer.setEmployee(employee);
        customerRepository.save(customer);

        log.info("Customer reassigned: customerId={}, newEmployeeId={}", customerId, employeeId);
    }

    @Override
    public void reassignCustomers(Long oldEmployeeId, Long newEmployeeId) {
        Assert.notNull(oldEmployeeId, "Old employee ID must not be null");
        Assert.notNull(newEmployeeId, "New employee ID must not be null");

        log.info("EntityOrchestratorServiceImpl::reassignCustomers oldEmployeeId: {}, newEmployeeId: {}",
                oldEmployeeId,
                newEmployeeId
        );

        if (newEmployeeId.equals(oldEmployeeId)) {
            log.warn("Old and new employee IDs must be different.");
            return;
        }

        validationService.validateEmployeeExists(oldEmployeeId);
        validationService.validateEmployeeExists(newEmployeeId);

        List<Customer> customers = customerService.getCustomersByEmployeeId(oldEmployeeId);
        if (customers.isEmpty()) {
            log.warn("No customers found for employee ID: {}", oldEmployeeId);
            return;
        }

        Employee newEmployee = employeeRepository.findById(newEmployeeId).orElseThrow(
                () -> new ResourceNotFoundException(String.format(EMPLOYEE_NOT_FOUND_WITH_ID, newEmployeeId))
        );

        handleCustomerReassignment(customers, newEmployee);

        log.info("Customers reassigned successfully: oldEmployeeId={}, newEmployeeId={}",
                oldEmployeeId,
                newEmployeeId
        );
    }

    private void handleCustomerReassignment(List<Customer> customers, Employee newEmployee) {
        List<Customer> reassignedCustomers = customers.stream()
                .peek(customer -> {
                    log.info("Reassigning customer ID: {} to new employee ID: {}", customer.getId(), newEmployee.getId());
                    customer.setEmployee(newEmployee);
                })
                .collect(Collectors.toList());

        customerRepository.saveAll(reassignedCustomers);
        log.info("Customers reassigned successfully");
    }
}
