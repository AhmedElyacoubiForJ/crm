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
 * @brief Service implementation for orchestrating entities.
 * <p>
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
     * @param oldEmployeeId ID of the employee to be deleted.
     * @param newEmployeeId ID of the employee to whom the customers will be reassigned.
     * @brief Deletes an employee and reassigns their customers to another employee.
     */
    @Transactional
    @Override
    public void deleteEmployeeAndReassignCustomers(Long oldEmployeeId, Long newEmployeeId) {
        log.info("::deleteEmployeeAndReassignCustomers started with: oldEmployeeId: {}, newEmployeeId: {}",
                oldEmployeeId, newEmployeeId
        );

        // parameter validation in this.reassignCustomers, otherwise we have double validation

        this.reassignCustomers(oldEmployeeId, newEmployeeId);

        EntityAction deleteEmployeeAction = createDeleteEmployeeAction();
        processEntityAction(oldEmployeeId, deleteEmployeeAction);

        log.info("::deleteEmployeeAndReassignCustomers completed successfully");
    }

    /**
     * @param customerId ID of the customer to be reassigned.
     * @param employeeId ID of the employee to whom the customer will be assigned.
     * @brief Reassigns a customer to a different employee.
     */
    @Override
    @Transactional
    public void reassignCustomerToEmployee(Long customerId, Long employeeId) {
        log.info("::reassignCustomerToEmployee started with: customerId: {}, employeeId: {}",
                customerId, employeeId
        );
        // EntityOrchestratorServiceImpl parameter warn
        if (customerId == null || employeeId == null || customerId < 0 || employeeId < 0) {
            String errorMessage = "Customer or Employee IDs must not be null and must be a positive number";
            log.warn("::reassignCustomerToEmployee parameter warn: {}", errorMessage);
            throw new IllegalArgumentException(errorMessage);
        }

        Customer customer = customerService.getCustomerById(customerId).get();
        Employee employee = employeeService.getEmployeeById(employeeId).get();

        customer.setEmployee(employee);
        customerService.updateCustomer(customerId, customer);

        log.info("::reassignCustomerToEmployee completed successfully");
    }

    /**
     * @param oldEmployeeId ID of the old employee.
     * @param newEmployeeId ID of the new employee.
     * @brief Reassigns customers from an old employee to a new employee.
     */
    @Override
    public void reassignCustomers(Long oldEmployeeId, Long newEmployeeId) {
        log.info("::reassignCustomers started with: oldEmployeeId: {}, newEmployeeId: {}",
                oldEmployeeId, newEmployeeId
        );

        if (oldEmployeeId == null || newEmployeeId == null || oldEmployeeId < 0 || newEmployeeId < 0) {
            String errorMessage = "Employee IDs must not be null and must be a positive number";
            log.warn("::reassignCustomers parameter warn: {}", errorMessage);
            throw new IllegalArgumentException(errorMessage);
        }

        if (newEmployeeId.equals(oldEmployeeId)) {
            String errorMessage = "Old and new employee IDs must be different";
            log.warn("::reassignCustomers parameter warn: {}", errorMessage);
            //log.warn(errorMessage);
            throw new IllegalArgumentException(errorMessage);
        }

        List<Customer> customers = customerService.getCustomersByEmployeeId(oldEmployeeId);

        if (customers.isEmpty()) {
            String errorMessage = "No customers found for oldEmployee ID: " + oldEmployeeId;
            log.warn("::reassignCustomers entity warn: {}", errorMessage);
            throw new ResourceNotFoundException(errorMessage);
        }

        Employee newEmployee = employeeService.getEmployeeById(newEmployeeId).get();

        handleCustomerReassignment(customers, newEmployee);

        log.info("::reassignCustomers completed successfully");
    }

    /**
     * @brief Creates a customer and assigns them to an employee.
     *
     * @param customer The customer to be created.
     * @param employeeId ID of the employee to whom the customer will be assigned.
     * @return The created customer.
     */
    @Override
    public Customer createCustomerForEmployee(Customer customer, Long employeeId) {
        log.info("::createCustomerForEmployee started with: customer: {}, employeeId: {}", customer, employeeId);

        // Validierung im service
        Employee existingEmployee = employeeService.getEmployeeById(employeeId).get();
        customer.setEmployee(existingEmployee);
        customer.setLastInteractionDate(LocalDate.now());

        Customer savedCustomer = customerService.createCustomer(customer);

        log.info("::createCustomerForEmployee completed successfully");
        return savedCustomer;
    }

    /**
     * @param customers   the list of customers to be reassigned.
     * @param newEmployee the new employee to whom the customers will be assigned.
     * @brief Handles the reassignment of customers to a new employee.
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
     * @return the action to delete an employee.
     * @brief Creates an action to delete an employee.
     */
    private EntityAction createDeleteEmployeeAction() {
        return id -> {
            Employee oldEmployee = employeeService.getEmployeeById(id).get();
            inactiveEmployeeService.createInactiveEmployee(oldEmployee);
            employeeService.deleteEmployee(id);
        };
    }

    /**
     * @param id     the ID of the entity on which the action is to be performed.
     * @param action the action to be executed.
     * @brief Executes an action on an entity identified by a Long ID.
     */
    private void processEntityAction(Long id, EntityAction action) {
        action.execute(id);
    }
}
