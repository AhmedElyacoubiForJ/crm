Warum wir diese Refaktoring-Maßnahmen durchgeführt haben:

## Dokumentation: Änderungen und Verbesserungen

### Grund für das Refactoring:
> Der Hauptgrund für dieses Refactoring war die Verbesserung der **Separation of Concerns** (Trennung der Zuständigkeiten). Dieser Grundsatz ist ein wichtiges Prinzip in der Softwareentwicklung, das darauf abzielt, den Code modularer, verständlicher und leichter wartbar zu gestalten. Durch die klare Trennung der Verantwortlichkeiten in verschiedene Komponenten können wir sicherstellen, dass jede Klasse und Methode eine spezifische Aufgabe hat. Dies führt zu besser wartbarem und verständlicherem Code, reduziert die Fehleranfälligkeit und erhöht die Wiederverwendbarkeit der Logik.

### 1. **Orchestrator und Separation of Concerns**
#### Hinzugefügte Klassen:
- **INoteOrchestrator**: Neue Schnittstelle für den Orchestrator.
- **NoteOrchestrator**: Implementierung der neuen Orchestrator-Klasse.

#### Orchestrator-Schnittstelle:
```java
public interface INoteOrchestrator {
    Note createNoteForCustomer(Note note, Long customerId);
}
```

#### Implementierung des Orchestrators:
```java
@Service
@RequiredArgsConstructor
@Slf4j
public class NoteOrchestrator implements INoteOrchestrator {

    private final EntityValidator entityValidator;
    private final ICustomerService customerService;
    private final INoteService noteService;

    @Override
    public Note createNoteForCustomer(Note note, Long customerId) {
        log.info("NoteOrchestrator::createNoteForCustomer execution start: note {}, customerId {}", note, customerId);

        // Validierung des Kunden
        entityValidator.validateCustomerExists(customerId);

        // Abruf des Kunden ohne Ausnahme
        Customer customer = customerService.getCustomerById(customerId).get();

        // Setzen des Kunden und Delegation an NoteService
        note.setCustomer(customer);
        Note savedNote = noteService.createNote(note);

        log.info("NoteOrchestrator::createNoteForCustomer execution end");
        return savedNote;
    }
}
```

### 2. **Anpassung der `NoteServiceImpl`**
#### Umbenennung der Methode:
- Umbenennung von `createNoteForCustomer` zu `createNote`.
- Hinzufügen einer Überprüfung, dass der Kunde nicht `null` ist.

#### NoteServiceImpl:
```java
@Service
@RequiredArgsConstructor
@Slf4j
public class NoteServiceImpl implements INoteService {

    private final NoteRepository noteRepository;

    @Override
    public Note createNote(Note note) {
        log.info("NoteServiceImpl::createNote execution start: note {}", note);

        if (note.getCustomer() == null) {
            throw new IllegalArgumentException("Customer must not be null");
        }

        Note savedNote = noteRepository.save(note);

        log.info("NoteServiceImpl::createNote execution end");
        return savedNote;
    }

    // Weitere Methoden bleiben unverändert...
}
```

### 3. **Validation-Component**
#### EntityValidator:
- Validierungskomponente zur Überprüfung, ob Entitäten wie Mitarbeiter, Notizen und Kunden existieren.

#### EntityValidator:
```java
@Component
@RequiredArgsConstructor
@Slf4j
public class EntityValidator {

    private final EmployeeRepository employeeRepository;
    private final NoteRepository noteRepository;
    private final CustomerRepository customerRepository;

    public void validateEmployeeExists(Long employeeId) {
        log.info("EntityValidatorService::validateEmployeeExists employeeId: {}", employeeId);

        if (!employeeRepository.existsById(employeeId)) {
            log.error("EntityValidatorService::validateEmployeeExists employeeId: {} not found", employeeId);
            throw new ResourceNotFoundException("Employee not found with ID: " + employeeId);
        }

        log.info("EntityValidatorService::validateEmployeeExists employeeId: {} successfully validated", employeeId);
    }

    public void validateNoteExists(Long id) {
        log.info("EntityValidatorService::validateNoteExists id: {}", id);

        if (!noteRepository.existsById(id)) {
            log.error("EntityValidatorService::validateNoteExists id: {} not found", id);
            throw new ResourceNotFoundException("Note not found with ID: " + id);
        }

        log.info("EntityValidatorService::validateNoteExists id: {} successfully validated", id);
    }

    public void validateCustomerExists(Long id) {
        log.info("EntityValidatorService::validateCustomerExists id: {}", id);

        if (!customerRepository.existsById(id)) {
            log.error("EntityValidatorService::validateCustomerExists id: {} not found", id);
            throw new ResourceNotFoundException("Customer not found with ID: " + id);
        }

        log.info("EntityValidatorService::validateCustomerExists id: {} successfully validated", id);
    }
}
```

### 4. **Unit Tests Anpassungen**
#### Aktualisierung der Unit Tests, um die neuen Validierungen und die Orchestrator-Logik zu berücksichtigen.

#### Beispiel eines angepassten Unit Tests:
```java
@Test
public void itShouldCreateNoteForCustomer() {
    // Given
    Long customerId = 1L;
    Note note = TestDataUtil.createNoteA(null);
    Customer customer = TestDataUtil.createCustomerA(null);
    when(customerService.getCustomerById(customerId)).thenReturn(Optional.of(customer));
    when(noteRepository.save(any(Note.class))).thenReturn(note);

    // When
    Note savedNote = underTest.createNoteForCustomer(note, customerId);

    // Then
    assertNotNull(savedNote);
    assertEquals(customer, savedNote.getCustomer());
    verify(noteRepository, times(1)).save(note);
}
```

### Zusammenfassung:
> Diese Änderungen und Dokumentation bieten einen klaren Überblick über die Implementierung und Verbesserungen hinsichtlich der Separation of Concerns.
> Wir haben die Architektur und Wartbarkeit des Codes erheblich verbessert, indem wir die Verantwortlichkeiten klar getrennt und die Validierungslogik zentralisiert haben.
> Dies führt zu modularerem, verständlicherem und leichter wartbarem Code.