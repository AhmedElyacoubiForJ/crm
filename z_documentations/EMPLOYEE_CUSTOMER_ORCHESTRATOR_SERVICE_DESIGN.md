
# EMPLOYEE-CUSTOMER-ORCHESTRATOR SERVICE DESIGN

## Problemstellung

> In unserem bisherigen Design hatten wir in mehreren Services Abhängigkeiten zu verschiedenen Repositories und anderen Services. Dies führte zu einem engen Kopplungsverhältnis zwischen den Komponenten und verringerte die Wartbarkeit und Testbarkeit des Codes. Ein Beispiel dafür war die Methode `reassignCustomerToEmployee`, die im `CustomerServiceImpl` direkt auf das `EmployeeRepository` zugriff. Solche engen Kopplungen verstoßen gegen das Prinzip der Separation of Concerns (SoC).

### Beispielhafte Problemfälle:

1. **Use-Case: Neuzuweisung von Kunden aufgrund von Abwesenheiten `reassignCustomers`**:
   - Kunden müssen während der Abwesenheit eines Mitarbeiters (z.B. Urlaub) einem anderen Mitarbeiter zugewiesen werden.
   - Enge Kopplung zwischen `CustomerService` und `EmployeeRepository`.

2. **Use-Case: Löschen eines Mitarbeiters `deleteEmployee`**:
   - Bevor ein Mitarbeiter gelöscht und in die Historie verschoben wird, müssen alle ihm zugewiesenen Kunden einem anderen Mitarbeiter zugewiesen werden.
   - Enge Kopplung zwischen `EmployeeService` und `CustomerRepository`.

## Lösungsansatz

> Um die oben genannten Probleme zu lösen, haben wir uns entschieden, einen speziellen Orchestrator-Service (`EmployeeCustomerOrchestratorService`) einzuführen.
> Der `EmployeeCustomerOrchestratorService` wird verwendet, um komplexe Operationen zu verwalten, die sowohl den `EmployeeService` als auch den `CustomerService` betreffen.
> Er kapselt die Geschäftslogik für Szenarien, die mehrere Services betreffen.
> Dieser Service ist für alle Use-Cases verantwortlich, die auch mehrere Repositories betreffen, und ermöglicht eine klare Trennung der Verantwortlichkeiten.
> Dies verbessert die Modularität und Wartbarkeit des Codes erheblich.

### Vorteile des Lösungsansatzes:

1. **Separation of Concerns**:
   - Jede Service-Implementierung ist nur für ihre spezifischen Aufgaben verantwortlich und bleibt von anderen Repositories oder Services entkoppelt.
   
2. **Erhöhte Testbarkeit**:
   - Durch die Entkopplung der Komponenten können einzelne Services einfacher getestet werden.

3. **Klare Verantwortlichkeiten**:
   - Der Orchestrator-Service übernimmt die Koordination komplexer Geschäftslogik, die mehrere Services betrifft.

## Implementierung

### Interface: IEmployeeCustomerOrchestratorService

```java
package edu.yacoubi.crm.service;

public interface IEmployeeCustomerOrchestratorService {
    /**
     * Deletes an employee and reassigns their customers to another employee.
     *
     * @param oldEmployeeId the ID of the employee to be deleted
     * @param newEmployeeId the ID of the employee to whom customers are reassigned
     */
    void deleteEmployeeAndReassignCustomers(Long oldEmployeeId, Long newEmployeeId);

    /**
     * Assigns a customer to an employee.
     *
     * @param customerId the ID of the customer to be assigned
     * @param employeeId the ID of the employee
     */
    void reassignCustomerToEmployee(Long customerId, Long employeeId);

    /**
     * Reassigns customers from the old employee to the new employee.
     *
     * @param oldEmployeeId the ID of the employee from whom customers are reassigned
     * @param newEmployeeId the ID of the employee to whom customers are reassigned
     */
    void reassignCustomers(Long oldEmployeeId, Long newEmployeeId);
}
```

### Service-Implementierung: EmployeeCustomerOrchestratorServiceImpl

