package edu.yacoubi.crm.service.impl;

import edu.yacoubi.crm.exception.ResourceNotFoundException;
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

import java.time.LocalDate;
import java.util.List;

/**
 * Service implementation for orchestrating entities.
 *
 * <p>This service handles operations related to employees and customers.</p>
 *
 * @author A. El Yacoubi
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class EntityOrchestratorServiceImpl implements IEntityOrchestratorService {
    private final IEmployeeService employeeService;
    private final ICustomerService customerService;
    private final IInactiveEmployeeService inactiveEmployeeService;

    /**
     * Deletes an employee and reassigns their customers to another employee.
     *
     * @param oldEmployeeId ID of the employee to be deleted
     * @param newEmployeeId ID of the employee to whom the customers will be reassigned
     */
    @Transactional
    @Override
    public void deleteEmployeeAndReassignCustomers(final Long oldEmployeeId, final Long newEmployeeId) {
        if (log.isInfoEnabled()) {
            log.info("::deleteEmployeeAndReassignCustomers started with: oldEmployeeId: {}, newEmployeeId: {}", oldEmployeeId, newEmployeeId);
        }

        // Parameter-Validierung in this.reassignCustomers, um doppelte Validierung zu vermeiden
        this.reassignCustomers(oldEmployeeId, newEmployeeId);

        final EntityAction deleteEmployeeAction = createDeleteEmployeeAction();
        processEntityAction(oldEmployeeId, deleteEmployeeAction);

        if (log.isInfoEnabled()) {
            log.info("::deleteEmployeeAndReassignCustomers completed successfully");
        }
    }

    /**
     * Reassigns a customer to a different employee.
     *
     * @param customerId ID of the customer to be reassigned
     * @param employeeId ID of the employee to whom the customer will be assigned
     */
    @Override
    @Transactional
    public void reassignCustomerToEmployee(final Long customerId, final Long employeeId) {
        if (log.isInfoEnabled()) {
            log.info("::reassignCustomerToEmployee started with: customerId: {}, employeeId: {}", customerId, employeeId);
        }

        if (customerId == null || employeeId == null || customerId < 0 || employeeId < 0) {
            final String errorMessage = "Customer or Employee IDs must not be null and must be a positive number";
            log.warn("::reassignCustomerToEmployee parameter warn: {}", errorMessage);
            throw new IllegalArgumentException(errorMessage);
        }

        final Customer customer = customerService.getCustomerById(customerId).get();
        final Employee employee = employeeService.getEmployeeById(employeeId).get();

        customer.setEmployee(employee);
        customerService.updateCustomer(customerId, customer);

        if (log.isInfoEnabled()) {
            log.info("::reassignCustomerToEmployee completed successfully");
        }
    }

    /**
     * Reassigns customers from an old employee to a new employee.
     *
     * @param oldEmployeeId ID of the old employee
     * @param newEmployeeId ID of the new employee
     */
    @Override
    public void reassignCustomers(final Long oldEmployeeId, final Long newEmployeeId) {
        if (log.isInfoEnabled()) {
            log.info("::reassignCustomers started with: oldEmployeeId: {}, newEmployeeId: {}", oldEmployeeId, newEmployeeId);
        }

        if (oldEmployeeId == null || newEmployeeId == null || oldEmployeeId < 0 || newEmployeeId < 0) {
            final String errorMessage = "Employee IDs must not be null and must be a positive number";
            log.warn("::reassignCustomers parameter warn: {}", errorMessage);
            throw new IllegalArgumentException(errorMessage);
        }

        if (newEmployeeId.equals(oldEmployeeId)) {
            final String errorMessage = "Old and new employee IDs must be different";
            log.warn("::reassignCustomers parameter warn: {}", errorMessage);
            throw new IllegalArgumentException(errorMessage);
        }

        final List<Customer> customers = customerService.getCustomersByEmployeeId(oldEmployeeId);

        if (customers.isEmpty()) {
            final String errorMessage = "No customers found for oldEmployee ID: " + oldEmployeeId;
            log.warn("::reassignCustomers entity warn: {}", errorMessage);
            throw new ResourceNotFoundException(errorMessage);
        }

        final Employee newEmployee = employeeService.getEmployeeById(newEmployeeId).get();

        handleCustomerReassignment(customers, newEmployee);

        if (log.isInfoEnabled()) {
            log.info("::reassignCustomers completed successfully");
        }
    }

    /**
     * Creates a customer and assigns them to an employee.
     *
     * @param customer   the customer to be created
     * @param employeeId ID of the employee to whom the customer will be assigned
     * @return the created customer
     */
    @Override
    public Customer createCustomerForEmployee(final Customer customer, final Long employeeId) {
        if (log.isInfoEnabled()) {
            log.info("::createCustomerForEmployee started with: customer: {}, employeeId: {}", customer, employeeId);
        }

        // Validierung im service
        final Employee existingEmployee = employeeService.getEmployeeById(employeeId).get();
        customer.setEmployee(existingEmployee);
        customer.setLastInteractionDate(LocalDate.now());

        final Customer savedCustomer = customerService.createCustomer(customer);

        if (log.isInfoEnabled()) {
            log.info("::createCustomerForEmployee completed successfully");
        }
        return savedCustomer;
    }

    /**
     * Handles the reassignment of customers to a new employee.
     *
     * @param customers   the list of customers to be reassigned
     * @param newEmployee the new employee to whom the customers will be assigned
     */
    private void handleCustomerReassignment(final List<Customer> customers, final Employee newEmployee) {
        customers.forEach(customer -> {
            log.info("Reassigning customer ID: {} to new employee ID: {}", customer.getId(), newEmployee.getId());
            customer.setEmployee(newEmployee);
        });

        customerService.updateCustomers(customers);
        if (log.isInfoEnabled()) {
            log.info("Customers reassigned successfully");
        }
    }

    /**
     * Creates an action to delete an employee.
     *
     * @return the action to delete an employee
     */
    private EntityAction createDeleteEmployeeAction() {
        return id -> {
            final Employee oldEmployee = employeeService.getEmployeeById(id).get();
            inactiveEmployeeService.createInactiveEmployee(oldEmployee);
            employeeService.deleteEmployee(id);
        };
    }

    /**
     * Executes an action on an entity identified by a Long ID.
     *
     * @param id     the ID of the entity on which the action is to be performed
     * @param action the action to be executed
     */
    private void processEntityAction(final Long id, final EntityAction action) {
        action.execute(id);
    }
}
