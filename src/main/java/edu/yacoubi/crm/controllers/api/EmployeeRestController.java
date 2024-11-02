package edu.yacoubi.crm.controllers.api;

import edu.yacoubi.crm.dto.APIResponse;
import edu.yacoubi.crm.dto.EmployeeRequestDTO;
import edu.yacoubi.crm.dto.EmployeeResponseDTO;
import edu.yacoubi.crm.exception.ResourceNotFoundException;
import edu.yacoubi.crm.model.Employee;
import edu.yacoubi.crm.service.IEmployeeService;
import edu.yacoubi.crm.util.ValueMapper;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
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
    public ResponseEntity<APIResponse> getAllEmployees() {
        log.info("EmployeeRestController::getAllEmployees request");

        List<EmployeeResponseDTO> employeeResponseDTOList = employeeService.getAllEmployees().stream()
                .map(ValueMapper::convertToResponseDTO)
                .collect(Collectors.toList());

        APIResponse<List<EmployeeResponseDTO>> response = APIResponse
                .<List<EmployeeResponseDTO>>builder()
                .status("success")
                .data(employeeResponseDTOList)
                .build();

        log.info("EmployeeRestController::getAllEmployees response {}", jsonAsString(response));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    @Operation(
            summary = "Get employee by ID",
            description = "Retrieve an employee by their unique ID."
    )
    @GetMapping("/{id}")
    public ResponseEntity<APIResponse<EmployeeResponseDTO>> getEmployeeById(@PathVariable Long id) {
        log.info("EmployeeRestController::getEmployeeById request id {}", id);

        Employee existingEmployee = employeeService.getEmployeeById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with ID: " + id));

        EmployeeResponseDTO employeeResponseDTO = convertToResponseDTO(existingEmployee);
        APIResponse<EmployeeResponseDTO> response = APIResponse.<EmployeeResponseDTO>builder()
                .status("success")
                .statusCode(HttpStatus.OK.value())
                .data(employeeResponseDTO)
                .build();

        log.info("EmployeeRestController::getEmployeeById response {}", jsonAsString(response));
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Create a new employee",
            description = "This operation creates a new employee in the CRM system."
    )
    @PostMapping
    public ResponseEntity<APIResponse<EmployeeResponseDTO>> createEmployee(
            @Valid @RequestBody EmployeeRequestDTO employeeRequestDTO) {
        log.info("EmployeeRestController::createEmployee request {}", jsonAsString(employeeRequestDTO));

        Employee employee = convertToEntity(employeeRequestDTO);
        Employee savedEmployee = employeeService.createEmployee(employee);

        EmployeeResponseDTO employeeResponseDTO = convertToResponseDTO(savedEmployee);
        APIResponse<EmployeeResponseDTO> response = APIResponse.<EmployeeResponseDTO>builder()
                .status("success")
                .statusCode(HttpStatus.CREATED.value())
                .data(employeeResponseDTO)
                .build();

        log.info("EmployeeRestController::createEmployee response {}", jsonAsString(response));
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Update employee",
            description = "Update the details of an existing employee by their unique ID."
    )
    @PutMapping("/{id}")
    public ResponseEntity<APIResponse<EmployeeResponseDTO>> updateEmployee(
            @PathVariable Long id,
            @Valid @RequestBody EmployeeRequestDTO employeeRequestDTO) {
        log.info("EmployeeRestController::updateEmployee request id {}, employee {}", id, jsonAsString(employeeRequestDTO));

        Employee existingEmployee = employeeService.getEmployeeById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with ID: " + id));

        Employee employeeRequest = convertToEntity(employeeRequestDTO);
        employeeRequest.setId(id);

        Employee updatedEmployee = employeeService.updateEmployee(employeeRequest);
        EmployeeResponseDTO employeeResponseDTO = convertToResponseDTO(updatedEmployee);
        APIResponse<EmployeeResponseDTO> response = APIResponse.<EmployeeResponseDTO>builder()
                .status("success")
                .statusCode(HttpStatus.OK.value())
                .data(employeeResponseDTO)
                .build();


        log.info("EmployeeRestController::updateEmployee response {}", jsonAsString(response));
        return ResponseEntity.ok(response);
    }
}
