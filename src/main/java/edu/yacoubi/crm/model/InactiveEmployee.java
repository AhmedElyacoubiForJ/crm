package edu.yacoubi.crm.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entity representing an inactive employee in the CRM system.
 * <p>This class represents an inactive employee and includes information such as first name,
 * last name, email, department, and the original employee ID before the employee became inactive.</p>
 *
 * @author A. El Yacoubi
 */
@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InactiveEmployee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "First name is mandatory")
    @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
    private String firstName;

    @NotBlank(message = "Last name is mandatory")
    @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters")
    private String lastName;

    @NotBlank(message = "Email is mandatory")
    @Email(message = "Email should be valid")
    private String email;

    @NotBlank(message = "Department is mandatory")
    private String department;

    private Long originalEmployeeId; // Keine NotBlank-Validierung notwendig
}
