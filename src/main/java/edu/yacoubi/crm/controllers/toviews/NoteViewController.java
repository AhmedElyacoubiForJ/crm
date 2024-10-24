package edu.yacoubi.crm.controllers.toviews;

import edu.yacoubi.crm.model.Customer;
import edu.yacoubi.crm.model.Note;
import edu.yacoubi.crm.service.ICustomerService;
import edu.yacoubi.crm.service.INoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class NoteViewController {

    @Autowired
    private INoteService noteService;

    @Autowired
    private ICustomerService customerService;

    // Notizencontroller
    @GetMapping("/customers/{customerId}/notes")
    public String listNotes(@PathVariable Long customerId, Model model) {
        Customer customer = customerService.getCustomerWithNotes(customerId);
                //.orElseThrow(() -> new IllegalArgumentException("Ungültige Kunden-ID: " + customerId));
        model.addAttribute("customer", customer);
        model.addAttribute("notes", customer.getNotes());
        return "note/note-list"; // Rückgabe der Notizenliste für den Kunden
    }

    @GetMapping("/customers/{customerId}/notes/new")
    public String showCreateNoteForm(@PathVariable Long customerId, Model model) {
        model.addAttribute("customerId", customerId);
        model.addAttribute("note", new Note());
        return "note/note-form"; // Rückgabe des Formulars zum Erstellen einer neuen Notiz
    }

    @PostMapping("/customers/{customerId}/notes")
    public String createNote(@PathVariable Long customerId, @ModelAttribute Note note) {
        Customer customer = customerService.getCustomerById(customerId)
                .orElseThrow(() -> new IllegalArgumentException("Ungültige Kunden-ID: " + customerId));
        note.setCustomer(customer);
        noteService.createNoteForCustomer(note, customerId);
        return "redirect:/customers/" + customerId + "/notes"; // Umleitung zur Notizenliste des Kunden
    }


}

