package edu.yacoubi.crm.controllers.api;

import edu.yacoubi.crm.dto.CustomerDTO;
import edu.yacoubi.crm.exception.ResourceNotFoundException;
import edu.yacoubi.crm.mapper.IMapper;
import edu.yacoubi.crm.model.Customer;
import edu.yacoubi.crm.model.Employee;
import edu.yacoubi.crm.service.ICustomerService;
import edu.yacoubi.crm.service.IEmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
public class CustomerRestController {

    private final ICustomerService customerService;
    private final IEmployeeService employeeService;
    private final IMapper<Customer, CustomerDTO> customerMapper;

    @GetMapping
    public List<CustomerDTO> getAllCustomers() {
        return customerService.getAllCustomers().stream()
                .map(customerMapper::mapTo)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomerDTO> getCustomerById(@PathVariable Long id) {
        Customer customer = customerService.getCustomerById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with ID: " + id));
        CustomerDTO customerDTO = customerMapper.mapTo(customer);
        return ResponseEntity.ok(customerDTO);
    }

    @PostMapping
    public ResponseEntity<CustomerDTO> createCustomer(
            @RequestParam Long employeeId,
            @RequestBody CustomerDTO customerRequestDTO) {
        Employee existingEmployee = employeeService.getEmployeeById(employeeId)
                .orElseThrow(
                        () -> new ResourceNotFoundException("Employee not found with ID: " + employeeId)
                );

        Customer customerRequest = customerMapper.mapFrom(customerRequestDTO);
        customerRequest.setEmployee(existingEmployee);

        Customer customerResponse = customerService.createCustomer(customerRequest);
        CustomerDTO customerResponseDTO = customerMapper.mapTo(customerResponse);
        return ResponseEntity.ok(customerResponseDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CustomerDTO> updateCustomer(
            @PathVariable Long id,
            @RequestBody CustomerDTO customerDTO) {
        Customer existingCustomer = customerService.getCustomerById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with ID: " + id));

        Customer customerRequest = customerMapper.mapFrom(customerDTO);
        customerRequest.setId(id);
        customerRequest.setEmployee(existingCustomer.getEmployee());

        Customer updatedCustomer = customerService.updateCustomer(id, customerRequest);
        CustomerDTO updatedCustomerDTO = customerMapper.mapTo(updatedCustomer);
        return ResponseEntity.ok(updatedCustomerDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable Long id) {
        customerService.getCustomerById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with ID: " + id)
                );

        customerService.deleteCustomer(id);
        return ResponseEntity.noContent().build();
    }
}
