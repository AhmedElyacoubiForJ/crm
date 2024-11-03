package edu.yacoubi.crm.service.impl;

import edu.yacoubi.crm.dto.employee.EmployeePatchDTO;
import edu.yacoubi.crm.exception.ResourceNotFoundException;
import edu.yacoubi.crm.model.Employee;
import edu.yacoubi.crm.repository.EmployeeRepository;
import edu.yacoubi.crm.service.IEmployeeService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaUpdate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmployeeServiceImpl implements IEmployeeService {

    private final EmployeeRepository employeeRepository;

    private final EntityManager entityManager;

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

    @Override
    @Transactional
    public void partialUpdateEmployee(Long id, EmployeePatchDTO employeePatchDTO) {
        log.info("EmployeeServiceImpl::partialUpdateEmployee id: {}, employeePatchDTO: {}", id, employeePatchDTO);

        if (!employeeRepository.existsById(id)) {
            log.warn("Employee not found with ID: {}", id);
            throw new ResourceNotFoundException("Employee not found with ID: " + id);
        }

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaUpdate<Employee> update = cb.createCriteriaUpdate(Employee.class);
        Root<Employee> root = update.from(Employee.class);

        if (employeePatchDTO.getFirstName() != null) {
            update.set(root.get("firstName"), employeePatchDTO.getFirstName());
        }
        if (employeePatchDTO.getLastName() != null) {
            update.set(root.get("lastName"), employeePatchDTO.getLastName());
        }
        if (employeePatchDTO.getEmail() != null) {
            update.set(root.get("email"), employeePatchDTO.getEmail());
        }
        if (employeePatchDTO.getDepartment() != null) {
            update.set(root.get("department"), employeePatchDTO.getDepartment());
        }

        update.where(cb.equal(root.get("id"), id));

        log.info("Partial update executed for employee ID: {}", id);
        entityManager.createQuery(update).executeUpdate();
    }
}

