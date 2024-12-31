package edu.yacoubi.crm.util;

import edu.yacoubi.crm.dto.customer.CustomerRequestDTO;
import edu.yacoubi.crm.dto.customer.CustomerResponseDTO;
import edu.yacoubi.crm.dto.employee.EmployeeRequestDTO;
import edu.yacoubi.crm.dto.employee.EmployeeResponseDTO;
import edu.yacoubi.crm.dto.note.NoteResponseDTO;
import edu.yacoubi.crm.model.Customer;
import edu.yacoubi.crm.model.Employee;
import edu.yacoubi.crm.model.Note;
import org.junit.jupiter.api.Test;

import static edu.yacoubi.crm.util.EntityTransformer.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class EntityITransformerTest {

    @Test
    public void testNoteToNoteResponseDTOTransformation() {
        // Given
        Customer customerA = TestDataUtil.createCustomerA(null);
        customerA.setId(2L);
        Note noteA = TestDataUtil.createNoteA(customerA);
        noteA.setId(1L);

        // When
        NoteResponseDTO noteADTO = noteToNoteResponseDto.transform(noteA);
        NoteResponseDTO noteDTOViaUtil = TransformerUtil.transform(noteToNoteResponseDto, noteA);

        // Then
        assertEquals(noteA.getId(), noteADTO.getId());
        assertEquals(noteA.getContent(), noteADTO.getContent());
        assertEquals(noteA.getInteractionType(), noteADTO.getInteractionType());
        assertEquals(noteA.getCustomer().getId(), noteADTO.getCustomerId());

        assertEquals(noteA.getId(), noteDTOViaUtil.getId());
        assertEquals(noteA.getContent(), noteDTOViaUtil.getContent());
        assertEquals(noteA.getInteractionType(), noteDTOViaUtil.getInteractionType());
        assertEquals(noteA.getCustomer().getId(), noteDTOViaUtil.getCustomerId());
    }

    @Test
    public void testEmployeeToEmployeeResponseDTOTransformation() {
        // Given
        Employee employeeB = TestDataUtil.createEmployeeB();
        employeeB.setId(1L);

        // When
        EmployeeResponseDTO employeeDTO =
                employeeToEmployeeResponseDto.transform(employeeB);
        EmployeeResponseDTO employeeDTOViaUtil =
                TransformerUtil.transform(employeeToEmployeeResponseDto, employeeB);

        // Then
        assertEquals(employeeDTO.getId(), employeeDTO.getId());
        assertEquals(employeeDTO.getFirstName(), employeeDTO.getFirstName());
        assertEquals(employeeDTO.getLastName(), employeeDTO.getLastName());
        assertEquals(employeeDTO.getEmail(), employeeDTO.getEmail());
        assertEquals(employeeDTO.getDepartment(), employeeDTO.getDepartment());

        assertEquals(employeeDTO.getId(), employeeDTOViaUtil.getId());
        assertEquals(employeeDTO.getFirstName(), employeeDTOViaUtil.getFirstName());
        assertEquals(employeeDTO.getLastName(), employeeDTOViaUtil.getLastName());
        assertEquals(employeeDTO.getEmail(), employeeDTOViaUtil.getEmail());
        assertEquals(employeeDTO.getDepartment(), employeeDTOViaUtil.getDepartment());
    }

    @Test
    public void testEmployeeRequestDTOToEmployeeTransformation() {
        // Given
        EmployeeRequestDTO employeeRequestDTOA = TestDataUtil.createEmployeeRequestDTOA();

        // When
        Employee employee = employeeRequestDtoToEmployee.transform(employeeRequestDTOA);
        Employee employeeViaUtil = TransformerUtil
                .transform(employeeRequestDtoToEmployee, employeeRequestDTOA);

        // Then
        assertEquals(employeeRequestDTOA.getFirstName(), employee.getFirstName());
        assertEquals(employeeRequestDTOA.getLastName(), employee.getLastName());
        assertEquals(employeeRequestDTOA.getEmail(), employee.getEmail());
        assertEquals(employeeRequestDTOA.getDepartment(), employee.getDepartment());

        assertEquals(employeeRequestDTOA.getFirstName(), employeeViaUtil.getFirstName());
        assertEquals(employeeRequestDTOA.getLastName(), employeeViaUtil.getLastName());
        assertEquals(employeeRequestDTOA.getEmail(), employeeViaUtil.getEmail());
        assertEquals(employeeRequestDTOA.getDepartment(), employeeViaUtil.getDepartment());
    }

    @Test
    public void testCustomerRequestDTOToCustomerTransformation() {
        // Given
        CustomerRequestDTO customerRequestDTOA = TestDataUtil.createCustomerRequestDTOA();

        // When
        Customer customer = customerRequestDtoToCustomer.transform(customerRequestDTOA);
        Customer customerViaUtil = TransformerUtil
                .transform(customerRequestDtoToCustomer, customerRequestDTOA);

        // Then
        assertEquals(customerRequestDTOA.getFirstName(), customer.getFirstName());
        assertEquals(customerRequestDTOA.getLastName(), customer.getLastName());
        assertEquals(customerRequestDTOA.getEmail(), customer.getEmail());
        assertEquals(customerRequestDTOA.getPhone(), customer.getPhone());
        assertEquals(customerRequestDTOA.getAddress(), customer.getAddress());
        assertEquals(customerRequestDTOA.getLastInteractionDate(), customer.getLastInteractionDate());

        assertEquals(customerRequestDTOA.getFirstName(), customerViaUtil.getFirstName());
        assertEquals(customerRequestDTOA.getLastName(), customerViaUtil.getLastName());
        assertEquals(customerRequestDTOA.getEmail(), customerViaUtil.getEmail());
        assertEquals(customerRequestDTOA.getPhone(), customerViaUtil.getPhone());
        assertEquals(customerRequestDTOA.getAddress(), customerViaUtil.getAddress());
        assertEquals(customerRequestDTOA.getLastInteractionDate(), customerViaUtil.getLastInteractionDate());
    }

    @Test
    public void testCustomerToCustomerResponseDTOTransformation() {
        // Given
        Employee employeeB = TestDataUtil.createEmployeeB();
        employeeB.setId(3L);
        Customer customerA = TestDataUtil.createCustomerA(employeeB);
        customerA.setId(1L);

        // When
        CustomerResponseDTO customerDTO = customerToCustomerResponseDto.transform(customerA);
        CustomerResponseDTO customerDTOViaUtil = TransformerUtil
                .transform(customerToCustomerResponseDto, customerA);

        // Then
        assertEquals(customerA.getId(), customerDTO.getId());
        assertEquals(customerA.getFirstName(), customerDTO.getFirstName());
        assertEquals(customerA.getLastName(), customerDTO.getLastName());
        assertEquals(customerA.getEmail(), customerDTO.getEmail());
        assertEquals(customerA.getPhone(), customerDTO.getPhone());
        assertEquals(customerA.getAddress(), customerDTO.getAddress());
        assertEquals(customerA.getLastInteractionDate(), customerDTO.getLastInteractionDate());
        assertEquals(customerA.getEmployee().getId(), customerDTO.getEmployeeId());

        assertEquals(customerA.getId(), customerDTOViaUtil.getId());
        assertEquals(customerA.getFirstName(), customerDTOViaUtil.getFirstName());
        assertEquals(customerA.getLastName(), customerDTOViaUtil.getLastName());
        assertEquals(customerA.getEmail(), customerDTOViaUtil.getEmail());
        assertEquals(customerA.getPhone(), customerDTOViaUtil.getPhone());
        assertEquals(customerA.getAddress(), customerDTOViaUtil.getAddress());
        assertEquals(customerA.getLastInteractionDate(), customerDTOViaUtil.getLastInteractionDate());
        assertEquals(customerA.getEmployee().getId(), customerDTOViaUtil.getEmployeeId());
    }

    @Test
    public void testJsonAsString() {
        // Given
        Object obj = new Object() {
            public String name = "Test";
            public int value = 123;
        };

        // When
        String jsonString = jsonAsString(obj);

        // Then
        assertTrue(jsonString.contains("\"name\":\"Test\""));
        assertTrue(jsonString.contains("\"value\":123"));
    }
}
