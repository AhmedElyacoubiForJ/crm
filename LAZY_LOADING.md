### Lazy Loading in Hibernate

## Was ist Lazy Loading?
> Lazy Loading bedeutet, dass Daten erst geladen werden, wenn sie tatsächlich benötigt werden.
> In Hibernate wird dies oft verwendet, um die Leistung zu verbessern.

## Warum tritt die `LazyIntializationException` auf?
> Diese Ausnahme tritt auf, wenn versucht wird, auf eine Lazy-geladene Sammlung zuzugreifen,
> nachdem die Hibernate-Session geschlossen wurde.

## Zwei Lösungen, um das Problem zu lösen

# Erster Ansatz: JPQL mit **@EntityGraph**
1. **Repository-Methode**:
    <details>
    <summary style="color: blue"><strong>Klicke hier, um den Code anzuzeigen</strong></summary>
    
   ```java
     @EntityGraph(attributePaths = {"notes", "employee", "employee.customers"})
     @Query("SELECT c FROM Customer c WHERE c.email = :email")
     Optional<Customer> findByEmailWithNotesAndEmployeeCustomers(@Param("email") String email);
    
    ```
    </details>
2. **Service-Methode**:
    <details>
    <summary style="color: blue"><strong>Klicke hier, um den Code anzuzeigen</strong></summary>
    
   ```java
   @Override
   @Transactional(readOnly = true)
   public Optional<Customer> getCustomerByEmailWithNotesAndEmployeeCustomers(String email) {
        return customerRepository.findByEmailWithNotesAndEmployeeCustomers(email);
   }
   ```
   </details>

3. **Szenario implementation**:
    <details>
    <summary style="color: blue"><strong>Klicke hier, um den Code anzuzeigen</strong></summary>
    
   ```java
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
        Customer customerF = customerService
                .getCustomerByEmailWithNotesAndEmployeeCustomers("c.customerB@gmail.com")
                .orElseThrow(() -> new RuntimeException("Kunde nicht gefunden"));

        System.out.println(customerF.getNotes());
        System.out.println("Neue Notiz erfolgreich hinzugefügt.");
    }
    ```
    </details>
4. **Ergänzung: Lösung des rekursiven Problems mit `toString()`**:
    - Lösung durch Entfernung der rekursiven `toString()`-Methoden:
    <details>
    <summary style="color: blue"><strong>Klicke hier, um den Code anzuzeigen</strong></summary>
    
   ```java
   
   @Entity
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public class Customer {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;
        private String firstName;
        private String lastName;
        private String email;
        private String phone;
        private String address;
        private LocalDate lastInteractionDate;

        @ManyToOne
        @JoinColumn(name = "employee_id", nullable = false)
        @JsonIgnoreProperties("customers")
        @ToString.Exclude
        private Employee employee;

        @OneToMany(
                mappedBy = "customer",
                fetch = FetchType.LAZY,
                cascade = CascadeType.ALL,
                orphanRemoval = true
        )
        @ToString.Exclude // Added
        List<Note> notes;

        public void addNote(Note note) {
            if (notes == null) {
                notes = new ArrayList<>();
            }
            notes.add(note);
            note.setCustomer(this);
        }

        public void removeNote(Note note) {
            if (notes == null) {
                return;
            }
            notes.remove(note);
            note.setCustomer(null);
        }
    }

   ```
   </details>

# Zweiter Ansatz: Explizite Initialisierung in der Service-Schicht
1. **Service-Methode**:
    <details>
    <summary style="color: blue"><strong>Klicke hier, um den Code anzuzeigen</strong></summary>
    
   ```java
    @Transactional(readOnly = true)
    public Customer getCustomerWithNotes(Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Kunde nicht gefunden"));
        customer.getNotes().size();
        return customer;
    }
    ```
    </details>
2. **Szenario implementation**:
    <details>
    <summary style="color: blue"><strong>Klicke hier, um den Code anzuzeigen</strong></summary>
    
   ```java
    private void scenarioAddNoteToExistingCustomerSecondApproach() {
        Customer customer = customerService.getCustomerByEmail("c.customerB@gmail.com")
                .orElseThrow(() -> new RuntimeException("Kunde nicht gefunden"));
        Note newNote = Note.builder()
                .interactionType(InteractionType.EMAIL)
                .content("Follow-up Email sent")
                .date(LocalDate.now())
                .build();
        noteService.createNoteForCustomer(newNote, customer.getId());
        Customer customerF = customerService.getCustomerWithNotes(customer.getId());
        System.out.println(customerF.getNotes());
    }
    ```
    </details>
   