package edu.yacoubi.crm.health;

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

