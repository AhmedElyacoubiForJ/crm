Der `TestAppender` für Wiederverwendbarkeit in allen Services. Die Dokumentation behandelt auch die Verwendung des `formattedMessage`.

## Logging in Tests: Wiederverwendbare TestAppender und Log-Überprüfung

### Überblick
In dieser Dokumentation wird erklärt, wie man eine wiederverwendbare Logging-Lösung für Testfälle erstellt. Dies beinhaltet das Einrichten eines `TestAppender` für das Abfangen von Log-Nachrichten und die Überprüfung auf spezifische Log-Inhalte und -Level.

### Einrichten des TestAppender

Erstelle eine gemeinsame `TestAppender`-Klasse, die in allen Testklassen wiederverwendet werden kann. Dies ermöglicht eine einfache Überprüfung der Log-Nachrichten in den Tests.

```java
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class TestAppender extends AppenderBase<ILoggingEvent> {
    private final List<ILoggingEvent> events = new ArrayList<>();

    @Override
    protected void append(ILoggingEvent event) {
        events.add(event);
    }

    public boolean contains(String message, String level) {
        return events.stream()
                .anyMatch(event -> event.getFormattedMessage().contains(message) && event.getLevel().toString().equals(level));
    }

    @Override
    public String toString() {
        return "TestAppender{" + "events=" + events + '}';
    }
}
```

### Einrichten des Loggers in den Tests

Füge den `TestAppender` zu deinem Logger in der Setup-Methode jeder Testklasse hinzu.

```java
import org.junit.jupiter.api.BeforeEach;
import org.mockito.MockitoAnnotations;

public class EntityOrchestratorServiceImplTest {

    private static TestAppender testAppender;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        Logger logger = (Logger) LoggerFactory.getLogger(EntityOrchestratorServiceImpl.class);
        testAppender = new TestAppender();
        testAppender.start();
        logger.addAppender(testAppender);
    }

    // Deine Testmethoden
}
```

### Beispieltest für Log-Überprüfung

Hier ist ein Beispiel für eine Testmethode, die überprüft, ob bestimmte INFO- und WARN-Log-Nachrichten erzeugt werden:

```java
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class EntityOrchestratorServiceImplTest {

    @Test
    void itShouldThrowExceptionWhenIdsAreEquals_ByCallingDeleteEmployeeAndReassignCustomers() {
        // Given
        Long oldEmployeeId = 1L;
        Long newEmployeeId = 1L;

        // When
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            underTest.deleteEmployeeAndReassignCustomers(oldEmployeeId, newEmployeeId);
        });

        // Then verify the exception message
        assertEquals("Old and new employee IDs must be different", exception.getMessage());

        // Verify that the info log is triggered
        assertTrue(testAppender.contains(
                "EntityOrchestratorServiceImpl::deleteEmployeeAndReassignCustomers employeeId: 1, newEmployeeId: 1", "INFO")
        );
        
        // Verify that the warning log is triggered
        assertTrue(testAppender.contains("Old and new employee IDs must be different", "WARN"));
    }
}
```

### Wichtiger Hinweis: Verwendung von `formattedMessage`

Stelle sicher, dass du in der `contains`-Methode von `TestAppender` die `getFormattedMessage()`-Methode verwendest, um sicherzustellen, dass du die vollständige formatierte Log-Nachricht überprüfst.

```java
boolean contains(String message, String level) {
    return events.stream()
            .anyMatch(event -> event.getFormattedMessage().contains(message) && event.getLevel().toString().equals(level));
}
```

### Wiederverwendbarkeit

- **Auslagerung**: Lagere die `TestAppender`-Klasse in eine gemeinsame Utility-Bibliothek aus, die von allen deinen Service-Tests importiert werden kann.
- **Dokumentation**: Pflege eine zentrale Dokumentation, die den Entwicklern zeigt, wie sie den `TestAppender` und die Log-Überprüfung in ihren Tests verwenden können.

Mit diesen Schritten kann man eine robuste und wiederverwendbare Lösung für das Testen von Log-Nachrichten in unseren Services sicherstellen.
