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

## TODOs
- JPA Entities spezifizieren: DONE
- Repositories: DONE
- Services: DONE
- DTOs: Done
- DTOs Mapper: DONE
- Repositories Unit-Tests: Done (to check)
- Repositories Integrationstestes: Done (to check)
- Services Init-Tests: Done (to check)
- Services IntegrationsTests: Done (to check)
- RestApi Endpoints-Implementations: DONE
- RestApi Swagger-Documentations: DONE (könnte verbessert werden)
- RestApi IntegrationsTests: TODO
- Spring MVC views Implementations: DONE (teilweise verbesserung notwendig)
