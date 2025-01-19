package edu.yacoubi.crm.controllers.api.v2;

import edu.yacoubi.crm.dto.APIResponse;
import edu.yacoubi.crm.dto.employee.EmployeePatchDTO;
import edu.yacoubi.crm.dto.employee.EmployeeRequestDTO;
import edu.yacoubi.crm.dto.employee.EmployeeResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface IEmployeeFacade {
    ResponseEntity<APIResponse<Page<EmployeeResponseDTO>>> getAllEmployees(int page, int size, String search);

    ResponseEntity<APIResponse<EmployeeResponseDTO>> getEmployeeById(Long employeeId);

    ResponseEntity<APIResponse<EmployeeResponseDTO>> createEmployee(EmployeeRequestDTO empReqDTO);

    ResponseEntity<APIResponse<EmployeeResponseDTO>> updateEmployee(Long employeeId, EmployeeRequestDTO empReqDTO);

    ResponseEntity<APIResponse<EmployeeResponseDTO>> patchEmployee(Long employeeId, EmployeePatchDTO employeePatchDTO);

    ResponseEntity<APIResponse<Void>> reassignAndDeleteEmployee(Long employeeId, Long newEmployeeId);

    ResponseEntity<APIResponse<List<String>>> getAllDepartments();
}
