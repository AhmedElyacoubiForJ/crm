package edu.yacoubi.crm.service.impl;

import edu.yacoubi.crm.exception.ResourceNotFoundException;
import edu.yacoubi.crm.model.Employee;
import edu.yacoubi.crm.repository.EmployeeRepository;
import edu.yacoubi.crm.service.IEmployeeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmployeeServiceImpl implements IEmployeeService {

    private final EmployeeRepository employeeRepository;

    @Override
    public Employee createEmployee(Employee employee) {
        log.info("EmployeeServiceImpl::createEmployee employee {}", employee);
        // Hier könnte auch eine Validierungslogik hinzukommen
        return employeeRepository.save(employee);
    }

    @Override
    public List<Employee> getAllEmployees() {
        log.info("EmployeeServiceImpl::getAllEmployees");
        return employeeRepository.findAll();
    }

    @Override
    public Optional<Employee> getEmployeeById(Long id) {
        log.info("EmployeeServiceImpl::getEmployeeById id {}", id);
        return employeeRepository.findById(id);
    }

    @Override
    public void deleteEmployee(Employee employee) {
        log.info("EmployeeServiceImpl::deleteEmployee employee: {}", employee);
        if (!employeeRepository.existsById(employee.getId())) {
            log.warn("Employee not found with ID: {}", employee.getId());
            throw new ResourceNotFoundException("Employee not found with ID: " + employee.getId());
        }
        employeeRepository.delete(employee);
    }

    @Override
    public Employee updateEmployee(Employee employee) {
        log.info("EmployeeServiceImpl::updateEmployee employee: {}", employee);
        // Validierung einfügen, falls notwendig
        if (!employeeRepository.existsById(employee.getId())) {
            log.warn("Employee not found with ID: {}", employee.getId());
            throw new ResourceNotFoundException("Employee not found with ID: " + employee.getId());
        }
        return employeeRepository.save(employee);
    }

    @Override
    public Optional<Employee> getEmployeeByEmail(String email) {
        log.info("EmployeeServiceImpl::getEmployeeByEmail email: {}", email);
        return employeeRepository.findByEmail(email);
    }
}

