# Dokumentation: Verwendung von Spring Boot Actuator im CRM-System

## Einbindung und Konfiguration von Actuator
1. **Abhängigkeit hinzufügen**: Füge die Actuator-Abhängigkeit in der `pom.xml` hinzu:
<details>
<summary style="color: blue"><strong>Klicke hier, um den Code anzuzeigen</strong></summary>
       
```xml
<dependency>
<groupId>org.springframework.boot</groupId>
<artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
```
</details>

2. **Konfiguration in `application.properties`**
<details>
<summary style="color: blue"><strong>Klicke hier, um den Code anzuzeigen</strong></summary>

```Properties
management.endpoints.web.exposure.include=loggers
management.endpoint.health.show-details=always
```
</details>

## Nutzung der Actuator-Endpunkte
1. **Gesundheit-Endpunkt**: Überwache den Gesundheitszustand der Anwendung:
<details>
<summary style="color: blue"><strong>Klicke hier, um den Code anzuzeigen</strong></summary>

```Bash
curl -X GET http://localhost:8080/actuator/health
```
</details>

2. **Metriken-Endpunkt**: Greife auf verschiedene Metriken zu, um die Leistung der Anwendung zu überwachen:
<details>
<summary style="color: blue"><strong>Klicke hier, um den Code anzuzeigen</strong></summary>
       
```Bash
curl -X GET http://localhost:8080/actuator/metrics
```
</details>

3. **Log-Level-Endpunkte**: Überwache und ändere die Log-Level dynamisch:
- **Alle Logger anzeigen:**:
<details>
<summary style="color: blue"><strong>Klicke hier, um den Code anzuzeigen</strong></summary>
       
```Bash
curl -X GET http://localhost:8080/actuator/loggers
```
</details>

- **Spezifischen Logger anzeigen:**:
<details>
<summary style="color: blue"><strong>Klicke hier, um den Code anzuzeigen</strong></summary>

```Bash
curl -X GET http://localhost:8080/actuator/loggers/edu.yacoubi.crm.controllers.api.EmployeeRestController
```
</details>

- **Log-Level eines spezifischen Loggers ändern**:
<details>
<summary style="color: blue"><strong>Klicke hier, um den Code anzuzeigen</strong></summary>

```Bash
curl -i -X POST -H 'Content-Type: application/json' -d '{"configuredLevel":"DEBUG"}' http://localhost:8080/actuator/loggers/edu.yacoubi.crm.controllers.api.EmployeeRestController

```
</details>

## Implementierung von Debug-Logs im `EmployeeRestController`
- Erweitere den `EmployeeRestController` mit Debug-Anweisungen:
<details>
<summary style="color: blue"><strong>Klicke hier, um den Code anzuzeigen</strong></summary>

```Java
package edu.yacoubi.crm.controllers.api;

import edu.yacoubi.crm.service.EmployeeService;
import edu.yacoubi.crm.model.Employee;
import edu.yacoubi.crm.dto.EmployeeResponseDTO;
import edu.yacoubi.crm.util.APIResponse;
import edu.yacoubi.crm.util.ValueMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/employees")
public class EmployeeRestController {

    private static final Logger log = LoggerFactory.getLogger(EmployeeRestController.class);

    @Autowired
    private EmployeeService employeeService;

    @GetMapping
    public ResponseEntity<APIResponse<Page<EmployeeResponseDTO>>> getAllEmployees(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "search", required = false) String search) {

        log.debug("EmployeeRestController::getAllEmployees request received - page: {}, size: {}, search: {}", page, size, search);

        Page<Employee> employeesPage;
        if (search != null && !search.isEmpty()) {
            log.debug("Searching for employees with search term: {}", search);
            employeesPage = employeeService.getEmployeesByFirstNameOrDepartment(search, page, size);
        } else {
            log.debug("Retrieving employees with pagination - page: {}, size: {}", page, size);
            employeesPage = employeeService.getEmployeesWithPagination(page, size);
        }

        Page<EmployeeResponseDTO> employeeResponseDTOPage = employeesPage.map(ValueMapper::convertToResponseDTO);

        APIResponse<Page<EmployeeResponseDTO>> response = APIResponse
                .<Page<EmployeeResponseDTO>>builder()
                .status("success")
                .statusCode(HttpStatus.OK.value())
                .data(employeeResponseDTOPage)
                .build();

        log.debug("EmployeeRestController::getAllEmployees response: {}", response);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}


```
</details>

## Testen der dynamischen Log-Level-Steuerung
1. **Aktuelle Log-Level anzeigen**:
<details>
<summary style="color: blue"><strong>Klicke hier, um den Code anzuzeigen</strong></summary>

```Bash
curl -X GET http://localhost:8080/actuator/loggers/edu.yacoubi.crm.controllers.api.EmployeeRestController

```
</details>

2. **Log-Level auf `DEBUG` setzen**:
<details>
<summary style="color: blue"><strong>Klicke hier, um den Code anzuzeigen</strong></summary>

```Bash
curl -i -X POST -H 'Content-Type: application/json' -d '{"configuredLevel":"DEBUG"}' http://localhost:8080/actuator/loggers/edu.yacoubi.crm.controllers.api.EmployeeRestController


```
</details>

3. **Log-Level auf `INFO` zurücksetzen:**:
<details>
<summary style="color: blue"><strong>Klicke hier, um den Code anzuzeigen</strong></summary>

```Bash
curl -i -X POST -H 'Content-Type: application/json' -d '{"configuredLevel":"INFO"}' http://localhost:8080/actuator/loggers/edu.yacoubi.crm.controllers.api.EmployeeRestController

```
</details>

> Mit dieser Dokumentation sollte man in der Lage sein, Spring Boot Actuator zu integrieren,
> den EmployeeRestController mit Debug-Logs zu erweitern und die Log-Level dynamisch während der Laufzeit zu steuern.