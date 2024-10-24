package edu.yacoubi.crm.controllers.toviews;

import edu.yacoubi.crm.model.Customer;
import edu.yacoubi.crm.service.ICustomerService;
import edu.yacoubi.crm.service.IEmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class CustomerViewController {

    @Autowired
    private ICustomerService customerService;

    @Autowired
    private IEmployeeService employeeService;

    @GetMapping("/customers/new")
    public String showCreateCustomerForm(Model model) {
        model.addAttribute("customer", new Customer());
        model.addAttribute("employees", employeeService.getAllEmployees());  // Mitarbeiterliste
        return "customer/customer-form";  // Gibt die HTML-Seite für die Kundenerstellung zurück
    }

    @PostMapping("/customers")
    public String createCustomer(@ModelAttribute Customer customer) {
        customerService.createCustomer(customer);  // Speichert den Kunden in der Datenbank
        return "redirect:/customers";  // Leitet zur Kundenliste weiter
    }


    @GetMapping("/customers")
    public String listCustomers(Model model) {
        model.addAttribute("customers", customerService.getAllCustomers());  // Lädt alle Kunden aus der Datenbank
        return "customer/customer-list";  // Ruft die HTML-Seite 'customer_list.html' auf
    }


    // Weitere Methoden zur Erstellung und Aktualisierung von Kunden
}

