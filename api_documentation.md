# Dokumentation: API-Dokumentation mit OpenAPI und Swagger UI

## Inhaltsverzeichnis

1. [Übersicht](#übersicht)
2. [Abhängigkeiten](#abhängigkeiten)
3. [Konfiguration](#konfiguration)
4. [Anpassung](#anpassung)
5. [API-Endpunkte](#API-Endpunkte)

# Übersicht
> Wir haben Springdoc OpenAPI in unser CRM-Projekt integriert, um umfassende API-Dokumentation
> und eine visuelle Benutzeroberfläche mit Swagger UI bereitzustellen.

# Abhängigkeiten
> Die folgende Abhängigkeit wurde der `pom.xml` hinzugefügt:
```Xml
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
    <version>2.5.0</version>
</dependency>
```

# Konfiguration
> Aktualisierte `application.properties`:
```Properties
# Swagger UI Pfad
springdoc.swagger-ui.path=/crm-api-ui.html

# Sortierung der Endpunkte
springdoc.swagger-ui.operationsSorter=method
springdoc.swagger-ui.tagsSorter=alpha

# API-Versionierung aktivieren
springdoc.api-docs.versioning.enabled=true

# Anzeigeeinstellungen der Dokumentation
springdoc.swagger-ui.docExpansion=none
springdoc.swagger-ui.defaultModelsExpandDepth=-1

# Pfad der API-Dokumentation
springdoc.api-docs.path=/crm-api-docs
```

# Anpassung
> Um sicherzustellen, dass die Swagger UI über einen benutzerdefinierten Pfad (`/crm-api-ui.html`) zugänglich ist,
> haben wir eine Konfigurationsklasse hinzugefügt:
```Java
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class SwaggerConfig implements WebMvcConfigurer {

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/crm-api-ui.html").setViewName("forward:/swagger-ui/index.html");
    }
}
```

# API-Endpunkte
> Controller-Methoden wurden mit `@Operation` annotiert, um eine bessere API-Dokumentation zu gewährleisten.
### CustomerRestController
```Java
@Operation(summary = "Get all customers", description = "Retrieve a list of all customers in the CRM system.")
@GetMapping
public List<CustomerDTO> getAllCustomers() {
    // Implementation
}

@Operation(summary = "Get customer by ID", description = "Retrieve a customer by their unique ID.")
@GetMapping("/{id}")
public ResponseEntity<CustomerDTO> getCustomerById(@PathVariable Long id) {
    // Implementation
}

@Operation(summary = "Create a new customer", description = "This operation creates a new customer in the CRM system.")
@PostMapping
public ResponseEntity<CustomerDTO> createCustomer(@RequestParam Long employeeId, @RequestBody CustomerDTO customerRequestDTO) {
    // Implementation
}

@Operation(summary = "Update customer", description = "Update the details of an existing customer by their unique ID.")
@PutMapping("/{id}")
public ResponseEntity<CustomerDTO> updateCustomer(@PathVariable Long id, @RequestBody CustomerDTO customerDTO) {
    // Implementation
}

@Operation(summary = "Delete customer", description = "Delete an existing customer by their unique ID.")
@DeleteMapping("/{id}")
public ResponseEntity<Void> deleteCustomer(@PathVariable Long id) {
    // Implementation
}
```
