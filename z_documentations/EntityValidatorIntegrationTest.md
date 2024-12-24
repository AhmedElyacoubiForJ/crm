## EntityValidatorIntegrationTest

> Der `EntityValidatorIntegrationTest` überprüft die Funktionalität der `EntityValidator`-Komponente in einer realistischen Umgebung, in der verschiedene Entitäten (Mitarbeiter, Notizen, Kunden) validiert werden.

### Aufbau des Tests:
- **Testumgebung**: Verwendet `@SpringBootTest` und `@DirtiesContext` zur Isolierung der Tests.
- **Testdaten**: Verwenden von `TestDataUtil`, um realistische Testdaten für Mitarbeiter, Notizen und Kunden zu erstellen.
- **Logger**: Nutzen des `TestAppender`, um Log-Nachrichten während der Validierungen zu überprüfen.

### Testfälle:
1. **itShouldValidateEmployeeExists**:
    - Überprüft, ob ein gespeicherter Mitarbeiter korrekt validiert wird.
2. **itShouldThrowWhenEmployeeDoesNotExist**:
    - Überprüft, ob eine Ausnahme geworfen wird, wenn ein nicht existierender Mitarbeiter validiert wird.
3. **itShouldValidateNoteExists**:
    - Überprüft, ob eine gespeicherte Notiz korrekt validiert wird.
4. **itShouldThrowWhenNoteDoesNotExist**:
    - Überprüft, ob eine Ausnahme geworfen wird, wenn eine nicht existierende Notiz validiert wird.
5. **itShouldValidateCustomerExists**:
    - Überprüft, ob ein gespeicherter Kunde korrekt validiert wird.
6. **itShouldThrowWhenCustomerDoesNotExist**:
    - Überprüft, ob eine Ausnahme geworfen wird, wenn ein nicht existierender Kunde validiert wird.

