package edu.yacoubi.crm.controllers.toviews;

import edu.yacoubi.crm.model.Employee;
import edu.yacoubi.crm.service.IEmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class EmployeeViewController {

    @Autowired
    private IEmployeeService employeeService;

    // Zeigt die Seite zum Erstellen eines neuen Mitarbeiters
    @GetMapping("/employees/new")
    public String showCreateEmployeeForm(Model model) {
        System.out.println("##################################");
        System.out.println("showCreateEmployeeForm");

        model.addAttribute("employee", new Employee());  // Neues Employee-Objekt zum Binden ans Formular
        return "employee/employee-form";  // Ruft die HTML-Seite 'create_employee.html' auf
    }

    // Verarbeitet das Formular zum Erstellen eines Mitarbeiters
    @PostMapping("/employees")
    public String createEmployee(@ModelAttribute Employee employee) {
        employeeService.createEmployee(employee);  // Speichert den neuen Mitarbeiter in der Datenbank
        return "redirect:/employees";  // Leitet zur Mitarbeiterliste weiter (optional)
    }

    // Zeigt eine Liste der Mitarbeiter (optional)
    @GetMapping("/employees")
    public String listEmployees(Model model) {
        model.addAttribute("employees", employeeService.getAllEmployees());  // Lädt alle Mitarbeiter aus der Datenbank
        return "employee/employee-list";  // Eine weitere HTML-Seite für die Mitarbeiterliste
    }

}

