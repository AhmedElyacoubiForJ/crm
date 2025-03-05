package edu.yacoubi.crm.controllers.api.v2;

import edu.yacoubi.crm.dto.APIResponse;
import edu.yacoubi.crm.dto.employee.EmployeeResponseDTO;
import edu.yacoubi.crm.repository.EmployeeRepository;
import edu.yacoubi.crm.util.TestDataInitializer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.junit.jupiter.api.Assertions.*;

@SpringJUnitConfig
@SpringBootTest
class EmployeeRestControllerV2IntegrationTest {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private IEmployeeFacade employeeFacade;

    @Autowired private
    TestDataInitializer testDataInitializer;

    @Autowired
    private EmployeeRestControllerV2 employeeController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(employeeController).build();
        // Testdaten hinzufügen
        testDataInitializer.addTestEmployees();
        employeeRepository.findAll().forEach(
                employee -> System.out.println(
                        employee
                )
        );
    }

    //@Test
    void itShouldReturnAllEmployeesPaged() throws Exception {
        // Given
        Page<EmployeeResponseDTO> employeePage = new PageImpl<>(Collections.emptyList());
        APIResponse<Page<EmployeeResponseDTO>> apiResponse = APIResponse.<Page<EmployeeResponseDTO>>builder()
                .status("success")
                .statusCode(200)
                .message("Completed successfully")
                .data(employeePage)
                .build();
/*
        // When
        MvcResult result = mockMvc.perform(
                        get("/api/v2/employees")
                                .param("page", "0")
                                .param("size", "10")
                                .param("search", "search"))
                .andExpect(status().isOk())
                .andReturn();

        // Protokolliere die Antwort
        String jsonResponse = result.getResponse().getContentAsString();
        System.out.println("********************************");
        System.out.println("Response: " + jsonResponse);
        System.out.println("Response: " + jsonResponse);
        System.out.println("Response: " + jsonResponse);
        System.out.println("********************************");

        // Then
        assertNotNull(jsonResponse);
        //assertTrue(jsonResponse.contains("\"status\":\"Success\""));
        //assertTrue(jsonResponse.contains("\"statusCode\":200"));
        //assertTrue(jsonResponse.contains("\"data\""));*/
    }
}
