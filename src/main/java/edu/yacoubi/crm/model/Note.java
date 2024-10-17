package edu.yacoubi.crm.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Note {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 1000)
    private String content; // Setzt die maximale Länge auf 1000 Zeichen und erlaubt keine Nullwerte

    @Column(nullable = false)
    private LocalDate date; // Erlaubt keine Nullwerte

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InteractionType interactionType; // Erlaubt keine Nullwerte

    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer; // Erlaubt keine Nullwerte
}
