package edu.yacoubi.crm.service;

import edu.yacoubi.crm.model.Employee;
import edu.yacoubi.crm.model.InactiveEmployee;

public interface IInactiveEmployeeService {
    InactiveEmployee createInactiveEmployee(Employee employee);
}
