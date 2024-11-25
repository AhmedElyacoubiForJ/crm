package edu.yacoubi.crm.service.impl;

import edu.yacoubi.crm.model.Employee;
import edu.yacoubi.crm.model.InactiveEmployee;
import edu.yacoubi.crm.repository.InactiveEmployeeRepository;
import edu.yacoubi.crm.service.IInactiveEmployeeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class InactiveEmployeeServiceImpl implements IInactiveEmployeeService {

    private final InactiveEmployeeRepository inactiveEmployeeRepository;

    @Override
    public InactiveEmployee createInactiveEmployee(Employee employee) {

        InactiveEmployee inactiveEmployee = InactiveEmployee.builder()
                .firstName(employee.getFirstName())
                .lastName(employee.getLastName())
                .email(employee.getEmail())
                .department(employee.getDepartment())
                .originalEmployeeId(employee.getId())
                .build();

        return inactiveEmployeeRepository.save(inactiveEmployee);
    }
}
