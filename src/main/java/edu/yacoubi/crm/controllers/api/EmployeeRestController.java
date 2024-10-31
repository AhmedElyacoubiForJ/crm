package edu.yacoubi.crm.controllers.api;

import edu.yacoubi.crm.dto.EmployeeRequestDTO;
import edu.yacoubi.crm.dto.EmployeeResponseDTO;
import edu.yacoubi.crm.exception.ResourceNotFoundException;
import edu.yacoubi.crm.model.Employee;
import edu.yacoubi.crm.service.IEmployeeService;
import edu.yacoubi.crm.util.ValueMapper;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static edu.yacoubi.crm.util.ValueMapper.*;

@RestController
@RequestMapping("/api/employees")
@RequiredArgsConstructor
@Slf4j
public class EmployeeRestController {
    private final IEmployeeService employeeService;

    @Operation(
            summary = "Get all employees",
            description = "Retrieve a list of all employees in the CRM system."
    )
    @GetMapping
    public List<EmployeeResponseDTO> getAllEmployees() {
        log.info("EmployeeRestController::getAllEmployees request");
        return employeeService.getAllEmployees().stream()
                .map(ValueMapper::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    @Operation(
            summary = "Get employee by ID",
            description = "Retrieve an employee by their unique ID."
    )
    @GetMapping("/{id}")
    public ResponseEntity<EmployeeResponseDTO> getEmployeeById(@PathVariable Long id) {
        log.info("EmployeeRestController::getEmployeeById request id {}", id);
        Employee existingEmployee = employeeService.getEmployeeById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with ID: " + id));
        EmployeeResponseDTO employeeResponseDTO = convertToResponseDTO(existingEmployee);

        log.info("EmployeeRestController::getEmployeeById response {}", jsonAsString(employeeResponseDTO));
        return ResponseEntity.ok(employeeResponseDTO);
    }

    @Operation(
            summary = "Create a new employee",
            description = "This operation creates a new employee in the CRM system."
    )
    @PostMapping
    public ResponseEntity<EmployeeResponseDTO> createEmployee(@RequestBody EmployeeRequestDTO employeeRequestDTO) {
        log.info("EmployeeRestController::createEmployee request {}", jsonAsString(employeeRequestDTO));

        Employee employee = convertToEntity(employeeRequestDTO);
        Employee savedEmployee = employeeService.createEmployee(employee);
        EmployeeResponseDTO employeeResponseDTO = convertToResponseDTO(savedEmployee);

        log.info("EmployeeRestController::createEmployee response {}", jsonAsString(employeeResponseDTO));
        return ResponseEntity.ok(employeeResponseDTO);
    }

    @Operation(
            summary = "Update employee",
            description = "Update the details of an existing employee by their unique ID."
    )
    @PutMapping("/{id}")
    public ResponseEntity<EmployeeResponseDTO> updateEmployee(
            @PathVariable Long id,
            @RequestBody EmployeeRequestDTO employeeRequestDTO) {
        log.info("EmployeeRestController::updateEmployee request id {}, employee {}", id, jsonAsString(employeeRequestDTO));
        Employee existingEmployee = employeeService.getEmployeeById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with ID: " + id));
        Employee employeeRequest = convertToEntity(employeeRequestDTO);
        employeeRequest.setId(id);

        Employee updatedEmployee = employeeService.updateEmployee(employeeRequest);
        EmployeeResponseDTO employeeResponseDTO = convertToResponseDTO(updatedEmployee);

        log.info("EmployeeRestController::updateEmployee response {}", jsonAsString(employeeResponseDTO));
        return ResponseEntity.ok(employeeResponseDTO);
    }
}
