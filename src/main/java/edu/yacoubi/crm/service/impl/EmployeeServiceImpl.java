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

    /**
     * Erstellt einen neuen Mitarbeiter.
     * Diese Methode ist als transaktional markiert, um sicherzustellen, dass die Operationen atomar sind,
     * und um bei Rollbacks Datenkonsistenz zu gewährleisten.
     */
    @Override
    @Transactional
    public Employee createEmployee(Employee employee) {
        log.info("::createEmployee started with: employee {}", employee);

        // Validate parameters first
        if (employee == null) {
            log.warn("Employee must not be null");
            throw new IllegalArgumentException("Employee must not be null");
        }

        // Setze die ID auf null, um sicherzustellen, dass ein neuer Datensatz erstellt wird
        employee.setId(null);

        // Speichere den Mitarbeiter in der Datenbank
        Employee savedEmployee = employeeRepository.save(employee);

        log.info("::createEmployee completed successfully");
        return savedEmployee;
    }

    /**
     * Ruft alle Mitarbeiter ab.
     * Diese Methode benötigt keine Transaktion, da sie nur Leseoperationen durchführt.
     */
    @Override
    public List<Employee> getAllEmployees() {
        log.info("::getAllEmployees started");

        List<Employee> employees = employeeRepository.findAll();

        log.info("::getAllEmployees completed successfully");
        return employees;
    }

    /**
     * Ruft Mitarbeiter mit Paginierung ab.
     * Diese Methode benötigt ebenfalls keine Transaktion, da sie nur Leseoperationen durchführt.
     */
    @Override
    public Page<Employee> getEmployeesWithPagination(int page, int size) {
        log.info("::getEmployeesWithPagination started with: page {}, size {}", page, size);

        Pageable pageable = PageRequest.of(page, size);
        Page<Employee> employeePage = employeeRepository.findAll(pageable);

        log.info("::getEmployeesWithPagination completed successfully");
        return employeePage;
    }

    /**
     * Ruft einen Mitarbeiter nach ID ab.
     * Diese Methode ist als read-only transaktional markiert,
     * um Datenkonsistenz bei gleichzeitiger Erhöhung der Performance zu gewährleisten.
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<Employee> getEmployeeById(Long employeeId) {
        log.info("::getEmployeeById started with: employeeId {}", employeeId);

        if (employeeId == null) {
            log.warn("Employee ID must not be null");
            throw new IllegalArgumentException("Employee ID must not be null");
        }

        entityValidator.validateEmployeeExists(employeeId);

        Optional<Employee> optionalEmployee = employeeRepository.findById(employeeId);

        log.info("::getEmployeeById completed successfully");
        return optionalEmployee;
    }

    /**
     * Aktualisiert einen bestehenden Mitarbeiter.
     * Diese Methode ist als transaktional markiert, um sicherzustellen,
     * dass die Operation atomar ist und um Datenkonsistenz zu gewährleisten.
     */
    @Override
    @Transactional
    public Employee updateEmployee(Long employeeId, Employee employee) {
        log.info("::updateEmployee started with: employee {}", employee);

        entityValidator.validateEmployeeExists(employeeId);
        employee.setId(employeeId);

        Employee updatedEmployee = employeeRepository.save(employee);

        log.info("::updateEmployee completed successfully");
        return updatedEmployee;
    }

    /**
     * Ruft einen Mitarbeiter nach E-Mail ab.
     * Diese Methode benötigt keine Transaktion, da sie nur Leseoperationen durchführt.
     */
    @Override
    public Optional<Employee> getEmployeeByEmail(String email) {
        log.info("::getEmployeeByEmail started with: email {}", email);

        Optional<Employee> optionalEmployee = employeeRepository.findByEmail(email);

        log.info("::getEmployeeByEmail completed successfully");
        return optionalEmployee;
    }

    /**
     * Führt eine teilweise Aktualisierung eines Mitarbeiters durch.
     * Diese Methode ist als transaktional markiert, da komplexe Schreiboperationen durchgeführt werden,
     * die atomar erfolgen sollten.
     */
    @Override
    @Transactional
    public void partialUpdateEmployee(Long employeeId, EmployeePatchDTO employeePatchDTO) {
        log.info("::partialUpdateEmployee started with: employeeId {}, employeePatchDTO {}",
                employeeId, employeePatchDTO);

        entityValidator.validateEmployeeExists(employeeId);

        // delegate to custom repository for more complex queries
        employeeCustomRepository.partialUpdateEmployee(employeeId, employeePatchDTO);
        log.info("::partialUpdateEmployee completed successfully");
    }

    /**
     * Sucht Mitarbeiter nach Vorname oder Abteilung.
     * Diese Methode benötigt keine Transaktion, da sie nur Leseoperationen durchführt.
     */
    @Override
    public Page<Employee> getEmployeesByFirstNameOrDepartment(String searchString, int page, int size) {
        log.info("::searchByFirstNameOrDepartment started with: searchString {}, page {}, size {}",
                searchString, page, size);

        Pageable pageable = PageRequest.of(page, size);

        Page<Employee> employeePage = employeeRepository
                .findByFirstNameContainingIgnoreCaseOrDepartmentContainingIgnoreCase(
                        searchString,
                        searchString,
                        pageable
                );

        log.info("::searchByFirstNameOrDepartment completed successfully");
        return employeePage;
    }

    /**
     * Ruft alle Abteilungen ab.
     * Diese Methode benötigt keine Transaktion, da sie nur Leseoperationen durchführt.
     */
    @Override
    public Optional<List<String>> getAllDepartments() {
        log.info("::getAllDepartments started");

        Optional<List<String>> optionalDepartments = employeeRepository.findAllDepartments();

        log.info("::getAllDepartments completed successfully");
        return optionalDepartments;
    }

    /**
     * Löscht einen Mitarbeiter.
     * Diese Methode ist als transaktional markiert, um sicherzustellen,
     * dass die Validierungen und die Löschoperation atomar erfolgen.
     */
    @Override
    @Transactional
    public void deleteEmployee(Long employeeId) {
        log.info("::deleteEmployee started with: employeeId: {}", employeeId);

        entityValidator.validateEmployeeExists(employeeId);

        // Überprüfung, ob der Mitarbeiter Kunden zugewiesen sind
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
        log.info("::deleteEmployee completed successfully");
    }

    /**
     * Überprüft, ob einem Mitarbeiter Kunden zugewiesen sind.
     * Diese Methode ist als read-only transaktional markiert,
     * um Datenkonsistenz bei gleichzeitiger Erhöhung der Performance zu gewährleisten.
     */
    @Override
    @Transactional(readOnly = true)
    public boolean hasCustomers(Long employeeId) {
        log.info("::hasCustomers started with: employeeId {}", employeeId);
        // Validate that the employee exists
        entityValidator.validateEmployeeExists(employeeId);

        // Use the query defined in the EmployeeRepository
        boolean hasCustomers = employeeRepository.hasCustomers(employeeId);

        log.info("::hasCustomers completed successfully");
        return hasCustomers;
    }
}

