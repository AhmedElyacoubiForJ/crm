# Dokumentation: Nutzung von Example und Criteria API für Suche und Updates

## Übersicht
   > Diese Dokumentation beschreibt die Konzepte und Implementierungen für die Suche und Updates im Backend-Layer meines Projekts.
   > Es werden zwei Ansätze vorgestellt: die Nutzung von `Example` für Suchabfragen und die Nutzung der `Criteria API` für Updates.

## Nutzung von Example für Suche
   > Der `Example`-Ansatz ermöglicht es, einfache und flexible Suchabfragen zu erstellen, ohne dass komplexe SQL- oder JPQL-Abfragen geschrieben werden müssen.
   > Mit `ExampleMatcher` können spezifische Vergleichsbedingungen definiert und bestimmte Felder ignoriert werden.
### Implementierung
   - **Service: CustomerServiceImpl**
       <details>
       <summary style="color: blue"><strong>Klicke hier, um den Code anzuzeigen</strong></summary>
    
       ```java
       @Override
        public List<Customer> getCustomersByExample(CustomerRequestDTO customerDTO) {
            Customer customerProbe = new Customer();
            if (customerDTO.getFirstName() != null) {
                customerProbe.setFirstName(customerDTO.getFirstName());
            }
            if (customerDTO.getLastName() != null) {
                customerProbe.setLastName(customerDTO.getLastName());
            }
            if (customerDTO.getEmail() != null) {
                customerProbe.setEmail(customerDTO.getEmail());
            }
        
            ExampleMatcher matcher = ExampleMatcher.matching()
                    .withIgnorePaths("id", "notes");
            Example<Customer> example = Example.of(customerProbe, matcher);
            return customerRepository.findAll(example);
        }
    
       ```
       </details>

## Nutzung der Criteria API für Updates

1. **Beschreibung**
   > Die Criteria API ermöglicht es, dynamische und komplexe Abfragen zu erstellen und gezielt einzelne Felder zu aktualisieren.
   > Dies macht sie besonders nützlich für partielle Updates, ohne das gesamte Objekt neu laden zu müssen.

