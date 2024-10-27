package edu.yacoubi.crm.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EmployeeRequestDTO {
    private String firstName;
    private String lastName;
    private String email;
    private String department;
}
