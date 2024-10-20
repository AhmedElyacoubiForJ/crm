package edu.yacoubi.crm.controllers.api;

import edu.yacoubi.crm.service.INoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/notes")
public class NoteRestController {

    @Autowired
    private INoteService noteService;

}

