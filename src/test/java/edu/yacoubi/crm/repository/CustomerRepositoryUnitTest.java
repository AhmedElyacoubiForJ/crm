package edu.yacoubi.crm.repository;

import edu.yacoubi.crm.TestDataUtil;
import edu.yacoubi.crm.model.Employee;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@DataJpaTest
@ExtendWith(SpringExtension.class)
class CustomerRepositoryUnitTest {

    @Autowired
    private CustomerRepository underTest;

    @MockBean
    private EmployeeRepository employeeRepository;

    @MockBean
    private NoteRepository noteRepository;

    @BeforeEach
    public void setUp() {
        Employee employee = TestDataUtil.createEmployeeA();
        employee.setId(1L); // Mock the ID as if it were saved
        when(employeeRepository.save(any(Employee.class))).thenReturn(employee);
    }

    //
}