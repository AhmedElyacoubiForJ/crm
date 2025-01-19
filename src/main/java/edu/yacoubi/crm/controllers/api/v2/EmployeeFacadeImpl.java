package edu.yacoubi.crm.controllers.api.v2;

import edu.yacoubi.crm.dto.APIResponse;
import edu.yacoubi.crm.dto.employee.EmployeePatchDTO;
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

    @Override
    public ResponseEntity<APIResponse<EmployeeResponseDTO>> getEmployeeById(Long employeeId) {
        // Implementiere die Logik unter Nutzung der Services
        return null;
    }

    @Override
    public ResponseEntity<APIResponse<EmployeeResponseDTO>> patchEmployee(Long employeeId, EmployeePatchDTO employeePatchDTO) {
        // Implementiere die Logik unter Nutzung der Services
        return null;
    }

    @Override
    public ResponseEntity<APIResponse<Void>> reassignAndDeleteEmployee(Long employeeId, Long newEmployeeId) {
        // Implementiere die Logik unter Nutzung der Services
        return null;
    }

    private Page<Employee> getEmployeePage(final int page, final int size, final String search) {
        final Page<Employee> employeesPage;
        if (search != null && !search.isEmpty()) {
            employeesPage = employeeService.getEmployeesByFirstNameOrDepartment(search, page, size);
        } else {
            employeesPage = employeeService.getEmployeesWithPagination(page, size);
        }
        return employeesPage;
    }
}

