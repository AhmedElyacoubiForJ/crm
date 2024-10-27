package edu.yacoubi.crm.controllers.api;

import edu.yacoubi.crm.dto.EmployeeRequestDTO;
import edu.yacoubi.crm.dto.EmployeeResponseDTO;
import edu.yacoubi.crm.exception.ResourceNotFoundException;
import edu.yacoubi.crm.mapper.IMapper;
import edu.yacoubi.crm.model.Employee;
import edu.yacoubi.crm.service.IEmployeeService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/employees")
@RequiredArgsConstructor
public class EmployeeRestController {
    private final IEmployeeService employeeService;
    private final IMapper<Employee, EmployeeRequestDTO> employeeRequestMapper;
    private final IMapper<Employee, EmployeeResponseDTO> employeeResponseMapper;

    @Operation(summary = "Get all employees", description = "Retrieve a list of all employees in the CRM system.")
    @GetMapping
    public List<EmployeeResponseDTO> getAllEmployees() {
        return employeeService.getAllEmployees().stream()
                .map(employeeResponseMapper::mapTo)
                .collect(Collectors.toList());
    }

    @Operation(summary = "Get employee by ID", description = "Retrieve an employee by their unique ID.")
    @GetMapping("/{id}")
    public ResponseEntity<EmployeeResponseDTO> getEmployeeById(@PathVariable Long id) {
        Employee employee = employeeService.getEmployeeById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with ID: " + id));
        EmployeeResponseDTO employeeResponseDTO = employeeResponseMapper.mapTo(employee);
        return ResponseEntity.ok(employeeResponseDTO);
    }

    @Operation(summary = "Create a new employee", description = "This operation creates a new employee in the CRM system.")
    @PostMapping
    public ResponseEntity<EmployeeResponseDTO> createEmployee(@RequestBody EmployeeRequestDTO employeeRequestDTO) {
        Employee employee = employeeRequestMapper.mapFrom(employeeRequestDTO);
        Employee savedEmployee = employeeService.createEmployee(employee);
        EmployeeResponseDTO savedEmployeeDTO = employeeResponseMapper.mapTo(savedEmployee);
        return ResponseEntity.ok(savedEmployeeDTO);
    }

    @Operation(summary = "Update employee", description = "Update the details of an existing employee by their unique ID.")
    @PutMapping("/{id}")
    public ResponseEntity<EmployeeResponseDTO> updateEmployee(@PathVariable Long id, @RequestBody EmployeeRequestDTO employeeRequestDTO) {
        Employee existingEmployee = employeeService.getEmployeeById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with ID: " + id));
        Employee employeeRequest = employeeRequestMapper.mapFrom(employeeRequestDTO);
        employeeRequest.setId(id);
        Employee updatedEmployee = employeeService.updateEmployee(employeeRequest);
        EmployeeResponseDTO updatedEmployeeDTO = employeeResponseMapper.mapTo(updatedEmployee);
        return ResponseEntity.ok(updatedEmployeeDTO);
    }
}
