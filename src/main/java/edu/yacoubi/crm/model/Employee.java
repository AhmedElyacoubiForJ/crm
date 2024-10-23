package edu.yacoubi.crm.model;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String firstName;
    private String lastName;
    private String email;
    private String department;

    @OneToMany(mappedBy = "employee", fetch = FetchType.LAZY)
    @JsonIgnoreProperties("employee")
    @ToString.Exclude
    List<Customer> customers;
}
