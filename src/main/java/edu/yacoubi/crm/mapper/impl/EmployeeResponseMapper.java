package edu.yacoubi.crm.mapper.impl;

import edu.yacoubi.crm.dto.EmployeeResponseDTO;
import edu.yacoubi.crm.mapper.IMapper;
import edu.yacoubi.crm.model.Employee;
import org.springframework.stereotype.Component;

@Component
public class EmployeeResponseMapper implements IMapper<Employee, EmployeeResponseDTO> {

    @Override
    public EmployeeResponseDTO mapTo(Employee employee) {
        return EmployeeResponseDTO.builder()
                .id(employee.getId())
                .firstName(employee.getFirstName())
                .lastName(employee.getLastName())
                .email(employee.getEmail())
                .department(employee.getDepartment())
                .build();
    }

    @Override
    public Employee mapFrom(EmployeeResponseDTO source) {
        return null;
    }
}
