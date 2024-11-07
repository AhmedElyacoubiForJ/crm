package edu.yacoubi.crm.controllers.api;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class StatusRestController {
    @GetMapping("/status")
    public ResponseEntity<String> getStatus() {
        return new ResponseEntity<>("Application is running", HttpStatus.OK);
    }

    @GetMapping("/health") public ResponseEntity<?> getHealth() {
        // Beispiel für eine benutzerdefinierte Gesundheitsprüfung
        return new ResponseEntity<>(Map.of("status", "UP"), HttpStatus.OK);
    }
}
