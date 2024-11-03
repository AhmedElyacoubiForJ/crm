package edu.yacoubi.crm.service;

import edu.yacoubi.crm.dto.EmployeePatchDTO;
import edu.yacoubi.crm.model.Employee;

import java.util.List;
import java.util.Optional;

public interface IEmployeeService {
    Employee createEmployee(Employee employee);

    List<Employee> getAllEmployees();

    Optional<Employee> getEmployeeById(Long id);

    void deleteEmployee(Employee employee);

    Employee updateEmployee(Employee employee);

    Optional<Employee> getEmployeeByEmail(String mail);

    void partialUpdateEmployee(Long id, EmployeePatchDTO employeePatchDTO);
}
