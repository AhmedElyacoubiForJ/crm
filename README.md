# Customer Relationship App (CRM)

## Projektbeschreibung:
> Dieses Projekt ist eine Implementierung für ***Customer Relationship Management System***, in dem es Mitarbeiter ermöglichen,
> Kunden zu verwalten, Notizen zu Interaktionen zu hinterlegen und Kunden nach bestimmten Kriterien zu durchsuchen.
> Kunden werden Mitarbeitern zugeordnet, und jede Kundeninteraktionen wird als Notiz dokumentiert.

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
   - Eine Notiz gehört zu einer Kunde und dokumentiert eine Interaktion mit ihm.
   - Eigenschaften einer Notiz:
      - Inhalt der Notiz (content)
      - Datum der Notiz (date)
      - Typ der Deklaration (interActionType) - als Enumeration (z.B. EMAIL, PHONE_CALL, MEETING)
3. **Mitarbeiter (Employee)**:
   - Jeder Kunde wird einen Mitarbeiter zugeordnet.
   - Eigenschaften eines Mitarbeiters:
     - Vorname (firstname) und Nachname (lastName)
     - E-Mail-Adresse (email)
     - Abteilung (department)
   - Ein Mitarbeiter kann mehrere Kunden betreuen, aber jeder Kunde gehört nur zu einem Mitarbeiter.
4. **Funktionale Anforderungen**:
   - **Kundenverwaltung**: Die App soll es ermöglichen, Kunden zu erstellen, zu aktualisieren, zu löschen und anzusehen.
   - **Notizenverwaltung**: Mitarbeiter sollen Notizen zu Kunden erstellen, aktualisieren und löschen können.
   - **Suche**: Die Mitarbeiter sollen Kunden nach Name, E-Mail oder Telefonnummer durchsuchen können.
   - **Interaktionen anzeigen**: Zeige Interaktionen eines Kunden geordnet nach dem Datum an.
5. **Zukunftspläne**
   - Implementierung ... 

## Technologien
- java 17
- Spring Boot
- JPA
- H2
- JUnit für unit-tests

## Installation
1. Klone das Repository.
2. Führe `mvn clean install` aus.
3. Starte die Anwendung mit `mvn spring-boot:run`.

## Beispiel:
Um eine Notiz zu erstellen, sende eine POST-Anfrage an `/notes`.


















