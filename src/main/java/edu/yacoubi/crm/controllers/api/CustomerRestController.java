package edu.yacoubi.crm.controllers.api;

import edu.yacoubi.crm.dto.CustomerDTO;
import edu.yacoubi.crm.dto.CustomerRequestDTO;
import edu.yacoubi.crm.dto.CustomerResponseDTO;
import edu.yacoubi.crm.exception.ResourceNotFoundException;
import edu.yacoubi.crm.mapper.IMapper;
import edu.yacoubi.crm.mapper.impl.CustomerRequestMapper;
import edu.yacoubi.crm.model.Customer;
import edu.yacoubi.crm.model.Employee;
import edu.yacoubi.crm.model.Note;
import edu.yacoubi.crm.service.ICustomerService;
import edu.yacoubi.crm.service.IEmployeeService;
import io.swagger.v3.oas.annotations.Operation;
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
    private final IMapper<Customer, CustomerRequestDTO> cRequestMapper;
    private final IMapper<Customer, CustomerResponseDTO> cResponseMapper;

    @Operation(summary = "Get all customers", description = "Retrieve a list of all customers in the CRM system.")
    @GetMapping
    public List<CustomerResponseDTO> getAllCustomers() {
        return customerService.getAllCustomers().stream()
                .map(cResponseMapper::mapTo)
                .collect(Collectors.toList());
    }

    @Operation(summary = "Get customer by ID", description = "Retrieve a customer by their unique ID.")
    @GetMapping("/{id}")
    public ResponseEntity<CustomerResponseDTO> getCustomerById(@PathVariable Long id) {
        Customer customer = customerService.getCustomerById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with ID: " + id));
        CustomerResponseDTO customerDTO = cResponseMapper.mapTo(customer);
        return ResponseEntity.ok(customerDTO);
    }

    @Operation(summary = "Create a new customer", description = "This operation creates a new customer in the CRM system.")
    @PostMapping
    public ResponseEntity<CustomerResponseDTO> createCustomer(
            @RequestParam Long employeeId,
            @RequestBody CustomerRequestDTO customerRequestDTO) {
        Employee existingEmployee = employeeService.getEmployeeById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with ID: " + employeeId));

        Customer customerRequest = cRequestMapper.mapFrom(customerRequestDTO);
        customerRequest.setEmployee(existingEmployee);

        Customer savedCustomer = customerService.createCustomer(customerRequest);
        CustomerResponseDTO customerResponseDTO = cResponseMapper.mapTo(savedCustomer);
        return ResponseEntity.ok(customerResponseDTO);
    }

    @Operation(summary = "Update customer", description = "Update the details of an existing customer by their unique ID.")
    @PutMapping("/{id}")
    public ResponseEntity<CustomerResponseDTO> updateCustomer(
            @PathVariable Long id,
            @RequestBody CustomerRequestDTO customerRequestDTO) {
        Customer existingCustomer = customerService.getCustomerById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with ID: " + id));

        // Laden der bestehenden Notizen, um sicherzustellen, dass sie referenziert werden
        List<Note> existingNotes = existingCustomer.getNotes();

        // Mapping des DTO auf das Entität-Objekt, ohne die bestehende Notizen zu überschreiben
        Customer customerRequest = cRequestMapper.mapFrom(customerRequestDTO);
        customerRequest.setId(id);
        customerRequest.setEmployee(existingCustomer.getEmployee());
        customerRequest.setNotes(existingNotes); // Setzen der bestehenden Notizen

        Customer updatedCustomer = customerService.updateCustomer(id, customerRequest);
        CustomerResponseDTO updatedCustomerDTO = cResponseMapper.mapTo(updatedCustomer);
        return ResponseEntity.ok(updatedCustomerDTO);
    }

    @Operation(summary = "Update customer by example", description = "Update the details of an existing customer using a provided example.")
    @PutMapping("/{id}/updateByExample")
    public ResponseEntity<CustomerResponseDTO> updateCustomerByExample(
            @PathVariable Long id,
            @RequestBody CustomerRequestDTO customerRequestDTO) {
        Customer updatedCustomer = customerService.updateCustomerByExample(customerRequestDTO, id);
        CustomerResponseDTO customerResponseDTO = cResponseMapper.mapTo(updatedCustomer);
        return ResponseEntity.ok(customerResponseDTO);
    }

    @Operation(summary = "Delete customer", description = "Delete an existing customer by their unique ID.")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable Long id) {
        customerService.getCustomerById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with ID: " + id));
        customerService.deleteCustomer(id);
        return ResponseEntity.noContent().build();
    }
}
