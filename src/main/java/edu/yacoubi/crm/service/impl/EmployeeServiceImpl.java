package edu.yacoubi.crm.service.impl;

import edu.yacoubi.crm.dto.employee.EmployeePatchDTO;
import edu.yacoubi.crm.exception.ResourceNotFoundException;
import edu.yacoubi.crm.model.Employee;
import edu.yacoubi.crm.repository.EmployeeRepository;
import edu.yacoubi.crm.repository.IEmployeeCustomRepository;
import edu.yacoubi.crm.service.IEmployeeService;
import edu.yacoubi.crm.service.validation.EntityValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
// TODO validate method parameters
public class EmployeeServiceImpl implements IEmployeeService {
    private final EmployeeRepository employeeRepository;
    private final IEmployeeCustomRepository employeeCustomRepository;
    private final EntityValidator entityValidator;

    @Override
    public Employee createEmployee(Employee employee) {
        log.info("EmployeeServiceImpl::createEmployee execution start: employee {}", employee);

        // Validate parameters first
        if (employee == null) {
            log.warn("Employee must not be null");
            throw new IllegalArgumentException("Employee must not be null");
        }

        // wäre besser die Methode mit EmployeeRequestDTO als parameter
        employee.setId(null);

        Employee savedEmployee = employeeRepository.save(employee);

        log.info("EmployeeServiceImpl::createEmployee execution end");
        return savedEmployee;
    }

    @Override
    public List<Employee> getAllEmployees() {
        log.info("EmployeeServiceImpl::getAllEmployees execution start");

        List<Employee> employees = employeeRepository.findAll();

        log.info("EmployeeServiceImpl::getAllEmployees execution end");
        return employees;
    }

    @Override
    public Page<Employee> getEmployeesWithPagination(int page, int size) {
        log.info("EmployeeServiceImpl::getEmployeesWithPagination execution start: page {}, size {}", page, size);

        Pageable pageable = PageRequest.of(page, size);
        Page<Employee> employeePage = employeeRepository.findAll(pageable);

        log.info("EmployeeServiceImpl::getEmployeesWithPagination execution end");
        return employeePage;
    }

    @Override
    public Optional<Employee> getEmployeeById(Long employeeId) {
        log.info("EmployeeServiceImpl::getEmployeeById execution start: employeeId {}", employeeId);

        if (employeeId == null) {
            log.warn("Employee ID must not be null");
            throw new IllegalArgumentException("Employee ID must not be null");
        }

        entityValidator.validateEmployeeExists(employeeId);

        Optional<Employee> optionalEmployee = employeeRepository.findById(employeeId);

        log.info("EmployeeServiceImpl::getEmployeeById execution end");
        return optionalEmployee;
    }

    @Override
    public Employee updateEmployee(Long employeeId, Employee employee) {
        log.info("EmployeeServiceImpl::updateEmployee execution start: employee {}", employee);

        entityValidator.validateEmployeeExists(employeeId);
        employee.setId(employeeId);

        Employee updatedEmployee = employeeRepository.save(employee);

        log.info("EmployeeServiceImpl::updateEmployee execution end");
        return updatedEmployee;
    }

    @Override
    public Optional<Employee> getEmployeeByEmail(String email) {
        log.info("EmployeeServiceImpl::getEmployeeByEmail execution start: email {}", email);

        Optional<Employee> optionalEmployee = employeeRepository.findByEmail(email);

        log.info("EmployeeServiceImpl::getEmployeeByEmail execution end");
        return optionalEmployee;
    }

    @Override
    @Transactional
    public void partialUpdateEmployee(Long employeeId, EmployeePatchDTO employeePatchDTO) {
        log.info("EmployeeServiceImpl::partialUpdateEmployee execution start: employeeId {}, employeePatchDTO {}", employeeId, employeePatchDTO);

        entityValidator.validateEmployeeExists(employeeId);

        // delegate
        // to custom repository for more complex queries
        employeeCustomRepository.partialUpdateEmployee(employeeId, employeePatchDTO);
        log.info("EmployeeServiceImpl::partialUpdateEmployee execution end");
    }

    @Override
    public Page<Employee> getEmployeesByFirstNameOrDepartment(String searchString, int page, int size) {
        log.info("EmployeeServiceImpl::searchByFirstNameOrDepartment execution start: searchString {}, page {}, size {}", searchString, page, size);

        Pageable pageable = PageRequest.of(page, size);

        Page<Employee> employeePage = employeeRepository
                .findByFirstNameContainingIgnoreCaseOrDepartmentContainingIgnoreCase(
                        searchString,
                        searchString,
                        pageable
                );

        log.info("EmployeeServiceImpl::searchByFirstNameOrDepartment execution end");
        return employeePage;
    }

    @Override
    public Optional<List<String>> getAllDepartments() {
        log.info("EmployeeServiceImpl::getAllDepartments execution start");

        Optional<List<String>> optionalDepartments = employeeRepository.findAllDepartments();

        log.info("EmployeeServiceImpl::getAllDepartments execution end");
        return optionalDepartments;
    }

    @Override
    public void deleteEmployee(Long employeeId) {
        log.info("EmployeeServiceImpl::deleteEmployee employeeId: {}", employeeId);

        entityValidator.validateEmployeeExists(employeeId);

        // Überprüfung, ob dem Mitarbeiter Kunden zugewiesen sind
        if (employeeRepository.hasCustomers(employeeId)) {
            log.warn("Cannot delete employee, customers are still assigned. employeeId: {}", employeeId);
            throw new IllegalArgumentException("Cannot delete employee, customers are still assigned.");
        }

        // Überprüfung, ob der Mitarbeiter archiviert ist
        try {
            entityValidator.validateInactiveEmployeeExists(employeeId);
        } catch (ResourceNotFoundException exception) {
            log.warn("Cannot delete not archived employee. EmployeeId: {}", employeeId);
            throw new IllegalArgumentException("Cannot delete not archived employee.");
        }

        employeeRepository.deleteById(employeeId);
        log.info("EmployeeServiceImpl::deleteEmployee execution end");
    }
}

