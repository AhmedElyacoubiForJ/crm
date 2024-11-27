package edu.yacoubi.crm.service;

import com.fasterxml.jackson.annotation.OptBoolean;
import edu.yacoubi.crm.model.Employee;
import edu.yacoubi.crm.model.InactiveEmployee;

import java.util.Optional;

public interface IInactiveEmployeeService {
    InactiveEmployee createInactiveEmployee(Employee employee);
    Optional<InactiveEmployee> getInactiveEmployeeById(Long id);
}
