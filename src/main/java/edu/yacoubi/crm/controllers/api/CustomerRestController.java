package edu.yacoubi.crm.controllers.api;

import edu.yacoubi.crm.dto.APIResponse;
import edu.yacoubi.crm.dto.customer.CustomerPatchDTO;
import edu.yacoubi.crm.dto.customer.CustomerRequestDTO;
import edu.yacoubi.crm.dto.customer.CustomerResponseDTO;
import edu.yacoubi.crm.exception.ResourceNotFoundException;
import edu.yacoubi.crm.model.Customer;
import edu.yacoubi.crm.model.Employee;
import edu.yacoubi.crm.model.Note;
import edu.yacoubi.crm.service.ICustomerService;
import edu.yacoubi.crm.service.IEmployeeService;
import edu.yacoubi.crm.util.EntityTransformer;
import edu.yacoubi.crm.util.TransformerUtil;
import edu.yacoubi.crm.util.ValueMapper;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

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
            description = "Retrieve a list of all customers in the CRM system with pagination and optional search."
    )
    @GetMapping
    public ResponseEntity<APIResponse<Page<CustomerResponseDTO>>> getAllCustomers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search) {

        log.info("CustomerRestController::getAllEmployees starting to fetch employees...");
        log.debug("CustomerRestController::getAllEmployees request received - page: {}, size: {}, search: {}", page, size, search);

        Page<Customer> customersPage;
        if (search != null && !search.isEmpty()) {
            // Filtere nach Vorname oder E-Mail, falls ein Suchbegriff vorhanden ist
            customersPage = customerService.getCustomersByFirstNameOrEmail(search, page, size);
        } else {
            // Keine Suche, also alle Kunden abfragen
            customersPage = customerService.getCustomersWithPagination(page, size);
        }

        Page<CustomerResponseDTO> customerResponseDTOPage = customersPage.map(
                customer -> TransformerUtil.transform(EntityTransformer.customerToCustomerResponseDto, customer)
        );

        APIResponse<Page<CustomerResponseDTO>> response = APIResponse.<Page<CustomerResponseDTO>>builder()
                .status("success")
                .statusCode(HttpStatus.OK.value())
                .data(customerResponseDTOPage)
                .build();

        log.info("Response successfully created.");
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

        Customer existingCustomer = customerService.getCustomerById(id).get();

        CustomerResponseDTO customerResponseDTO = TransformerUtil.transform(
                EntityTransformer.customerToCustomerResponseDto,
                existingCustomer
        );

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

        Employee existingEmployee = employeeService.getEmployeeById(employeeId).get();

        // Setze lastInteractionDate auf das aktuelle Datum, wenn es vor oder nach dem aktuellen Datum liegt
        if (customerRequestDTO.getLastInteractionDate().isAfter(LocalDate.now()) ||
                customerRequestDTO.getLastInteractionDate().isBefore(LocalDate.now())) {
            customerRequestDTO.setLastInteractionDate(LocalDate.now());
        }

        //Customer customerRequest = convertToEntity(customerRequestDTO);
        Customer customerRequest = TransformerUtil.transform(
                EntityTransformer.customerRequestDtoToCustomer,
                customerRequestDTO
        );

        customerRequest.setEmployee(existingEmployee);

        Customer savedCustomer = customerService.createCustomer(customerRequest);

        CustomerResponseDTO customerResponseDTO = TransformerUtil.transform(
                EntityTransformer.customerToCustomerResponseDto,
                savedCustomer
        );

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
    @PutMapping("/{customerId}")
    public ResponseEntity<APIResponse<CustomerResponseDTO>> updateCustomer(
            @PathVariable Long customerId,
            @Valid @RequestBody CustomerRequestDTO customerRequestDTO) {
        log.info("CustomerRestController::updateCustomer request customerId {}, customer dto {}", customerId, jsonAsString(customerRequestDTO));

        Customer existingCustomer = customerService.getCustomerById(customerId).get();

        // Laden der bestehenden Notizen, um sicherzustellen, dass sie referenziert werden
        List<Note> existingNotes = existingCustomer.getNotes();

        // Mapping des DTO auf das Entität-Objekt, ohne die bestehende Notizen zu überschreiben
        Customer customerRequest = TransformerUtil.transform(
                EntityTransformer.customerRequestDtoToCustomer, customerRequestDTO
        );

        customerRequest.setId(customerId);
        customerRequest.setEmployee(existingCustomer.getEmployee());
        customerRequest.setNotes(existingNotes); // Setzen der bestehenden Notizen

        Customer updatedCustomer = customerService.updateCustomer(customerId, customerRequest);
        CustomerResponseDTO customerResponseDTO = TransformerUtil.transform(
                EntityTransformer.customerToCustomerResponseDto,
                updatedCustomer
        );

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
            @Valid @RequestBody CustomerRequestDTO customerRequestDTO) {
        log.info("CustomerRestController::updateCustomerByExample request id {}, customer dto {}", id, jsonAsString(customerRequestDTO));

        Customer updatedCustomer = customerService.updateCustomerByExample(customerRequestDTO, id);

        CustomerResponseDTO customerResponseDTO = TransformerUtil.transform(
                EntityTransformer.customerToCustomerResponseDto,
                updatedCustomer
        );

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

        Customer updatedCustomer = customerService.getCustomerById(id).get();

        CustomerResponseDTO customerResponseDTO = TransformerUtil.transform(
                EntityTransformer.customerToCustomerResponseDto,
                updatedCustomer
        );

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
