package edu.yacoubi.crm.util;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DateFormattingTest {

    @Test
    public void testDateToStringTransformation() {
        // Given
        // an implementation transformer to format a date
        ITransformer<LocalDate, String> dateToString =
                date -> date.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        LocalDate today = LocalDate.of(2024, 12, 29);
        String expected = "29-12-2024";

        // When
        // the date is formatted using the transformer
        String formatted = dateToString.transform(today);

        // Then
        // verify that the formatted date is as expected
        assertEquals(expected, formatted);

        // When
        // the date is formatted using the TransformerUtil
        String formattedViaUtil = TransformerUtil.transform(dateToString, today);

        // Then
        // verify that the formatted date is as expected
        assertEquals(expected, formattedViaUtil);
    }
}
