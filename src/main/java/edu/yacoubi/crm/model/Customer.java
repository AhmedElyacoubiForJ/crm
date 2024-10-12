package edu.yacoubi.crm.model;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Entity
@Data
@Builder
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String address;
    private LocalDate lastInteractionDate;

    @ManyToOne
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    // Notizen sind keine eigenständigen Entitäten, sondern direkt
    // mit dem Kunden verbunden.
    // Beim Löschen eines Kunden auch die dazugehörigen Notizen werden gelöscht.
    @OneToMany(
            mappedBy = "customer",
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    List<Note> notes;
}
