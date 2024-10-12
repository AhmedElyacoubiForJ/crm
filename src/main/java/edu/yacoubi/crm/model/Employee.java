package edu.yacoubi.crm.model;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Data
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String firstName;
    private String lastName;
    private String email;
    private String department;

    @OneToMany(mappedBy = "employee", fetch = FetchType.LAZY)
    List<Customer> customers;
}
