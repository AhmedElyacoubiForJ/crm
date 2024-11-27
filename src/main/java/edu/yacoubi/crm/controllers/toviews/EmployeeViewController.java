package edu.yacoubi.crm.controllers.toviews;

import edu.yacoubi.crm.model.Employee;
import edu.yacoubi.crm.service.IEmployeeService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class EmployeeViewController {

    @Autowired
    private IEmployeeService employeeService;

    // Initialisiert Testdaten
    //@PostConstruct
    public void initTestData() {
        for (int i = 1; i <= 27; i++) {
            Employee employee = new Employee();
            employee.setFirstName("TestVorname" + i);
            employee.setLastName("TestNachname" + i);
            employee.setEmail("test" + i + "@example.com");
            employee.setDepartment("TestAbteilung" + ((i % 5) + 1)); // Abteilungen 1 bis 5
            employeeService.createEmployee(employee); }
    }

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
    public String getEmployees(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "search", required = false) String search,
            Model model) {

        Page<Employee> employeesPage;

        if (search != null && !search.isEmpty()) {
            // Filtere nach Vorname oder Abteilung, falls ein Suchbegriff vorhanden ist
            employeesPage = employeeService.getEmployeesByFirstNameOrDepartment(search, page, size);
        } else {
            // Keine Filterung, nur Paging
            employeesPage = employeeService.getEmployeesWithPagination(page, size);
        }

        model.addAttribute("employeesPage", employeesPage);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", employeesPage.getTotalPages());
        return "employee/employee-list"; // Das View-Template für die Anzeige der Mitarbeiterliste
    }
}
