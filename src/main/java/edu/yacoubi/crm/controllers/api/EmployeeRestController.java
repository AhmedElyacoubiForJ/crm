package edu.yacoubi.crm.controllers.api;

import edu.yacoubi.crm.dto.APIResponse;
import edu.yacoubi.crm.dto.employee.EmployeePatchDTO;
import edu.yacoubi.crm.dto.employee.EmployeeRequestDTO;
import edu.yacoubi.crm.dto.employee.EmployeeResponseDTO;
import edu.yacoubi.crm.model.Employee;
import edu.yacoubi.crm.service.IEmployeeService;
import edu.yacoubi.crm.service.IEntityOrchestratorService;
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
import static edu.yacoubi.crm.util.EntityTransformer.jsonAsString;

/**
 * REST controller for managing employee resources in the CRM system.
 *
 * @author A. El Yacoubi
 */
@RestController
@RequestMapping("/api/employees")
@RequiredArgsConstructor
@Slf4j
public class EmployeeRestController {

    /**
     * Success message.
     */
    public static final String SUCCESS = "Success";

    /**
     * Completion message for a successful operation.
     */
    public static final String COMPLETED = "Operation completed";

    /**
     * Service for employee operations.
     */
    private final IEmployeeService employeeService;

    /**
     * Orchestrator service for deleting and reassigning customers.
     */
    private final IEntityOrchestratorService orchestratorSvc;

