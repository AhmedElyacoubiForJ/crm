# Erweiterung des CRM-Projekts

## Ziel

> Das Ziel dieser Erweiterung besteht darin, Benutzerverwaltung und Auditing in das bestehende CRM-Projekt zu integrieren,
> ohne den bestehenden Code zu verändern oder nur minimal anzupassen.
> Diese Anforderung kommt vom Auftraggeber und soll sicherstellen, dass die bestehende Funktionalität nicht beeinträchtigt wird.

## Anforderungen

1. **Bestehender Code soll nicht verändert werden**: Die Erweiterung muss so implementiert werden, dass der bestehende Code unberührt bleibt oder nur minimal angepasst wird.
2. **Benutzerverwaltung**: Implementierung eines Systems zur Verwaltung von Benutzern und deren Zugriffsrechten.
3. **Auditing**: Einführung eines Auditing-Mechanismus, der Benutzeraktionen und Änderungen in der Anwendung protokolliert.

## Mögliche Ansätze und Überlegungen

### Benutzerverwaltung

#### Ansatz: Externe Benutzerdatenbank
- **Externe Benutzerdatenbank**: Nutzen Sie eine separate Datenbank oder einen externen Dienst zur Verwaltung der Benutzer, ohne den bestehenden Code zu verändern.
- **JWT (JSON Web Token)**: Implementieren Sie JWT für die Authentifizierung und Autorisierung, um sicherzustellen, dass Benutzer sicher auf die Anwendung zugreifen können.
- **Externe API**: Erstellen Sie eine externe API zur Verwaltung der Benutzer, die unabhängig von Ihrer Hauptanwendung arbeitet.

### Auditing

#### Ansatz: Aspektorientierte Programmierung (AOP)
- **AOP (Aspect-Oriented Programming)**: Nutzen Sie AOP, um Auditing-Logik einzuführen, ohne den bestehenden Code zu verändern. Mit AOP können Sie Methodenaufrufe abfangen und Auditing-Informationen protokollieren.
- **Entity Listener**: Verwenden Sie JPA Entity Listener, um Änderungen an Entitäten zu erfassen und Auditing-Informationen zu speichern.
- **Separater Audit-Service**: Erstellen Sie einen separaten Audit-Service, der über Nachrichtenwarteschlangen (z.B. RabbitMQ oder Kafka) Auditing-Ereignisse empfängt und verarbeitet.

### Beispiel für AOP-basiertes Auditing
1. **Erstellen eines Aspekts für das Auditing**: Fangen Sie Methodenaufrufe ab und protokollieren Sie Auditing-Informationen.
2. **Audit-Logik implementieren**: Speichern Sie die Auditing-Informationen in einer separaten Datenbank oder einem Protokollierungssystem.

## Zusammenfassung

> Mit diesen Ansätzen können wir unsere Anwendung erweitern, ohne den bestehenden Code wesentlich zu verändern.
> Dies entspricht den Anforderungen des Auftraggebers und stellt sicher, dass die Erweiterungen nahtlos in das bestehende System integriert werden.


