package edu.yacoubi.crm.service.util;

/**
 * Functional interface representing an action that can be performed on an entity identified by a Long ID.
 * This interface is typically used to encapsulate actions that need to be executed on entities within
 * service implementations.
 *
 * <p>Example usage:</p>
 * <pre>{@code
 * EntityAction deleteEmployeeAction = id -> {
 *     // action logic here
 * };
 * }</pre>
 *
 * @FunctionalInterface
 */
@FunctionalInterface
public interface EntityAction {
    /**
     * Executes an action on the entity identified by the given ID.
     *
     * @param id the ID of the entity on which the action is to be performed.
     */
    void execute(Long id);
}