    /**
     * Retrieve a list of all employees in the CRM system with pagination and optional search.
     *
     * @param page   the page number to retrieve, default is 0
     * @param size   the size of the page to retrieve, default is 10
     * @param search an optional search parameter to filter employees by first name or department
     * @return a paginated list of all employees wrapped in an APIResponse
     */
    @Operation(
            summary = "Get all employees",
            description = "Retrieve a list of all employees in the CRM system with pagination and optional search."
    )
    @GetMapping
    public ResponseEntity<APIResponse<Page<EmployeeResponseDTO>>> getAllEmployees(
            final @RequestParam(value = "page", defaultValue = "0") int page,
            final @RequestParam(value = "size", defaultValue = "10") int size,
            final @RequestParam(value = "search", required = false) String search) {
        if (log.isInfoEnabled()) {
            log.info("::getAllEmployees started with: page: {}, size: {}, search: {}", page, size, search);
        }

        final Page<Employee> employeesPage = getEmployeePage(page, size, search);

        final Page<EmployeeResponseDTO> empRespDTO = employeesPage.map(
                employee -> TransformerUtil.transform(EntityTransformer.employeeToEmployeeResponseDto, employee)
        );

        final APIResponse<Page<EmployeeResponseDTO>> response =
                getPageAPIResponse(COMPLETED, SUCCESS, HttpStatus.OK, empRespDTO);

        if (log.isInfoEnabled()) {
            log.info("::getAllEmployees completed successfully with: response {}", jsonAsString(response));
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Retrieve an employee by their unique ID.
     *
     * @param employeeId the unique ID of the employee to retrieve
     * @return the employee details wrapped in an APIResponse
     */
    @Operation(
            summary = "Get employee by ID",
            description = "Retrieve an employee by their unique ID."
    )
    @GetMapping("/{employeeId}")
    public ResponseEntity<APIResponse<EmployeeResponseDTO>> getEmployeeById(
            final @PathVariable Long employeeId) {
        if (log.isInfoEnabled()) {
            log.info("::getEmployeeById started with: employeeId {}", employeeId);
        }

        // Exception handling is done in the service and caught by the global handler
        final Employee existingEmployee = employeeService.getEmployeeById(employeeId).get();

        final EmployeeResponseDTO empResDTO = TransformerUtil.transform(
                EntityTransformer.employeeToEmployeeResponseDto,
                existingEmployee
        );

        final APIResponse<EmployeeResponseDTO> response =
                getDTOAPIResponse(COMPLETED, SUCCESS, HttpStatus.OK, empResDTO);

        if (log.isInfoEnabled()) {
            log.info("::getEmployeeById completed successfully with: response {}", jsonAsString(response));
        }
        return ResponseEntity.ok(response);
    }

    /**
     * This operation creates a new employee in the CRM system.
     *
     * @param empReqDTO the employee request data transfer object containing the details of the employee to be created
     * @return the created employee details wrapped in an APIResponse
     */
    @Operation(
            summary = "Create a new employee",
            description = "This operation creates a new employee in the CRM system."
    )
    @PostMapping
    public ResponseEntity<APIResponse<EmployeeResponseDTO>> createEmployee(
            final @Valid @RequestBody EmployeeRequestDTO empReqDTO) {
        if (log.isInfoEnabled()) {
            log.info("::createEmployee started with: employeeRequestDTO {}", jsonAsString(empReqDTO));
        }

        final Employee employeeRequest = TransformerUtil.transform(
                EntityTransformer.employeeRequestDtoToEmployee,
                empReqDTO
        );

        final Employee savedEmployee = employeeService.createEmployee(employeeRequest);

        final EmployeeResponseDTO empRespDTO = TransformerUtil.transform(
                EntityTransformer.employeeToEmployeeResponseDto,
                savedEmployee
        );

        final APIResponse<EmployeeResponseDTO> response =
                getDTOAPIResponse(COMPLETED, SUCCESS, HttpStatus.CREATED, empRespDTO);

        if (log.isInfoEnabled()) {
            log.info("::createEmployee completed successfully with: response {}", jsonAsString(response));
        }
        return ResponseEntity.ok(response);
    }

    /**
     * Update the details of an existing employee by their unique ID.
     *
     * @param employeeId the unique ID of the employee to update
     * @param empReqDTO  the employee request data transfer object containing the updated details of the employee
     * @return the updated employee details wrapped in an APIResponse
     */
    @Operation(
            summary = "Update employee",
            description = "Update the details of an existing employee by their unique ID."
    )
    @PutMapping("/{employeeId}")
    public ResponseEntity<APIResponse<EmployeeResponseDTO>> updateEmployee(
            final @PathVariable Long employeeId,
            final @Valid @RequestBody EmployeeRequestDTO empReqDTO) {
        if (log.isInfoEnabled()) {
            log.info("::updateEmployee started with: employeeId {}, employeeRequestDTO {}",
                    employeeId, jsonAsString(empReqDTO)
            );
        }

        final Employee employeeRequest = TransformerUtil.transform(
                EntityTransformer.employeeRequestDtoToEmployee,
                empReqDTO
        );

        final Employee updatedEmployee = employeeService.updateEmployee(employeeId, employeeRequest);

        final EmployeeResponseDTO empRespDTO = TransformerUtil.transform(
                EntityTransformer.employeeToEmployeeResponseDto,
                updatedEmployee
        );

        final APIResponse<EmployeeResponseDTO> response =
                getDTOAPIResponse(COMPLETED, SUCCESS, HttpStatus.OK, empRespDTO);

        if (log.isInfoEnabled()) {
            log.info("::updateEmployee completed successfully with: response {}", jsonAsString(response));
        }
        return ResponseEntity.ok(response);
    }

    /**
     * Partial update of an existing employee by their unique ID.
     *
     * @param employeeId       the unique ID of the employee to partially update
     * @param employeePatchDTO the employee patch data transfer object containing the partial update details of the employee
     * @return the updated employee details wrapped in an APIResponse
     */
    @Operation(
            summary = "Partial update employee",
            description = "Partial update of an existing employee by their unique ID."
    )
    @PatchMapping("/{employeeId}")
    public ResponseEntity<APIResponse<EmployeeResponseDTO>> patchEmployee(
            final @PathVariable Long employeeId,
            final @Valid @RequestBody EmployeePatchDTO employeePatchDTO) {
        if (log.isInfoEnabled()) {
            log.info("::patchEmployee started with: employeeId {}, employeePatchDTO {}",
                    employeeId, jsonAsString(employeePatchDTO));
        }

        employeeService.partialUpdateEmployee(employeeId, employeePatchDTO);

        final Employee updatedEmployee = employeeService.getEmployeeById(employeeId).get();

        final EmployeeResponseDTO empRespDTO = TransformerUtil.transform(
                EntityTransformer.employeeToEmployeeResponseDto,
                updatedEmployee
        );

        final APIResponse<EmployeeResponseDTO> response =
                getDTOAPIResponse(COMPLETED, SUCCESS, HttpStatus.OK, empRespDTO);

        if (log.isInfoEnabled()) {
            log.info("::patchEmployee completed successfully with: response {}", jsonAsString(response));
        }
        return ResponseEntity.ok(response);
    }

    /**
     * Assign a customer to a new employee and delete the old employee.
     *
     * @param employeeId    the unique ID of the employee to delete
     * @param newEmployeeId the unique ID of the new employee to whom the customers will be reassigned
     * @return a response indicating the successful reassignment and deletion
     */
    @Operation(
            summary = "Assign customer to new employee and delete old employee",
            description = "Assign a customer to an employee by their unique ID."
    )
    @DeleteMapping("/{employeeId}/reassignAndDelete")
    public ResponseEntity<APIResponse<Void>> reassignAndDeleteEmployee(
            final @PathVariable Long employeeId,
            final @RequestParam Long newEmployeeId) {
        if (log.isInfoEnabled()) {
            log.info("::reassignAndDeleteEmployee started with: employeeId {}, newEmployeeId {}",
                    employeeId,
                    newEmployeeId
            );
        }

        orchestratorSvc.deleteEmployeeAndReassignCustomers(employeeId, newEmployeeId);

        final APIResponse<Void> response =
                getDTOAPIResponse("Employee customers reassigned and deleted successfully", "success",
                        HttpStatus.OK, null);

        if (log.isInfoEnabled()) {
            log.info("::reassignAndDeleteEmployee completed successfully with: response {}", jsonAsString(response));
        }
        return ResponseEntity.ok(response);
    }

    /**
     * Get all departments in the CRM system.
     *
     * @return a list of all departments wrapped in an APIResponse
     */
    @Operation(
            summary = "Get all departments",
            description = "Get all departments in the CRM system."
    )
    @GetMapping("/departments")
    public ResponseEntity<APIResponse<List<String>>> getAllDepartments() {
        if (log.isInfoEnabled()) {
            log.info("::getAllDepartments started");
        }

        final List<String> allDepartments = employeeService.getAllDepartments()
                .orElse(Collections.emptyList());

        final APIResponse<List<String>> response = getDTOAPIResponse(
                "All departments retrieved successfully", "success", HttpStatus.OK, allDepartments);

        if (log.isInfoEnabled()) {
            log.info("::getAllDepartments completed successfully with: response {}", jsonAsString(response));
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Retrieves a page of employees, optionally filtered by a search term.
     *
     * @param page   the page number to retrieve
     * @param size   the size of the page to retrieve
     * @param search an optional search parameter to filter employees by first name or department
     * @return a page of employees matching the search criteria
     */
    private Page<Employee> getEmployeePage(
            final int page, final int size, final String search) {
        return (search != null && !search.isEmpty())
                ? employeeService.getEmployeesByFirstNameOrDepartment(search, page, size)
                : employeeService.getEmployeesWithPagination(page, size);
    }
}