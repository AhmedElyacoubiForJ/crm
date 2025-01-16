package edu.yacoubi.crm.util;

public class Constants {
    // Error messages
    public static final String ERROR_INVALID_IDS_MSG =
            "Employee IDs must not be null and must be a positive number";
    public static final String ERROR_SAME_IDS_MSG =
            "Old and new employee IDs must be different";
    public static final String ERROR_MSG_NO_CUSTOMERS =
            "No customers found for oldEmployee ID: %d";

    // Logger infos
    public static final String INFO_LOG_REASSIGN_CUSTOMERS_ENTRY_POINT =
            "::reassignCustomers started with: oldEmployeeId: %d, newEmployeeId: %d";
    public static final String INFO_LOG_REASSIGN_CUSTOMERS_EXIT_POINT =
            "::reassignCustomers completed successfully";
    // reassignCustomer parameter warn
    public static final String WARN_ENTITY_LOG_REASSIGN_CUSTOMERS =
            "::reassignCustomers entity warn: %s";
    public static final String WARN_PARAM_LOG_REASSIGN_CUSTOMERS =
            "::reassignCustomers parameter warn: %s";

    public static final String INFO_LOG_REASSIGN_CUS_2_EMP_ENTRY_POINT =
            "::reassignCustomerToEmployee started with: customerId: %d, employeeId: %d";
    public static final String INFO_LOG_REASSIGN_CUS_2_EMP_EXIT_POINT =
            "::reassignCustomerToEmployee completed successfully";
    // reassignCustomerToEmployee parameter warn
    public static final String WARN_PARAM_LOG_REASSIGN_CUS_2_EMP =
            "::reassignCustomerToEmployee parameter warn: %s";

    public static final String INFO_LOG_DEL_EMP_AND_REASSIGN_CUSTOMERS_ENTRY_POINT =
            "::deleteEmployeeAndReassignCustomers started with: oldEmployeeId: %d, newEmployeeId: %d";
    public static final String INFO_LOG_DEL_EMP_AND_REASSIGN_CUSTOMERS_EXIT_POINT =
            "::deleteEmployeeAndReassignCustomers completed successfully";

    public static final String INFO_LOG_CREATE_CUT_4_EMP_ENTRY_POINT =
            "::createCustomerForEmployee started with: customer: %s, employeeId: %d";
    public static final String INFO_LOG_CREATE_CUT_4_EMP_EXIT_POINT =
            "::createCustomerForEmployee completed successfully";


    // Assert supplied failure message
    public static final String WARN_SUPPLIED_MSG = "Warn message should be: %s";
    public static final String ERROR_SUPPLIED_MSG = "Error message should be: %s";
    public static final String INFO_SUPPLIED_MSG = "Info message should be: %s";
}
