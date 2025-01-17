package edu.yacoubi.crm.controllers.api;

import edu.yacoubi.crm.dto.APIResponse;
import edu.yacoubi.crm.dto.customer.CustomerPatchDTO;
import edu.yacoubi.crm.dto.customer.CustomerRequestDTO;
import edu.yacoubi.crm.dto.customer.CustomerResponseDTO;
import edu.yacoubi.crm.model.Customer;
import edu.yacoubi.crm.service.ICustomerService;
import edu.yacoubi.crm.service.IEntityOrchestratorService;
import edu.yacoubi.crm.util.ApiResponseHelper;
import edu.yacoubi.crm.util.EntityTransformer;
import edu.yacoubi.crm.util.TransformerUtil;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

import static edu.yacoubi.crm.util.ValueMapper.jsonAsString;

/**
 * REST controller for managing customer resources in the CRM system.
 *
 * @author A. El Yacoubi
 */
@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
@Slf4j
public class CustomerRestController {
    /**
     * Success message.
     */
    public static final String SUCCESS = "Success";

    /**
     * Completion message for a successful operation.
     */
    public static final String COMPLETED = "Operation completed";

    /**
     * Service for customer operations.
     */
    private final ICustomerService customerService;

    /**
     * Orchestrator service for deleting and reassigning customers.
     */
    private final IEntityOrchestratorService entityOrchestratorService;

