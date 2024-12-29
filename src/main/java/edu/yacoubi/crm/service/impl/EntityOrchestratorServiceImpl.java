package edu.yacoubi.crm.service.impl;

import edu.yacoubi.crm.model.Customer;
import edu.yacoubi.crm.model.Employee;
import edu.yacoubi.crm.service.ICustomerService;
import edu.yacoubi.crm.service.IEmployeeService;
import edu.yacoubi.crm.service.IEntityOrchestratorService;
import edu.yacoubi.crm.service.IInactiveEmployeeService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class EntityOrchestratorServiceImpl implements IEntityOrchestratorService {
    private final IEmployeeService employeeService;
    private final ICustomerService customerService;
    private final IInactiveEmployeeService inactiveEmployeeService;

    @Transactional
    @Override
    public void deleteEmployeeAndReassignCustomers(Long oldEmployeeId, Long newEmployeeId) {
        Assert.notNull(oldEmployeeId, "Old employee ID must not be null");
        Assert.notNull(newEmployeeId, "New employee ID must not be null");

        log.info("EntityOrchestratorServiceImpl::deleteEmployeeAndReassignCustomers oldEmployeeId: {}, newEmployeeId: {}",
                oldEmployeeId, newEmployeeId
        );

        if (oldEmployeeId.equals(newEmployeeId)) {
            log.warn("Old and new employee IDs must be different");
            throw new IllegalArgumentException("Old and new employee IDs must be different");
        }

        reassignCustomers(oldEmployeeId, newEmployeeId);

        Employee oldEmployee = employeeService.getEmployeeById(oldEmployeeId).get();

        inactiveEmployeeService.createInactiveEmployee(oldEmployee);
        employeeService.deleteEmployee(oldEmployeeId);

        log.info("Employee deleted and customers reassigned: oldEmployeeId= {}, newEmployeeId= {}",
                oldEmployeeId, newEmployeeId
        );
    }

    @Override
    public void reassignCustomerToEmployee(Long customerId, Long employeeId) {
        log.info("EntityOrchestratorServiceImpl::reassignCustomerToEmployee customerId: {}, employeeId: {}",
                customerId, employeeId
        );

        if (customerId == null || employeeId == null || customerId < 0 || employeeId < 0) {
            log.warn("Customer or Employee IDs must not be null and must be a positive number");
            throw new IllegalArgumentException("Customer or Employee IDs must not be null and a positive number");
        }

        Customer customer = customerService.getCustomerById(customerId).get();
        Employee employee = employeeService.getEmployeeById(employeeId).get();

        customer.setEmployee(employee);
        customerService.updateCustomer(customerId, customer);

        log.info("Customer reassigned: customerId= {}, newEmployeeId= {}", customerId, employeeId);
    }

    @Override
    public void reassignCustomers(Long oldEmployeeId, Long newEmployeeId) {
        log.info("EntityOrchestratorServiceImpl::reassignCustomers oldEmployeeId: {}, newEmployeeId: {}",
                oldEmployeeId, newEmployeeId
        );

        if (oldEmployeeId == null || newEmployeeId == null || oldEmployeeId < 0 || newEmployeeId < 0) {
            log.warn("Employee IDs must not be null and must be a positive number");
            throw new IllegalArgumentException("Employee IDs must not be null and a positive number");
        }

        if (newEmployeeId.equals(oldEmployeeId)) {
            log.warn("Old and new employee IDs must be different");
            throw new IllegalArgumentException("Old and new employee IDs must be different");
        }

        List<Customer> customers = customerService.getCustomersByEmployeeId(oldEmployeeId);

        if (customers.isEmpty()) {
            log.warn("No customers found for oldEmployee ID: {}", oldEmployeeId);
            throw new IllegalArgumentException("No customers found for oldEmployee ID: " + oldEmployeeId);
        }

        Employee newEmployee = employeeService.getEmployeeById(newEmployeeId).get();

        handleCustomerReassignment(customers, newEmployee);

        log.info("Customers reassigned successfully: oldEmployeeId= {}, newEmployeeId= {}",
                oldEmployeeId, newEmployeeId
        );
    }

    private void handleCustomerReassignment(List<Customer> customers, Employee newEmployee) {
        customers.forEach(customer -> {
            log.info("Reassigning customer ID: {} to new employee ID: {}", customer.getId(), newEmployee.getId());
            customer.setEmployee(newEmployee);
        });

        customerService.updateCustomers(customers);
        log.info("Customers reassigned successfully");
    }
}
