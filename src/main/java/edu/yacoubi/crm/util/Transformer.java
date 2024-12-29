package edu.yacoubi.crm.util;

/**
 * Functional Interface für die Transformation von Entitäten und DTOs.
 *
 * @param <T> Eingabetyp
 * @param <R> Rückgabetyp
 */
@FunctionalInterface
public interface Transformer<T, R> {
    R transform(T t);
}
