package edu.yacoubi.crm.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TemperatureConversionTest {

    @Test
    public void testCelsiusToFahrenheitTransformation() {
        // Given
        // an implementation transformer to convert Celsius to Fahrenheit
        ITransformer<Double, Double> celsiusToFahrenheit = celsius -> (celsius * 9/5) + 32;
        Double celsius = 25.0;
        Double erwartet = (celsius * 9/5) + 32;

        // When
        // the temperature is converted using the transformer
        Double transformedTemperature = celsiusToFahrenheit.transform(celsius);

        // Then
        // verify that the converted temperature is as expected
        assertEquals(erwartet, transformedTemperature);

        // When
        // the temperature is converted using the TransformerUtil
        Double fahrenheit = TransformerUtil.transform(celsiusToFahrenheit, celsius);

        // Then
        // verify that the converted temperature is as expected
        assertEquals(erwartet, fahrenheit);
    }
}
