package edu.yacoubi.crm.controllers.api;

import edu.yacoubi.crm.dto.*;
import edu.yacoubi.crm.dto.customer.CustomerPatchDTO;
import edu.yacoubi.crm.dto.customer.CustomerResponseDTO;
import edu.yacoubi.crm.dto.customer.CustomerRequestDTO;
import edu.yacoubi.crm.exception.ResourceNotFoundException;
import edu.yacoubi.crm.model.Customer;
import edu.yacoubi.crm.model.Employee;
import edu.yacoubi.crm.model.Note;
import edu.yacoubi.crm.service.ICustomerService;
import edu.yacoubi.crm.service.IEmployeeService;
import edu.yacoubi.crm.util.ValueMapper;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import static edu.yacoubi.crm.util.ValueMapper.*;

@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
@Slf4j
public class CustomerRestController {
    private final ICustomerService customerService;
    private final IEmployeeService employeeService;

    @Operation(
            summary = "Get all customers",
            description = "Retrieve a list of all customers in the CRM system."
    )
    @GetMapping
    public ResponseEntity<APIResponse<List<CustomerResponseDTO>>> getAllCustomers() {
        log.info("CustomerRestController::getAllCustomers");

        List<CustomerResponseDTO> customerResponseDTOList = customerService.getAllCustomers().stream()
                .map(ValueMapper::convertToResponseDTO)
                .collect(Collectors.toList());

        APIResponse<List<CustomerResponseDTO>> response = APIResponse
                .<List<CustomerResponseDTO>>builder()
                .status("success")
                .statusCode(HttpStatus.OK.value())
                .data(customerResponseDTOList)
                .build();

        log.info("CustomerRestController::getAllCustomers response {}", jsonAsString(response));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(
            summary = "Get customer by ID",
            description = "Retrieve a customer by their unique ID."
    )
    @GetMapping("/{id}")
    public ResponseEntity<APIResponse<CustomerResponseDTO>> getCustomerById(@PathVariable Long id) {
        log.info("CustomerRestController::getCustomerById request id {}", id);

        Customer existingCustomer = customerService.getCustomerById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with ID: " + id));
        CustomerResponseDTO customerResponseDTO = convertToResponseDTO(existingCustomer);

        APIResponse<CustomerResponseDTO> response = APIResponse.<CustomerResponseDTO>builder()
                .status("success")
                .statusCode(HttpStatus.OK.value())
                .data(customerResponseDTO)
                .build();

        log.info("CustomerRestController::getCustomer response dto {}", jsonAsString(response));
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Create a new customer",
            description = "This operation creates a new customer in the CRM system."
    )
    @PostMapping
    public ResponseEntity<APIResponse<CustomerResponseDTO>> createCustomer(
            @RequestParam Long employeeId,
            @Valid @RequestBody CustomerRequestDTO customerRequestDTO) {
        log.info("CustomerRestController::createCustomer request employeeId {}, customer dto {}", employeeId, jsonAsString(customerRequestDTO));

        Employee existingEmployee = employeeService.getEmployeeById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with ID: " + employeeId));

        // Setze lastInteractionDate auf das aktuelle Datum, wenn es vor oder nach dem aktuellen Datum liegt
        if (customerRequestDTO.getLastInteractionDate().isAfter(LocalDate.now()) ||
                customerRequestDTO.getLastInteractionDate().isBefore(LocalDate.now())) {
            customerRequestDTO.setLastInteractionDate(LocalDate.now());
        }

        Customer customerRequest = convertToEntity(customerRequestDTO);
        customerRequest.setEmployee(existingEmployee);

        Customer savedCustomer = customerService.createCustomer(customerRequest);
        CustomerResponseDTO customerResponseDTO = convertToResponseDTO(savedCustomer);

        APIResponse<CustomerResponseDTO> response = APIResponse.<CustomerResponseDTO>builder()
                .status("success")
                .statusCode(HttpStatus.CREATED.value())
                .data(customerResponseDTO)
                .build();

        log.info("CustomerRestController::createCustomer response dto {}", jsonAsString(response));
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(
            summary = "Full update of customer",
            description = "Full update of an existing customer by their unique ID."
    )
    @PutMapping("/{id}")
    public ResponseEntity<APIResponse<CustomerResponseDTO>> updateCustomer(
            @PathVariable Long id,
            @RequestBody CustomerRequestDTO customerRequestDTO) {
        log.info("CustomerRestController::updateCustomer request id {}, customer dto {}", id, jsonAsString(customerRequestDTO));

        Customer existingCustomer = customerService.getCustomerById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with ID: " + id));

        // Laden der bestehenden Notizen, um sicherzustellen, dass sie referenziert werden
        List<Note> existingNotes = existingCustomer.getNotes();

        // Mapping des DTO auf das Entität-Objekt, ohne die bestehende Notizen zu überschreiben
        Customer customerRequest = convertToEntity(customerRequestDTO);
        customerRequest.setId(id);
        customerRequest.setEmployee(existingCustomer.getEmployee());
        customerRequest.setNotes(existingNotes); // Setzen der bestehenden Notizen

        Customer updatedCustomer = customerService.updateCustomer(id, customerRequest);
        CustomerResponseDTO customerResponseDTO = convertToResponseDTO(updatedCustomer);

        APIResponse<CustomerResponseDTO> response = APIResponse.<CustomerResponseDTO>builder()
                .status("success")
                .statusCode(HttpStatus.OK.value())
                .data(customerResponseDTO)
                .build();

        log.info("CustomerRestController::updateCustomer response dto {}", jsonAsString(response));
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Partial update of customer by example, Deprecated",
            description = "Partial update of an existing customer using a provided example."
    )
    @PutMapping("/{id}/updateByExample")
    @Deprecated
    public ResponseEntity<APIResponse<CustomerResponseDTO>> updateCustomerByExample(
            @PathVariable Long id,
            @RequestBody CustomerRequestDTO customerRequestDTO) {
        log.info("CustomerRestController::updateCustomerByExample request id {}, customer dto {}", id, jsonAsString(customerRequestDTO));

        Customer updatedCustomer = customerService.updateCustomerByExample(customerRequestDTO, id);
        CustomerResponseDTO customerResponseDTO = convertToResponseDTO(updatedCustomer);
        APIResponse<CustomerResponseDTO> response = APIResponse.<CustomerResponseDTO>builder()
                .status("success")
                .statusCode(HttpStatus.OK.value())
                .data(customerResponseDTO)
                .build();

        log.info("CustomerRestController::updateCustomerByExample response dto {}", jsonAsString(response));
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Partial update of customer",
            description = "Partial update of an existing customer by their unique ID."
    )
    @PatchMapping("/{id}")
    public ResponseEntity<APIResponse<CustomerResponseDTO>> patchCustomer(
            @PathVariable Long id,
            @Valid @RequestBody CustomerPatchDTO customerPatchDTO) {
        log.info("CustomerRestController::patchCustomer request id {}, customer dto {}", id, jsonAsString(customerPatchDTO));

        customerService.partialUpdateCustomer(id, customerPatchDTO);

        Customer updatedCustomer = customerService.getCustomerById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with ID: " + id));

        CustomerResponseDTO customerResponseDTO = convertToResponseDTO(updatedCustomer);
        APIResponse<CustomerResponseDTO> response = APIResponse.<CustomerResponseDTO>builder()
                .status("success")
                .statusCode(HttpStatus.OK.value())
                .data(customerResponseDTO)
                .build();

        log.info("CustomerRestController::patchCustomer response {}", jsonAsString(response));
        return ResponseEntity.ok(response);
    }


    @Operation(
            summary = "Delete customer",
            description = "Delete an existing customer by their unique ID."
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<APIResponse<Void>> deleteCustomer(@PathVariable Long id) {
        log.info("CustomerRestController::deleteCustomer request id {}", id);

        // Check if the customer exists, throw exception if not found
        customerService.getCustomerById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with ID: " + id));

        // Delete the customer
        customerService.deleteCustomer(id);
        log.info("Customer successfully deleted, id: {}", id);

        // Build the API response
        APIResponse<Void> response = APIResponse.<Void>builder()
                .status("success")
                .statusCode(HttpStatus.OK.value())
                .build();

        return ResponseEntity.ok(response);
    }
}
