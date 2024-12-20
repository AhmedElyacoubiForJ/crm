# Customer Relationship App (CRM)

## Projektbeschreibung:
Dieses Projekt ist eine Implementierung eines **Customer Relationship Management Systems** (CRM), das Mitarbeitern ermöglicht, Kunden zu verwalten, Notizen zu Interaktionen zu hinterlegen und Kunden nach bestimmten Kriterien zu durchsuchen. Kunden werden Mitarbeitern zugeordnet, und jede Kundeninteraktion wird als Notiz dokumentiert. Dieses Projekt wird mit Spring Boot, Spring Data JPA und React entwickelt.

## Details:
1. **Kunde (Customer)**:
    - Ein Kunde hat die folgenden Eigenschaften:
        - Vorname (firstname) und Nachname (lastname)
        - E-Mail-Adresse (email)
        - Telefonnummer (phone)
        - Adresse (address)
        - Datum der letzten Interaktion (lastInteractionDate)
    - Jeder Kunde kann mehrere **Notizen** zu Interaktionen haben.

2. **Notizen (Note)**:
    - Eine Notiz gehört zu einem Kunden und dokumentiert eine Interaktion mit ihm.
    - Eigenschaften einer Notiz:
        - Inhalt der Notiz (content)
        - Datum der Notiz (date)
        - Typ der Interaktion (interactionType) - als Enumeration (z.B. EMAIL, PHONE_CALL, MEETING)

3. **Mitarbeiter (Employee)**:
    - Jeder Kunde wird einem Mitarbeiter zugeordnet.
    - Eigenschaften eines Mitarbeiters:
        - Vorname (firstname) und Nachname (lastname)
        - E-Mail-Adresse (email)
        - Abteilung (department)
    - Ein Mitarbeiter kann mehrere Kunden betreuen, aber jeder Kunde gehört nur zu einem Mitarbeiter.

4. **Funktionale Anforderungen**:
    - **Kundenverwaltung**: Die App soll es ermöglichen, Kunden zu erstellen, zu aktualisieren, zu löschen und anzusehen.
    - **Notizen verwaltung**: Mitarbeiter sollen Notizen zu Kunden erstellen, aktualisieren und löschen können.
    - **Suche**: Die Mitarbeiter sollen Kunden nach Name, E-Mail oder Telefonnummer durchsuchen können.
    - **Interaktionen anzeigen**: Zeige Interaktionen eines Kunden geordnet nach dem Datum an.

5. **Zukunftspläne**:
    - Sicherheitsmechanismen wie Authentifizierung und Autorisierung
    - Integration von Analytik-Tools
    - Automatisierte E-Mail-Kampagnen

## Geplante Schritte
1. **Backend-Entwicklung**:
    - Implementierung der Geschäftslogik und Datenpersistenz mit Spring Boot und Spring Data JPA.
    - Erstellung von Unit-Tests und Integrationstests für alle wichtigen Komponenten.

2. **Einführung von DTOs**:
    - Einführung von Data Transfer Objects (DTOs) zur Entkopplung der Datenbankstruktur und der API-Schichten.
    - Beispiel:
      - `CustomerRequestDTO` - [Code ansehen](src/main/java/edu/yacoubi/crm/dto/customer/CustomerRequestDTO.java)
      - `CustomerResponseDTO` - [Code ansehen](src/main/java/edu/yacoubi/crm/dto/customer/CustomerResponseDTO.java)
      - `CustomerPatchDTO` - [Code ansehen](src/main/java/edu/yacoubi/crm/dto/customer/CustomerPatchDTO.java)
    - Implementierung der Mapper basierend auf [ValueMapper](src/main/java/edu/yacoubi/crm/util/ValueMapper.java)

3. **Globaler Exception-Handler**:
    - Definition eines globalen Exception-Handlers zur zentralen Fehlerbehandlung.

