package edu.yacoubi.crm.service.impl;

import edu.yacoubi.crm.model.Employee;
import edu.yacoubi.crm.model.InactiveEmployee;
import edu.yacoubi.crm.repository.InactiveEmployeeRepository;
import edu.yacoubi.crm.service.IInactiveEmployeeService;
import edu.yacoubi.crm.service.validation.EntityValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class InactiveEmployeeServiceImpl implements IInactiveEmployeeService {

    private final InactiveEmployeeRepository inactiveEmployeeRepository;
    private final EntityValidator entityValidator;

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

    @Override
    public Optional<InactiveEmployee> getInactiveEmployeeById(Long id) {
        return inactiveEmployeeRepository.findById(id);
    }

    @Override
    public boolean existsByOriginalEmployeeId(Long originalEmployeeId) {
        return inactiveEmployeeRepository
                .existsByOriginalEmployeeId(originalEmployeeId);
    }
}
