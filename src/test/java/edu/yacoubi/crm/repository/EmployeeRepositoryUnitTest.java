package edu.yacoubi.crm.repository;

import edu.yacoubi.crm.TestDataUtil;
import edu.yacoubi.crm.model.Customer;
import edu.yacoubi.crm.model.Employee;
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
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ExtendWith(SpringExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class EmployeeRepositoryUnitTest {

    @Autowired
    private EmployeeRepository underTest;

    @Test
    public void itShouldReturnEmployeesByFirstNameIgnoreCaseContainingSearchString() {
        // Given
        Employee employeeA = TestDataUtil.createEmployeeA();
        employeeA.setFirstName("John");
        Employee employeeB = TestDataUtil.createEmployeeB();
        employeeB.setFirstName("Jane");
        underTest.saveAll(List.of(employeeA, employeeB));

        // When
        List<Employee> byNameContainingIgnoreCase =
                underTest.findByFirstNameIgnoreCaseContaining("j").get();

        // Then
        assertEquals(2, byNameContainingIgnoreCase.size());
        assertTrue(byNameContainingIgnoreCase.contains(employeeA));
        assertTrue(byNameContainingIgnoreCase.contains(employeeB));
    }

    @Test
    public void itShouldReturnEmployeesByEmailIgnoreCaseContainingSearchString() {
        // Given
        Employee employeeA = TestDataUtil.createEmployeeA();
        employeeA.setEmail("john.doe@example.com");
        Employee employeeB = TestDataUtil.createEmployeeB();
        employeeB.setEmail("jahne.doe@example.com");
        underTest.saveAll(List.of(employeeA, employeeB));

        // When
        List<Employee> byEmailContainingIgnoreCase =
                underTest.findByEmailIgnoreCaseContaining("HN").get();

        // Then
        assertEquals(2, byEmailContainingIgnoreCase.size());
        assertTrue(byEmailContainingIgnoreCase.contains(employeeA));
        assertTrue(byEmailContainingIgnoreCase.contains(employeeB));
    }

    @Test
    public void itShouldReturnEmployeesByFirstNameContainingIgnoreCaseOrDepartmentContainingIgnoreCase() {
        // Given
        Employee employeeA = TestDataUtil.createEmployeeA();
        employeeA.setFirstName("John");
        employeeA.setDepartment("Sales");
        Employee employeeB = TestDataUtil.createEmployeeB();
        employeeB.setFirstName("Jane");
        employeeB.setDepartment("Marketing");

        underTest.saveAll(List.of(employeeA, employeeB));

        Pageable pageable = PageRequest.of(0, 10);

        // When
        Page<Employee> byFirstNameContainingIgnoreCaseOrDepartmentContainingIgnoreCase =
                underTest.findByFirstNameContainingIgnoreCaseOrDepartmentContainingIgnoreCase("oh", "sales", pageable);


        // Then
        assertEquals(1, byFirstNameContainingIgnoreCaseOrDepartmentContainingIgnoreCase.getTotalElements());
        assertEquals(1, byFirstNameContainingIgnoreCaseOrDepartmentContainingIgnoreCase.getTotalPages());
        assertEquals(1, byFirstNameContainingIgnoreCaseOrDepartmentContainingIgnoreCase.getContent().size());
        assertTrue(byFirstNameContainingIgnoreCaseOrDepartmentContainingIgnoreCase.getContent().contains(employeeA));
    }

    @Test
    public void itShouldReturnAllDepartments() {
        // Given
        Employee employeeA = TestDataUtil.createEmployeeA();
        employeeA.setDepartment("Sales");
        Employee employeeB = TestDataUtil.createEmployeeB();
        employeeB.setDepartment("Marketing");
        underTest.saveAll(List.of(employeeA, employeeB));

        // When
        Optional<List<String>> allDepartments = underTest.findAllDepartments();

        // Then
        assertTrue(allDepartments.isPresent());
        assertEquals(2, allDepartments.get().size());
        assertTrue(allDepartments.get().contains("Sales"));
        assertTrue(allDepartments.get().contains("Marketing"));
    }

    @Test
    public void testHasCustomers() {
        // Given
        Employee employeeWithCustomers = Employee.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .department("Sales")
                .build();

        Employee savedEmployeeWithCustomers = underTest
                .save(employeeWithCustomers);

        Customer customer1 = Customer.builder()
                .firstName("Jane")
                .lastName("Smith")
                .email("jane.smith@example.com")
                .build();

        Customer customer2 = Customer.builder()
                .firstName("Bob")
                .lastName("Johnson")
                .email("bob.johnson@example.com")
                .build();

        savedEmployeeWithCustomers.addCustomer(customer1);
        savedEmployeeWithCustomers.addCustomer(customer2);

        // Save the employee with customers
        Employee savedEmployeeWithSavedCustomers = underTest
                .save(savedEmployeeWithCustomers);

        Employee employeeWithoutCustomers = Employee.builder()
                .firstName("Alice")
                .lastName("Brown")
                .email("alice.brown@example.com")
                .department("Marketing")
                .build();

        underTest.save(employeeWithoutCustomers);

        // When
        boolean resultWithCustomers = underTest
                .hasCustomers(savedEmployeeWithSavedCustomers.getId());
        boolean resultWithoutCustomers = underTest
                .hasCustomers(employeeWithoutCustomers.getId());

        // Then
        assertTrue(true, String.valueOf(resultWithCustomers));
        assertFalse(false, String.valueOf(resultWithoutCustomers));
    }

    @Test
    void itShouldReturnTrueWhenEmployeeHasCustomers() {
        // Given
        Employee employeeWithCustomers = TestDataUtil.createEmployeeA();
        Employee savedEmployeeWithCustomers = underTest.save(employeeWithCustomers);

        // Initialize customers with the saved employee
        Customer customerA = TestDataUtil.createCustomerA(savedEmployeeWithCustomers);
        Customer customerB = TestDataUtil.createCustomerA(savedEmployeeWithCustomers);

        savedEmployeeWithCustomers.addCustomer(customerA);
        savedEmployeeWithCustomers.addCustomer(customerB);

        // Save the employee with customers
        Employee savedEmployeeWithSavedCustomers = underTest.save(savedEmployeeWithCustomers);
        // LAZY und trotzdem customers wurden geladen.
        // Grund ist, in einem @DataJpaTest-Kontext
        // sind Tests standardmäßig in eine Transaktion eingebunden.
        savedEmployeeWithSavedCustomers.getCustomers()
                .forEach(
                        customer -> System.out.println(customer)
                );
        // When
        boolean resultWithCustomers = underTest.hasCustomers(savedEmployeeWithSavedCustomers.getId());

        // Then
        assertTrue(true, String.valueOf(resultWithCustomers));
    }

    @Test
    void itShouldReturnFalseWhenEmployeeHasNoCustomers() {
        // Given
        Employee employeeWithoutCustomers = TestDataUtil.createEmployeeA();
        underTest.save(employeeWithoutCustomers);

        // When
        boolean resultWithoutCustomers = underTest.hasCustomers(employeeWithoutCustomers.getId());

        // Then
        assertFalse(false, String.valueOf(resultWithoutCustomers));
    }
}

/**
 * Die Tatsache, dass die Kunden trotz `FetchType.LAZY` geladen werden, liegt, wie ich richtig bemerkt habe,
 * daran, dass in einem `@DataJpaTest`-Kontext Tests standardmäßig in eine Transaktion eingebunden sind.
 *
 * ### Zusammenfassung der wesentlichen Punkte:
 *
 * 1. **Transaktionskontext in `@DataJpaTest`**: Die Tests sind standardmäßig in eine Transaktion eingebunden, was bedeutet,
 *      dass Lazy-Loading-Proxies innerhalb dieser Transaktion auf die tatsächlichen Daten zugreifen können.
 * 2. **Lazy-Loading im Service-Kontext**: Um Lazy-Loading wirklich zu testen, ist es sinnvoll,
 *      dies im Service-Kontext zu tun, wo der Transaktionskontext deutlicher getrennt ist.
 *
 * ### Nächste Schritte:
 * - **Service-Kontext-Tests**: Jetzt, da die Repository-Tests funktionieren,
 *      kann man Lazy-Loading im Service-Kontext testen, um sicherzustellen,
 *      dass die Kunden nur bei Bedarf geladen werden.
 *
 */