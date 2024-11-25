# Dokumentation: Deaktivierung und Archivierung von Mitarbeitern

## Übersicht

Diese Dokumentation beschreibt den Ansatz zur Deaktivierung und Archivierung von Mitarbeitern in einem CRM-System. Der Ansatz umfasst die Erstellung einer speziellen Tabelle für inaktive Mitarbeiter, die Implementierung einer Methode zur Zuordnung von Kunden zu Mitarbeitern und die logische Platzierung dieser Methode innerhalb der Service-Schichten.

## Gründe für das Löschen von Mitarbeitern

1. **Datenbereinigung**: Um die Datenbank sauber und übersichtlich zu halten, könnten ehemalige oder doppelte Einträge gelöscht werden.
2. **Datenschutz**: Gesetzliche Vorgaben wie die DSGVO (Datenschutz-Grundverordnung) können das Löschen von Daten ehemaliger Mitarbeiter nach einem bestimmten Zeitraum vorschreiben.
3. **Aktualität**: Entfernen von veralteten Informationen hilft dabei, nur aktuelle und relevante Daten im System zu behalten.

## Gründe gegen das Löschen von Mitarbeitern

1. **Historische Daten**: Ehemalige Mitarbeiter könnten in historischen Berichten, Projekten oder Interaktionen von Bedeutung sein. Das Löschen könnte den Verlust wichtiger Kontextinformationen bedeuten.
2. **Rechtliche Anforderungen**: Einige Informationen müssen möglicherweise für eine bestimmte Zeit aufbewahrt werden, um gesetzlichen oder organisatorischen Anforderungen gerecht zu werden.
3. **Vermeidung von Datenverlust**: Das dauerhafte Löschen könnte ungewollten Datenverlust bedeuten, was zukünftige Analysen und Berichte beeinträchtigen könnte.

## Entscheidung und Lösungsansatz

**Unsere Entscheidung**: Statt Mitarbeiterdaten vollständig zu löschen, werden die Mitarbeiter deaktiviert. Dies geschieht durch das Verschieben in eine spezielle Tabelle für inaktive Mitarbeiter, um den Aufwand zu reduzieren und die Datenintegrität zu gewährleisten.

### Lösungsansatz

1. **Deaktivierung von Mitarbeitern**: Mitarbeiter werden deaktiviert und ihre Daten in eine spezielle `InactiveEmployee`-Tabelle verschoben.
2. **Zuweisung von Kunden**: Bevor ein Mitarbeiter deaktiviert wird, müssen seine zugewiesenen Kunden auf einen anderen aktiven Mitarbeiter umverteilt werden, um sicherzustellen, dass keine Kunden mit inaktiven Mitarbeitern verbunden bleiben.
3. **Spezielle Methode**: Implementierung einer Methode `assignCustomerToEmployee(Long customerId, Long employeeId)`, um Kunden zu Mitarbeitern zuzuweisen. Diese Methode wird in verschiedenen Szenarien verwendet, z.B. bei Urlaub, Beschwerden, Support-Level-Wechsel oder Löschvorgängen.

### Designüberlegungen

#### Verwendung von `ICustomerService` für die Kundenlogik

- **Zentralisierung der Kundenlogik**: Der `ICustomerService` kapselt die gesamte Geschäftslogik für Kundenoperationen, was die Wartbarkeit und Kapselung verbessert.
- **Modularität**: Die Methode `assignCustomerToEmployee` im `ICustomerService` kann in verschiedenen Szenarien wiederverwendet werden.

#### Platzierung der Methode `assignCustomerToEmployee` im `IEmployeeService`

- **Logische Konsistenz**: Die Methode `assignCustomerToEmployee` gehört logisch zum `IEmployeeService`, da sie die Zuweisung von Kunden zu Mitarbeitern verwaltet.

## Implementierung

### `ICustomerService`

```java
package edu.yacoubi.crm.service;

import java.util.List;

public interface ICustomerService {
    void assignCustomerToEmployee(Long customerId, Long employeeId);
    List<Customer> findCustomersByEmployeeId(Long employeeId);
}
```

