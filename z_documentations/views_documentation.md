# Dokumentation: Erstellung der Views in der CRM-Anwendung

## Inhaltsverzeichnis
1. [Einführung](#einführung)
2. [Erstellung der Mitarbeiter-View](#erstellung-der-mitarbeiter-view)
3. [Erstellung der Kunden-View](#erstellung-der-kunden-view)
4. [Erstellung der Notizen-View](#erstellung-der-notizen-view)
5. [Navigation zwischen den Views](#navigation-zwischen-den-views)
6. [Zusammenfassung](#zusammenfassung)

## Einführung
In diesem Dokument wird die Implementierung der verschiedenen Views in der CRM-Anwendung beschrieben. Jede View repräsentiert ein Template, das mit Daten gefüllt und gerendert wird. Die Views sind im Ordner `src/main/resources/templates` gespeichert und nutzen Thymeleaf zur Darstellung der Inhalte.

## Erstellung der Mitarbeiter-View
### 1. Formular zur Erstellung eines Mitarbeiters
> Die View für die Erstellung eines neuen Mitarbeiters befindet sich in der Datei `employee_form.html`.
    
```html
        <form th:action="@{/employees}" th:object="${employee}" method="post">
            <div class="form-group">
                <label for="firstName">Vorname</label>
                <input type="text" th:field="*{firstName}" class="form-control" id="firstName" placeholder="Vorname">
            </div>
            <div class="form-group">
                <label for="lastName">Nachname</label>
                <input type="text" th:field="*{lastName}" class="form-control" id="lastName" placeholder="Nachname">
            </div>
            <div class="form-group">
                <label for="email">E-Mail</label>
                <input type="email" th:field="*{email}" class="form-control" id="email" placeholder="E-Mail">
            </div>
            <div class="form-group">
                <label for="department">Abteilung</label>
                <input type="text" th:field="*{department}" class="form-control" id="department" placeholder="Abteilung">
            </div>
            <button type="submit" class="btn btn-primary">Speichern</button>
        </form>
```
### 2. Liste der Mitarbeiter
> Die Mitarbeiterliste wird in der Datei employee_list.html angezeigt. Sie stellt alle vorhandenen Mitarbeiter in einer Tabelle dar.
```html
        <table class="table table-striped">
        <thead>
            <tr>
                <th>ID</th>
                <th>Vorname</th>
                <th>Nachname</th>
                <th>E-Mail</th>
                <th>Abteilung</th>
            </tr>
        </thead>
        <tbody>
            <tr th:each="employee : ${employees}">
                <td th:text="${employee.id}"></td>
                <td th:text="${employee.firstName}"></td>
                <td th:text="${employee.lastName}"></td>
                <td th:text="${employee.email}"></td>
                <td th:text="${employee.department}"></td>
            </tr>
        </tbody>
        </table>
```

## Erstellung der Kunden-View
> Ähnlich wie die Mitarbeiter-View, bietet die Kunden-View ein Formular und eine Liste, um Kunden anzuzeigen und zu verwalten.

### 1. Formular zur Erstellung eines Kunden (`customer_form.html`)
```html
    <form th:action="@{/customers}" th:object="${customer}" method="post">
        <!-- Felder für Vorname, Nachname, E-Mail, Telefon, Adresse und Mitarbeiter -->
    </form>
```
### 2. Liste der Kunden (`customer_list.html`)
```html
    <table>
        <!-- Ähnlich wie employee_list.html -->
    </table>
```

## Erstellung der Notizen-View
> Die Notizen-View wird verwendet, um Notizen zu einem bestimmten Kunden anzuzeigen und neue Notizen hinzuzufügen.

### 1. Liste der Notizen für einen Kunden (`note_list.html`)
```html
    <h2>Notizen für <span th:text="${customer.firstName}"></span> <span th:text="${customer.lastName}"></span></h2>
    <table>
        <!-- Liste der Notizen für den Kunden -->
    </table>
```
### 2. Formular zur Erstellung einer neuen Notiz (`note_form.html`)
```html
    <form th:action="@{/customers/{customerId}/notes}" method="post">
        <!-- Felder für den Inhalt der Notiz, Datum und Interaktionstyp -->
    </form>
```

## Navigation zwischen den Views
> Jede View enthält eine Navigation, um einfach zwischen den verschiedenen Seiten zu wechseln.
```html
   <nav>
    <ul>
        <li><a th:href="@{/employees}">Mitarbeiter</a></li>
        <li><a th:href="@{/customers}">Kunden</a></li>
        <li><a th:href="@{/notes}">Notizen</a></li>
    </ul>
   </nav>
```

## Zusammenfassung
> Diese Dokumentation bietet eine einfache Anleitung zur Implementierung und Struktur der Views im CRM-Projekt. Spätere Refactorings können die Views verbessern und modularisieren, indem Thymeleaf-Fragments hinzugefügt werden.
