package edu.yacoubi.crm.controllers.api.v2;

import edu.yacoubi.crm.dto.APIResponse;
import edu.yacoubi.crm.dto.employee.EmployeePatchDTO;
import edu.yacoubi.crm.dto.employee.EmployeeRequestDTO;
import edu.yacoubi.crm.dto.employee.EmployeeResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static edu.yacoubi.crm.util.EntityTransformer.jsonAsString;

/**
 * REST controller (v2) for managing employee resources in the CRM system.
 *
 * @author A. El Yacoubi
 */
@RestController
@RequestMapping("/api/v2/employees")
@RequiredArgsConstructor
@Slf4j
public class EmployeeRestControllerV2 {
    private final IEmployeeFacade employeeFacade;

    @Operation(
            summary = "Get all employees (v2)",
            description = """ 
                    Retrieve a list of all employees in the CRM system with pagination and optional search.
                    This is version 2 of the API.
                    """
    )
    @GetMapping
    public ResponseEntity<APIResponse<Page<EmployeeResponseDTO>>> getAllEmployees(
            final @RequestParam(value = "page", defaultValue = "0") int page,
            final @RequestParam(value = "size", defaultValue = "10") int size,
            final @RequestParam(value = "search", required = false) String search) {
        if (log.isInfoEnabled()) {
            log.info("::getAllEmployees started with: page: {}, size: {}, search: {}",
                    page, size, search);
        }

        final ResponseEntity<APIResponse<Page<EmployeeResponseDTO>>> response = employeeFacade
                .getAllEmployees(page, size, search);

        if (log.isInfoEnabled()) {
            log.info("::getAllEmployees completed successfully with: response {}",
                    jsonAsString(response));
        }
        return response;
    }

    /**
     * Retrieve an employee (v2) by their unique ID.
     *
     * @param employeeId the unique ID of the employee to retrieve
     * @return the employee details wrapped in an APIResponse
     */
    @Operation(
            summary = "Get employee (v2) by ID",
            description = "Retrieve an employee by their unique ID."
    )
    @GetMapping("/{employeeId}")
    public ResponseEntity<APIResponse<EmployeeResponseDTO>> getEmployeeById(
            final @PathVariable Long employeeId) {
        if (log.isInfoEnabled()) {
            log.info("::getEmployeeById started with: id: {}",
                    employeeId);
        }

        final ResponseEntity<APIResponse<EmployeeResponseDTO>> response = employeeFacade
                .getEmployeeById(employeeId);

        if (log.isInfoEnabled()) {
            log.info("::getEmployeeById completed successfully with: response {}",
                    jsonAsString(response));
        }
        return response;
    }

    // create a new employee

    /**
     * This operation creates a new employee (v2) in the CRM system.
     *
     * @param empReqDTO the employee request data transfer object containing the details of the employee to be created
     * @return the created employee details wrapped in an APIResponse
     */
    @Operation(
            summary = "Create a new employee (v2)",
            description = "This operation creates a new employee in the CRM system."
    )
    @PostMapping
    public ResponseEntity<APIResponse<EmployeeResponseDTO>> createEmployee(
            final @Valid @RequestBody EmployeeRequestDTO empReqDTO) {
        if (log.isInfoEnabled()) {
            log.info("::createEmployee started with: employeeRequestDTO {}",
                    jsonAsString(empReqDTO));
        }

        final ResponseEntity<APIResponse<EmployeeResponseDTO>> response =
                employeeFacade.createEmployee(empReqDTO);

        if (log.isInfoEnabled()) {
            log.info("::createEmployee completed successfully with: response {}",
                    jsonAsString(response));
        }
        return response;
    }

