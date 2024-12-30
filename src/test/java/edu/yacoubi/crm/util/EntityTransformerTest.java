package edu.yacoubi.crm.util;

import edu.yacoubi.crm.dto.customer.CustomerRequestDTO;
import edu.yacoubi.crm.dto.customer.CustomerResponseDTO;
import edu.yacoubi.crm.dto.employee.EmployeeRequestDTO;
import edu.yacoubi.crm.dto.employee.EmployeeResponseDTO;
import edu.yacoubi.crm.dto.note.NoteResponseDTO;
import edu.yacoubi.crm.model.Customer;
import edu.yacoubi.crm.model.Employee;
import edu.yacoubi.crm.model.InteractionType;
import edu.yacoubi.crm.model.Note;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static edu.yacoubi.crm.util.EntityTransformer.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class EntityTransformerTest {

    @Test
    public void testNoteToNoteResponseDTOTransformation() {
        // Given
        Note note = Note.builder()
                .id(1L)
                .content("Test Note")
                .date(LocalDate.now())
                .interactionType(InteractionType.EMAIL)
                .customer(Customer.builder().id(2L).build())
                .build();

        // When
        NoteResponseDTO noteDTO = noteToNoteResponseDto.transform(note);
        NoteResponseDTO noteDTOViaUtil = TransformerUtil.transform(noteToNoteResponseDto, note);

        // Then
        assertEquals(1L, noteDTO.getId());
        assertEquals("Test Note", noteDTO.getContent());
        assertEquals(InteractionType.EMAIL, noteDTO.getInteractionType());
        assertEquals(2L, noteDTO.getCustomerId());

        assertEquals(1L, noteDTOViaUtil.getId());
        assertEquals("Test Note", noteDTOViaUtil.getContent());
        assertEquals(InteractionType.EMAIL, noteDTOViaUtil.getInteractionType());
        assertEquals(2L, noteDTOViaUtil.getCustomerId());
    }

    @Test
    public void testEmployeeToEmployeeResponseDTOTransformation() {
        // Given
        Employee employee = Employee.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .department("Sales")
                .build();

        // When
        EmployeeResponseDTO employeeDTO = employeeToEmployeeResponseDto.transform(employee);
        EmployeeResponseDTO employeeDTOViaUtil =
                TransformerUtil.transform(employeeToEmployeeResponseDto, employee);

        // Then
        assertEquals(1L, employeeDTO.getId());
        assertEquals("John", employeeDTO.getFirstName());
        assertEquals("Doe", employeeDTO.getLastName());
        assertEquals("john.doe@example.com", employeeDTO.getEmail());
        assertEquals("Sales", employeeDTO.getDepartment());

        assertEquals(1L, employeeDTOViaUtil.getId());
        assertEquals("John", employeeDTOViaUtil.getFirstName());
        assertEquals("Doe", employeeDTOViaUtil.getLastName());
        assertEquals("john.doe@example.com", employeeDTOViaUtil.getEmail());
        assertEquals("Sales", employeeDTOViaUtil.getDepartment());
    }

    @Test
    public void testEmployeeRequestDTOToEmployeeTransformation() {
        // Given
        EmployeeRequestDTO employeeRequestDTO = EmployeeRequestDTO.builder()
                .firstName("Jane")
                .lastName("Smith")
                .email("jane.smith@example.com")
                .department("Marketing")
                .build();

        // When
        Employee employee = employeeRequestDtoToEmployee.transform(employeeRequestDTO);
        Employee employeeViaUtil = TransformerUtil.transform(employeeRequestDtoToEmployee, employeeRequestDTO);

        // Then
        assertEquals("Jane", employee.getFirstName());
        assertEquals("Smith", employee.getLastName());
        assertEquals("jane.smith@example.com", employee.getEmail());
        assertEquals("Marketing", employee.getDepartment());

        assertEquals("Jane", employeeViaUtil.getFirstName());
        assertEquals("Smith", employeeViaUtil.getLastName());
        assertEquals("jane.smith@example.com", employeeViaUtil.getEmail());
        assertEquals("Marketing", employeeViaUtil.getDepartment());
    }

    @Test
    public void testCustomerRequestDTOToCustomerTransformation() {
        // Given
        CustomerRequestDTO customerRequestDTO = CustomerRequestDTO.builder()
                .firstName("Jane")
                .lastName("Smith")
                .email("jane.smith@example.com")
                .phone("123456789")
                .address("123 Main St")
                .lastInteractionDate(LocalDate.now())
                .build();

        // When
        Customer customer = customerRequestDtoToCustomer.transform(customerRequestDTO);
        Customer customerViaUtil = TransformerUtil.transform(customerRequestDtoToCustomer, customerRequestDTO);

        // Then
        assertEquals("Jane", customer.getFirstName());
        assertEquals("Smith", customer.getLastName());
        assertEquals("jane.smith@example.com", customer.getEmail());
        assertEquals("123456789", customer.getPhone());
        assertEquals("123 Main St", customer.getAddress());
        assertEquals(customerRequestDTO.getLastInteractionDate(), customer.getLastInteractionDate());

        assertEquals("Jane", customerViaUtil.getFirstName());
        assertEquals("Smith", customerViaUtil.getLastName());
        assertEquals("jane.smith@example.com", customerViaUtil.getEmail());
        assertEquals("123456789", customerViaUtil.getPhone());
        assertEquals("123 Main St", customerViaUtil.getAddress());
        assertEquals(customerRequestDTO.getLastInteractionDate(), customerViaUtil.getLastInteractionDate());
    }

    @Test
    public void testCustomerToCustomerResponseDTOTransformation() {
        // Given
        Customer customer = Customer.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .phone("987654321")
                .address("456 Elm St")
                .lastInteractionDate(LocalDate.now())
                .employee(Employee.builder().id(3L).build())
                .build();

        // When
        CustomerResponseDTO customerDTO = customerToCustomerResponseDto.transform(customer);
        CustomerResponseDTO customerDTOViaUtil = TransformerUtil.transform(customerToCustomerResponseDto, customer);

        // Then
        assertEquals(1L, customerDTO.getId());
        assertEquals("John", customerDTO.getFirstName());
        assertEquals("Doe", customerDTO.getLastName());
        assertEquals("john.doe@example.com", customerDTO.getEmail());
        assertEquals("987654321", customerDTO.getPhone());
        assertEquals("456 Elm St", customerDTO.getAddress());
        assertEquals(customer.getLastInteractionDate(), customerDTO.getLastInteractionDate());
        assertEquals(3L, customerDTO.getEmployeeId());

        assertEquals(1L, customerDTOViaUtil.getId());
        assertEquals("John", customerDTOViaUtil.getFirstName());
        assertEquals("Doe", customerDTOViaUtil.getLastName());
        assertEquals("john.doe@example.com", customerDTOViaUtil.getEmail());
        assertEquals("987654321", customerDTOViaUtil.getPhone());
        assertEquals("456 Elm St", customerDTOViaUtil.getAddress());
        assertEquals(customer.getLastInteractionDate(), customerDTOViaUtil.getLastInteractionDate());
        assertEquals(3L, customerDTOViaUtil.getEmployeeId());
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
