package jm.task.core.jdbc.exception;

import lombok.Getter;
import java.time.LocalDateTime;

@Getter
public class DatabaseException extends RuntimeException {
    private final String reason;

    private final LocalDateTime timestamp;

    public DatabaseException(String message, Throwable cause) {
        super("DB Exception: " + message, cause);
        this.reason = message;
        this.timestamp = LocalDateTime.now();
    }

    public DatabaseException(Throwable cause) {
        super("DB Exception", cause);
        this.reason = "Unknown error with DB";
        this.timestamp = LocalDateTime.now();
    }
}
