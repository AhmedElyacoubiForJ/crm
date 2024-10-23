package edu.yacoubi.crm.scenarios;

import edu.yacoubi.crm.model.Customer;
import edu.yacoubi.crm.model.Employee;
import edu.yacoubi.crm.model.InteractionType;
import edu.yacoubi.crm.model.Note;
import edu.yacoubi.crm.service.ICustomerService;
import edu.yacoubi.crm.service.IEmployeeService;
import edu.yacoubi.crm.service.INoteService;
import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
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
        scenarioCreateCustomerAndAssignEmployee();
        scenarioUpdateCustomerDetails();
        //scenarioAddNoteToExistingCustomer();
        scenarioAddNoteToExistingCustomerFirstApproach();
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

    // Szenario zur Erstellung eines neuen Kunden und Zuweisung eines Mitarbeiters.
    private void scenarioCreateCustomerAndAssignEmployee() {
        Employee employee = employeeService
                .getEmployeeByEmail("employeeB@example.com")
                .orElseGet(() -> employeeService.createEmployee(
                        Employee.builder()
                                .firstName("BFirstname")
                                .lastName("BLastname")
                                .email("employeeB@example.com")
                                .department("Support")
                                .build())
                );

        Customer newCustomer = Customer.builder()
                .firstName("CFIrstname")
                .lastName("CLastname")
                .email("new.customer@example.com")
                .phone("1231231230")
                .address("789 Elm St")
                .lastInteractionDate(LocalDate.now())
                .employee(employee)
                .build();

        customerService.createCustomer(newCustomer);

        System.out.println("Neuer Kunde erfolgreich erstellt und Mitarbeiter zugewiesen.");
    }

    // Szenario zur Aktualisierung von Kundendaten
    private void scenarioUpdateCustomerDetails() {
        // Kunden finden
        Customer customer = customerService.getCustomerByEmail("c.customerB@gmail.com")
                .orElseThrow(() -> new RuntimeException("Kunde nicht gefunden"));

        // Kundendaten aktualisieren
        customer.setPhone("5556667777");
        customer.setAddress("Updated Address");

        customerService.updateCustomer(customer.getId(), customer);

        System.out.println("Kundendaten erfolgreich aktualisiert.");
    }
    // Szenario zum Hinzufügen einer Notiz zu einem bestehenden Kunden
    private void scenarioAddNoteToExistingCustomer() {
        // Kunden finden
        Customer customer = customerService.getCustomerByEmail("c.customerB@gmail.com")
                .orElseThrow(() -> new RuntimeException("Kunde nicht gefunden"));

        // Neue Notiz erstellen
        Note newNote = Note.builder()
                .interactionType(InteractionType.EMAIL)
                .content("Follow-up Email sent")
                .date(LocalDate.now())
                .build();

        noteService.createNoteForCustomer(newNote, customer.getId());

        Customer customerF = customerService.getCustomerByEmail("c.customerB@gmail.com").orElseThrow(() -> new RuntimeException("Kunde nicht gef"));

        // Lazy loading problem
        //System.out.println(customerF.getNotes());

        System.out.println("Neue Notiz erfolgreich hinzugefügt.");
    }

    // Lazy loading umzugehen, first approach
    // Erster Ansatz: JPQL mit @Transactional
    private void scenarioAddNoteToExistingCustomerFirstApproach() {
        // Kunden finden
        Customer customer = customerService.getCustomerByEmail("c.customerB@gmail.com")
                .orElseThrow(() -> new RuntimeException("Kunde nicht gefunden"));
        // Neue Notiz erstellen
        Note newNote = Note.builder()
                .interactionType(InteractionType.EMAIL)
                .content("Follow-up Email sent")
                .date(LocalDate.now())
                .build();
        noteService.createNoteForCustomer(newNote, customer.getId());

        // Kunden und Notizen laden
        Customer customerF = customerService.getCustomerByEmailWithNotesAndEmployeeCustomers("c.customerB@gmail.com")
                .orElseThrow(() -> new RuntimeException("Kunde nicht gefunden"));

        System.out.println(customerF.getNotes());
        System.out.println("Neue Notiz erfolgreich hinzugefügt.");
    }
    // Zweiter Ansatz: Lazy Loading mit @Transactional
//    private void scenarioAddNoteToExistingCustomerSecondApproach() {
//        // Kunden finden
//        Customer customer = customerService.getCustomerByEmail("c.customerB@gmail.com")
//                .orElseThrow(() -> new RuntimeException("Kunde nicht gefunden"));
//        // Neue Notiz erstellen
//        Note newNote = Note.builder()
//                .interactionType(InteractionType.EMAIL)
//                .content("Follow-up Email sent")
//                .date(LocalDate.now())
//                .build();
//        noteService.createNoteForCustomer(newNote, customer.getId());
//
//        // Kunden und Notizen laden mit Lazy Loading
//        Customer customerF = customerService.getCustomerWithNotes(customer.getId());
//
//        System.out.println(customerF.getNotes());
//        System.out.println("Neue Notiz erfolgreich hinzugefügt.");
//    }

}
