package edu.yacoubi.crm.controllers;


import edu.yacoubi.crm.model.Customer;
import edu.yacoubi.crm.service.ICustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customers")
public class CustomerRestController {

    @Autowired
    private ICustomerService customerService;

    @GetMapping
    public List<Customer> getAllCustomers() {
        return null;
        //return customerService.findAll();
    }

    @GetMapping("/{id}")
    public Customer getCustomerById(@PathVariable Long id) {
        return null;
        //return customerService.findById(id).orElseThrow(() -> new ResourceNotFoundException("Customer not found"));
    }

    @PostMapping
    public Customer createCustomer(@RequestBody Customer customer) {
        return null;
        //return customerService.save(customer);
    }

    // Weitere Methoden zur Aktualisierung und Löschung von Kunden
}

