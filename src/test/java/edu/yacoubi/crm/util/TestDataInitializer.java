package edu.yacoubi.crm.util;

import edu.yacoubi.crm.model.Employee;
import edu.yacoubi.crm.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

//@Component
public class TestDataInitializer {

    @Autowired
    private EmployeeRepository employeeRepository;

    public void addTestEmployees() {
        // Lösche alle vorhandenen Testdaten
        employeeRepository.deleteAll();
        Employee employee1 = new Employee();
        employee1.setFirstName("John");
        employee1.setLastName("Doe");
        employee1.setEmail("john.doe@example.com");
        employee1.setDepartment("HR");

        Employee employee2 = new Employee();
        employee2.setFirstName("Jane");
        employee2.setLastName("Smith");
        employee2.setEmail("jane.smith@example.com");
        employee2.setDepartment("Finance");

        employeeRepository.save(employee1);
        employeeRepository.save(employee2);
    }
}
