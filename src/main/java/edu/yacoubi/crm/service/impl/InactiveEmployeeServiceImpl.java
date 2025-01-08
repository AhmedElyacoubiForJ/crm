package edu.yacoubi.crm.service.impl;

import edu.yacoubi.crm.model.Employee;
import edu.yacoubi.crm.model.InactiveEmployee;
import edu.yacoubi.crm.repository.InactiveEmployeeRepository;
import edu.yacoubi.crm.service.IInactiveEmployeeService;
import edu.yacoubi.crm.service.validation.EntityValidator;
import edu.yacoubi.crm.util.EntityTransformer;
import edu.yacoubi.crm.util.TransformerUtil;
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
        // Parameter-Validierung
        if (employee == null) {
            throw new IllegalArgumentException("Employee is null.");
        }

        // Feld-Validierung des Employees
        validateBasicEmployeeFields(employee);

        if (entityValidator.validateEmployeeHasCustomers(employee.getId())) {
            throw new IllegalArgumentException("Employee has assigned customers.");
        }

        InactiveEmployee inactiveEmployee = TransformerUtil
                .transform(EntityTransformer.employeeToInactiveEmployee, employee);

        return inactiveEmployeeRepository.save(inactiveEmployee);
    }

    @Override
    public Optional<InactiveEmployee> getInactiveEmployeeById(Long id) {
        return inactiveEmployeeRepository.findById(id);
    }

    @Override
    public boolean existsByOriginalEmployeeId(Long originalEmployeeId) {
        return inactiveEmployeeRepository.existsByOriginalEmployeeId(originalEmployeeId);
    }

    // Methode zur Basis-Validierung von Employee-Feldern
    private void validateBasicEmployeeFields(Employee employee) {
        log.info("validateBasicEmployeeFields employeeId: {}", employee.getId());

        if (employee.getFirstName() == null || employee.getFirstName().isEmpty() ||
                employee.getLastName() == null || employee.getLastName().isEmpty() ||
                employee.getEmail() == null || employee.getEmail().isEmpty() ||
                employee.getDepartment() == null || employee.getDepartment().isEmpty()) {
            throw new IllegalArgumentException("Employee has incomplete information.");
        }
    }
}
