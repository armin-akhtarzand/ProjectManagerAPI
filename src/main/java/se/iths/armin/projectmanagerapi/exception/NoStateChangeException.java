package se.iths.armin.projectmanagerapi.exception;

public class NoStateChangeException extends RuntimeException {
    public NoStateChangeException(String message) {
        super(message);
    }
}
