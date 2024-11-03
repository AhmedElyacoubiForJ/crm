package edu.yacoubi.crm.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import edu.yacoubi.crm.dto.customer.CustomerRequestDTO;
import edu.yacoubi.crm.dto.customer.CustomerResponseDTO;
import edu.yacoubi.crm.dto.employee.EmployeeRequestDTO;
import edu.yacoubi.crm.dto.employee.EmployeeResponseDTO;
import edu.yacoubi.crm.dto.note.NoteRequestDTO;
import edu.yacoubi.crm.dto.note.NoteResponseDTO;
import edu.yacoubi.crm.model.Customer;
import edu.yacoubi.crm.model.Employee;
import edu.yacoubi.crm.model.Note;

public class ValueMapper {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    static {
        objectMapper.registerModule(new JavaTimeModule());
    }

    private ValueMapper() {
    }

    public static NoteResponseDTO convertToResponseDTO(Note note) {
        return NoteResponseDTO.builder()
                .id(note.getId())
                .content(note.getContent())
                .date(note.getDate())
                .interactionType(note.getInteractionType())
                .customerId(note.getCustomer().getId())
                .build();
    }

    public static Note convertToEntity(NoteRequestDTO noteRequestDTO) {
        return Note.builder()
                .content(noteRequestDTO.getContent())
                .date(noteRequestDTO.getDate())
                .interactionType(noteRequestDTO.getInteractionType())
                .customer(Customer.builder().id(noteRequestDTO.getCustomerId()).build())
                .build();
    }

    public static EmployeeResponseDTO convertToResponseDTO(Employee employee) {
        return EmployeeResponseDTO.builder()
                .id(employee.getId())
                .firstName(employee.getFirstName())
                .lastName(employee.getLastName())
                .email(employee.getEmail())
                .department(employee.getDepartment())
                .build();
    }

    public static Employee convertToEntity(EmployeeRequestDTO employeeRequestDTO) {
        return Employee.builder()
                .firstName(employeeRequestDTO.getFirstName())
                .lastName(employeeRequestDTO.getLastName())
                .email(employeeRequestDTO.getEmail())
                .department(employeeRequestDTO.getDepartment())
                .build();
    }

    public static Customer convertToEntity(CustomerRequestDTO customerRequestDTO) {
        return Customer.builder()
                .firstName(customerRequestDTO.getFirstName())
                .lastName(customerRequestDTO.getLastName())
                .email(customerRequestDTO.getEmail())
                .phone(customerRequestDTO.getPhone())
                .address(customerRequestDTO.getAddress())
                .lastInteractionDate(customerRequestDTO.getLastInteractionDate())
                .build();
    }

    public static CustomerResponseDTO convertToResponseDTO(Customer customer) {
        return CustomerResponseDTO.builder()
                .id(customer.getId())
                .firstName(customer.getFirstName())
                .lastName(customer.getLastName())
                .email(customer.getEmail())
                .phone(customer.getPhone())
                .address(customer.getAddress())
                .lastInteractionDate(customer.getLastInteractionDate())
                .employeeId(customer.getEmployee().getId())
                .build();
    }

    public static String jsonAsString(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error converting object to JSON string", e);
        }
    }
}
