package edu.yacoubi.crm.controllers.toviews;

import edu.yacoubi.crm.service.INoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/notes")
public class NoteViewController {

    @Autowired
    private INoteService noteService;


    // Weitere Methoden zur Erstellung und Aktualisierung von Kunden
}

