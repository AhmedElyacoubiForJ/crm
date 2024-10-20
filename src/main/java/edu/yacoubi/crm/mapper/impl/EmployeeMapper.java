package edu.yacoubi.crm.mapper.impl;

import edu.yacoubi.crm.dto.EmployeeDTO;
import edu.yacoubi.crm.mapper.IMapper;
import edu.yacoubi.crm.model.Employee;
import org.springframework.stereotype.Component;

@Component
public class EmployeeMapperImpl implements IMapper<Employee, EmployeeDTO> {
    @Override
    public EmployeeDTO mapTo(Employee employee) {
        return EmployeeDTO.builder()
                .id(employee.getId())
                .firstName(employee.getFirstName())
                .lastName(employee.getLastName())
                .email(employee.getEmail())
                .department(employee.getDepartment())
                .build();
    }

    @Override
    public Employee mapFrom(EmployeeDTO employeeDTO) {
        return Employee.builder()
                .id(employeeDTO.getId())
                .firstName(employeeDTO.getFirstName())
                .lastName(employeeDTO.getLastName())
                .email(employeeDTO.getEmail())
                .department(employeeDTO.getDepartment())
                .build();
    }
}
