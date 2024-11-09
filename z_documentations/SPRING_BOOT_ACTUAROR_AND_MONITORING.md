# Dokumentation zu Spring Boot Actuator und Monitoring

## Einführung

Spring Boot Actuator ist ein nützliches Modul, das verschiedene Funktionen zur Überwachung und Verwaltung von Anwendungen bietet. Hier sind die Hauptvorteile und Verwendungszwecke:

1. **Bereitstellung von Produktions-Ready Features**: Actuator stellt sofort einsetzbare Produktions-Ready Features wie Metriken, Health Checks und Application Insights bereit.
2. **Health Checks**: Bietet Endpunkte zur Überprüfung des Gesundheitszustands der Anwendung, wie `/health`, die Informationen über den Zustand verschiedener Komponenten der Anwendung liefern.
3. **Metriken**: Sie können Metriken wie CPU- und Speicherverbrauch, Anzahl der aktiven Threads, HTTP-Anfragen usw. überwachen. Diese Informationen sind über Endpunkte wie `/metrics` verfügbar.
4. **Auditing**: Actuator unterstützt Auditing-Funktionen, die helfen können, sicherheitsrelevante Ereignisse in der Anwendung zu verfolgen.
5. **Application Insights**: Sie erhalten detaillierte Einblicke in die intern verwendeten Beans, die Konfigurationen der Umgebungsvariablen und vieles mehr.
6. **Application Endpoints**: Actuator bietet eine Vielzahl von HTTP Endpoints wie `/info`, `/env`, `/loggers`, die zur Verwaltung und Überwachung der Anwendung verwendet werden können.
7. **Anpassbarkeit**: Sie können Actuator einfach anpassen, um genau die Informationen zu liefern, die für Ihr Anwendungsszenario relevant sind, und über verschiedene Schnittstellen wie HTTP, JMX oder sogar über E-Mail und SMS verfügbar machen.
8. **Integration**: Actuator lässt sich leicht mit Überwachungs- und Visualisierungstools wie Prometheus, Grafana und anderen integrieren, um eine ganzheitliche Sicht auf die Anwendungsleistung zu bieten.

## Implementierungen für das CRM-Projekt

### Health Check für PostgreSQL

**Custom Health Indicator für PostgreSQL-Datenbank**:

```java
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@Component
public class DatabaseHealthIndicator implements HealthIndicator {

    @Autowired
    private DataSource dataSource;

    @Override
    public Health health() {
        try (Connection connection = dataSource.getConnection()) {
            if (connection.isValid(1)) {
                return Health.up().build();
            } else {
                return Health.down().withDetail("Error", "Database is not responding").build();
            }
        } catch (SQLException e) {
            return Health.down(e).build();
        }
    }
}
```

### Simulierte Verbindung zu einem externen Dienst

**Beispiel für einen externen Dienstaufruf (Placeholder API)**:

```java
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
public class ExternalServiceHealthIndicator implements HealthIndicator {

    private final RestTemplate restTemplate;

    public ExternalServiceHealthIndicator() {
        this.restTemplate = new RestTemplate();
    }

    @Override
    public Health health() {
        String url = "https://jsonplaceholder.typicode.com/posts"; // Beispiel-URL der Placeholder API
        try {
            restTemplate.getForObject(url, String.class);
            return Health.up().build();
        } catch (Exception e) {
            return Health.down().withDetail("Error", "Cannot reach external service").build();
        }
    }
}
```

### Integration mit Prometheus und Grafana

#### Prometheus Integration

**Abhängigkeiten hinzufügen (pom.xml)**:
```xml
<dependency>
    <groupId>io.micrometer</groupId>
    <artifactId>micrometer-registry-prometheus</artifactId>
</dependency>
```

**Konfiguration (application.properties)**:
```properties
management.endpoints.web.exposure.include=*
management.metrics.export.prometheus.enabled=true
```

**Prometheus Konfiguration (prometheus.yml)**:
```yaml
global:
  scrape_interval: 15s

scrape_configs:
  - job_name: 'spring-actuator'
    metrics_path: '/actuator/prometheus'
    static_configs:
      - targets: ['localhost:8080']
```

#### Grafana Integration

1. **Grafana installieren und starten**:
    - Laden Sie Grafana von der [offiziellen Website](https://grafana.com/grafana/download) herunter und installieren Sie es.
    - Entpacken Sie die ZIP-Datei in ein Verzeichnis Ihrer Wahl.
    - Navigieren Sie zum entpackten Verzeichnis und öffnen Sie das Terminal oder die Eingabeaufforderung.
    - Navigieren Sie zum `bin`-Verzeichnis von Grafana:
        ```sh
        cd path/to/grafana/bin
        ```
    - Starten Sie den Grafana-Server:
        ```sh
        ./grafana-server web
        ```
    - Öffnen Sie das Grafana-Webinterface in Ihrem Browser unter `http://localhost:3000`.

2. **Datenquelle hinzufügen**:
    - Melden Sie sich bei Grafana an (Standardbenutzername: `admin`, Standardpasswort: `admin`).
    - Gehen Sie zu "Configuration" > "Data Sources".
    - Klicken Sie auf "Add data source" und wählen Sie "Prometheus".
    - Geben Sie die URL Ihres Prometheus-Servers an:
        ```sh
        http://localhost:9090
        ```
    - Klicken Sie auf "Save & Test", um sicherzustellen, dass die Verbindung funktioniert.

3. **Dashboards erstellen**:
    - Erstellen Sie ein neues Dashboard in Grafana:
        - Gehen Sie zu "Create" > "Dashboard".
    - Fügen Sie Panels hinzu, um verschiedene Metriken zu visualisieren:
        - Klicken Sie auf "Add new panel".
        - Wählen Sie die Prometheus-Datenquelle aus.
        - Geben Sie eine Abfrage ein, z.B. `http_server_requests_seconds_count{job="spring-actuator"}`.
        - Wählen Sie den gewünschten Visualisierungstyp aus (z.B. Graph, Bar Gauge, Table).
    - Passen Sie das Panel an und speichern Sie das Dashboard.

### Beispiel einer einfachen Abfrage in Grafana:
```promql
http_server_requests_seconds_count{status="200"}
```

### Prometheus Query Language (PromQL) Beispiele

1. **Gesamtanzahl der HTTP-Anfragen**:
   ```promql
   http_server_requests_seconds_count
   ```
2. **Anzahl der HTTP-Anfragen pro Statuscode**:
   ```promql
   http_server_requests_seconds_count{status="200"}
   ```
3. **CPU-Auslastung des Prozesses**:
   ```promql
   process_cpu_seconds_total
   ```
4. **Speichernutzung der JVM**:
   ```promql
   jvm_memory_used_bytes
   ```
5. **Heap-Speicherverbrauch der JVM**:
   ```promql
   jvm_memory_used_bytes{area="heap"}
   ```
6. **Anzahl der aktiven Threads**:
   ```promql
   jvm_threads_live
   ```
7. **Anzahl der HTTP-Anfragen pro Sekunde (Rate)**:
   ```promql
   rate(http_server_requests_seconds_count[1m])
   ```
8. **Durchschnittliche Antwortzeit der HTTP-Anfragen**:
   ```promql
   rate(http_server_requests_seconds_sum[1m]) / rate(http_server_requests_seconds_count[1m])
   ```