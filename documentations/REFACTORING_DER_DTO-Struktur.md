# Dokumentation: Refactoring der DTO-Struktur

## Inhaltsverzeichnis
1. [Übersicht](#übersicht)
2. [Vorteile der Trennung von RequestDTO und ResponseDTO](#vorteile-der-trennung-von-requestdto-und-responsedto)
3. [Geplantes Refactoring](#geplantes-Refactoring)

## Übersicht
> Der aktuelle Zustand der DTOs (Data Transfer Objects) in der CRM-RestAPI verwendet für jede Entität nur ein allgemeines DTO. Es wird empfohlen, separate DTOs für Anfragen (RequestDTO) und Antworten (ResponseDTO) zu erstellen, um die Struktur und Sicherheit der Daten besser zu verwalten.

## Vorteile der Trennung von RequestDTO und ResponseDTO
1. **Sicherheit**:
    - Nur die benötigten Daten werden vom Client gesendet und empfangen. Minimiert das Risiko unbeabsichtigter Datenübermittlung.

2. **Flexibilität**:
    - Unterschiedliche Anforderungen für Anfragedaten und Antwortdaten können besser berücksichtigt werden.

3. **Klarheit**:
    - Der Code wird klarer und verständlicher, insbesondere wenn verschiedene Eigenschaften in Anfragen und Antworten benötigt werden.

## Geplantes Refactoring
> Aktuell wird ein allgemeines DTO verwendet, beispielsweise `CustomerDTO`. Das geplante Refactoring beinhaltet die Einführung von spezifischen DTOs:

**Beispiel für CustomerRequestDTO**:
```java
    public class CustomerRequestDTO {
        @NotBlank
        private String firstName;
    
        @NotBlank
        private String lastName;
    
        @Email
        private String email;
    
        private String phone;
        private String address;
    }
```
**Beispiel für CustomerResponseDTO**:
```java

public class CustomerResponseDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String address;
    private LocalDate lastInteractionDate;
}
```