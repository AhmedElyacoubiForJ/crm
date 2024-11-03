## Dokumentation: Partielle Updates für Employee

# Übersicht
> Diese Dokumentation beschreibt zwei Ansätze zur Durchführung partieller Updates an der `Employee`-Ressource in diesem Projekt.
> Beide Ansätze ermöglichen es, nur ausgewählte Felder eines bestehenden Datensatzes zu aktualisieren.

# Ansatz 1: Partielles Update mit `EmployeePatchDTO`
1. **Beschreibung**
    > Dieser Ansatz verwendet ein spezielles DTO (`EmployeePatchDTO`), um nur die Felder zu übermitteln, die aktualisiert werden sollen.
    > Dies ist besonders nützlich, wenn nur ein oder zwei Felder geändert werden müssen.

2. **Implementierung**
- DTO: EmployeePatchDTO
    <details>
    <summary style="color: blue"><strong>Klicke hier, um den Code anzuzeigen</strong></summary>
    
    ```java
    import jakarta.validation.constraints.Email;
    import lombok.Builder;
    import lombok.Data;
    
    @Data
    @Builder
    public class EmployeePatchDTO {
        private String firstName;
        private String lastName;
    
        @Email(message = "Email should be valid")
        private String email;
    
        private String department;
    }
    ```
    </details>

- Controller-Methode
    <details>
    <summary style="color: blue"><strong>Klicke hier, um den Code anzuzeigen</strong></summary>
    
    ```java
    @PatchMapping("/{id}")
    public ResponseEntity<APIResponse<EmployeeResponseDTO>> patchEmployee(
            @PathVariable Long id,
            @RequestBody EmployeePatchDTO employeePatchDTO) {
        log.info("EmployeeRestController::patchEmployee request id {}, employee {}", id, jsonAsString(employeePatchDTO));
    
        Employee existingEmployee = employeeService.getEmployeeById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with ID: " + id));
        
        if (employeePatchDTO.getFirstName() != null) {
            existingEmployee.setFirstName(employeePatchDTO.getFirstName());
        }
        if (employeePatchDTO.getLastName() != null) {
            existingEmployee.setLastName(employeePatchDTO.getLastName());
        }
        if (employeePatchDTO.getEmail() != null) {
            existingEmployee.setEmail(employeePatchDTO.getEmail());
        }
        if (employeePatchDTO.getDepartment() != null) {
            existingEmployee.setDepartment(employeePatchDTO.getDepartment());
        }
    
        Employee updatedEmployee = employeeService.updateEmployee(existingEmployee);
        EmployeeResponseDTO employeeResponseDTO = convertToResponseDTO(updatedEmployee);
        APIResponse<EmployeeResponseDTO> response = APIResponse.<EmployeeResponseDTO>builder()
                .status("success")
                .statusCode(HttpStatus.OK.value())
                .data(employeeResponseDTO)
                .build();
    
        log.info("EmployeeRestController::patchEmployee response {}", jsonAsString(response));
        return ResponseEntity.ok(response);
    }
    ```
    </details>

# Ansatz 2: Partielles Update mit Criteria API
1. **Beschreibung**
    > Dieser Ansatz verwendet die Criteria API, um dynamische Abfragen zu erstellen und die Felder programmgesteuert zu aktualisieren.
    > Dies bietet eine flexible und leistungsstarke Möglichkeit, partielle Updates durchzuführen.
 
2. **Implementierung**
    - Service: EmployeeServiceImpl
        <details>
        <summary style="color: blue"><strong>Klicke hier, um den Code anzuzeigen</strong></summary>
        
        ```java
        import jakarta.persistence.EntityManager;
        import jakarta.persistence.criteria.CriteriaBuilder;
        import jakarta.persistence.criteria.CriteriaUpdate;
        import jakarta.persistence.criteria.Root;
        import lombok.RequiredArgsConstructor;
        import org.springframework.stereotype.Service;
        import org.springframework.transaction.annotation.Transactional;

        @Service
        @RequiredArgsConstructor
        public class EmployeeServiceImpl implements IEmployeeService {
            private final EmployeeRepository employeeRepository;
            private final EntityManager entityManager;

            @Override
            @Transactional
            public void partialUpdateEmployee(Long id, EmployeePatchDTO employeePatchDTO) {
                CriteriaBuilder cb = entityManager.getCriteriaBuilder();
                CriteriaUpdate<Employee> update = cb.createCriteriaUpdate(Employee.class);
                Root<Employee> root = update.from(Employee.class);

                if (employeePatchDTO.getFirstName() != null) {
                    update.set(root.get("firstName"), employeePatchDTO.getFirstName());
                }
                if (employeePatchDTO.getLastName() != null) {
                    update.set(root.get("lastName"), employeePatchDTO.getLastName());
                }
                if (employeePatchDTO.getEmail() != null) {
                    update.set(root.get("email"), employeePatchDTO.getEmail());
                }
                if (employeePatchDTO.getDepartment() != null) {
                    update.set(root.get("department"), employeePatchDTO.getDepartment());
                }

                update.where(cb.equal(root.get("id"), id));
                entityManager.createQuery(update).executeUpdate();
            }
        }
        ```
        </details>
    - Controller-Methode
        <details>
        <summary style="color: blue"><strong>Klicke hier, um den Code anzuzeigen</strong></summary>
        
        ```java
        @PatchMapping("/{id}")
        public ResponseEntity<APIResponse<EmployeeResponseDTO>> patchEmployee(
                @PathVariable Long id,
                @RequestBody EmployeePatchDTO employeePatchDTO) {
            log.info("EmployeeRestController::patchEmployee request id {}, employee {}", id, jsonAsString(employeePatchDTO));

            employeeService.partialUpdateEmployee(id, employeePatchDTO);
            
            Employee updatedEmployee = employeeService.getEmployeeById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Employee not found with ID: " + id));

            EmployeeResponseDTO employeeResponseDTO = convertToResponseDTO(updatedEmployee);
            APIResponse<EmployeeResponseDTO> response = APIResponse.<EmployeeResponseDTO>builder()
                    .status("success")
                    .statusCode(HttpStatus.OK.value())
                    .data(employeeResponseDTO)
                    .build();

            log.info("EmployeeRestController::patchEmployee response {}", jsonAsString(response));
            return ResponseEntity.ok(response);
        }
        ```   
        </details>
