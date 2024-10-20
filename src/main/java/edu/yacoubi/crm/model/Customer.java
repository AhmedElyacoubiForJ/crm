package edu.yacoubi.crm.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
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

    @OneToMany(
            mappedBy = "customer",
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    List<Note> notes;

    public void addNote(Note note) {
        if (notes == null) {
            notes = new ArrayList<>();
        }
        notes.add(note);
        note.setCustomer(this);
    }

    public void removeNote(Note note) {
        if (notes == null) {
            return;
        }
        notes.remove(note);
        note.setCustomer(null);
    }
}