    /**
     * Retrieve a list of all customers in the CRM system with pagination and optional search.
     *
     * @param page   the page number to retrieve, default is 0
     * @param size   the size of the page to retrieve, default is 10
     * @param search an optional search parameter to filter customers by first name or email
     * @return a paginated list of all customers wrapped in an APIResponse
     */
    @Operation(
            summary = "Get all customers",
            description = "Retrieve a list of all customers in the CRM system with pagination and optional search."
    )
    @GetMapping
    public ResponseEntity<APIResponse<Page<CustomerResponseDTO>>> getAllCustomers(
            final @RequestParam(defaultValue = "0") int page,
            final @RequestParam(defaultValue = "10") int size,
            final @RequestParam(required = false) String search) {
        if (log.isInfoEnabled()) {
            log.info("::getAllEmployees started with: page: {}, size: {}, search: {}", page, size, search);
        }

        final Page<Customer> customersPage = getCustomerPage(page, size, search);

        final Page<CustomerResponseDTO> customerResponseDTOPage = customersPage.map(
                customer -> TransformerUtil.transform(EntityTransformer.customerToCustomerResponseDto, customer)
        );

        final APIResponse<Page<CustomerResponseDTO>> response = ApiResponseHelper.getPageAPIResponse(
                COMPLETED, SUCCESS, HttpStatus.OK, customerResponseDTOPage
        );

        if (log.isInfoEnabled()) {
            log.info("::getAllCustomers completed successfully with: response {}", jsonAsString(response));
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Retrieve a customer by their unique ID.
     *
     * @param customerId the unique ID of the customer to retrieve
     * @return the customer details wrapped in an APIResponse
     */
    @Operation(
            summary = "Get customer by ID",
            description = "Retrieve a customer by their unique ID."
    )
    @GetMapping("/{customerId}")
    public ResponseEntity<APIResponse<CustomerResponseDTO>> getCustomerById(
            final @PathVariable Long customerId) {
        if (log.isInfoEnabled()) {
            log.info("::getCustomerById started with: customerId {}", customerId);
        }

        final Customer existingCustomer = customerService.getCustomerById(customerId).get();

        final CustomerResponseDTO customerResponseDTO = TransformerUtil.transform(
                EntityTransformer.customerToCustomerResponseDto,
                existingCustomer
        );

        final APIResponse<CustomerResponseDTO> response = ApiResponseHelper.getDTOAPIResponse(
                COMPLETED, SUCCESS, HttpStatus.OK, customerResponseDTO
        );

        if (log.isInfoEnabled()) {
            log.info("::getCustomerById completed successfully with: response {}", jsonAsString(response));
        }
        return ResponseEntity.ok(response);
    }

    /**
     * This operation creates a new customer in the CRM system.
     *
     * @param customerRequestDTO the customer request data transfer object containing the details of the customer to be created
     * @param employeeId         the unique ID of the employee to whom the customer will be assigned
     * @return the created customer details wrapped in an APIResponse
     */
    @Operation(
            summary = "Create a new customer",
            description = "This operation creates a new customer in the CRM system."
    )
    @PostMapping
    public ResponseEntity<APIResponse<CustomerResponseDTO>> createCustomerForEmployee(
            final @Valid @RequestBody CustomerRequestDTO customerRequestDTO,
            final @RequestParam Long employeeId) {
        if (log.isInfoEnabled()) {
            log.info("::createCustomerForEmployee started with: customerRequestDTO {}, employeeId {}",
                    jsonAsString(customerRequestDTO), employeeId);
        }

        // Setze lastInteractionDate auf das aktuelle Datum
        customerRequestDTO.setLastInteractionDate(LocalDate.now());

        final Customer customerRequest = TransformerUtil.transform(
                EntityTransformer.customerRequestDtoToCustomer,
                customerRequestDTO
        );

        final Customer savedCustomer = entityOrchestratorService.createCustomerForEmployee(
                customerRequest, employeeId
        );

        final CustomerResponseDTO customerResponseDTO = TransformerUtil.transform(
                EntityTransformer.customerToCustomerResponseDto,
                savedCustomer
        );

        final APIResponse<CustomerResponseDTO> response = ApiResponseHelper.getDTOAPIResponse(
                COMPLETED, SUCCESS, HttpStatus.CREATED, customerResponseDTO
        );

        if (log.isInfoEnabled()) {
            log.info("::createCustomerForEmployee completed successfully with: response {}", jsonAsString(response));
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Full update of an existing customer by their unique ID.
     *
     * @param customerId         the unique ID of the customer to update
     * @param customerRequestDTO the customer request data transfer object containing the updated details of the customer
     * @return the updated customer details wrapped in an APIResponse
     */
    @Operation(
            summary = "Full update of customer",
            description = "Full update of an existing customer by their unique ID."
    )
    @PutMapping("/{customerId}")
    public ResponseEntity<APIResponse<CustomerResponseDTO>> updateCustomer(
            final @PathVariable Long customerId,
            final @Valid @RequestBody CustomerRequestDTO customerRequestDTO) {
        if (log.isInfoEnabled()) {
            log.info("::updateCustomer started with: customerId {}, customerRequestDTO {}",
                    customerId, jsonAsString(customerRequestDTO));
        }

        final Customer updatedCustomer = customerService.updateCustomer(customerId, TransformerUtil.transform(
                EntityTransformer.customerRequestDtoToCustomer, customerRequestDTO)
        );

        final CustomerResponseDTO customerResponseDTO = TransformerUtil.transform(
                EntityTransformer.customerToCustomerResponseDto,
                updatedCustomer
        );

        final APIResponse<CustomerResponseDTO> response = ApiResponseHelper.getDTOAPIResponse(
                COMPLETED, SUCCESS, HttpStatus.OK, customerResponseDTO
        );

        if (log.isInfoEnabled()) {
            log.info("::updateCustomer completed successfully with: response {}", jsonAsString(response));
        }
        return ResponseEntity.ok(response);
    }

    /**
     * Partial update of an existing customer by their unique ID.
     *
     * @param customerId the unique ID of the customer to update
     * @param customerPatchDTO the customer patch data transfer object containing the partial updates of the customer
     * @return the updated customer details wrapped in an APIResponse
     */
    @Operation(
            summary = "Partial update of customer",
            description = "Partial update of an existing customer by their unique ID."
    )
    @PatchMapping("/{customerId}")
    public ResponseEntity<APIResponse<CustomerResponseDTO>> patchCustomer(
            final @PathVariable Long customerId,
            final @Valid @RequestBody CustomerPatchDTO customerPatchDTO) {
        if (log.isInfoEnabled()) {
            log.info("::patchCustomer started with: customerId {}, customerPatchDTO {}",
                    customerId, jsonAsString(customerPatchDTO));
        }

        customerService.partialUpdateCustomer(customerId, customerPatchDTO);

        final Customer updatedCustomer = customerService.getCustomerById(customerId).get();

        final CustomerResponseDTO customerResponseDTO = TransformerUtil.transform(
                EntityTransformer.customerToCustomerResponseDto,
                updatedCustomer
        );

        final APIResponse<CustomerResponseDTO> response = ApiResponseHelper.getDTOAPIResponse(
                COMPLETED, SUCCESS, HttpStatus.OK, customerResponseDTO
        );

        if (log.isInfoEnabled()) {
            log.info("::patchCustomer completed successfully with: response {}", jsonAsString(response));
        }
        return ResponseEntity.ok(response);
    }

    /**
     * Delete an existing customer by their unique ID.
     *
     * @param customerId the unique ID of the customer to delete
     * @return a response indicating the success of the deletion
     */
    @Operation(
            summary = "Delete customer",
            description = "Delete an existing customer by their unique ID."
    )
    @DeleteMapping("/{customerId}")
    public ResponseEntity<APIResponse<Void>> deleteCustomer(final @PathVariable Long customerId) {
        if (log.isInfoEnabled()) {
            log.info("::deleteCustomer started with: customerId {}", customerId);
        }

        customerService.deleteCustomer(customerId);

        final APIResponse<Void> response = ApiResponseHelper.getVoidAPIResponse(
                COMPLETED, SUCCESS, HttpStatus.OK
        );

        if (log.isInfoEnabled()) {
            log.info("::deleteCustomer completed successfully with: response {}", jsonAsString(response));
        }
        return ResponseEntity.ok(response);
    }


    /**
     * Retrieves a page of customers, optionally filtered by a search term.
     *
     * @param page   the page number to retrieve
     * @param size   the size of the page to retrieve
     * @param search an optional search parameter to filter customers by first name or email
     * @return a page of customers matching the search criteria
     */
    private Page<Customer> getCustomerPage(int page, int size, String search) {
        Page<Customer> customersPage;
        if (search != null && !search.isEmpty()) {
            customersPage = customerService.getCustomersByFirstNameOrEmail(search, page, size);
        } else {
            customersPage = customerService.getCustomersWithPagination(page, size);
        }
        return customersPage;
    }
}
