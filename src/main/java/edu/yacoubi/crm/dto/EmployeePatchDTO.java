package edu.yacoubi.crm.dto;

import jakarta.validation.constraints.Email;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EmployeePatchDTO {
    private String firstName;
    private String lastName;

    @Email(message = "Email should be valid")
    private String email;

    private String department;
}

