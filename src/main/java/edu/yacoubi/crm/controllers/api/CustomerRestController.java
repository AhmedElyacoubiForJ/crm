package edu.yacoubi.crm.controllers.api;

import edu.yacoubi.crm.model.Customer;
import edu.yacoubi.crm.service.ICustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/customers")
public class CustomerRestController {

    @Autowired
    private ICustomerService customerService;

    @PostMapping
    public Customer createCustomerForEmployee(
            @RequestBody Customer customer,
            @RequestParam Long employeeId
    ) {

        return null;
    }

}

