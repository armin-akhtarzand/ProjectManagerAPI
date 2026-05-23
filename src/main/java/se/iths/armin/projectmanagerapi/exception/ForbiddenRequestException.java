package se.iths.armin.projectmanagerapi.exception;

public class ForbiddenRequestException extends RuntimeException {
    public ForbiddenRequestException(String message) {
        super(message);
    }
}
