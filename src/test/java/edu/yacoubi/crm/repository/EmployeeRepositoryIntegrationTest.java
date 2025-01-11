package edu.yacoubi.crm.repository;

import edu.yacoubi.crm.model.Customer;
import edu.yacoubi.crm.model.Employee;
import edu.yacoubi.crm.util.TestDataUtil;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integrationstests für das EmployeeRepository.
 * <p>
 * Diese Tests überprüfen die Funktionalität der benutzerdefinierten Abfragen
 * und stellen sicher, dass die Interaktionen mit der Datenbank wie erwartet
 * funktionieren. Diese Tests verwenden JPA und Hibernate, um die Datenbankzugriffe
 * zu simulieren und zu überprüfen.
 * <p>
 * Fachbegriffe und Konzepte:
 * - Persistence Context: Ein von JPA/Hibernate verwalteter Kontext, der alle
 * verwalteten Entitäten nachverfolgt. Dieser Kontext sorgt dafür, dass Änderungen
 * an Entitäten automatisch synchronisiert werden.
 * - Auto-generierte IDs: Beim Speichern von Entitäten generiert die Datenbank
 * automatisch eindeutige IDs, die den Entitäten zugewiesen werden. Diese IDs
 * werden sofort im Persistence Context aktualisiert und in den entsprechenden
 * Objekten reflektiert.
 * - `@DataJpaTest`: Eine Annotation, die speziell für JPA-Tests vorgesehen ist.
 * Sie konfiguriert eine In-Memory-Datenbank und initialisiert die Repository-Komponenten.
 * - `@DirtiesContext`: Eine Annotation, die dafür sorgt, dass der Anwendungskontext
 * nach jedem Testmethodenlauf bereinigt wird, um Seiteneffekte zwischen den Tests
 * zu vermeiden.
 * <p>
 * Die Tests in dieser Klasse decken folgende Szenarien ab:
 * - Suche nach Mitarbeitern nach Vorname (Case-Insensitive) oder Abteilung
 * (Case-Insensitive).
 * - Sicherstellung, dass die richtigen Ergebnisse zurückgegeben werden, wenn
 * verschiedene Suchparameter verwendet werden.
 * <p>
 * Beispiel für einen Testfall:
 * - itShouldReturnEmployeesByFirstNameContainsIgnoreCaseOrDepartmentContainsIgnoreCase:
 * Testet, ob Mitarbeiter gefunden werden, deren Vorname oder Abteilung den angegebenen
 * Suchstring (unabhängig von Groß- und Kleinschreibung) enthält.
 */
