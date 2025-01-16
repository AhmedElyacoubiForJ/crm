package edu.yacoubi.crm.controllers.api;

import edu.yacoubi.crm.dto.APIResponse;
import edu.yacoubi.crm.dto.employee.EmployeePatchDTO;
import edu.yacoubi.crm.dto.employee.EmployeeRequestDTO;
import edu.yacoubi.crm.dto.employee.EmployeeResponseDTO;
import edu.yacoubi.crm.model.Employee;
import edu.yacoubi.crm.service.IEmployeeService;
import edu.yacoubi.crm.service.IEntityOrchestratorService;
import edu.yacoubi.crm.util.ApiResponseHelper;
import edu.yacoubi.crm.util.EntityTransformer;
import edu.yacoubi.crm.util.TransformerUtil;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

import static edu.yacoubi.crm.util.ApiResponseHelper.getDTOAPIResponse;
import static edu.yacoubi.crm.util.ApiResponseHelper.getPageAPIResponse;
import static edu.yacoubi.crm.util.ValueMapper.jsonAsString;

@RestController
@RequestMapping("/api/employees")
@RequiredArgsConstructor
@Slf4j
public class EmployeeRestController {
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
        log.info("::getAllEmployees started with: page: {}, size: {}, search: {}", page, size, search);

        Page<Employee> employeesPage;
        if (search != null && !search.isEmpty()) {
            employeesPage = employeeService.getEmployeesByFirstNameOrDepartment(search, page, size);
        } else {
            employeesPage = employeeService.getEmployeesWithPagination(page, size);
        }

        Page<EmployeeResponseDTO> employeeResponseDTOPage = employeesPage.map(
                employee -> TransformerUtil.transform(EntityTransformer.employeeToEmployeeResponseDto, employee)
        );

        APIResponse<Page<EmployeeResponseDTO>> response =
                getPageAPIResponse("Operation completed", "Success", HttpStatus.OK, employeeResponseDTOPage);

        log.info("::getAllEmployees completed successfully with: response {}", jsonAsString(response));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(
            summary = "Get employee by ID",
            description = "Retrieve an employee by their unique ID."
    )
    @GetMapping("/{employeeId}")
    public ResponseEntity<APIResponse<EmployeeResponseDTO>> getEmployeeById(@PathVariable Long employeeId) {
        log.info("::getEmployeeById started with: employeeId {}", employeeId);

        // exception werden im service behandelt und im globaler handler abgefangen
        Employee existingEmployee = employeeService.getEmployeeById(employeeId).get();

        EmployeeResponseDTO employeeResponseDTO = TransformerUtil.transform(
                EntityTransformer.employeeToEmployeeResponseDto,
                existingEmployee
        );

        APIResponse<EmployeeResponseDTO> response =
                getDTOAPIResponse("Operation completed", "Success", HttpStatus.OK, employeeResponseDTO);

        log.info("::getEmployeeById completed successfully with: response {}", jsonAsString(response));
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Create a new employee",
            description = "This operation creates a new employee in the CRM system."
    )
    @PostMapping
    public ResponseEntity<APIResponse<EmployeeResponseDTO>> createEmployee(
            @Valid @RequestBody EmployeeRequestDTO employeeRequestDTO) {
        log.info("::createEmployee started with: employeeRequestDTO {}", jsonAsString(employeeRequestDTO));

        Employee employeeRequest = TransformerUtil.transform(
                EntityTransformer.employeeRequestDtoToEmployee,
                employeeRequestDTO
        );

        Employee savedEmployee = employeeService.createEmployee(employeeRequest);

        EmployeeResponseDTO employeeResponseDTO = TransformerUtil.transform(
                EntityTransformer.employeeToEmployeeResponseDto,
                savedEmployee
        );

        APIResponse<EmployeeResponseDTO> response =
                getDTOAPIResponse("Operation completed", "Success", HttpStatus.CREATED, employeeResponseDTO);

        log.info("::createEmployee completed successfully with: response {}", jsonAsString(response));
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Update employee",
            description = "Update the details of an existing employee by their unique ID."
    )
    @PutMapping("/{employeeId}")
    public ResponseEntity<APIResponse<EmployeeResponseDTO>> updateEmployee(
            @PathVariable Long employeeId,
            @Valid @RequestBody EmployeeRequestDTO employeeRequestDTO) {
        log.info("::updateEmployee started with: employeeId {}, employeeRequestDTO {}",
                employeeId, jsonAsString(employeeRequestDTO)
        );

        // transform to entity
        Employee employeeRequest = TransformerUtil.transform(
                EntityTransformer.employeeRequestDtoToEmployee,
                employeeRequestDTO
        );

        // update
        Employee updatedEmployee = employeeService.updateEmployee(employeeId, employeeRequest);

        // transform to response DOT
        EmployeeResponseDTO employeeResponseDTO = TransformerUtil.transform(
                EntityTransformer.employeeToEmployeeResponseDto,
                updatedEmployee
        );

        APIResponse<EmployeeResponseDTO> response =
                getDTOAPIResponse("Operation completed", "Success", HttpStatus.OK, employeeResponseDTO);

        log.info("::updateEmployee completed successfully with: response {}", jsonAsString(response));
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Partial update employee",
            description = "Partial update of an existing employee by their unique ID."
    )
    @PatchMapping("/{employeeId}")
    public ResponseEntity<APIResponse<EmployeeResponseDTO>> patchEmployee(
            @PathVariable Long employeeId,
            @Valid @RequestBody EmployeePatchDTO employeePatchDTO) {
        log.info("::patchEmployee started with: employeeId {}, employeePatchDTO {}",
                employeeId, jsonAsString(employeePatchDTO));

        // partial update
        employeeService.partialUpdateEmployee(employeeId, employeePatchDTO);

        // get the updated employee
        Employee updatedEmployee = employeeService.getEmployeeById(employeeId).get();

        // transform to response DTO
        EmployeeResponseDTO employeeResponseDTO = TransformerUtil.transform(
                EntityTransformer.employeeToEmployeeResponseDto,
                updatedEmployee
        );

        APIResponse<EmployeeResponseDTO> response =
                getDTOAPIResponse("Operation completed", "Success", HttpStatus.OK, employeeResponseDTO);

        log.info("::patchEmployee completed successfully with: response {}", jsonAsString(response));
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
        log.info("::reassignAndDeleteEmployee started with: employeeId {}, newEmployeeId {}",
                employeeId,
                newEmployeeId
        );

        orchestratorService.deleteEmployeeAndReassignCustomers(employeeId, newEmployeeId);

        APIResponse<Void> response =
                getDTOAPIResponse("Employee customers reassigned and deleted successfully", "success",
                        HttpStatus.OK, null);

        log.info("::reassignAndDeleteEmployee completed successfully with: response {}", jsonAsString(response));
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Get all departments",
            description = "Get all departments in the CRM system."
    )
    @GetMapping("/departments")
    public ResponseEntity<APIResponse<List<String>>> getAllDepartments() {
        log.info("::getAllDepartments started");

        List<String> allDepartments = employeeService.getAllDepartments()
                .orElse(Collections.emptyList());

        // Verwende die generische ApiResponse-Methode
        APIResponse<List<String>> response = getDTOAPIResponse(
                "All departments retrieved successfully", "success", HttpStatus.OK, allDepartments );

        log.info("::getAllDepartments completed successfully with: response {}", jsonAsString(response));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}