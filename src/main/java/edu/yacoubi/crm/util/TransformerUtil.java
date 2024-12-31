package edu.yacoubi.crm.util;

/**
 * Utility-Klasse für allgemeine Transformationsmethoden.
 *
 * <p>Diese Klasse stellt eine universelle Methode zur Verfügung, um beliebige Transformationen
 * mithilfe des {@link ITransformer} Functional Interface durchzuführen. Sie ermöglicht es,
 * eingehende Entitäten in verschiedene Zieltypen zu transformieren, ohne die Logik
 * der Transformationsmethode zu ändern.</p>
 *
 * <p>Beispielverwendung:</p>
 * <pre>{@code
 * Transformer<SourceType, TargetType> transformer = source -> new TargetType(source.getProperty());
 * TargetType target = TransformerUtil.transform(transformer, sourceInstance);
 * }</pre>
 *
 * <p>Die Klasse kann nicht instanziiert werden, da der Konstruktor privat ist.</p>
 */
public class TransformerUtil {

    private TransformerUtil() {
        // Privater Konstruktor, um Instanziierung zu verhindern
    }

    /**
     * Universelle Transformationsmethode, die den übergebenen {@link ITransformer} auf die Entität anwendet.
     *
     * <p>Diese Methode nimmt eine Entität und einen {@link ITransformer} entgegen und gibt die transformierte
     * Entität zurück. Sie ist nützlich für allgemeine Transformationen zwischen verschiedenen Typen.</p>
     *
     * <p>Beispiel:</p>
     * <pre>{@code
     * Transformer<Note, NoteDTO> noteToDtoTransformer = note -> new NoteDTO(note.getId(), note.getContent());
     * NoteDTO noteDto = TransformerUtil.transform(noteToDtoTransformer, noteInstance);
     * }</pre>
     *
     * @param ITransformer Functional Interface zur Transformation
     * @param entity die zu transformierende Entität
     * @param <T> Eingabetyp
     * @param <R> Rückgabetyp
     * @return transformierte Entität
     * @throws NullPointerException wenn der Transformer oder die Entität null ist
     */
    public static <T, R> R transform(ITransformer<T, R> ITransformer, T entity) {
        if (ITransformer == null || entity == null) {
            throw new IllegalArgumentException("Transformer and entity must not be null");
        }
        return ITransformer.transform(entity);
    }
}
