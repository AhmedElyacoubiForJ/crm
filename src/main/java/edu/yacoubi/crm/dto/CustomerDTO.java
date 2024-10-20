package edu.yacoubi.crm.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class CustomerDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String address;
    private LocalDate lastInteractionDate;
    private Long employeeId; // Referenz auf Employee
}
