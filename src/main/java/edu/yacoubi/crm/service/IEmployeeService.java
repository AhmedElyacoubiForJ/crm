package edu.yacoubi.crm.service;

import edu.yacoubi.crm.dto.employee.EmployeePatchDTO;
import edu.yacoubi.crm.model.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;

public interface IEmployeeService {
    Employee createEmployee(Employee employee);

    List<Employee> getAllEmployees();

    Page<Employee> getEmployeesWithPagination(int page, int size);

    Optional<Employee> getEmployeeById(Long id);

    void deleteEmployee(Long id);

    Employee updateEmployee(Employee employee);

    Optional<Employee> getEmployeeByEmail(String mail);

    void partialUpdateEmployee(Long id, EmployeePatchDTO employeePatchDTO);

    Optional<List<Employee>> getEmployeesByFirstName(String searchString);

    Optional<List<Employee>> getEmployeesByEmail(String searchString);

    Page<Employee> getEmployeesByFirstNameOrDepartment(String search, int page, int size);
}
