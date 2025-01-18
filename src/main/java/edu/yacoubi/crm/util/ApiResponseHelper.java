package edu.yacoubi.crm.util;

import edu.yacoubi.crm.dto.APIResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;

/**
 * Helper class for creating API responses.
 *
 * <p>This class provides static methods to create API responses for different data types.</p>
 *
 * <p>Usage example:</p>
 * <pre>
 *     APIResponse<Page<MyEntity>> response = ApiResponseHelper
 *     .getPageAPIResponse("Success", "OK", HttpStatus.OK, myPageData);
 * </pre>
 *
 * @author A. El Yacoubi
 */
@Slf4j
public class ApiResponseHelper {
    private ApiResponseHelper() {
    }

    /**
     * Creates an API response for a paginated data set.
     *
     * @param <T>        the type of the data
     * @param msg        the message to include in the response
     * @param statusMsg  the status message to include in the response
     * @param statusCode the HTTP status code to include in the response
     * @param dataPage   the paginated data to include in the response
     * @return the API response
     */
    public static <T> APIResponse<Page<T>> getPageAPIResponse(
            final String msg,
            final String statusMsg,
            final HttpStatus statusCode,
            final Page<T> dataPage) {

        if (log.isInfoEnabled()) {
            log.info("::getPageAPIResponse started with: msg {}, statusMsg {}, statusCode {}",
                    msg, statusMsg, statusCode);
        }

        String finalMsg = msg == null ? "No message provided" : msg;

        APIResponse<Page<T>> response = APIResponse.<Page<T>>builder()
                .message(finalMsg) // Setzt die Nachricht
                .status(statusMsg) // Setzt den Status
                .statusCode(statusCode.value()) // Setzt den Statuscode
                .data(dataPage) // Setzt die Daten
                .build();

        if (log.isInfoEnabled()) {
            log.info("::getPageAPIResponse completed successfully");
        }

        return response;
    }

    /**
     * Creates an API response for a DTO object.
     *
     * @param <T>        the type of the data
     * @param msg        the message to include in the response
     * @param statusMsg  the status message to include in the response
     * @param statusCode the HTTP status code to include in the response
     * @param dto        the data to include in the response
     * @return the API response
     */
    public static <T> APIResponse<T> getDTOAPIResponse(
            final String msg,
            final String statusMsg,
            final HttpStatus statusCode,
            final T dto) {

        if (log.isInfoEnabled()) {
            log.info("::getDTOAPIResponse started with: msg {}, statusMsg {}, statusCode {}",
                    msg, statusMsg, statusCode);
        }

        String finalMsg = msg == null ? "No message provided" : msg;

        APIResponse<T> response = APIResponse.<T>builder()
                .message(finalMsg) // Setzt die Nachricht
                .status(statusMsg) // Setzt den Status
                .statusCode(statusCode.value()) // Setzt den Statuscode
                .data(dto) // Setzt die Daten
                .build();

        if (log.isInfoEnabled()) {
            log.info("::getDTOAPIResponse completed successfully");
        }

        return response;
    }

    /**
     * Creates an API response without any data.
     *
     * @param msg        the message to include in the response
     * @param statusMsg  the status message to include in the response
     * @param statusCode the HTTP status code to include in the response
     * @return the API response
     */
    public static APIResponse<Void> getVoidAPIResponse(
            final String msg,
            final String statusMsg,
            final HttpStatus statusCode) {

        if (log.isInfoEnabled()) {
            log.info("::getVoidAPIResponse started with: msg {}, statusMsg {}, statusCode {}",
                    msg, statusMsg, statusCode);
        }

        String finalMsg = msg == null ? "No message provided" : msg;

        APIResponse<Void> response = APIResponse.<Void>builder()
                .message(finalMsg) // Setzt die Nachricht
                .status(statusMsg) // Setzt den Status
                .statusCode(statusCode.value()) // Setzt den Statuscode
                .build();

        if (log.isInfoEnabled()) {
            log.info("::getVoidAPIResponse completed successfully");
        }

        return response;
    }
}
