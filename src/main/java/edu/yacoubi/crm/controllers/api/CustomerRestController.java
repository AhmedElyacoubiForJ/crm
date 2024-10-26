package edu.yacoubi.crm.controllers.api;

import edu.yacoubi.crm.dto.CustomerDTO;
import edu.yacoubi.crm.exception.ResourceNotFoundException;
import edu.yacoubi.crm.mapper.IMapper;
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

    @Operation(summary = "Get all customers", description = "Retrieve a list of all customers in the CRM system.")
    @GetMapping
    public List<CustomerDTO> getAllCustomers() {
        return customerService.getAllCustomers().stream()
                .map(customerMapper::mapTo)
                .collect(Collectors.toList());
    }

    @Operation(summary = "Get customer by ID", description = "Retrieve a customer by their unique ID.")
    @GetMapping("/{id}")
    public ResponseEntity<CustomerDTO> getCustomerById(@PathVariable Long id) {
        Customer customer = customerService.getCustomerById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with ID: " + id));
        CustomerDTO customerDTO = customerMapper.mapTo(customer);
        return ResponseEntity.ok(customerDTO);
    }

    @Operation(summary = "Create a new customer", description = "This operation creates a new customer in the CRM system.")
    public ResponseEntity<CustomerDTO> createCustomer(
            @RequestParam Long employeeId,
            @RequestBody CustomerDTO customerRequestDTO) {
        Employee existingEmployee = employeeService.getEmployeeById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with ID: " + employeeId));

        Customer customerRequest = customerMapper.mapFrom(customerRequestDTO);
        customerRequest.setEmployee(existingEmployee);

        Customer customerResponse = customerService.createCustomer(customerRequest);
        CustomerDTO customerResponseDTO = customerMapper.mapTo(customerResponse);
        return ResponseEntity.ok(customerResponseDTO);
    }

    @Operation(summary = "Update customer", description = "Update the details of an existing customer by their unique ID.")
    @PutMapping("/{id}")
    public ResponseEntity<CustomerDTO> updateCustomer(
            @PathVariable Long id,
            @RequestBody CustomerDTO customerDTO) {
        Customer existingCustomer = customerService.getCustomerById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with ID: " + id));

        // Laden der bestehenden Notizen, um sicherzustellen, dass sie referenziert werden
        List<Note> existingNotes = existingCustomer.getNotes();

        // Mapping des DTO auf das Entität-Objekt, ohne die bestehende Notizen zu überschreiben
        Customer customerRequest = customerMapper.mapFrom(customerDTO);
        customerRequest.setId(id);
        customerRequest.setEmployee(existingCustomer.getEmployee());
        customerRequest.setNotes(existingNotes); // Setzen der bestehenden Notizen

        Customer updatedCustomer = customerService.updateCustomer(id, customerRequest);
        CustomerDTO updatedCustomerDTO = customerMapper.mapTo(updatedCustomer);

        return ResponseEntity.ok(updatedCustomerDTO);
    }

    @Operation(summary = "Update customer by example", description = "Update the details of an existing customer using a provided example.")
    public ResponseEntity<CustomerDTO> updateCustomerByExample(
            @PathVariable Long id,
            @RequestBody CustomerDTO customerDTO) {
        Customer updatedCustomer = customerService.updateCustomerByExample(customerDTO, id);
        CustomerDTO updatedCustomerDTO = customerMapper.mapTo(updatedCustomer);
        return ResponseEntity.ok(updatedCustomerDTO);
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
