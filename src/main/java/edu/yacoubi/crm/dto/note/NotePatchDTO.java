package edu.yacoubi.crm.dto.note;

import edu.yacoubi.crm.model.InteractionType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class NotePatchDTO {
    @Size(max = 1000, message = "Content must be less than 1000 characters")
    private String content;

    private LocalDate date;

    @Schema(description = "The type of interaction", example = "PHONE_CALL")
    private InteractionType interactionType;

    @Schema(description = "The ID of the customer. This field is optional as it can also be provided as a path variable.")
    private Long customerId;
}
