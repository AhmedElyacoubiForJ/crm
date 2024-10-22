package edu.yacoubi.crm.scenarios;

import edu.yacoubi.crm.model.Customer;
import edu.yacoubi.crm.model.Employee;
import edu.yacoubi.crm.model.InteractionType;
import edu.yacoubi.crm.model.Note;
import edu.yacoubi.crm.service.ICustomerService;
import edu.yacoubi.crm.service.IEmployeeService;
import edu.yacoubi.crm.service.INoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class CRMScenarioRunner implements CommandLineRunner {

    private final IEmployeeService employeeService;

    private final ICustomerService customerService;

    private final INoteService noteService;

    @Override
    public void run(String... args) throws Exception {
        setupEmployeesAndCustomers();
        scenarioLoginAndInteraction();
    }

    private void setupEmployeesAndCustomers() {
        // Beispiel: Mitarbeiter hinzufügen
        Employee employeeA = Employee.builder()
                .firstName("EFirstname A")
                .lastName("ELastname A")
                .email("a.employeeA@gmail.com")
                .department("Sales")
                .build();
        Employee savedEmployeeA = employeeService.createEmployee(employeeA);

        // Beispiel: Kunde A hinzufügen
        Customer customer = Customer.builder()
                .firstName("EFirstname A")
                .lastName("ELastname A")
                .email("c.customerA@gmail.com")
                .phone("1234567890")
                .address("123 Main St")
                .lastInteractionDate(LocalDate.now())
                .employee(savedEmployeeA)
                .build();
        customerService.createCustomer(customer);

        // Beispiel: Kunde B hinzufügen
        Customer customerB = Customer.builder()
                .firstName("EFirstname B")
                .lastName("ELastname B")
                .email("c.customerB@gmail.com")
                .phone("9876543210")
                .address("456 Oak St")
                .lastInteractionDate(LocalDate.now())
                .employee(savedEmployeeA)
                .build();
        customerService.createCustomer(customerB);
    }

    private void scenarioLoginAndInteraction() {
        // Kunden anmelden
        Customer customer = customerService
                .getCustomerByEmail("c.customerA@gmail.com")
                .orElseThrow(() -> new RuntimeException("Kunde nicht gefunden"));

        // Interaktionstyp auswählen
        InteractionType interactionType = InteractionType.MEETING;

        // Notiz erstellen und speichern
        Note note = Note.builder()
                .interactionType(interactionType)
                .content("Details zur Interaktion")
                .date(LocalDate.now()).build();

        noteService.createNoteForCustomer(note, customer.getId());

        System.out.println("Interaktion erfolgreich gespeichert.");
    }

    // Hier kannst du weitere Szenarien hinzufügen
    private void scenarioAnotherUseCase() {
        // Implementiere hier ein weiteres Szenario
    }
}
