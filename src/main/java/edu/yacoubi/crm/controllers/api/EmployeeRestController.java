package edu.yacoubi.crm.controllers.api;

import edu.yacoubi.crm.dto.EmployeeDTO;
import edu.yacoubi.crm.exception.ResourceNotFoundException;
import edu.yacoubi.crm.mapper.IMapper;
import edu.yacoubi.crm.model.Employee;
import edu.yacoubi.crm.service.IEmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/employees")
public class EmployeeRestController {

    private final IEmployeeService employeeService;
    private final IMapper<Employee, EmployeeDTO> employeeMapper;

    @Autowired
    public EmployeeRestController(IEmployeeService employeeService, IMapper<Employee, EmployeeDTO> employeeMapper) {
        this.employeeService = employeeService;
        this.employeeMapper = employeeMapper;
    }

    @GetMapping
    public List<EmployeeDTO> getAllEmployees() {
        return employeeService.getAllEmployees().stream()
                .map(employeeMapper::mapTo)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EmployeeDTO> getEmployeeById(@PathVariable Long id) {
        Employee employee = employeeService.getEmployeeById(id)
                .orElseThrow(
                        () -> new ResourceNotFoundException("Employee not found with ID: " + id)
                );
        EmployeeDTO employeeDTO = employeeMapper.mapTo(employee);
        return ResponseEntity.ok(employeeDTO);
    }

    @PostMapping
    public ResponseEntity<EmployeeDTO> createEmployee(@RequestBody EmployeeDTO employeeDTO) {
        Employee employee = employeeMapper.mapFrom(employeeDTO);
        Employee savedEmployee = employeeService.createEmployee(employee);
        EmployeeDTO savedEmployeeDTO = employeeMapper.mapTo(savedEmployee);
        return ResponseEntity.ok(savedEmployeeDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EmployeeDTO> updateEmployee(@PathVariable Long id, @RequestBody EmployeeDTO employeeDTO) {
        Employee existingEmployee = employeeService.getEmployeeById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with ID: " + id));
        Employee employeeRequest = employeeMapper.mapFrom(employeeDTO);
        employeeRequest.setId(id);
        Employee updatedEmployee = employeeService.updateEmployee(employeeRequest);
        EmployeeDTO updatedEmployeeDTO = employeeMapper.mapTo(updatedEmployee);
        return ResponseEntity.ok(updatedEmployeeDTO);
    }
}
