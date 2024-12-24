## EntityValidatorUnitTest

> Der `EntityValidatorUnitTest` überprüft die Funktionalität der `EntityValidator`-Komponente durch isolierte Unit-Tests, um sicherzustellen, dass verschiedene Entitäten (Mitarbeiter, Notizen, Kunden) korrekt validiert werden.

### Testfälle:
1. **itShouldValidateEmployeeExists**:
    - Überprüft, ob ein existierender Mitarbeiter korrekt validiert wird.
2. **itShouldThrowWhenEmployeeDoesNotExit**:
    - Überprüft, ob eine Ausnahme geworfen wird, wenn ein nicht existierender Mitarbeiter validiert wird.
3. **itShouldValidateNoteExists**:
    - Überprüft, ob eine existierende Notiz korrekt validiert wird.
4. **itShouldThrowWhenNoteDoesNotExit**:
    - Überprüft, ob eine Ausnahme geworfen wird, wenn eine nicht existierende Notiz validiert wird.
5. **itShouldValidateCustomerExists**:
    - Überprüft, ob ein existierender Kunde korrekt validiert wird.
6. **itShouldThrowWhenCustomerDoesNotExit**:
    - Überprüft, ob eine Ausnahme geworfen wird, wenn ein nicht existierender Kunde validiert wird.

> Der Unit-Test verwendet Mock-Objekte für die Repositories und einen `TestAppender`, um die Log-Nachrichten während der Validierungen zu überprüfen.
