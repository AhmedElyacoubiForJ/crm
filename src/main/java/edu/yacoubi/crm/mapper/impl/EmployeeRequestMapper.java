package edu.yacoubi.crm.mapper.impl;

import edu.yacoubi.crm.dto.EmployeeRequestDTO;
import edu.yacoubi.crm.mapper.IMapper;
import edu.yacoubi.crm.model.Employee;
import org.springframework.stereotype.Component;

@Component
public class EmployeeRequestMapper implements IMapper<Employee, EmployeeRequestDTO> {

    @Override
    public EmployeeRequestDTO mapTo(Employee employee) {
        return EmployeeRequestDTO.builder()
                .firstName(employee.getFirstName())
                .lastName(employee.getLastName())
                .email(employee.getEmail())
                .department(employee.getDepartment())
                .build();
    }

    @Override
    public Employee mapFrom(EmployeeRequestDTO employeeRequestDTO) {
        return Employee.builder()
                .firstName(employeeRequestDTO.getFirstName())
                .lastName(employeeRequestDTO.getLastName())
                .email(employeeRequestDTO.getEmail())
                .department(employeeRequestDTO.getDepartment())
                .build();
    }
}
