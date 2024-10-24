package edu.yacoubi.crm.controllers.toviews;

import edu.yacoubi.crm.service.ICustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/customers")
public class CustomerViewController {

    @Autowired
    private ICustomerService customerService;

    @GetMapping
    public String listCustomers(Model model) {
        //model.addAttribute("customers", customerService.findAll());
        return "templates/customer/customer-list";
    }

    @GetMapping("/{id}")
    public String getCustomerById(@PathVariable Long id, Model model) {
        //Customer customer = customerService.findById(id).orElseThrow(() -> new ResourceNotFoundException("Customer not found"));
        model.addAttribute("templates/customer", null);
        return "templates/customer/customer-detail";
    }

    // Weitere Methoden zur Erstellung und Aktualisierung von Kunden
}

