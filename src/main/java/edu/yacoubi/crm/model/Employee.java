package edu.yacoubi.crm.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Entity representing an employee in the CRM system.
 *
 * <p>This class represents an employee and includes information such as first name,
 * last name, email, and department.</p>
 * <p>The relationships include:</p>
 * <ul>
 *     <li>A one-to-many relationship with customers (Customer).</li>
 * </ul>
 *
 * @author A. El Yacoubi
 */
@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Employee {
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

    @OneToMany(
            mappedBy = "employee",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    @JsonIgnoreProperties("employee")
    @ToString.Exclude
    @Builder.Default
    List<Customer> customers = new ArrayList<>();

    public void addCustomer(Customer customer) {
        customers.add(customer);
        customer.setEmployee(this);
    }

    public void removeCustomer(Customer customer) {
        customers.remove(customer);
        customer.setEmployee(null);
    }

    /*@Override
    public String toString() {
        return "Employee{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", department='" + department + '\'' +
                ", customers=" + customers.stream().map(Customer::getId).collect(Collectors.toList()) +
                '}';
    }*/
}
