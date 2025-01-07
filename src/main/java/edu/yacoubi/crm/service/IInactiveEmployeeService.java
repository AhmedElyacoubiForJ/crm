package edu.yacoubi.crm.service;

import edu.yacoubi.crm.model.Employee;
import edu.yacoubi.crm.model.InactiveEmployee;

import java.util.Optional;

public interface IInactiveEmployeeService {
    InactiveEmployee createInactiveEmployee(Employee employee);

    Optional<InactiveEmployee> getInactiveEmployeeById(Long id);

    boolean existsByOriginalEmployeeId(Long employeeId);
}
