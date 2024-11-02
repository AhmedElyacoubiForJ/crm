package edu.yacoubi.crm.util;

import edu.yacoubi.crm.dto.*;
import edu.yacoubi.crm.model.Customer;
import edu.yacoubi.crm.model.Employee;
import edu.yacoubi.crm.model.Note;
import org.junit.jupiter.api.Test;

import static edu.yacoubi.crm.TestDataUtil.*;
import static edu.yacoubi.crm.util.ValueMapper.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

class ValueMapperTest {

    @Test
    void itShouldConvertToResponseDTO() {
        // Employee
        // Given
        Employee employee = createEmployeeA();
        employee.setId(1L);

        // When
        EmployeeResponseDTO employeeResponseDTO = convertToResponseDTO(employee);

        // Then
        assertThat(employeeResponseDTO).satisfies(response -> {
            assertThat(response.getId()).isEqualTo(employee.getId());
            assertThat(response.getFirstName()).isEqualTo(employee.getFirstName());
            assertThat(response.getLastName()).isEqualTo(employee.getLastName());
            assertThat(response.getEmail()).isEqualTo(employee.getEmail());
            assertThat(response.getDepartment()).isEqualTo(employee.getDepartment());
        });

        // Customer
        // Given
        Customer customerA = createCustomerA(employee);
        customerA.setId(1L);

        // When
        CustomerResponseDTO customerResponseDTO = convertToResponseDTO(customerA);

        // Then
        assertThat(customerA.getId()).isNotNull();
        assertThat(customerResponseDTO).satisfies(response -> {
            assertThat(response.getId()).isEqualTo(customerA.getId());
            assertThat(response.getFirstName()).isEqualTo(customerA.getFirstName());
            assertThat(response.getLastName()).isEqualTo(customerA.getLastName());
            assertThat(response.getEmail()).isEqualTo(customerA.getEmail());
            assertThat(response.getEmployeeId()).isEqualTo(customerA.getEmployee().getId());
        });

        // Note
        // Given
        Note noteA = createNoteA(customerA);
        noteA.setId(1L);

        // When
        NoteResponseDTO noteResponseDTOA = convertToResponseDTO(noteA);

        // Then
        assertThat(noteA.getId()).isNotNull();
        assertThat(noteResponseDTOA).satisfies(response -> {
            assertThat(response.getId()).isEqualTo(noteA.getId());
            assertThat(response.getContent()).isEqualTo(noteA.getContent());
            assertThat(response.getDate()).isEqualTo(noteA.getDate());
            assertThat(response.getInteractionType()).isEqualTo(noteA.getInteractionType());
            assertThat(response.getCustomerId()).isEqualTo(noteA.getCustomer().getId());
        });
    }

    @Test
    void itShouldConvertToEntity() {
        // Employee
        // Given
        EmployeeRequestDTO employeeRequestDTOA = createEmployeeRequestDTOA();

        // When
        Employee employee = convertToEntity(employeeRequestDTOA);

        // Then
        assertThat(employee).satisfies(entity -> {
            assertThat(entity.getFirstName()).isEqualTo(employeeRequestDTOA.getFirstName());
            assertThat(entity.getLastName()).isEqualTo(employeeRequestDTOA.getLastName());
            assertThat(entity.getEmail()).isEqualTo(employeeRequestDTOA.getEmail());
            assertThat(entity.getDepartment()).isEqualTo(employeeRequestDTOA.getDepartment());
        });

        // Customer
        // Given
        CustomerRequestDTO customerRequestDTOA = createCustomerRequestDTOA();

        // When
        Customer customer = convertToEntity(customerRequestDTOA);

        // Then
        assertThat(customer).satisfies(entity -> {
            assertThat(entity.getFirstName()).isEqualTo(customerRequestDTOA.getFirstName());
            assertThat(entity.getLastName()).isEqualTo(customerRequestDTOA.getLastName());
            assertThat(entity.getEmail()).isEqualTo(customerRequestDTOA.getEmail());
            assertThat(entity.getPhone()).isEqualTo(customerRequestDTOA.getPhone());
            assertThat(entity.getAddress()).isEqualTo(customerRequestDTOA.getAddress());
            assertThat(entity.getLastInteractionDate()).isEqualTo(customerRequestDTOA.getLastInteractionDate());
            assertThat(entity.getEmployee()).isNull();
        });

        // Note
        // Given
        customer.setId(1L);
        NoteRequestDTO noteRequestDTOA = createNoteRequestDTOA(customer);

        // When
        Note noteA = convertToEntity(noteRequestDTOA);

        // Then
        assertThat(noteA).satisfies(entity -> {
            assertThat(entity.getContent()).isEqualTo(noteRequestDTOA.getContent());
            assertThat(entity.getInteractionType()).isEqualTo(noteRequestDTOA.getInteractionType());
            assertThat(entity.getCustomer()).isNotNull();
            assertThat(entity.getCustomer().getId()).isEqualTo(customer.getId());
        });
    }

    @Test
    void itShouldConvertObjectToJsonString() {
        // Employee
        // Given
        EmployeeRequestDTO employeeRequestDTOA = createEmployeeRequestDTOA();

        // When
        String jsonEmployeeRequestDTOA = jsonAsString(employeeRequestDTOA);

        // Then
        String expectedJsonString = jsonAsString(employeeRequestDTOA);
        assertThat(jsonEmployeeRequestDTOA).isEqualTo(expectedJsonString);

        // Customer
        // Given
        CustomerRequestDTO customerRequestDTOA = createCustomerRequestDTOA();

        // When
        String jsonCustomerRequestDTOA = jsonAsString(customerRequestDTOA);

        // Then
        String expectedCustomerRequestDTOAJsonString = jsonAsString(customerRequestDTOA);
        assertThat(jsonCustomerRequestDTOA).isEqualTo(expectedCustomerRequestDTOAJsonString);

        // Note
        // Given
        Customer customer = createCustomerA(createEmployeeA());
        NoteRequestDTO noteRequestDTOA = createNoteRequestDTOA(customer);

        // When
        String jsonNoteRequestDTOA = jsonAsString(noteRequestDTOA);

        // Then
        String expectedNoteRequestDTOAJsonString = jsonAsString(noteRequestDTOA);
        assertThat(jsonNoteRequestDTOA).isEqualTo(expectedNoteRequestDTOAJsonString);
    }


    @Test
    void itShouldThrowExceptionWhenObjectCannotBeConverted() {
        // Given
        Object invalidObject = new Object() {
            // Simulate an object that Jackson cannot serialize
            @Override
            public String toString() {
                throw new RuntimeException("Cannot serialize");
            }
        };
        // When & Then
        assertThatThrownBy(() -> jsonAsString(invalidObject))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Error converting object to JSON string");
    }
}
