package edu.yacoubi.crm.util;

/**
 * Functional Interface für die Transformation von Entitäten und DTOs.
 *
 * <p>Dieses Interface definiert eine Methode zur Transformation einer Entität vom Typ {@code T}
 * in einen Zieltyp {@code R}. Es kann verwendet werden, um allgemeine Transformationen zwischen
 * verschiedenen Typen zu abstrahieren und wiederverwendbare TransformationsLogik zu schaffen.</p>
 *
 * <p>Beispielverwendung:</p>
 * <pre>{@code
 * Transformer<Note, NoteDTO> noteToDtoTransformer = note -> new NoteDTO(note.getId(), note.getContent());
 * NoteDTO noteDto = noteToDtoTransformer.transform(noteInstance);
 * }</pre>
 *
 * @param <T> der Eingabetyp der zu transformierenden Entität
 * @param <R> der Rückgabetyp der transformierten Entität
 */
@FunctionalInterface
public interface Transformer<T, R> {
    /**
     * Transformiert eine Instanz der Eingabetyp {@code T} in eine Instanz des Rückgabetyps {@code R}.
     *
     * @param t die zu transformierende Instanz der Eingabetyp
     * @return die transformierte Instanz des Rückgabetyps
     */
    R transform(T t);
}
