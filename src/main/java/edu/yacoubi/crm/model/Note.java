package edu.yacoubi.crm.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;

/**
 * Entity representing a note in the CRM system.
 *
 * <p>This class represents a note and includes information such as content,
 * date, interaction type, and the customer it belongs to.</p>
 * <p>The relationships include:</p>
 * <ul>
 *     <li>A many-to-one relationship with a customer (Customer).</li>
 * </ul>
 *
 * @author A. El Yacoubi
 */
@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Note {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Content is mandatory")
    @Size(max = 1000, message = "Content must be less than 1000 characters")
    @Column(nullable = false, length = 1000)
    private String content;

    @NotNull(message = "Date is mandatory")
    @Column(nullable = false)
    private LocalDate date;

    @NotNull(message = "Interaction type is mandatory")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InteractionType interactionType;

    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    @ToString.Exclude
    private Customer customer;
}
