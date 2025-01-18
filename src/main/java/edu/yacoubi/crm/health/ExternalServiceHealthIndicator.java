package edu.yacoubi.crm.health;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * HealthIndicator für die Überprüfung der Verfügbarkeit eines externen Services.
 *
 * <p>Diese Klasse dient als Beispiel, wie das CRM-System auf eine externe API zugreifen kann,
 * um deren Verfügbarkeit zu überprüfen. Der HealthIndicator führt eine GET-Anfrage an eine
 * Beispiel-URL aus und gibt den Gesundheitsstatus des externen Services zurück.</p>
 *
 * <p>In diesem Fall wird die JSONPlaceholder API verwendet, die Testdaten zur Verfügung stellt.</p>
 *
 * @author A. El Yacoubi
 */
@Component
public class ExternalServiceHealthIndicator implements HealthIndicator {

    private final RestTemplate restTemplate;

    public ExternalServiceHealthIndicator() {
        this.restTemplate = new RestTemplate();
    }

    @Override
    public Health health() {
        final String url = "https://jsonplaceholder.typicode.com/posts"; // Beispiel-URL der Placeholder API
        try {
            restTemplate.getForObject(url, String.class);
            return Health.up().build();
        } catch (Exception e) {
            return Health.down().withDetail("Error", "Cannot reach external service").build();
        }
    }
}
