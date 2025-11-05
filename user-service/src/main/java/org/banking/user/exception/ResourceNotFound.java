package org.banking.user.exception;

public class ResourceNotFound extends GlobalException {

    public ResourceNotFound() {
        super("Resource not found on server", GlobalError.NOT_FOUND);
    }

    public ResourceNotFound(String message) {
        super(message, GlobalError.NOT_FOUND);
    }

}
