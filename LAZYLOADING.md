### Lazy Loading in Hibernate

## Was ist Lazy Loading?
> Lazy Loading bedeutet, dass Daten erst geladen werden, wenn sie tatsächlich benötigt werden.
> In Hibernate wird dies oft verwendet, um die Leistung zu verbessern.

## Warum tritt die `LazyIntializationException` auf?
> Diese Ausnahme tritt auf, wenn versucht wird, auf eine Lazy-geladene Sammlung zuzugreifen,
> nachdem die Hibernate-Session geschlossen wurde.

## Zwei Lösungen, um das Problem zu lösen
# Erster Ansatz: JPQL mit **@EntityGraph**
1. Repository-Methode:
    <details>
    <summary style="color: blue"><strong>Klicke hier, um den Code anzuzeigen</strong></summary>
    
    ```java
    
    @EntityGraph(attributePaths = {"notes"})
    @Query("SELECT c FROM Customer c WHERE c.email = :email")
    Optional<Customer> findByEmailWithNotes(@Param("email") String email);
    
    ```
    </details>

2. Service-Methode:
    <details>
    <summary style="color: blue"><strong>Klicke hier, um den Code anzuzeigen</strong></summary>
   
    ```java
    
    @Transactional(readOnly = true)
    public Optional<Customer> getCustomerByEmailWithNotes(String email) {
        return customerRepository.findByEmailWithNotes(email);
    }
    ```
    </details>

3. Szenario implementation:
    <details>
    <summary style="color: blue"><strong>Klicke hier, um den Code anzuzeigen</strong></summary>
   
    ```java
    
    private void scenarioAddNoteToExistingCustomerFirstApproach() {
        Customer customer = customerService.getCustomerByEmail("c.customerB@gmail.com")
                .orElseThrow(() -> new RuntimeException("Kunde nicht gefunden"));
        
        Note newNote = Note.builder()
                .interactionType(InteractionType.EMAIL)
                .content("Follow-up Email sent")
                .date(LocalDate.now())
                .build();
        noteService.createNoteForCustomer(newNote, customer.getId());
        
        Customer customerF = customerService.getCustomerByEmailWithNotes("c.customerB@gmail.com")
                .orElseThrow(() -> new RuntimeException("Kunde nicht gefunden"));
        
        System.out.println(customerF.getNotes());
    }
    ```
    </details>
   