2. **Implementierung**
   - **Service: CustomerServiceImpl**
       <details>
       <summary style="color: blue"><strong>Klicke hier, um den Code anzuzeigen</strong></summary>

       ```java
        @Service
        @RequiredArgsConstructor
        public class CustomerServiceImpl implements ICustomerService {
        private final CustomerRepository customerRepository;
        private final EntityManager entityManager;
        
            @Override
            @Transactional
            public void partialUpdateCustomer(Long id, CustomerPatchDTO customerPatchDTO) {
                CriteriaBuilder cb = entityManager.getCriteriaBuilder();
                CriteriaUpdate<Customer> update = cb.createCriteriaUpdate(Customer.class);
                Root<Customer> root = update.from(Customer.class);
        
                if (customerPatchDTO.getFirstName() != null) {
                    update.set(root.get("firstName"), customerPatchDTO.getFirstName());
                }
                if (customerPatchDTO.getLastName() != null) {
                    update.set(root.get("lastName"), customerPatchDTO.getLastName());
                }
                if (customerPatchDTO.getEmail() != null) {
                    update.set(root.get("email"), customerPatchDTO.getEmail());
                }
                if (customerPatchDTO.getAddress() != null) {
                    update.set(root.get("address"), customerPatchDTO.getAddress());
                }
        
                update.where(cb.equal(root.get("id"), id));
                entityManager.createQuery(update).executeUpdate();
            }
        }
       ```
       </details>
   - **Controller: CustomerRestController**
      - **Deprecated Methode für Update nach Beispiel**
         <details>
         <summary style="color: blue"><strong>Klicke hier, um den Code anzuzeigen</strong></summary>
         
         ```java
         @Operation(
            summary = "Partial update of customer by example",
            description = "Partial update of an existing customer using a provided example."
         )
         @PutMapping("/{id}/updateByExample")
         @Deprecated
         public ResponseEntity<CustomerResponseDTO> updateCustomerByExample(
         @PathVariable Long id,
         @RequestBody CustomerRequestDTO customerRequestDTO) {
         log.info("CustomerRestController::updateCustomerByExample request id {}, customer dto {}", id, jsonAsString(customerRequestDTO));

         Customer updatedCustomer = customerService.updateCustomerByExample(customerRequestDTO, id);
         CustomerResponseDTO customerResponseDTO = convertToResponseDTO(updatedCustomer);

         log.info("CustomerRestController::updateCustomerByExample response dto {}", jsonAsString(customerResponseDTO));
         return ResponseEntity.ok(customerResponseDTO);
         }

         ```
         </details>
      -  **Neue Methode für partielles Update mit Criteria API**
         <details>
         <summary style="color: blue"><strong>Klicke hier, um den Code anzuzeigen</strong></summary>

         ```java
         @Operation(
           summary = "Partial update of customer",
           description = "Partial update of an existing customer by their unique ID."
         )
         @PatchMapping("/{id}")
         public ResponseEntity<APIResponse<CustomerResponseDTO>> patchCustomer(
         @PathVariable Long id,
         @RequestBody CustomerPatchDTO customerPatchDTO) {
         log.info("CustomerRestController::patchCustomer request id {}, customer dto {}", id, jsonAsString(customerPatchDTO));

         customerService.partialUpdateCustomer(id, customerPatchDTO);

         Customer updatedCustomer = customerService.getCustomerById(id)
              .orElseThrow(() -> new ResourceNotFoundException("Customer not found with ID: " + id));

         CustomerResponseDTO customerResponseDTO = convertToResponseDTO(updatedCustomer);
         APIResponse<CustomerResponseDTO> response = APIResponse.<CustomerResponseDTO>builder()
              .status("success")
              .statusCode(HttpStatus.OK.value())
              .data(customerResponseDTO)
              .build();

         log.info("CustomerRestController::patchCustomer response {}", jsonAsString(response));
         return ResponseEntity.ok(response);
         }

         ```
         </details>

> Da ein Patch-DTO oft teilweise Updates ermöglicht, muss man sicherstellen, dass die Felder, die übergeben werden,
> den erwarteten Formaten entsprechen. Hier einige Anmerkungen und Empfehlungen:
1. **Optionale Felder**:
   > Da die Felder optional sind, überprüfe nur deren Format, aber erfordere sie nicht zwingend.
2. **Individuelle Validierungen**:
   > Stelle sicher, dass individuelle Validierungen sinnvoll und notwendig sind, um Datenkonsistenz zu gewährleisten.
3. **Beispiel**: Hier ist dein aktualisiertes `CustomerPatchDTO`
   <details>
   <summary style="color: blue"><strong>Klicke hier, um den Code anzuzeigen</strong></summary>

   ```java
   package edu.yacoubi.crm.dto;

   import jakarta.validation.constraints.Email;
   import jakarta.validation.constraints.Size;

   import java.time.LocalDate;

   public class CustomerPatchDTO {
   private String firstName;
   private String lastName;

    @Email(message = "Email should be valid")
    private String email;

    @Size(min = 10, max = 15, message = "Phone number must be between 10 and 15 characters")
    private String phone;

    @Size(max = 100, message = "Address must not exceed 100 characters")
    private String address;

    private LocalDate lastInteractionDate;
    }

   ```
   </details>
   
   **Erklärung**:
   - `Email`: Überprüft, ob die E-Mail gültig ist, wenn sie angegeben wird.
   - `Phone`: Überprüft die Länge der Telefonnummer, wenn sie angegeben wird.
   - `Address`: Überprüft die maximale Länge der Adresse, wenn sie angegeben wird.
   - `LastInteractionDate`: Dieses Feld bleibt optional, solange es keine spezifische Validierung erfordert.






