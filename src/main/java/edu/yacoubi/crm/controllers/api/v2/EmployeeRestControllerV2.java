package edu.yacoubi.crm.controllers.api.v2;

import edu.yacoubi.crm.dto.APIResponse;
import edu.yacoubi.crm.dto.employee.EmployeePatchDTO;
import edu.yacoubi.crm.dto.employee.EmployeeResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static edu.yacoubi.crm.util.EntityTransformer.jsonAsString;

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

        ResponseEntity<APIResponse<Page<EmployeeResponseDTO>>> response = employeeFacade
                .getAllEmployees(page, size, search);

        if (log.isInfoEnabled()) {
            log.info("::getAllEmployees completed successfully with: response {}", jsonAsString(response));
        }
        return response;
    }

    @GetMapping("/{id}")
    public ResponseEntity<APIResponse<EmployeeResponseDTO>> getEmployeeById(
            @PathVariable("id") Long employeeId) {
        if (log.isInfoEnabled()) {
            log.info("::getEmployeeById started with: id: {}", employeeId);
        }

        ResponseEntity<APIResponse<EmployeeResponseDTO>> response = employeeFacade
                .getEmployeeById(employeeId);

        if (log.isInfoEnabled()) {
            log.info("::getEmployeeById completed successfully with: response {}", jsonAsString(response));
        }
        return response;
    }

    @PatchMapping("/{id}")
    public ResponseEntity<APIResponse<EmployeeResponseDTO>> patchEmployee(
            @PathVariable("id") Long employeeId,
            @Valid @RequestBody EmployeePatchDTO employeePatchDTO) {
        if (log.isInfoEnabled()) {
            log.info("::patchEmployee started with: id: {}, dto: {}",
                    employeeId, jsonAsString(employeePatchDTO));
        }

        ResponseEntity<APIResponse<EmployeeResponseDTO>> response = employeeFacade
                .patchEmployee(employeeId, employeePatchDTO);

        if (log.isInfoEnabled()) {
            log.info("::patchEmployee completed successfully with: response {}", jsonAsString(response));
        }
        return response;
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<APIResponse<Void>> reassignAndDeleteEmployee(
            @PathVariable("id") Long employeeId,
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
}
