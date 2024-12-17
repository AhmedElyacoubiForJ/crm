package edu.yacoubi.crm.service;

import edu.yacoubi.crm.exception.ResourceNotFoundException;
import edu.yacoubi.crm.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ValidationService {

    private final EmployeeRepository employeeRepository;

    public void validateEmployeeExists(Long employeeId) {
        log.info("ValidationService::validateEmployeeExists employeeId: {}", employeeId);

        if (!employeeRepository.existsById(employeeId)) {
            log.error("ValidationService::validateEmployeeExists employeeId: {} not found", employeeId);
            throw new ResourceNotFoundException("Employee not found with ID: " + employeeId);
        }

        log.info("ValidationService::validateEmployeeExists employeeId: {} successfully validated", employeeId);
    }
}
