package edu.yacoubi.crm.util;

import edu.yacoubi.crm.dto.APIResponse;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;

public class ApiResponseHelper {
    // Privater Konstruktor, um die Instanziierung dieser Helper-Klasse zu verhindern
    private ApiResponseHelper() {
    }

    public static <T> APIResponse<Page<T>> getPageAPIResponse(
            final String msg, final String statusMsg, final HttpStatus statusCode, final Page<T> dataPage) {
        String finalMsg = msg == null ? "No message provided" : msg;

        return APIResponse.<Page<T>>builder()
                .message(finalMsg) // Setzt die Nachricht
                .status(statusMsg) // Setzt den Status
                .statusCode(statusCode.value()) // Setzt den Statuscode
                .data(dataPage) // Setzt die Daten
                .build();
    }

    public static <T> APIResponse<T> getDTOAPIResponse(
            final String msg, final String statusMsg, final HttpStatus statusCode, final T dto) {
        String finalMsg = msg == null ? "No message provided" : msg;

        return APIResponse.<T>builder()
                .message(finalMsg) // Setzt die Nachricht
                .status(statusMsg) // Setzt den Status
                .statusCode(statusCode.value()) // Setzt den Statuscode
                .data(dto) // Setzt die Daten
                .build();
    }

    public static APIResponse<Void> getVoidAPIResponse(
            final String msg, final String statusMsg, final HttpStatus statusCode) {
        String finalMsg = msg == null ? "No message provided" : msg;

        return APIResponse.<Void>builder()
                .message(finalMsg) // Setzt die Nachricht
                .status(statusMsg) // Setzt den Status
                .statusCode(statusCode.value()) // Setzt den Statuscode
                .build();
    }
}