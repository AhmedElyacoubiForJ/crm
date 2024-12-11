package edu.yacoubi.crm.repository;

import edu.yacoubi.crm.TestDataUtil;
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

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
}