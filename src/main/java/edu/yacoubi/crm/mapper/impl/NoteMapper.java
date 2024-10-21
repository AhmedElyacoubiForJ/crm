package edu.yacoubi.crm.mapper.impl;

import edu.yacoubi.crm.dto.NoteDTO;
import edu.yacoubi.crm.mapper.IMapper;
import edu.yacoubi.crm.model.Customer;
import edu.yacoubi.crm.model.Note;
import org.springframework.stereotype.Component;

@Component
public class NoteMapper implements IMapper<Note, NoteDTO> {
    @Override
    public NoteDTO mapTo(Note note) {
        return NoteDTO.builder()
                .id(note.getId())
                .content(note.getContent())
                .date(note.getDate())
                .interactionType(note.getInteractionType())
                .customerId(note.getCustomer().getId())
                .build();
    }

    @Override
    public Note mapFrom(NoteDTO noteDTO) {
        return Note.builder()
                .id(noteDTO.getId())
                .content(noteDTO.getContent())
                .date(noteDTO.getDate())
                .interactionType(noteDTO.getInteractionType())
                .customer(Customer.builder().id(noteDTO.getCustomerId()).build())
                .build();
    }
}
