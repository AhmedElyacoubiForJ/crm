package edu.yacoubi.crm.service;

import edu.yacoubi.crm.model.Customer;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Optional;

/**
 * @brief Service interface for orchestrating entities.
 *
 * This interface defines operations related to employees and customers.
 */
public interface IEntityOrchestratorService {

    /**
     * @brief Deletes an employee and reassigns their customers to another employee.
     *
     * @param oldEmployeeId ID of the employee to be deleted.
     * @param newEmployeeId ID of the employee to whom the customers will be reassigned.
     */
    void deleteEmployeeAndReassignCustomers(Long oldEmployeeId, Long newEmployeeId);

    /**
     * @brief Reassigns a customer to a different employee.
     *
     * @param customerId ID of the customer to be reassigned.
     * @param employeeId ID of the employee to whom the customer will be assigned.
     */
    void reassignCustomerToEmployee(Long customerId, Long employeeId);

    /**
     * @brief Reassigns customers from an old employee to a new employee.
     *
     * @param oldEmployeeId ID of the old employee.
     * @param newEmployeeId ID of the new employee.
     */
    void reassignCustomers(Long oldEmployeeId, Long newEmployeeId);
}