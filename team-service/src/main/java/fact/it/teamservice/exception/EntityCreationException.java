package fact.it.teamservice.exception;

public class EntityCreationException extends RuntimeException {
    public EntityCreationException(String entityType, String message) {
        super("Error creating entity of type " + entityType + ": " + message);
    }
}
