package com.isga.backend_helpdesk.common.exception;

/** Exception métier traduite en réponse HTTP 404 par le gestionnaire global. */
public class ResourceNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public ResourceNotFoundException(String message) {
        super(message);
    }
}
