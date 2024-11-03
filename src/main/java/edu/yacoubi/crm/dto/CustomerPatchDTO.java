package edu.yacoubi.crm.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class CustomerPatchDTO {
    private String firstName;
    private String lastName;

    @Email(message = "Email should be valid")
    private String email;

    @Size(min = 10, max = 15, message = "Phone number must be between 10 and 15 characters")
    private String phone;

    @Size(max = 100, message = "Address must not exceed 100 characters")
    private String address;

    private LocalDate lastInteractionDate;
}
