package edu.yacoubi.crm.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import edu.yacoubi.crm.dto.customer.CustomerRequestDTO;
import edu.yacoubi.crm.dto.customer.CustomerResponseDTO;
import edu.yacoubi.crm.dto.employee.EmployeeRequestDTO;
import edu.yacoubi.crm.dto.employee.EmployeeResponseDTO;
import edu.yacoubi.crm.dto.note.NoteResponseDTO;
import edu.yacoubi.crm.model.Customer;
import edu.yacoubi.crm.model.Employee;
import edu.yacoubi.crm.model.Note;

/**
 * Utility-Klasse für die Transformation von Entitäten und DTOs.
 *
 * <p>Diese Klasse stellt verschiedene Transformer bereit, die verwendet werden können,
 * um Entitäten in DTOs und umgekehrt zu transformieren. Jeder Transformer ist als
 * {@link ITransformer} implementiert und kann auf die entsprechenden Eingabe- und
 * Ausgabe-Typen angewendet werden.</p>
 */
public class EntityTransformer {

    // Transformer für Note
    /**
     * Transformer zur Umwandlung einer Note in ein NoteResponseDTO.
     */
    public static final ITransformer<Note, NoteResponseDTO> noteToNoteResponseDto =
            note -> NoteResponseDTO.builder()
                    .id(note.getId())
                    .content(note.getContent())
                    .date(note.getDate())
                    .interactionType(note.getInteractionType()) // Enum to String
                    .customerId(note.getCustomer().getId())
                    .build();

    /**
     * Transformer zur Umwandlung eines NoteResponseDTO in eine Note.
     */
    public static final ITransformer<NoteResponseDTO, Note> noteResponseDtoToNote =
            noteResponseDTO -> Note.builder()
                    .id(noteResponseDTO.getId())
                    .content(noteResponseDTO.getContent())
                    .date(noteResponseDTO.getDate())
                    .interactionType(noteResponseDTO.getInteractionType())
                    .customer(Customer.builder().id(noteResponseDTO.getCustomerId()).build())
                    .build();

    // Transformer für Employee
    /**
     * Transformer zur Umwandlung eines Employee in ein EmployeeResponseDTO.
     */
    public static final ITransformer<Employee, EmployeeResponseDTO> employeeToEmployeeResponseDto =
            employee -> EmployeeResponseDTO.builder()
                    .id(employee.getId())
                    .firstName(employee.getFirstName())
                    .lastName(employee.getLastName())
                    .email(employee.getEmail())
                    .department(employee.getDepartment())
                    .build();

    /**
     * Transformer zur Umwandlung eines EmployeeRequestDTO in einen Employee.
     */
    public static final ITransformer<EmployeeRequestDTO, Employee> employeeRequestDtoToEmployee =
            employeeRequestDTO -> Employee.builder()
                    .firstName(employeeRequestDTO.getFirstName())
                    .lastName(employeeRequestDTO.getLastName())
                    .email(employeeRequestDTO.getEmail())
                    .department(employeeRequestDTO.getDepartment())
                    .build();

    // Transformer für Customer
    /**
     * Transformer zur Umwandlung eines CustomerRequestDTO in einen Customer.
     */
    public static final ITransformer<CustomerRequestDTO, Customer> customerRequestDtoToCustomer =
            customerRequestDTO -> Customer.builder()
                    .firstName(customerRequestDTO.getFirstName())
                    .lastName(customerRequestDTO.getLastName())
                    .email(customerRequestDTO.getEmail())
                    .phone(customerRequestDTO.getPhone())
                    .address(customerRequestDTO.getAddress())
                    .lastInteractionDate(customerRequestDTO.getLastInteractionDate())
                    .build();

    /**
     * Transformer zur Umwandlung einen Customer in ein CustomerResponseDTO.
     */
    public static final ITransformer<Customer, CustomerResponseDTO> customerToCustomerResponseDto =
            customer -> CustomerResponseDTO.builder()
                    .id(customer.getId())
                    .firstName(customer.getFirstName())
                    .lastName(customer.getLastName())
                    .email(customer.getEmail())
                    .phone(customer.getPhone())
                    .address(customer.getAddress())
                    .lastInteractionDate(customer.getLastInteractionDate())
                    .employeeId(customer.getEmployee().getId())
                    .build();

    private static final ObjectMapper objectMapper = new ObjectMapper();

    static {
        objectMapper.registerModule(new JavaTimeModule());
    }

    private EntityTransformer() {
    }

    /**
     * Wandelt ein Objekt in einen JSON-String um.
     *
     * @param obj das zu konvertierende Objekt
     * @return JSON-String
     * @throws RuntimeException wenn das Objekt nicht in einen JSON-String konvertiert werden kann
     */
    public static String jsonAsString(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error converting object to JSON string", e);
        }
    }
}
