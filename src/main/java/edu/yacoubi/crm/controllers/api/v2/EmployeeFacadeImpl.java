package edu.yacoubi.crm.controllers.api.v2;

import edu.yacoubi.crm.dto.APIResponse;
import edu.yacoubi.crm.dto.employee.EmployeePatchDTO;
import edu.yacoubi.crm.dto.employee.EmployeeRequestDTO;
import edu.yacoubi.crm.dto.employee.EmployeeResponseDTO;
import edu.yacoubi.crm.model.Employee;
import edu.yacoubi.crm.service.IEmployeeService;
import edu.yacoubi.crm.service.IEntityOrchestratorService;
import edu.yacoubi.crm.util.EntityTransformer;
import edu.yacoubi.crm.util.TransformerUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

import static edu.yacoubi.crm.util.ApiResponseHelper.getDTOAPIResponse;
import static edu.yacoubi.crm.util.ApiResponseHelper.getPageAPIResponse;
import static edu.yacoubi.crm.util.EntityTransformer.jsonAsString;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmployeeFacadeImpl implements IEmployeeFacade {
    public static final String SUCCESS = "Success";
    public static final String COMPLETED = "Operation completed";

    private final IEmployeeService employeeService;
    private final IEntityOrchestratorService orchestratorService;


    @Override
    public ResponseEntity<APIResponse<Page<EmployeeResponseDTO>>> getAllEmployees(
            final int page, final int size, final String search) {
        if (log.isInfoEnabled()) {
            log.info("::getAllEmployees started with: page: {}, size: {}, search: {}",
                    page, size, search);
        }

        final Page<Employee> employeesPage = getEmployeePage(page, size, search);

        final Page<EmployeeResponseDTO> empRespDTO = employeesPage.map(
                employee -> TransformerUtil.transform(
                        EntityTransformer.employeeToEmployeeResponseDto,
                        employee
                )
        );

        final APIResponse<Page<EmployeeResponseDTO>> response =
                getPageAPIResponse(COMPLETED, SUCCESS, HttpStatus.OK, empRespDTO);

        if (log.isInfoEnabled()) {
            log.info("::getAllEmployees completed successfully with: response {}",
                    jsonAsString(response));
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<APIResponse<EmployeeResponseDTO>> getEmployeeById(
            final Long employeeId) {
        if (log.isInfoEnabled()) {
            log.info("::getEmployeeById started with: employeeId: {}", employeeId);
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
            log.info("::getEmployeeById completed successfully with: response {}",
                    jsonAsString(response));
        }
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<APIResponse<EmployeeResponseDTO>> createEmployee(
            final EmployeeRequestDTO empReqDTO) {
        if (log.isInfoEnabled()) {
            log.info("::createEmployee started with: employeeRequestDTO {}",
                    jsonAsString(empReqDTO));
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
            log.info("::createEmployee completed successfully with: response {}",
                    jsonAsString(response));
        }
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<APIResponse<EmployeeResponseDTO>> updateEmployee(
            final Long employeeId, final EmployeeRequestDTO empReqDTO) {
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
            log.info("::updateEmployee completed successfully with: response {}",
                    jsonAsString(response));
        }
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<APIResponse<EmployeeResponseDTO>> patchEmployee(
            final Long employeeId, final EmployeePatchDTO employeePatchDTO) {
        if (log.isInfoEnabled()) {
            log.info("::patchEmployee started with: employeeId: {}, employeePatchDTO: {}",
                    employeeId, employeePatchDTO);
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
            log.info("::patchEmployee completed successfully with: response {}",
                    jsonAsString(response));
        }
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<APIResponse<Void>> reassignAndDeleteEmployee(
            final Long employeeId, final Long newEmployeeId) {
        if (log.isInfoEnabled()) {
            log.info("::reassignAndDeleteEmployee started with: employeeId: {}, newEmployeeId: {}",
                    employeeId, newEmployeeId);
        }

        orchestratorService.deleteEmployeeAndReassignCustomers(employeeId, newEmployeeId);

        final APIResponse<Void> response =
                getDTOAPIResponse(COMPLETED, "success", HttpStatus.OK, null);

        if (log.isInfoEnabled()) {
            log.info("::reassignAndDeleteEmployee completed successfully with: response {}",
                    jsonAsString(response));
        }
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<APIResponse<List<String>>> getAllDepartments() {
        if (log.isInfoEnabled()) {
            log.info("::getAllDepartments started");
        }

        final List<String> allDepartments = employeeService.getAllDepartments()
                .orElse(Collections.emptyList());

        final APIResponse<List<String>> response = getDTOAPIResponse(
                COMPLETED, "success", HttpStatus.OK, allDepartments);

        if (log.isInfoEnabled()) {
            log.info("::getAllDepartments completed successfully with: response {}",
                    jsonAsString(response));
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    private Page<Employee> getEmployeePage(
            final int page, final int size, final String search) {
        return (search != null && !search.isEmpty())
                ? employeeService.getEmployeesByFirstNameOrDepartment(search, page, size)
                : employeeService.getEmployeesWithPagination(page, size);
    }
}

