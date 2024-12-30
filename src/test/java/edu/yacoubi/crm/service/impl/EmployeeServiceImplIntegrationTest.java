package edu.yacoubi.crm.service.impl;

import edu.yacoubi.crm.util.TestDataUtil;
import edu.yacoubi.crm.model.Customer;
import edu.yacoubi.crm.model.Employee;
import edu.yacoubi.crm.repository.EmployeeRepository;
import edu.yacoubi.crm.service.IEmployeeService;
import edu.yacoubi.crm.service.validation.EntityValidator;
import org.hibernate.LazyInitializationException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Erklärung des Verhaltens:
 * Speichern (save) im EmployeeRepository:
 *
 * Beim Speichern eines Employee-Objekts mit zugehörigen Customer-Objekten
 * und einer CascadeType.ALL-Konfiguration werden die Customer-Objekte zusammen mit dem Employee gespeichert.
 *
 * Da es sich hierbei um eine aktive Sitzung handelt, sind die Customer-Objekte verfügbar
 * und können bei Bedarf abgerufen werden.
 *
 * Abfragen (findById) im EmployeeRepository:
 *
 * Beim Abfragen eines Employee-Objekts mit findById und FetchType.LAZY
 * wird nur das Employee-Objekt selbst geladen.
 * Die Customer-Objekte werden erst dann geladen, wenn explizit darauf zugegriffen wird.
 *
 * Wenn die Sitzung jedoch geschlossen ist, tritt eine LazyInitializationException auf,
 * wenn versucht wird, auf die Customer-Liste zuzugreifen.
 */
@SpringBootTest
public class EmployeeServiceImplIntegrationTest {

    @Autowired
    private IEmployeeService employeeService;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private EntityValidator entityValidator;

    /**
     * itShouldReturnTrueWhenEmployeeHasCustomers:
     *
     * - Verifiziert, dass Employee-Objekte korrekt Customer-Objekte haben, und überprüft Lazy-Loading.
     * - Sicherstellt, dass LazyInitializationException auftritt, wenn versucht wird, auf die Customer-Liste nach Schließen der Session zuzugreifen.
     */
    @Test
    void itShouldReturnTrueWhenEmployeeHasCustomers() {
        // Given
        Employee employeeWithCustomers = TestDataUtil.createEmployeeA();
        Employee savedEmployeeWithCustomers = employeeRepository.save(employeeWithCustomers);

        Customer customerA = TestDataUtil.createCustomerA(savedEmployeeWithCustomers);
        Customer customerB = TestDataUtil.createCustomerB(savedEmployeeWithCustomers);

        savedEmployeeWithCustomers.addCustomer(customerA);
        savedEmployeeWithCustomers.addCustomer(customerB);

        Employee savedEmployee = employeeRepository.save(savedEmployeeWithCustomers);
        // LAZY und trotzdem wurden geladen
        savedEmployee.getCustomers().forEach(
                customer -> System.out.println(customer)
        );
        // hier werden customers nicht geladen wie erwartet.
        // Ensure customers are not loaded initially (Lazy Loading)
        Employee retrievedEmployee = employeeRepository.findById(savedEmployee.getId()).orElseThrow();

        LazyInitializationException lazyInitializationException = assertThrows(
                LazyInitializationException.class,
                () -> retrievedEmployee.getCustomers().forEach(
                        customer -> System.out.println(customer)
                )
        );
        assertTrue(
                lazyInitializationException
                        .getMessage()
                        .contains("failed to lazily initialize a collection of role")
        );

        // When
        boolean result = employeeService.hasCustomers(savedEmployeeWithCustomers.getId());

        // Then
        assertTrue(result, "Employee with customers should return true");
    }

    /**
     * itShouldReturnFalseWhenEmployeeHasNoCustomers:
     *
     * - Überprüft, dass Employee-Objekte ohne Kunden korrekt gehandhabt werden.
     */
    @Test
    void itShouldReturnFalseWhenEmployeeHasNoCustomers() {
        // Given
        Employee employeeWithoutCustomers = TestDataUtil.createEmployeeA();
        Employee savedEmployeeWithoutCustomers = employeeRepository.save(employeeWithoutCustomers);

        // When
        boolean result = employeeService.hasCustomers(savedEmployeeWithoutCustomers.getId());

        // Then
        assertFalse(result, "Employee without customers should return false");
    }
}
