## Dokumentation: Logging Best Practices im `EmployeeRestController`

# Einleitung
> Diese Dokumentation erläutert die Implementierung von Logging im `EmployeeRestController` nach den besten Praktiken.
> Das Ziel ist es, wichtige Informationen auf dem `INFO`-Level und detaillierte Debug-Informationen auf dem `DEBUG`-Level zu protokollieren.

# Info-Level-Logs (`log.info`)
- **Zweck**: Enthalten wichtige Informationen über den normalen Betrieb der Anwendung.
- **Best Practices**: Halte die `info`-Logs kurz und prägnant, um die Performance nicht zu beeinträchtigen.

# Debug-Level-Logs (`log.debug`)
- **Zweck**: Enthalten detaillierte Informationen über die Fehlersuche und das Debugging.
- **Best Practices**: Nutze `debug`-Logs, um beim Bedarf tiefgreifende Informationen zu erhalten. Diese Logs sollten dynamisch ein- und ausgeschaltet werden, um die Systemressourcen zu schonen.

# Beispiel: `EmployeeRestController`
<details>
<summary style="color: blue"><strong>Klicke hier, um den Code anzuzeigen</strong></summary>

```Java
    @GetMapping
    public ResponseEntity<APIResponse<Page<EmployeeResponseDTO>>> getAllEmployees(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "search", required = false) String search) {

        log.info("EmployeeRestController::getAllEmployees starting to fetch employees...");
        log.debug("EmployeeRestController::getAllEmployees request received - page: {}, size: {}, search: {}", page, size, search);

        Page<Employee> employeesPage;
        if (search != null && !search.isEmpty()) {
            log.info("Retrieving employees with pagination and Search query");
            log.debug("Retrieving employees with pagination and Search query - page: {}, size: {}, search: {}", page, size, search);
            employeesPage = employeeService.getEmployeesByFirstNameOrDepartment(search, page, size);
        } else {
            log.info("Retrieving employees with pagination");
            log.debug("Retrieving employees with pagination - page: {}, size: {}, search: {}", page, size, search);
            employeesPage = employeeService.getEmployeesWithPagination(page, size);
        }

        Page<EmployeeResponseDTO> employeeResponseDTOPage = employeesPage.map(ValueMapper::convertToResponseDTO);

        APIResponse<Page<EmployeeResponseDTO>> response = APIResponse
                .<Page<EmployeeResponseDTO>>builder()
                .status("success")
                .statusCode(HttpStatus.OK.value())
                .data(employeeResponseDTOPage)
                .build();

        log.info("Response successfully created.");
        log.debug("Response details: {}", response);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
```
</details>

# Dynamisches Ein- und Ausschalten der Debug-Logs
1. Aktuelle Log-Level anzeigen:
   <details>
   <summary style="color: blue"><strong>Klicke hier, um den Code anzuzeigen</strong></summary>

   ```Bash
   curl -X GET http://localhost:8080/actuator/loggers/edu.yacoubi.crm.controllers.api.EmployeeRestController

   ```
   </details>

2. Debug-Level aktivieren:
   <details>
   <summary style="color: blue"><strong>Klicke hier, um den Code anzuzeigen</strong></summary>

   ```Bash
   curl -i -X POST -H 'Content-Type: application/json' -d '{"configuredLevel":"DEBUG"}' http://localhost:8080/actuator/loggers/edu.yacoubi.crm.controllers.api.EmployeeRestController

   ```
   </details>

3. Debug-Level deaktivieren:
<details>
   <summary style="color: blue"><strong>Klicke hier, um den Code anzuzeigen</strong></summary>

   ```Bash
   curl -i -X POST -H 'Content-Type: application/json' -d '{"configuredLevel":"INFO"}' http://localhost:8080/actuator/loggers/edu.yacoubi.crm.controllers.api.EmployeeRestController

   ```
   </details>

# Zusammenfassung
> Diese Dokumentation fasst die Best Practices für das Logging im EmployeeRestController zusammen,
> um wichtige Informationen effizient zu protokollieren und die Performance der Anwendung zu optimieren.
