package edu.yacoubi.crm.service;

import edu.yacoubi.crm.dto.employee.EmployeePatchDTO;
import edu.yacoubi.crm.model.Employee;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Optional;

public interface IEmployeeService {
    Employee createEmployee(Employee employee);

    List<Employee> getAllEmployees();

    Page<Employee> getEmployeesWithPagination(int page, int size);

    Optional<Employee> getEmployeeById(Long employeeId);

    Employee updateEmployee(Long employeeId, Employee employee);

    Optional<Employee> getEmployeeByEmail(String mail);

    void partialUpdateEmployee(Long employeeId, EmployeePatchDTO employeePatchDTO);

    Page<Employee> getEmployeesByFirstNameOrDepartment(String search, int page, int size);

    Optional<List<String>> getAllDepartments();
}
