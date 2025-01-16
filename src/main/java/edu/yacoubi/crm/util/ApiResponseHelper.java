package edu.yacoubi.crm.util;

import edu.yacoubi.crm.dto.APIResponse;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;

public class ApiResponseHelper {
    public static <T> APIResponse<Page<T>> getPageAPIResponse(
            String msg, String statusMsg, HttpStatus statusCode, Page<T> dataPage) {
        if (msg == null) {
            msg = "No message provided"; // Standardnachricht setzen
        }

        APIResponse<Page<T>> response = APIResponse.<Page<T>>builder()
                .message(msg) // Setzt die Nachricht
                .status(statusMsg) // Setzt den Status
                .statusCode(statusCode.value()) // Setzt den Statuscode
                .data(dataPage) // Setzt die Daten
                .build();
        return response;
    }

    public static <T> APIResponse<T> getDTOAPIResponse(
            String msg, String statusMsg, HttpStatus statusCode, T dto) {
        if (msg == null) {
            msg = "No message provided"; // Standardnachricht setzen
        }

        APIResponse<T> response = APIResponse.<T>builder()
                .message(msg) // Setzt die Nachricht
                .status(statusMsg) // Setzt den Status
                .statusCode(statusCode.value()) // Setzt den Statuscode
                .data(dto) // Setzt die Daten
                .build();
        return response;
    }

    // Leerer Konstruktor, um die Instanziierung dieser Helper-Klasse zu verhindern
    private void ApiResponseUtil() {
    }
}