@DataJpaTest
@ExtendWith(SpringExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class EmployeeRepositoryIntegrationTest {

    @Autowired
    private EmployeeRepository underTest;

    /**
     * Testet, ob Mitarbeiter gefunden werden, deren Vorname oder Abteilung
     * den angegebenen Suchstring (unabhängig von Groß- und Kleinschreibung) enthält.
     */
    @Test
    void itShouldReturnEmployeesByFirstNameIgnoreCaseContainsSearchString() {
        // Given
        final String searchStr = "j";
        final int expectedCounter = 2;
        final NamedEmployeeObject nEmployeeObjA = new NamedEmployeeObject(
                "employeeA", TestDataUtil.createEmployeeA()
        );
        final NamedEmployeeObject nEmployeeObjB = new NamedEmployeeObject(
                "employeeB", TestDataUtil.createEmployeeB()
        );
        nEmployeeObjA.getEmployeeObject().setFirstName("John");
        nEmployeeObjB.getEmployeeObject().setFirstName("Jane");
        underTest.saveAll(
                List.of(nEmployeeObjA.getEmployeeObject(), nEmployeeObjB.getEmployeeObject())
        );

        // When
        final List<Employee> byFNameContainsIgnoreCase = underTest
                .findByFirstNameIgnoreCaseContaining(searchStr)
                .orElse(new ArrayList<>());

        // Then
        assertEquals(expectedCounter, byFNameContainsIgnoreCase.size(),
                "Expected to find " + expectedCounter + " employees with first name containing " + searchStr);
        assertTrue(byFNameContainsIgnoreCase.contains(nEmployeeObjA.getEmployeeObject()),
                "Expected to find " + nEmployeeObjA.getName() + " in the result set");
        assertTrue(byFNameContainsIgnoreCase.contains(nEmployeeObjB.getEmployeeObject()),
                "Expected to find " + nEmployeeObjB.getName() + " in the result set");
    }

    @Test
    void itShouldReturnEmployeesByEmailIgnoreCaseContainsSearchString() {
        // Given
        final String searchStr = "HN";
        final int expectedCounter = 2;
        final NamedEmployeeObject nEmployeeObjA = new NamedEmployeeObject(
                "employeeA", TestDataUtil.createEmployeeA()
        );
        final NamedEmployeeObject nEmployeeObjB = new NamedEmployeeObject(
                "employeeB", TestDataUtil.createEmployeeB()
        );
        nEmployeeObjA.getEmployeeObject().setEmail("john.doe@example.com");
        nEmployeeObjB.getEmployeeObject().setEmail("jahne.doe@example.com");
        underTest.saveAll(
                List.of(nEmployeeObjA.getEmployeeObject(), nEmployeeObjB.getEmployeeObject())
        );

        // When
        final List<Employee> byEmailContainsIgnoreCase = underTest
                .findByEmailIgnoreCaseContaining(searchStr)
                .orElse(new ArrayList<>());

        // Then
        assertEquals(expectedCounter, byEmailContainsIgnoreCase.size(),
                "Expected to find " + expectedCounter + " employees with email containing " + searchStr);
        assertTrue(byEmailContainsIgnoreCase.contains(nEmployeeObjA.getEmployeeObject()),
                "Expected to find " + nEmployeeObjA.getName() + " in the result set");
        assertTrue(byEmailContainsIgnoreCase.contains(nEmployeeObjB.getEmployeeObject()),
                "Expected to find " + nEmployeeObjB.getName() + " in the result set");
    }

    @Test
    void itShouldReturnEmployeesByFirstNameContainsIgnoreCaseOrDepartmentContainsIgnoreCase() {
        // Given
        final String fNameSearchStr = "oh";
        final String departmentSearchStr = "Sales";
        final NamedEmployeeObject nEmployeeObjA = new NamedEmployeeObject(
                "employeeA", TestDataUtil.createEmployeeA()
        );
        nEmployeeObjA.getEmployeeObject().setFirstName("John");
        nEmployeeObjA.getEmployeeObject().setDepartment("Sales");
        final NamedEmployeeObject nEmployeeObjB = new NamedEmployeeObject(
                "employeeB", TestDataUtil.createEmployeeB()
        );
        nEmployeeObjB.getEmployeeObject().setFirstName("Jane");
        nEmployeeObjB.getEmployeeObject().setDepartment("Marketing");
        underTest.saveAll(
                List.of(nEmployeeObjA.getEmployeeObject(), nEmployeeObjB.getEmployeeObject())
        );
        final Pageable pageable = PageRequest.of(0, 10);


        // When
        Page<Employee> byFNameContainsOrDepartmentContainsAllIgnoreCase = underTest
                .findByFirstNameContainingIgnoreCaseOrDepartmentContainingIgnoreCase(
                        fNameSearchStr, departmentSearchStr, pageable
                );

        // Then
        long totalElements = byFNameContainsOrDepartmentContainsAllIgnoreCase.getTotalElements();
        int totalPages = byFNameContainsOrDepartmentContainsAllIgnoreCase.getTotalPages();
        int size = byFNameContainsOrDepartmentContainsAllIgnoreCase.getContent().size();
        List<Employee> content = byFNameContainsOrDepartmentContainsAllIgnoreCase.getContent();
        assertEquals(1, totalElements);
        assertEquals(1, totalPages);
        assertEquals(1, size);
        assertTrue(content.contains(nEmployeeObjA.getEmployeeObject()),
                "Expected to find " + nEmployeeObjA.getName() + " in the result set");
        assertFalse(content.contains(nEmployeeObjB.getEmployeeObject()),
                "Expected not to find " + nEmployeeObjB.getName() + " in the result set");
    }

    @Test
    void itShouldReturnAllDepartments() {
        // Given
        final Employee employeeA = TestDataUtil.createEmployeeA();
        employeeA.setDepartment("Sales");
        final Employee employeeB = TestDataUtil.createEmployeeB();
        employeeB.setDepartment("Marketing");
        underTest.saveAll(List.of(employeeA, employeeB));

        // When
        final Optional<List<String>> allDepartments = underTest.findAllDepartments();

        // Then
        assertTrue(allDepartments.isPresent(),
                "Expected to find departments in the result set"
        );
        assertEquals(2, allDepartments.get().size(),
                "Expected to find two departments in the result set"
        );
        assertTrue(allDepartments.get().contains("Sales"),
                "Expected to find 'Sales' in the result set");
        assertTrue(allDepartments.get().contains("Marketing"),
                "Expected to find 'Marketing' in the result set"
        );
    }

    /**
     * Die Tatsache, dass die Kunden trotz `FetchType.LAZY` geladen werden, liegt, wie ich richtig bemerkt habe,
     * daran, dass in einem `@DataJpaTest`-Kontext Tests standardmäßig in eine Transaktion eingebunden sind.
     * <p>
     * ### Zusammenfassung der wesentlichen Punkte:
     * <p>
     * 1. **Transaktionskontext in `@DataJpaTest`**: Die Tests sind standardmäßig in eine Transaktion eingebunden,
     * was bedeutet, dass Lazy-Loading-Proxies innerhalb dieser Transaktion auf die tatsächlichen Daten zugreifen können.
     * 2. **Lazy-Loading im Service-Kontext**: Um Lazy-Loading wirklich zu testen, ist es sinnvoll,
     * dies im Service-Kontext zu tun, wo der Transaktionskontext deutlicher getrennt ist.
     * <p>
     * ### Nächste Schritte:
     * - **Service-Kontext-Tests**: Jetzt, da die Repository-Tests funktionieren,
     * kann man Lazy-Loading im Service-Kontext testen, um sicherzustellen,
     * dass die Kunden nur bei Bedarf geladen werden.
     */
    @Test
    void itShouldReturnTrueWhenEmployeeHasCustomers() {
        // Given
        final Employee employeeA = TestDataUtil.createEmployeeA();
        final Employee existingEmployee = underTest.save(employeeA);

        // Initialize customers with the saved employee
        final Customer customerA = TestDataUtil.createCustomerA(existingEmployee);
        final Customer customerB = TestDataUtil.createCustomerA(existingEmployee);

        existingEmployee.addCustomer(customerA);
        existingEmployee.addCustomer(customerB);

        // Save the employee with customers
        final Employee existingEmployeeWithCustomers = underTest.save(existingEmployee);
        // LAZY und trotzdem customers wurden geladen.
        // Grund ist, in einem @DataJpaTest-Kontext
        // sind Tests standardmäßig in eine Transaktion eingebunden.
        existingEmployeeWithCustomers
                .getCustomers()
                .forEach(customer -> System.out.println(customer));
        // werden nicht geladen
        underTest.findAll().forEach(employee -> System.out.println(employee));

        // When
        boolean resultWithCustomers = underTest.hasCustomers(existingEmployeeWithCustomers.getId());

        // Then
        assertTrue(resultWithCustomers, "Employee with customers should return true");
    }

    @Test
    void itShouldReturnFalseWhenEmployeeHasNoCustomers() {
        // Given
        final Employee employeeWithoutCustomers = TestDataUtil.createEmployeeA();
        underTest.save(employeeWithoutCustomers);

        // When
        final boolean resultWithoutCustomers = underTest.hasCustomers(employeeWithoutCustomers.getId());

        // Then
        assertFalse(resultWithoutCustomers, "Employee without customers should return false");
    }

    @Test
    void itShouldReturnTrueWhenEmployeeHasCustomersUsingFindByIdWithCustomers() {
        // Given
        final Employee employeeA = TestDataUtil.createEmployeeA();
        final Employee existingEmployee = underTest.save(employeeA);

        // Initialize customers with the saved employee
        final Customer customerA = TestDataUtil.createCustomerA(existingEmployee);
        final Customer customerB = TestDataUtil.createCustomerA(existingEmployee);

        existingEmployee.addCustomer(customerA);
        existingEmployee.addCustomer(customerB);

        // Save the employee with customers and flush to synchronize the persistence context
        final Employee existingEmployeeWithCustomers = underTest.saveAndFlush(existingEmployee);

        // When
        Optional<Employee> employeeWithCustomersOptional =
                underTest.findByIdWithCustomers(existingEmployeeWithCustomers.getId());

        // Then
        assertTrue(employeeWithCustomersOptional.isPresent());
        Employee employeeWithCustomers = employeeWithCustomersOptional.get();
        assertNotNull(employeeWithCustomers.getCustomers());
        assertEquals(2, employeeWithCustomers.getCustomers().size());

        System.out.println("Employee with customers: " + employeeWithCustomers);
        employeeWithCustomers.getCustomers().forEach(customer -> System.out.println("Customer: " + customer));
    }

    // BasisKlasse NamedObject
    @AllArgsConstructor
    @Getter
    class NamedObject<T> {
        private final String name;
        private final T object;

        // Zugänglich nur für diese Klasse und ihre Unterklassen
        private T getObject() {
            return object;
        }
    }

    // Spezialisierte Version für Employee
    class NamedEmployeeObject extends NamedObject<Employee> {
        public NamedEmployeeObject(final String name, final Employee object) {
            super(name, object);
        }

        public Employee getEmployeeObject() {
            return super.getObject();
        }
    }
}
