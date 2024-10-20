package edu.yacoubi.crm.controllers;

import edu.yacoubi.crm.model.Customer;
import edu.yacoubi.crm.service.ICustomerService;
import edu.yacoubi.crm.service.exception.ResourceNotFoundException;
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
        return "customer/customer-list";
    }

    @GetMapping("/{id}")
    public String getCustomerById(@PathVariable Long id, Model model) {
        //Customer customer = customerService.findById(id).orElseThrow(() -> new ResourceNotFoundException("Customer not found"));
        model.addAttribute("customer", null);
        return "customer/customer-detail";
    }

    // Weitere Methoden zur Erstellung und Aktualisierung von Kunden
}

