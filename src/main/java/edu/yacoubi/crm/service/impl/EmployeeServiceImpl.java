package edu.yacoubi.crm.service.impl;

import edu.yacoubi.crm.model.Employee;
import edu.yacoubi.crm.repository.EmployeeRepository;
import edu.yacoubi.crm.service.IEmployeeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmployeeServiceImpl implements IEmployeeService {

    private final EmployeeRepository employeeRepository;

    @Override
    public Employee createEmployee(Employee employee) {
        log.info("Creating new Employee");
        // Hier könnte auch eine Validierungslogik hinzukommen
        return employeeRepository.save(employee);
    }
}
