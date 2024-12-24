package edu.yacoubi.crm.controllers.api;

import edu.yacoubi.crm.dto.APIResponse;
import edu.yacoubi.crm.dto.employee.EmployeePatchDTO;
import edu.yacoubi.crm.dto.employee.EmployeeRequestDTO;
import edu.yacoubi.crm.dto.employee.EmployeeResponseDTO;
import edu.yacoubi.crm.exception.ResourceNotFoundException;
import edu.yacoubi.crm.model.Employee;
import edu.yacoubi.crm.service.IEmployeeService;
import edu.yacoubi.crm.service.IEntityOrchestratorService;
import edu.yacoubi.crm.util.ValueMapper;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

import static edu.yacoubi.crm.util.ValueMapper.*;

@RestController
@RequestMapping("/api/employees")
@RequiredArgsConstructor
//@Slf4j
public class EmployeeRestController {
    private static final Logger log = LoggerFactory.getLogger(EmployeeRestController.class);

    private final IEmployeeService employeeService;
    private final IEntityOrchestratorService orchestratorService;

    @Operation(
            summary = "Get all employees",
            description = "Retrieve a list of all employees in the CRM system with pagination and optional search."
    )
    @GetMapping
    public ResponseEntity<APIResponse<Page<EmployeeResponseDTO>>> getAllEmployees(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "search", required = false) String search) {

        log.info("EmployeeRestController::getAllEmployees starting to fetch employees...");
        log.debug("EmployeeRestController::getAllEmployees request received - page: {}, size: {}, search: {}", page, size, search);

        Page<Employee> employeesPage;
        if (search != null && !search.isEmpty()) {
            log.info("Retrieving employees with pagination and Search query");
            log.debug("Retrieving employees with pagination and Search query - page: {}, size: {}, search: {}", page, size, search);
            employeesPage = employeeService.getEmployeesByFirstNameOrDepartment(search, page, size);
        } else {
            log.info("Retrieving employees with pagination");
            log.debug("Retrieving employees with pagination - page: {}, size: {}, search: {}", page, size, search);
            employeesPage = employeeService.getEmployeesWithPagination(page, size);
        }

        Page<EmployeeResponseDTO> employeeResponseDTOPage = employeesPage.map(ValueMapper::convertToResponseDTO);

        APIResponse<Page<EmployeeResponseDTO>> response = APIResponse
                .<Page<EmployeeResponseDTO>>builder()
                .status("success")
                .statusCode(HttpStatus.OK.value())
                .data(employeeResponseDTOPage)
                .build();

        log.info("Response successfully created.");
        log.debug("Response details: {}", jsonAsString(response));
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
        // wird im service gesetzt
        //employeeRequest.setId(id);

        Employee updatedEmployee = employeeService.updateEmployee(id, employeeRequest);
        EmployeeResponseDTO employeeResponseDTO = convertToResponseDTO(updatedEmployee);
        APIResponse<EmployeeResponseDTO> response = APIResponse.<EmployeeResponseDTO>builder()
                .status("success")
                .statusCode(HttpStatus.OK.value())
                .data(employeeResponseDTO)
                .build();

        log.info("EmployeeRestController::updateEmployee response {}", jsonAsString(response));
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Partial update employee",
            description = "Partial update of an existing employee by their unique ID."
    )
    @PatchMapping("/{id}")
    public ResponseEntity<APIResponse<EmployeeResponseDTO>> patchEmployee(
            @PathVariable Long id,
            @Valid @RequestBody EmployeePatchDTO employeePatchDTO) {
        log.info("EmployeeRestController::patchEmployee request id {}, employee {}", id, jsonAsString(employeePatchDTO));

        employeeService.partialUpdateEmployee(id, employeePatchDTO);
        Employee updatedEmployee = employeeService.getEmployeeById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with ID: " + id));

        EmployeeResponseDTO employeeResponseDTO = convertToResponseDTO(updatedEmployee);
        APIResponse<EmployeeResponseDTO> response = APIResponse.<EmployeeResponseDTO>builder()
                .status("success")
                .statusCode(HttpStatus.OK.value())
                .data(employeeResponseDTO)
                .build();

        log.info("EmployeeRestController::patchEmployee response {}", jsonAsString(response));
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Assign customer to new employee and delete old employee",
            description = "Assign a customer to an employee by their unique ID."
    )
    @DeleteMapping("/{employeeId}/reassignAndDelete")
    public ResponseEntity<APIResponse<Void>> reassignAndDeleteEmployee(
            @PathVariable Long employeeId,
            @RequestParam Long newEmployeeId) {
        log.info(
                "EmployeeRestController::reassignAndDeleteEmployee request employeeId {}, newEmployeeId {}",
                employeeId,
                newEmployeeId
        );

        orchestratorService.deleteEmployeeAndReassignCustomers(employeeId, newEmployeeId);
        //employeeService.deleteEmployee(employeeId, newEmployeeId);

        APIResponse<Void> response = APIResponse.<Void>builder()
                .status("success")
                .statusCode(HttpStatus.OK.value())
                .message("Employee customers reassigned and deleted successfully.")
                .build();

        log.info("EmployeeRestController::reassignAndDeleteEmployee response {}", jsonAsString(response));
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Get all departments",
            description = "Get all departments in the CRM system."
    )
    @GetMapping("/departments")
    public ResponseEntity<APIResponse<List<String>>> getAllDepartments() {
        log.info("EmployeeRestController::getAllDepartments request");

        Optional<List<String>> allDepartments = employeeService.getAllDepartments();

        APIResponse<List<String>> response = APIResponse
                .<List<String>>builder()
                .status("success")
                .statusCode(HttpStatus.OK.value())
                .data(allDepartments.get())
                .build();

        log.info("EmployeeRestController::getAllDepartments response {}", jsonAsString(response));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}