    /**
     * Update (v2) the details of an existing employee by their unique ID.
     *
     * @param employeeId the unique ID of the employee to update
     * @param empReqDTO  the employee request data transfer object containing the updated details of the employee
     * @return the updated employee details wrapped in an APIResponse
     */
    @Operation(
            summary = "Update employee (v2)",
            description = "Update the details of an existing employee by their unique ID."
    )
    @PutMapping("/{employeeId}")
    public ResponseEntity<APIResponse<EmployeeResponseDTO>> updateEmployee(
            final @PathVariable Long employeeId,
            final @Valid @RequestBody EmployeeRequestDTO empReqDTO) {
        if (log.isInfoEnabled()) {
            log.info("::updateEmployee started with: id: {}, employeeRequestDTO {}",
                    employeeId, jsonAsString(empReqDTO));
        }

        ResponseEntity<APIResponse<EmployeeResponseDTO>> response = employeeFacade
                .updateEmployee(employeeId, empReqDTO);

        if (log.isInfoEnabled()) {
            log.info("::updateEmployee completed successfully with: response {}",
                    jsonAsString(response));
        }
        return response;
    }

    /**
     * Partial update (v2) of an existing employee by their unique ID.
     *
     * @param employeeId       the unique ID of the employee to partially update
     * @param employeePatchDTO the employee patch data transfer object containing the partial update details of the employee
     * @return the updated employee details wrapped in an APIResponse
     */
    @Operation(
            summary = "Partial update employee (v2)",
            description = "Partial update of an existing employee by their unique ID."
    )
    @PatchMapping("/{employeeId}")
    public ResponseEntity<APIResponse<EmployeeResponseDTO>> patchEmployee(
            @PathVariable Long employeeId,
            @Valid @RequestBody EmployeePatchDTO employeePatchDTO) {
        if (log.isInfoEnabled()) {
            log.info("::patchEmployee started with: id: {}, dto: {}",
                    employeeId, jsonAsString(employeePatchDTO));
        }

        ResponseEntity<APIResponse<EmployeeResponseDTO>> response = employeeFacade
                .patchEmployee(employeeId, employeePatchDTO);

        if (log.isInfoEnabled()) {
            log.info("::patchEmployee completed successfully with: response {}",
                    jsonAsString(response));
        }
        return response;
    }

    /**
     * Assign a customer (v2) to a new employee and delete the old employee.
     *
     * @param employeeId    the unique ID of the employee to delete
     * @param newEmployeeId the unique ID of the new employee to whom the customers will be reassigned
     * @return a response indicating the successful reassignment and deletion
     */
    @Operation(
            summary = "Assign customer (v2) to new employee and delete old employee",
            description = "Assign a customer to an employee by their unique ID."
    )
    @DeleteMapping("/{employeeId}")
    public ResponseEntity<APIResponse<Void>> reassignAndDeleteEmployee(
            @PathVariable Long employeeId,
            @RequestParam("newEmployeeId") Long newEmployeeId) {
        if (log.isInfoEnabled()) {
            log.info("::reassignAndDeleteEmployee started with: id: {}, newEmployeeId: {}",
                    employeeId, newEmployeeId);
        }

        ResponseEntity<APIResponse<Void>> response = employeeFacade
                .reassignAndDeleteEmployee(employeeId, newEmployeeId);

        if (log.isInfoEnabled()) {
            log.info("::reassignAndDeleteEmployee completed successfully with: response {}",
                    jsonAsString(response));
        }
        return response;
    }

    /**
     * Get all departments (v2) in the CRM system.
     *
     * @return a list of all departments wrapped in an APIResponse
     */
    @Operation(
            summary = "Get all departments (v2)",
            description = "Get all departments in the CRM system."
    )
    @GetMapping("/departments")
    public ResponseEntity<APIResponse<List<String>>> getAllDepartments() {
        if (log.isInfoEnabled()) {
            log.info("::getAllDepartments started");
        }

        ResponseEntity<APIResponse<List<String>>> response = employeeFacade.getAllDepartments();

        if (log.isInfoEnabled()) {
            log.info("::getAllDepartments completed successfully with: response {}",
                    jsonAsString(response));
        }
        return response;
    }
}
