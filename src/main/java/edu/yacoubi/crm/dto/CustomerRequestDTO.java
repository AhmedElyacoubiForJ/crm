package edu.yacoubi.crm.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class CustomerRequestDTO {
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String address;
    private LocalDate lastInteractionDate;
}