4. **GUI-Entwicklung mit Spring MVC**:
    - Erstellung grundlegender Views zur Verwaltung von Kunden und Notizen.
    - Bereitstellung erster funktionaler Oberflächen für Testzwecke.
    - Weitere Informationen zur Erstellung der Views findest du [hier](z_documentations/views_documentation.md).

5. **RESTful APIs**:
    - Implementierung von REST-APIs, um den Zugriff von externen Clients zu ermöglichen.
    - Sicherstellen, dass alle CRUD-Operationen über die APIs verfügbar sind.

6. **React-Client**:
    - Entwicklung einer modernen Single-Page Application mit React.
    - Integration der REST-APIs zur Datenmanipulation und Anzeige.
    - Das Frontend-Repository für dieses Projekt findest du hier: [CRM React Frontend](https://github.com/AhmedElyacoubiForJ/crm-react)

7. **Dokumentation**:
    - Regelmäßige Aktualisierung der README.md-Datei, um den Fortschritt und die nächsten Schritte zu dokumentieren.

8. **Projektstruktur**
    - **controllers**: Enthält Controller-Klassen für die Spring MVC Views und APIs.
    - **model**: Definiert die JPA-Entitäten
    - **repository**: Enthält die Schnittstellen für den Datenzugriff.
    - **service**: Implementiert die geschäftslogik.
    - **templates**: Enthält HTML-Templates für die Spring MVC Views.
    - **test**: Beinhaltet die Unit-Tests und Integrationstests für das Projekt.

9. **Verzeichnisstruktur**
    - **src/main/java**: Enthält den Anwendungsquellcode.
    - **src/main/resources**: Beinhaltet Ressourcen wie `application.properties` und HTML-Templates.
    - **src/test/java**: Beinhaltet die Testklassen.

## Aktueller Stand
- Backend-Entwicklung ist abgeschlossen.
- Erste GUI-Entwicklung mit Spring MVC in Arbeit.
- REST-APIs und React-Client folgen als nächste Schritte.

## Technologien
- Java 17
- Spring Boot
- JPA
- H2 (Development) / PostgreSQL (Production)
- JUnit für Unit-Tests

## Installation
1. Klone das Repository.
2. Führe `mvn clean install` aus.
3. Starte die Anwendung mit `mvn spring-boot:run`.

## Profile Konfiguration
Diese Anwendung verwendet verschiedene Profile für die Test- und Produktionsumgebung.
- **Testprofil**: Verwendet eine in-memory H2-Datenbank im PostgreSQL-Modus.
- **Produktionsprofil**: Verwendet eine PostgreSQL-Datenbank.

### Testumgebung
Die Konfigurationsdatei für die Testumgebung ist `application-test.properties`.
```properties
# application-test.properties
spring.application.name=crm
spring.datasource.url=jdbc:h2:mem:crm;MODE=PostgreSQL
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
spring.h2.console.enabled=true
```

### Produktionsumgebung
Die Konfigurationsdatei für die Produktionsumgebung ist application-prod.properties.
```properties
# application-prod.properties
spring.application.name=crm
spring.datasource.url=jdbc:postgresql://crmdb
spring.datasource.driverClassName=org.postgresql.Driver
spring.datasource.username=${DB_USER_NAME}
spring.datasource.password=${DB_PASSWORD}
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
spring.h2.console.enabled=false
```

### Aktivieren der Profile
- Stelle sicher, dass du die Profile in deiner application.properties Datei aktivierst.
```properties
# application.properties
spring.profiles.active=test # or prod
```

### Verwendung der Profile
- Du kannst die Profile über die Kommandozeile oder deine IDE aktivieren.
```sh
./mvnw test

```
- IDE (IntelliJ IDEA):
   - Gehe zu Run > Edit Configurations.
   - Wähle deine Spring Boot Konfiguration aus.
   - Füge im Feld Active profiles test hinzu.

## [API Testing-Dokumentation](z_documentations/TESTING.md)

## Struktur des Projektes

```plaintext
src/
└── main/
    ├── java/
    │   └── edu.yacoubi.crm/
    │       ├── config/
    │       │   ├── CorsConfig.java
    │       │   ├── OpenApiConfig.java
    │       │   └── SwaggerConfig.java
    │       ├── controllers/
    │       │   ├── api/
    │       │   │   ├── CustomerRestController.java
    │       │   │   ├── EmployeeRestController.java
    │       │   │   ├── NoteRestController.java
    │       │   │   └── StatusRestController.java
    │       │   └── toviews/
    │       │       ├── CustomerViewController.java
    │       │       ├── EmployeeViewController.java
    │       │       ├── HomeController.java
    │       │       └── NoteController.java
    │       ├── dto/
    │       │   ├── customer/
    │       │   │   ├── CustomerPatchDTO.java
    │       │   │   ├── CustomerRequestDTO.java
    │       │   │   └── CustomerResponseDTO.java
    │       │   ├── employee/
    │       │   │   ├── EmployeePatchDTO.java
    │       │   │   ├── EmployeeRequestDTO.java
    │       │   │   └── EmployeeResponseDTO.java
    │       │   ├── note/
    │       │   │   ├── NotePatchDTO.java
    │       │   │   ├── NoteRequestDTO.java
    │       │   │   └── NoteResponseDTO.java
    │       │   ├── APIResponse.java
    │       │   └── ValidationError.java
    │       ├── exception/
    │       │   ├── GlobalExceptionHandler.java
    │       │   └── ResourceNotFoundException.java
    │       ├── health/
    │       │   ├── DatabaseHealtIndicator.java
    │       │   └── ExternalServcieHealthIndicator.java
    │       ├── mapper/
    │       │   └── IMapper.java
    │       ├── modell/
    │       │   ├── Customer.java
    │       │   ├── Employee.java
    │       │   ├── InactiveEmployee.java
    │       │   ├── InterationType.java
    │       │   └── Note.java
    │       ├── repository/
    │       │   ├── CustomCustomerRepository.java
    │       │   ├── CustomCustomerRepositoryImpl.java
    │       │   ├── CustomerRepository.java
    │       │   ├── EmployeeRepository.java
    │       │   ├── InactiveEmployeeRepository.java
    │       │   └── NoteRepository.java
    │       ├── scenarios/
    │       │   └── CRMScenarioRunner.java
    │       ├── security/
    │       │   ├── config/
    │       │   │   └── SecurityConfig.java
    │       │   ├── dto/
    │       │   │   └── UserRequestDTO
    │       │   ├── model/
    │       │   │   ├── Role.java
    │       │   │   ├── User.java
    │       │   │   ├── UserCustomer.java
    │       │   │   ├── UserCustomerKey.java
    │       │   │   ├── UserEmployee.java
    │       │   │   └── UserEmployeeKey.java  
    │       │   ├── repository/
    │       │   │   ├── RoleRepository.java
    │       │   │   ├── UserCutomerRepository.java
    │       │   │   ├── UserEmployeeRepository.java
    │       │   │   └── UserRepository.java
    │       │   └── service/
    │       │       └── UserService.java
    │       ├── service/
    │       │   ├── impl/
    │       │   │   ├── CustomerServiceImpl.java
    │       │   │   ├── EmployeeServiceImpl.java
    │       │   │   ├── EntityOrchestratorServiceImpl.java
    │       │   │   ├── InactiveEmployeeServiceImpl.java
    │       │   │   └── NoteServiceImpl.java
    │       │   ├── ICustomerService.java
    │       │   ├── IEmployeeService.java
    │       │   ├── IEntityOrchestratorService.java
    │       │   ├── IInactiveEmployeeService.java
    │       │   ├── INoteServcie.java
    │       │   └── ValidationService.java
    │       ├── util/
    │       │   └── ValueMapper.java  
    │       └── Main.java
    ├── resources/
    │   ├── static/
    │   │   ├── css/
    │   │   │   └── styles.css
    │   │   └── favicon.ico
    │   ├── templates/
    │   │   ├── customer/
    │   │   │   ├── customer-form.html
    │   │   │   └── customer-list.html
    │   │   ├── employee/
    │   │   │   ├── employee-form.html
    │   │   │   └── employee-list.html
    │   │   ├── note/
    │   │   │   ├── note-form.html
    │   │   │   └── note-list.html
    │   │   └── home.html
    │   ├── application.properties
    │   ├── application-prod.properties
    │   ├── application-test.properties
    │   ├── banner.txt
    │   ├── data.sql
    │   └── schema.sql
    └── test/
        └── java/
            └── edu.yacoubi.crm/
                ├── controllers.api/
                │   └── CustomerRestControllerTest.java
                ├── repository/
                │   ├── CustomerRepositoryIntegrationTest.java
                │   ├── CustomerRepositoryUnitTest.java
                │   ├── EmployeeRepositoryUnitTest.java
                │   └── NoteRepositoryIntegrationTest.java
                ├── service.impl/
                │   ├── CustomerServiceImplIntegrationTest.java
                │   ├── CustomerServiceImplUnit.java
                │   ├── EmployeeServiceImplIntegrationTest.java
                │   ├── EmployeeServiceImplUnitTest.java
                │   ├── EntityOrchestratorImplUnitTest.java
                │   ├── EntityOrchestratorImplIntegrationTest.java
                │   ├── NoteServiceImplIntegrationTest.java
                │   └── NoteServcieImplUnitTest.java
                ├── util/
                │   └── ValueMapperTest.java
                ├── MainTests.java
                ├── TestAppender.java
                └── TestDataUtil.java
z_documentationy/
├── ACTUATOR_IN_CRM.md
├── api_documentation.md
├── APIResponse_Documentation.md
├── customerDTO.json
├── DELETE_EMPLOYEE_AND_REASSIGN_CUSTOMERS_UNIT_TEST.mg
├── Delete_Employee_Impl.md
├── DETAILED_DOCUMENTATION.md
├── ENTITY_ORCHESTRATOR_SERVICE_DESIGN.md
├── Example_And_CriteriaAPI_DOC.md
├── Hosting_Plattform.md
├── LOGGING_BEST_PRACTICES.md
├── NAMENSAUNDERUNG_DES_ORCHESTRATOR_SERVICES.md
├── PAGED_MODEL_MIT_HATEOAS.md
├── TEST_APPENDER_LOG_NACHRICHTEN.md
├── TESTING.md
├── Ueberlegungen_Mitarbeiter_Loeschen.md
├── ValueMapper_unit_testes.md
└── views_documentation.md
.gitattributes
.gitignore
AUTHENTICATION_ERWEITERUNG.md
AUTHENTICATION_TABELLEN.md
Exception_Workflow_Doc.md
HELP.md
PROJEKT_STRUKTUR_SECURITY.md
SCENARIEN_CHECK_LOESUNGSANSATZ.md
TESTS-md
UEBERBLICK_DES_LOESUNGSANSATZES.md
LAZY_LOADING.md
pom.xml
README.md
```

## TODOs
- JPA Entities spezifizieren: DONE
- Repositories: DONE
- Services: DONE
- DTOs: Done
- DTOs Mapper: DONE
- Repositories Unit-Tests: Done
- Repositories Integrationstestes: Done
- Services Unit-Tests: Done
- Services IntegrationsTests: Done
- RestApi Endpoints-Implementations: DONE
- RestApi Swagger-Documentations: DONE (könnte verbessert werden)
- RestApi Unit-,IntegrationsTests: TODO
- Spring MVC views Implementations: DONE (teilweise verbesserung notwendig)

