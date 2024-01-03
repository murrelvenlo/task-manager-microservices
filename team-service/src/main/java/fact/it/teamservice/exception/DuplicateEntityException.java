package fact.it.teamservice.exception;

public class DuplicateEntityException extends RuntimeException {
    private final String entityType;

    public DuplicateEntityException(String entityType, String message) {
        super(message);
        this.entityType = entityType;
    }

    public String getEntityType() {
        return entityType;
    }
}
