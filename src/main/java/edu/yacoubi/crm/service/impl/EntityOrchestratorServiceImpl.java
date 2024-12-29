package edu.yacoubi.crm.service.impl;

import edu.yacoubi.crm.model.Customer;
import edu.yacoubi.crm.model.Employee;
import edu.yacoubi.crm.service.ICustomerService;
import edu.yacoubi.crm.service.IEmployeeService;
import edu.yacoubi.crm.service.IEntityOrchestratorService;
import edu.yacoubi.crm.service.IInactiveEmployeeService;
import edu.yacoubi.crm.service.util.EntityAction;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @brief Service implementation for orchestrating entities.
 *
 * This service handles operations related to employees and customers.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class EntityOrchestratorServiceImpl implements IEntityOrchestratorService {
    private final IEmployeeService employeeService;
    private final ICustomerService customerService;
    private final IInactiveEmployeeService inactiveEmployeeService;

    /**
     * @brief Deletes an employee and reassigns their customers to another employee.
     *
     * @param oldEmployeeId ID of the employee to be deleted.
     * @param newEmployeeId ID of the employee to whom the customers will be reassigned.
     */
    @Transactional
    @Override
    public void deleteEmployeeAndReassignCustomers(Long oldEmployeeId, Long newEmployeeId) {
        log.info("EntityOrchestratorServiceImpl::deleteEmployeeAndReassignCustomers oldEmployeeId: {}, newEmployeeId: {}",
                oldEmployeeId, newEmployeeId
        );

        if (oldEmployeeId == null || newEmployeeId == null || oldEmployeeId < 0 || newEmployeeId < 0) {
            log.warn("Employee IDs must not be null and must be a positive number");
            throw new IllegalArgumentException("Employee IDs must not be null and must be a positive number");
        }

        if (oldEmployeeId.equals(newEmployeeId)) {
            log.warn("Old and new employee IDs must be different");
            throw new IllegalArgumentException("Old and new employee IDs must be different");
        }

        reassignCustomers(oldEmployeeId, newEmployeeId);

        processEntityAction(oldEmployeeId, createDeleteEmployeeAction());

        log.info("Employee deleted and customers reassigned: oldEmployeeId= {}, newEmployeeId= {}",
                oldEmployeeId, newEmployeeId
        );
    }

    /**
     * @brief Reassigns a customer to a different employee.
     *
     * @param customerId ID of the customer to be reassigned.
     * @param employeeId ID of the employee to whom the customer will be assigned.
     */
    @Override
    public void reassignCustomerToEmployee(Long customerId, Long employeeId) {
        log.info("EntityOrchestratorServiceImpl::reassignCustomerToEmployee customerId: {}, employeeId: {}",
                customerId, employeeId
        );

        if (customerId == null || employeeId == null || customerId < 0 || employeeId < 0) {
            log.warn("Customer or Employee IDs must not be null and must be a positive number");
            throw new IllegalArgumentException("Customer or Employee IDs must not be null and must be a positive number");
        }

        Customer customer = customerService.getCustomerById(customerId).get();
        Employee employee = employeeService.getEmployeeById(employeeId).get();

        customer.setEmployee(employee);
        customerService.updateCustomer(customerId, customer);

        log.info("Customer reassigned: customerId= {}, newEmployeeId= {}", customerId, employeeId);
    }

    /**
     * @brief Reassigns customers from an old employee to a new employee.
     *
     * @param oldEmployeeId ID of the old employee.
     * @param newEmployeeId ID of the new employee.
     */
    @Override
    public void reassignCustomers(Long oldEmployeeId, Long newEmployeeId) {
        log.info("EntityOrchestratorServiceImpl::reassignCustomers oldEmployeeId: {}, newEmployeeId: {}",
                oldEmployeeId, newEmployeeId
        );

        if (oldEmployeeId == null || newEmployeeId == null || oldEmployeeId < 0 || newEmployeeId < 0) {
            log.warn("Employee IDs must not be null and must be a positive number");
            throw new IllegalArgumentException("Employee IDs must not be null and must be a positive number");
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

    /**
     * @brief Handles the reassignment of customers to a new employee.
     *
     * @param customers the list of customers to be reassigned.
     * @param newEmployee the new employee to whom the customers will be assigned.
     */
    private void handleCustomerReassignment(List<Customer> customers, Employee newEmployee) {
        customers.forEach(customer -> {
            log.info("Reassigning customer ID: {} to new employee ID: {}", customer.getId(), newEmployee.getId());
            customer.setEmployee(newEmployee);
        });

        customerService.updateCustomers(customers);
        log.info("Customers reassigned successfully");
    }

    /**
     * @brief Creates an action to delete an employee.
     *
     * @return the action to delete an employee.
     */
    private EntityAction createDeleteEmployeeAction() {
        return id -> {
            Employee oldEmployee = employeeService.getEmployeeById(id).get();
            inactiveEmployeeService.createInactiveEmployee(oldEmployee);
            employeeService.deleteEmployee(id);
        };
    }

    /**
     * @brief Executes an action on an entity identified by a Long ID.
     *
     * @param id the ID of the entity on which the action is to be performed.
     * @param action the action to be executed.
     */
    private void processEntityAction(Long id, EntityAction action) {
        action.execute(id);
    }
}
