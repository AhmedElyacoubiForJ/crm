package edu.yacoubi.crm.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class StringManipulationTest {

    @Test
    public void testReverseStringTransformation() {
        // Given
        // an implementation transformer to reverse a string
        Transformer<String, String> reverseString = str -> new StringBuilder(str).reverse().toString();
        String original = "FunctionalInterface";
        String expected = "ecafretnIlanoitcnuF";

        // When
        // the string is reversed using the transformer
        String reversed = reverseString.transform(original);

        // Then
        // verify that the reversed string is as expected
        assertEquals(expected, reversed);

        // When
        // the string is reversed using the TransformerUtil
        String reversedViaUtil = TransformerUtil.transform(reverseString, original);

        // Then
        // verify that the reversed string is as expected
        assertEquals(expected, reversedViaUtil);
    }
}