```java
package edu.yacoubi.crm.service.impl;

import edu.yacoubi.crm.exception.ResourceNotFoundException;
import edu.yacoubi.crm.model.Customer;
import edu.yacoubi.crm.model.Employee;
import edu.yacoubi.crm.repository.CustomerRepository;
import edu.yacoubi.crm.repository.EmployeeRepository;
import edu.yacoubi.crm.service.ICustomerService;
import edu.yacoubi.crm.service.IEmployeeCustomerOrchestratorService;
import edu.yacoubi.crm.service.IInactiveEmployeeService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmployeeCustomerOrchestratorServiceImpl implements IEmployeeCustomerOrchestratorService {
    public static final String EMPLOYEE_NOT_FOUND_WITH_ID = "Employee not found with ID: %d";
    public static final String CUSTOMER_NOT_FOUND_WITH_ID = "Customer not found with ID: %d";

    private final EmployeeRepository employeeRepository;
    private final ICustomerService customerService;
    private final CustomerRepository customerRepository;
    private final IInactiveEmployeeService inactiveEmployeeService;

    @Transactional
    @Override
    public void deleteEmployeeAndReassignCustomers(Long oldEmployeeId, Long newEmployeeId) {
        log.info("EmployeeOrchestratorServiceImpl::deleteEmployeeAndReassignCustomers employeeId: {}, newEmployeeId: {}", oldEmployeeId, newEmployeeId);

        if (oldEmployeeId.equals(newEmployeeId)) {
            throw new IllegalArgumentException("Old and new employee IDs must be different.");
        }

        Employee oldEmployee = employeeRepository.findById(oldEmployeeId).orElseThrow(
                () -> new ResourceNotFoundException(String.format(EMPLOYEE_NOT_FOUND_WITH_ID, oldEmployeeId))
        );
        Employee newEmployee = employeeRepository.findById(newEmployeeId).orElseThrow(
                () -> new ResourceNotFoundException(String.format(EMPLOYEE_NOT_FOUND_WITH_ID, newEmployeeId))
        );

        // Kunden neu zuweisen
        reassignCustomers(oldEmployee.getId(), newEmployee.getId());

        // Archivierung des Mitarbeiters
        inactiveEmployeeService.createInactiveEmployee(oldEmployee);

        // Löschen des Mitarbeiters
        employeeRepository.delete(oldEmployee);

        log.info("Employee deleted and customers reassigned: oldEmployeeId={}, newEmployeeId={}", oldEmployeeId, newEmployeeId);
    }

    @Override
    public void reassignCustomerToEmployee(Long customerId, Long employeeId) {
        log.info("EmployeeOrchestratorServiceImpl::reassignCustomerToEmployee customerId: {}, employeeId: {}", customerId, employeeId);

        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException(String.format(CUSTOMER_NOT_FOUND_WITH_ID, customerId)));
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException(String.format(EMPLOYEE_NOT_FOUND_WITH_ID, employeeId)));

        customer.setEmployee(employee);
        customerRepository.save(customer);

        log.info("Customer reassigned: customerId={}, newEmployeeId={}", customerId, employeeId);
    }

    @Override
    public void reassignCustomers(Long oldEmployeeId, Long newEmployeeId) {
        log.info("EmployeeOrchestratorServiceImpl::reassignCustomers oldEmployeeId: {}, newEmployeeId: {}", oldEmployeeId, newEmployeeId);

        if (newEmployeeId.equals(oldEmployeeId)) {
            log.warn("Old and new employee IDs must be different.");
            return;
        }

        List<Customer> customers = customerService.getCustomersByEmployeeId(oldEmployeeId);

        if (customers.isEmpty()) {
            log.warn("No customers found for employee ID: {}", oldEmployeeId);
            return;
        }

        Employee newEmployee = employeeRepository.findById(newEmployeeId)
                .orElseThrow(() -> new ResourceNotFoundException(String.format(EMPLOYEE_NOT_FOUND_WITH_ID, newEmployeeId)));

        customers.forEach(customer -> {
            log.info("Reassigning customer ID: {} to new employee ID: {}", customer.getId(), newEmployeeId);
            customer.setEmployee(newEmployee);
        });

        customerRepository.saveAll(customers);
        log.info("Customers reassigned successfully: oldEmployeeId={}, newEmployeeId={}", oldEmployeeId, newEmployeeId);
    }
}
```

## Vorteile des Ansatzes

1. **Wiederverwendbarkeit**: Der Orchestrator-Service kann für verschiedene Anwendungsfälle wiederverwendet werden.
2. **Wartbarkeit**: Änderungen in der Geschäftslogik müssen nur an einer Stelle vorgenommen werden.
3. **Entkopplung**: Die spezifischen Services (`EmployeeService` und `CustomerService`) bleiben unabhängig und einfach.

## Refactoring Hinweis

> Im Zusammenhang mit dem Löschen eines Mitarbeiters stellt die isolierte Methode `deleteEmployee` im `EmployeeServiceImpl` keinen Mehrwert mehr dar und sollte entfernt werden, um Redundanz und Inkonsistenzen zu vermeiden. Hier sind die Schritte:

1. **Überprüfen der Aufrufe**: Stelle sicher, dass keine anderen Teile des Codes die `deleteEmployee` Methode direkt aufrufen. Falls doch, sollten diese Verweise auf die neue Methode im `EmployeeOrchestratorService` umgeleitet werden.

2. **Refactoring**: Entferne die Methode `deleteEmployee` aus `EmployeeServiceImpl`, um Redundanz zu vermeiden und sicherzustellen, dass alle Löschoperationen über den `EmployeeOrchestratorService` laufen.

3. **Dokumentation aktualisieren**: Aktualisiere die Dokumentation entsprechend, um klarzustellen, dass die Lösch- und Neuzuweisungslogik jetzt zentral im `EmployeeOrchestratorService` gehandhabt wird.

## Zusammenfassung

> Der `EmployeeCustomerOrchestratorService` ist ein zentraler Service, der alle Use-Cases koordiniert, die mehrere Repositories oder Services betreffen.
> Dies ermöglicht eine klare Trennung der Verantwortlichkeiten und verbessert die Modularität, Wartbarkeit und Testbarkeit des Codes.
> Die Methode `reassignCustomers` und der Use-Case `deleteEmployee` wurden als Auslöser dieser Designentscheidung identifiziert und implementiert.