### `CustomerServiceImpl`

```java
package edu.yacoubi.crm.service.impl;

import edu.yacoubi.crm.model.Customer;
import edu.yacoubi.crm.model.Employee;
import edu.yacoubi.crm.repository.CustomerRepository;
import edu.yacoubi.crm.repository.EmployeeRepository;
import edu.yacoubi.crm.service.ICustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerServiceImpl implements ICustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Override
    public void assignCustomerToEmployee(Long customerId, Long employeeId) {
        Customer customer = customerRepository.findById(customerId).orElseThrow(
                () -> new ResourceNotFoundException("Customer not found with ID: " + customerId)
        );

        Employee employee = employeeRepository.findById(employeeId).orElseThrow(
                () -> new ResourceNotFoundException("Employee not found with ID: " + employeeId)
        );

        customer.setEmployee(employee);
        customerRepository.save(customer);
    }

    @Override
    public List<Customer> findCustomersByEmployeeId(Long employeeId) {
        return customerRepository.findByEmployeeId(employeeId);
    }
}
```

### `IEmployeeService`

```java
package edu.yacoubi.crm.service;

public interface IEmployeeService {
    void deleteAndArchiveEmployee(Long id);
    void assignCustomerToEmployee(Long customerId, Long employeeId);
}
```

### `EmployeeServiceImpl`

```java
package edu.yacoubi.crm.service.impl;

import edu.yacoubi.crm.model.Employee;
import edu.yacoubi.crm.service.IEmployeeService;
import edu.yacoubi.crm.service.IInactiveEmployeeService;
import edu.yacoubi.crm.service.ICustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class EmployeeServiceImpl implements IEmployeeService {

    @Autowired
    private IInactiveEmployeeService inactiveEmployeeService;

    @Autowired
    private ICustomerService customerService;

    @Override
    @Transactional
    public void deleteAndArchiveEmployee(Long id) {
        log.info("EmployeeServiceImpl::deleteAndArchiveEmployee id: {}", id);

        Employee employee = employeeRepository.findById(id).orElseThrow(
                () -> {
                    log.warn("Employee not found with ID: {}", id);
                    return new ResourceNotFoundException("Employee not found with ID: " + id);
                }
        );

        // Kunden neu zuweisen
        reassignCustomers(employee.getId());

        // Archivierung des Mitarbeiters
        inactiveEmployeeService.createInactiveEmployee(employee);

        // Löschen des Mitarbeiters
        employeeRepository.delete(employee);

        log.info("Employee deleted and archived with ID: {}", id);
    }

    private void reassignCustomers(Long employeeId) {
        List<Customer> customers = customerService.findCustomersByEmployeeId(employeeId);
        for (Customer customer : customers) {
            // Implementiere Logik zur neuen Zuweisung
            Long newEmployeeId = 1L ; // Logik zur Bestimmung des neuen Mitarbeiters
            customerService.assignCustomerToEmployee(customer.getId(), newEmployeeId);
        }
    }

    @Override
    public void assignCustomerToEmployee(Long customerId, Long employeeId) {
        customerService.assignCustomerToEmployee(customerId, employeeId);
    }
}
```

### Vorteile dieser Implementierung

1. **Kapselung und Modularität**: Durch die Verwendung von `ICustomerService` und `IEmployeeService` bleibt die Logik sauber getrennt und modular.
2. **Wiederverwendbarkeit**: Die Methode `assignCustomerToEmployee` kann in verschiedenen Szenarien wiederverwendet werden, wie z.B. Urlaub, Beschwerden, Support-Level-Wechsel oder Löschvorgänge.
3. **Datenintegrität und Konsistenz**: Die Datenintegrität wird durch die zusätzliche Validierung und die Trennung der inaktiven Mitarbeiter gewährleistet.
4. **Saubere Architektur**: Das Design bleibt sauber und leicht wartbar, da die Verantwortlichkeiten klar getrennt sind.

---

Diese Dokumentation bietet eine umfassende Übersicht über die getroffenen Entscheidungen, den Implementierungsansatz und die Vorteile der gewählten Lösung.