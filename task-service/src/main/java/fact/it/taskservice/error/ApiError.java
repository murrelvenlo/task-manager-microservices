package fact.it.taskservice.error;

import org.springframework.http.HttpStatus;

public class ApiError {
    private final HttpStatus status;
    private final String message;
    private final String entityType;
    public ApiError(HttpStatus status, String message, String entityType) {
        this.status = status;
        this.message = message;
        this.entityType = entityType;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public String getEntityType() {
        return entityType;
    }
}

