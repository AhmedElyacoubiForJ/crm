package edu.yacoubi.crm.controllers.view;

import edu.yacoubi.crm.service.IEmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/employees")
public class EmployeeViewController {

    @Autowired
    private IEmployeeService employeeService;


    // Weitere Methoden zur Erstellung und Aktualisierung von Kunden
}

