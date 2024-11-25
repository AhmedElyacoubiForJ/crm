### Überlegungen zum Löschen von Mitarbeitern

#### Gründe für das Löschen:
1. **Datenbereinigung**: Um die Datenbank sauber und übersichtlich zu halten, könnten ehemalige oder doppelte Einträge gelöscht werden.
2. **Datenschutz**: Gesetzliche Vorgaben wie die DSGVO (Datenschutz-Grundverordnung) können das Löschen von Daten ehemaliger Mitarbeiter nach einem bestimmten Zeitraum vorschreiben.
3. **Aktualität**: Entfernen von veralteten Informationen hilft dabei, nur aktuelle und relevante Daten im System zu behalten.

#### Gründe gegen das Löschen:
1. **Historische Daten**: Ehemalige Mitarbeiter könnten in historischen Berichten, Projekten oder Interaktionen von Bedeutung sein. Das Löschen könnte den Verlust wichtiger Kontextinformationen bedeuten.
2. **Rechtliche Anforderungen**: Einige Informationen müssen möglicherweise für eine bestimmte Zeit aufbewahrt werden, um gesetzlichen oder organisatorischen Anforderungen gerecht zu werden.
3. **Vermeidung von Datenverlust**: Das dauerhafte Löschen könnte ungewollten Datenverlust bedeuten, was zukünftige Analysen und Berichte beeinträchtigen könnte.

### Entscheidung und Lösungsansätze

**Entscheidung**: Statt Mitarbeiterdaten vollständig zu löschen, wird der Mitarbeiter deaktiviert, um die Datenintegrität und historische Kontexte zu bewahren.

**Lösungsansatz**:
1. **Deaktivierung von Mitarbeitern**: Anstatt Mitarbeiter zu löschen, werden sie deaktiviert. Dies kann über ein Flag in der JPA-Entity (`active: boolean`) oder eine spezielle Tabelle für inaktive Mitarbeiter erfolgen.
2. **Zuweisung von Kunden**: Bevor ein Mitarbeiter deaktiviert wird, müssen seine zugewiesenen Kunden auf einen anderen aktiven Mitarbeiter umverteilt werden, um sicherzustellen, dass keine Kunden mit inaktiven Mitarbeitern verbunden bleiben.
3. **Spezieller Use Case**: Implementierung eines speziellen Use Cases, der prüft, ob Kunden eines zu deaktivierenden Mitarbeiters vorhanden sind und eine Umverteilung der Kunden ermöglicht.

### Überlegungen zur Implementierung

- **Flag in JPA-Entity**: Ein einfacher Ansatz wäre die Einführung eines `active` Flags in der `Employee` Entität. Dies erfordert Anpassungen in den Abfragen und eventuell in der Geschäftslogik, um nur aktive Mitarbeiter standardmäßig anzuzeigen.

- **Spezielle Tabelle für inaktive Mitarbeiter**: Eine separate Tabelle kann helfen, aktive und inaktive Mitarbeiter klar zu trennen, jedoch erfordert dies zusätzliche Logik und Migration bestehender Daten.

**Implementierungsansatz mit Flag**:
1. **JPA-Entity Anpassung**:
    ```java
    @Entity
    public class Employee {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        private String firstName;
        private String lastName;
        private String email;
        private String department;
        private boolean active; // Flag für Aktiv/Inaktiv

        // Getter und Setter
    }
    ```

2. **Abfragen anpassen**:
    ```java
    @Query("SELECT e FROM Employee e WHERE e.active = true")
    List<Employee> findAllActiveEmployees();
    ```

3. **Service und Business Logik**:
    - Beim Deaktivieren eines Mitarbeiters:
        - Kunden neu zuweisen.
        - Mitarbeiter deaktivieren (`active` Flag auf `false` setzen).

**Implementierungsansatz mit separater Tabelle**:
1. **Neue Tabelle für inaktive Mitarbeiter**:
    ```java
    @Entity
    public class InactiveEmployee {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        private String firstName;
        private String lastName;
        private String email;
        private String department;
        private Long originalEmployeeId;

        // Getter und Setter
    }
    ```

2. **Mitarbeiter umziehen**:
    - Kunden neu zuweisen.
    - Daten des Mitarbeiters in die `InactiveEmployee` Tabelle kopieren und aus der `Employee` Tabelle löschen.