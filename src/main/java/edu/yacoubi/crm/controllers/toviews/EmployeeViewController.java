package edu.yacoubi.crm.controllers.toviews;

import edu.yacoubi.crm.model.Employee;
import edu.yacoubi.crm.service.IEmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

@Controller
public class EmployeeViewController {

    @Autowired
    private IEmployeeService employeeService;

    // Zeigt die Seite zum Erstellen eines neuen Mitarbeiters
    @GetMapping("/employees/new")
    public String showCreateEmployeeForm(Model model) {
        // Neues Employee-Objekt zum Binden ans Formular
        model.addAttribute("employee", new Employee());
        // Ruft die HTML-Seite 'employee-form.html' auf
        return "employee/employee-form";
    }

    // Verarbeitet das Formular zum Erstellen eines Mitarbeiters
    @PostMapping("/employees")
    public String createEmployee(@ModelAttribute Employee employee) {
        employeeService.createEmployee(employee);
        // Leitet zur Mitarbeiterliste weiter
        return "redirect:/employees";
    }

    @GetMapping("/employees")
    public String listEmployees(
            @RequestParam(value = "search", required = false) String search,
            Model model) {
        List<Employee> employees;
        if (search != null && !search.isEmpty()) {
            employees = employeeService.getEmployeesByNameLike(search)
                    .orElse(employeeService.getEmployeesByEmailLike(search)
                            .orElse(new ArrayList<>()));
        } else {
            employees = employeeService.getAllEmployees();
        }
        model.addAttribute("employees", employees);
        return "employee/employee-list";
    }
}
