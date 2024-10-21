package edu.yacoubi.crm.dto;

import edu.yacoubi.crm.model.InteractionType;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class NoteDTO {
    private Long id;
    private String content;
    private LocalDate date;
    private InteractionType interactionType;
    private Long customerId;
}
