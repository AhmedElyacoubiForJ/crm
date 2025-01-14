package edu.yacoubi.crm.service;

import edu.yacoubi.crm.model.Customer;

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

    /**
     * @brief Creates a customer and assigns them to an employee.
     *
     * @param customer The customer to be created.
     * @param employeeId ID of the employee to whom the customer will be assigned.
     * @return The created customer.
     */
    Customer createCustomerForEmployee(Customer customer, Long employeeId);
}
