package edu.yacoubi.crm;

import edu.yacoubi.crm.dto.customer.CustomerRequestDTO;
import edu.yacoubi.crm.dto.employee.EmployeeRequestDTO;
import edu.yacoubi.crm.dto.note.NoteRequestDTO;
import edu.yacoubi.crm.model.Customer;
import edu.yacoubi.crm.model.Employee;
import edu.yacoubi.crm.model.InteractionType;
import edu.yacoubi.crm.model.Note;

import java.time.LocalDate;

public final class TestDataUtil {
    private TestDataUtil() {
    }

    public static Employee createEmployeeA() {
        return Employee.builder()
                .firstName("Jon")
                .lastName("Wayne")
                .email("jon.wayne@example.com")
                .department("Sales")
                .build();
    }

    public static Employee createStandardEmployee() {
        return Employee.builder()
                .firstName("Bruce")
                .lastName("Wayne")
                .email("bruce.wayne@example.com")
                .department("Sales")
                .build();
    }

    public static EmployeeRequestDTO createEmployeeRequestDTOA() {
        return EmployeeRequestDTO.builder()
                .firstName("Jon")
                .lastName("Wayne")
                .email("jon.wayne@example.com")
                .department("Sales")
                .build();
    }

    public static Employee createEmployeeB() {
        return Employee.builder()
                .firstName("Peter")
                .lastName("Parker")
                .email("peter.parker@example.com")
                .department("Marketing")
                .build();
    }

    public static Employee createEmployeeC() {
        return Employee.builder()
                .firstName("Bruce")
                .lastName("Banner")
                .email("bruce.banner@example.com")
                .department("Finance")
                .build();
    }

    public static Customer createCustomerA(Employee employee) {
        return Customer.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .phone("1234567890")
                .address("123 Main St")
                .lastInteractionDate(LocalDate.now())
                .employee(employee)
                .build();
    }

    public static CustomerRequestDTO createCustomerRequestDTOA() {
        return CustomerRequestDTO.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .phone("1234567890")
                .address("123 Main St")
                .lastInteractionDate(LocalDate.now())
                .build();
    }

    public static Customer createCustomerB(Employee employee) {
        return Customer.builder()
                .firstName("Jane")
                .lastName("Smith")
                .email("jane.smith@example.com")
                .phone("9876543210")
                .address("456 Elm St")
                .lastInteractionDate(LocalDate.now().minusDays(1))
                .employee(employee)
                .build();
    }

    public static Customer createCustomerC(Employee employee) {
        return Customer.builder()
                .firstName("Michael")
                .lastName("Jordan")
                .email("michael.jordan@example.com")
                .phone("0987654321")
                .address("789 Oak St")
                .lastInteractionDate(LocalDate.now().minusMonths(1))
                .employee(employee)
                .build();
    }

    public static Note createNoteA(Customer customer) {
        return Note.builder()
                .content("First interaction")
                .date(LocalDate.now())
                .interactionType(InteractionType.EMAIL)
                .customer(customer)
                .build();
    }

    public static NoteRequestDTO createNoteRequestDTOA(Customer customer) {
        return NoteRequestDTO.builder()
               .content("First interaction")
               .date(LocalDate.now())
               .interactionType(InteractionType.EMAIL)
               .customerId(customer.getId())
               .build();
    }

    public static Note createNoteB(Customer customer) {
        return Note.builder()
                .content("Follow-up call")
                .date(LocalDate.now().plusDays(1))
                .interactionType(InteractionType.PHONE_CALL)
                .customer(customer)
                .build();
    }

    public static Note createNoteC(Customer customer) {
        return Note.builder()
                .content("Meeting scheduled")
                .date(LocalDate.now().plusDays(2))
                .interactionType(InteractionType.MEETING)
                .customer(customer)
                .build();
    }
}
