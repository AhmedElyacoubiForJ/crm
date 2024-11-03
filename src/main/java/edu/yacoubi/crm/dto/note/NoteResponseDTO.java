package edu.yacoubi.crm.dto.note;

import edu.yacoubi.crm.model.InteractionType;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class NoteResponseDTO {
    private Long id;
    private String content;
    private LocalDate date;
    private InteractionType interactionType;
    private Long customerId;
}
