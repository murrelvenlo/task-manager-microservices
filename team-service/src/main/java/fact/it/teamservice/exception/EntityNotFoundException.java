package fact.it.teamservice.exception;


public class EntityNotFoundException extends RuntimeException {
    private String identifier;

    public EntityNotFoundException(String entityType, String identifier) {
        super("Entity not found: " + entityType + " with identifier: " + identifier);
        this.identifier = identifier;
    }

    public EntityNotFoundException(String entityType) {
        super("Entity not found: " + entityType);
    }

    // Getter and setter for identifier if needed
    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }
}
