package edu.yacoubi.crm.service.impl;

import edu.yacoubi.crm.exception.ResourceNotFoundException;
import edu.yacoubi.crm.model.Customer;
import edu.yacoubi.crm.model.Employee;
import edu.yacoubi.crm.repository.CustomerRepository;
import edu.yacoubi.crm.repository.EmployeeRepository;
import edu.yacoubi.crm.service.*;
import edu.yacoubi.crm.service.validation.EntityValidator;
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
    public static String LogInfoStartReassignCustomerToEmployee =
            "EntityOrchestratorServiceImpl::reassignCustomerToEmployee customerId: %d, employeeId: %d";
    public static String LogInfoEndReassignCustomerToEmployee =
            "Customer reassigned: customerId= %d, newEmployeeId= %d";

    public static final String EMPLOYEE_NOT_FOUND_WITH_ID = "Employee not found with ID: %d";
    public static final String CUSTOMER_NOT_FOUND_WITH_ID = "Customer not found with ID: %d";

    private final EmployeeRepository employeeRepository;
    private final ICustomerService customerService;
    private final CustomerRepository customerRepository;
    private final IInactiveEmployeeService inactiveEmployeeService;
    private final EntityValidator entityValidator;

    @Transactional
    @Override
    public void deleteEmployeeAndReassignCustomers(Long oldEmployeeId, Long newEmployeeId) {
        Assert.notNull(oldEmployeeId, "Old employee ID must not be null");
        Assert.notNull(newEmployeeId, "New employee ID must not be null");

        log.info("EntityOrchestratorServiceImpl::deleteEmployeeAndReassignCustomers oldEmployeeId: {}, newEmployeeId: {}",
                oldEmployeeId,
                newEmployeeId
        );

        if (oldEmployeeId.equals(newEmployeeId)) {
            log.warn("Old and new employee IDs must be different");
            throw new IllegalArgumentException("Old and new employee IDs must be different");
        }

        entityValidator.validateEmployeeExists(oldEmployeeId);
        entityValidator.validateEmployeeExists(newEmployeeId);

        Employee oldEmployee = employeeRepository.findById(oldEmployeeId).orElseThrow(
                () -> new ResourceNotFoundException(String.format(EMPLOYEE_NOT_FOUND_WITH_ID, oldEmployeeId))
        );
        Employee newEmployee = employeeRepository.findById(newEmployeeId).orElseThrow(
                () -> new ResourceNotFoundException(String.format(EMPLOYEE_NOT_FOUND_WITH_ID, newEmployeeId))
        );

        // Kunden neu zuweisen
        this.reassignCustomers(oldEmployee.getId(), newEmployee.getId());

        // Archivierung des Mitarbeiters
        inactiveEmployeeService.createInactiveEmployee(oldEmployee);

        // Löschen des Mitarbeiters
        employeeRepository.delete(oldEmployee);

        log.info("Employee deleted and customers reassigned: oldEmployeeId= {}, newEmployeeId= {}",
                oldEmployeeId,
                newEmployeeId
        );
    }

    @Override
    public void reassignCustomerToEmployee(Long customerId, Long employeeId) {
        // Log the start of the method call
        log.info(String.format(LogInfoStartReassignCustomerToEmployee, customerId, employeeId));

        // Validate parameters first
        if (customerId == null || employeeId == null || customerId < 0 || employeeId < 0) {
            log.warn("Customer or Employee IDs must not be null and must be a positive number");
            throw new IllegalArgumentException("Customer or Employee IDs must not be null and a positive number");
        }

        // Ensure employee exists
        entityValidator.validateEmployeeExists(employeeId);

        // Fetch customer and employee entities
        Customer customer = customerRepository.findById(customerId).orElseThrow(
                () -> new ResourceNotFoundException(String.format(CUSTOMER_NOT_FOUND_WITH_ID, customerId))
        );
        Employee employee = employeeRepository.findById(employeeId).orElseThrow(
                () -> new ResourceNotFoundException(String.format(EMPLOYEE_NOT_FOUND_WITH_ID, employeeId))
        );

        // Reassign customer to new employee
        customer.setEmployee(employee);
        customerRepository.save(customer);

        // Log the reassign completion
        log.info(String.format(LogInfoEndReassignCustomerToEmployee, customerId, employeeId));
    }

    @Override
    public void reassignCustomers(Long oldEmployeeId, Long newEmployeeId) {
        // Log the start of the method call
        log.info("EntityOrchestratorServiceImpl::reassignCustomers oldEmployeeId: {}, newEmployeeId: {}",
                oldEmployeeId,
                newEmployeeId
        );

        if (oldEmployeeId == null || newEmployeeId == null || oldEmployeeId < 0 || newEmployeeId < 0) {
            log.warn("Employee IDs must not be null and must be a positive number");
            throw new IllegalArgumentException("Employee IDs must not be null and a positive number");
        }

        if (newEmployeeId.equals(oldEmployeeId)) {
            log.warn("Old and new employee IDs must be different");
            throw new IllegalArgumentException("Old and new employee IDs must be different");
        }

        entityValidator.validateEmployeeExists(oldEmployeeId);
        entityValidator.validateEmployeeExists(newEmployeeId);

        List<Customer> customers = customerService.getCustomersByEmployeeId(oldEmployeeId);
        if (customers.isEmpty()) {
            log.warn(String.format("No customers found for oldEmployee ID: %d", oldEmployeeId));
            throw new IllegalArgumentException(String.format("No customers found for oldEmployee ID: %d", oldEmployeeId));
            //return;
        }

        Employee newEmployee = employeeRepository.findById(newEmployeeId).orElseThrow(
                () -> new ResourceNotFoundException(String.format(EMPLOYEE_NOT_FOUND_WITH_ID, newEmployeeId))
        );

        handleCustomerReassignment(customers, newEmployee);

        log.info("Customers reassigned successfully: oldEmployeeId= {}, newEmployeeId= {}",
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
