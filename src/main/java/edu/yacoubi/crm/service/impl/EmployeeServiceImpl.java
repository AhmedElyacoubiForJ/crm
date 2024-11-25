package edu.yacoubi.crm.service.impl;

import edu.yacoubi.crm.dto.employee.EmployeePatchDTO;
import edu.yacoubi.crm.exception.ResourceNotFoundException;
import edu.yacoubi.crm.model.Customer;
import edu.yacoubi.crm.model.Employee;
import edu.yacoubi.crm.repository.EmployeeRepository;
import edu.yacoubi.crm.service.ICustomerService;
import edu.yacoubi.crm.service.IEmployeeService;
import edu.yacoubi.crm.service.IInactiveEmployeeService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaUpdate;
import jakarta.persistence.criteria.Root;
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
public class EmployeeServiceImpl implements IEmployeeService {

    private final EmployeeRepository employeeRepository;

    private final IInactiveEmployeeService inactiveEmployeeService;

    private final ICustomerService customerService;

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
    public Page<Employee> getEmployeesWithPagination(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return employeeRepository.findAll(pageable);
    }

    @Override
    public Optional<Employee> getEmployeeById(Long id) {
        log.info("EmployeeServiceImpl::getEmployeeById id {}", id);
        return employeeRepository.findById(id);
    }

    @Override
    public void deleteEmployee(Long id) {
        log.info("EmployeeServiceImpl::deleteEmployee id: {}", id);
        if (!employeeRepository.existsById(id)) {
            log.warn("Employee not found with ID: {}", id);
            throw new ResourceNotFoundException("Employee not found with ID: " + id);
        }
        employeeRepository.deleteById(id);
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

    @Override
    public Optional<List<Employee>> getEmployeesByFirstName(String fNameString) {
        log.info("EmployeeServiceImpl::getEmployeesByNameLike searchString: {}", fNameString);
        return employeeRepository.findByFirstNameIgnoreCaseContaining(fNameString);
    }

    @Override
    public Optional<List<Employee>> getEmployeesByEmail(String emailString) {
        log.info("EmployeeServiceImpl::getEmployeesByEmailLike emailString: {}", emailString);
        return employeeRepository.findByEmailIgnoreCaseContaining(emailString);
    }

    @Override
    public Page<Employee> getEmployeesByFirstNameOrDepartment(
            String searchString,
            int page,
            int size) {
        log.info("EmployeeServiceImpl::searchByFirstNameOrDepartment searchString: {}, page: {}, size: {}", searchString, page, size);
        Pageable pageable = PageRequest.of(page, size);

        return employeeRepository.findByFirstNameContainingIgnoreCaseOrDepartmentContainingIgnoreCase(
                searchString,
                searchString,
                pageable
        );
    }

    @Override
    public void deleteAndArchiveEmployee(Long id) {
        log.info("EmployeeServiceImpl::deleteAndArchiveEmployee id: {}", id);

        Employee employee = employeeRepository.findById(id).orElseThrow(
                () -> {
                    log.warn("Employee not found with ID: {}", id);
                    return new ResourceNotFoundException("Employee not found with ID: " + id);
                }
        );

        // Kunden neu zuweisen (Implementiere diese Logik entsprechend)
        reassignCustomers(employee.getId());

        // Archivierung des Mitarbeiters
        inactiveEmployeeService.createInactiveEmployee(employee);

        // Löschen des Mitarbeiters
        employeeRepository.delete(employee);

        log.info("Employee deleted and archived with ID: {}", id);
    }

    @Override public void assignCustomerToEmployee(Long customerId, Long employeeId) {
        customerService.assignCustomerToEmployee(customerId, employeeId); // Delegation an CustomerService
    }

    private void reassignCustomers(Long employeeId) {
        List<Customer> customers = customerService.getCustomersByEmployeeId(employeeId);
        for (Customer customer : customers) {
            // Logik zur neuen Zuweisung, z.B. an einen Standardmitarbeiter oder nach bestimmten Regeln
            Long newEmployeeId = 1L ; // Logik zur Bestimmung des neuen Mitarbeiters
            customerService.assignCustomerToEmployee(customer.getId(), newEmployeeId);
        }
    }

}

