package edu.yacoubi.crm.service;

public interface IEmployeeCustomerOrchestratorService {
    /**
     * Deletes an employee and reassigns their customers to another employee.
     *
     * @param oldEmployeeId the ID of the employee to be deleted
     * @param newEmployeeId the ID of the employee to whom customers are reassigned
     */
    void deleteEmployeeAndReassignCustomers(Long oldEmployeeId, Long newEmployeeId);

    /**
     * Assigns a customer to an employee.
     *
     * @param customerId the ID of the customer to be assigned
     * @param employeeId the ID of the employee
     */
    void reassignCustomerToEmployee(Long customerId, Long employeeId);

    /**
     * Reassigns customers from the old employee to the new employee.
     *
     * @param oldEmployeeId the ID of the employee from whom customers are reassigned
     * @param newEmployeeId the ID of the employee to whom customers are reassigned
     */
    void reassignCustomers(Long oldEmployeeId, Long newEmployeeId);
}
